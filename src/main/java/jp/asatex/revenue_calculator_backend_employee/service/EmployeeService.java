package jp.asatex.revenue_calculator_backend_employee.service;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.dto.PageRequest;
import jp.asatex.revenue_calculator_backend_employee.dto.PageResponse;
import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import jp.asatex.revenue_calculator_backend_employee.exception.DuplicateEmployeeNumberException;
import jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundException;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
import jp.asatex.revenue_calculator_backend_employee.util.LoggingUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Employee service layer
 * Provides employee business logic processing
 */
@Service
public class EmployeeService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TransactionMonitoringService transactionMonitoringService;

    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private DatabaseAuditService databaseAuditService;
    
    @Autowired
    private Counter employeeOperationCounter;
    
    @Autowired
    private Counter employeeCreateCounter;
    
    @Autowired
    private Counter employeeUpdateCounter;
    
    @Autowired
    private Counter employeeDeleteCounter;
    
    @Autowired
    private Counter employeeQueryCounter;
    
    @Autowired
    private Timer employeeOperationTimer;
    
    @Autowired
    private Timer databaseOperationTimer;
    
    /**
     * Get all employees
     * @return Flux<EmployeeDto>
     */
    public Flux<EmployeeDto> getAllEmployees() {
        logger.debug("Retrieving all employees");
        employeeQueryCounter.increment();
        
        return employeeRepository.findAll()
                .map(this::convertToDto)
                .doOnNext(employee -> LoggingUtil.logDataAccess("SELECT", "employees", employee.getEmployeeId()))
                .doOnComplete(() -> logger.info("Successfully retrieved all employees"))
                .doOnSubscribe(subscription -> {
                    Timer.Sample sample = Timer.start();
                    sample.stop(employeeOperationTimer);
                });
    }
    
    /**
     * Get employee by ID
     * @param id Employee ID
     * @return Mono<EmployeeDto>
     */
    @Cacheable(value = "employees", key = "#id", unless = "#result == null")
    public Mono<EmployeeDto> getEmployeeById(Long id) {
        long startTime = System.currentTimeMillis();
        employeeQueryCounter.increment();
        return employeeRepository.findById(id)
                .map(this::convertToDto)
                .doOnSuccess(employee -> {
                    if (employee != null) {
                        long executionTime = System.currentTimeMillis() - startTime;
                        // Log database audit
                        // SELECT operation affected rows is the number of records returned by query
                        int affectedRows = employee != null ? 1 : 0;
                        databaseAuditService.logSelectOperation(
                                "employees",
                                employee.getEmployeeId().toString(),
                                "SELECT * FROM employees WHERE id = ?",
                                executionTime, // Dynamic execution time
                                affectedRows   // Dynamic affected rows
                        ).subscribe();
                    }
                });
    }
    
    /**
     * Get employee by employee number
     * @param employeeNumber Employee number
     * @return Mono<EmployeeDto>
     */
    @Cacheable(value = "employees", key = "'number:' + #employeeNumber", unless = "#result == null")
    public Mono<EmployeeDto> getEmployeeByNumber(String employeeNumber) {
        return employeeRepository.findByEmployeeNumber(employeeNumber)
                .map(this::convertToDto);
    }
    
    /**
     * Search employees by name
     * @param name Name keyword
     * @return Flux<EmployeeDto>
     */
    public Flux<EmployeeDto> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContaining("%" + name + "%")
                .map(this::convertToDto);
    }
    
    /**
     * Search employees by furigana
     * @param furigana Furigana keyword
     * @return Flux<EmployeeDto>
     */
    public Flux<EmployeeDto> searchEmployeesByFurigana(String furigana) {
        return employeeRepository.findByFuriganaContaining("%" + furigana + "%")
                .map(this::convertToDto);
    }
    
    /**
     * Create new employee
     * @param employeeDto Employee information
     * @return Mono<EmployeeDto>
     */
    @Transactional
    @CacheEvict(value = {"employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
    public Mono<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
        logger.info("Creating new employee with number: {}", employeeDto.getEmployeeNumber());
        LoggingUtil.logBusinessOperation("CREATE_EMPLOYEE", "Employee number: %s", employeeDto.getEmployeeNumber());
        employeeOperationCounter.increment();
        employeeCreateCounter.increment();
        
        long startTime = System.currentTimeMillis();
        
        // Use transaction monitoring to wrap operation
        return transactionMonitoringService.monitorTransaction(
                "CREATE_EMPLOYEE", 
                "Employee number: " + employeeDto.getEmployeeNumber(),
                // Check if employee number already exists
                employeeRepository.existsByEmployeeNumber(employeeDto.getEmployeeNumber())
                        .flatMap(exists -> {
                            if (exists) {
                                logger.warn("Duplicate employee number detected: {}", employeeDto.getEmployeeNumber());
                                LoggingUtil.logSecurity("DUPLICATE_EMPLOYEE_NUMBER", "Attempted to create employee with existing number: " + employeeDto.getEmployeeNumber());
                                return Mono.error(new DuplicateEmployeeNumberException("Employee number already exists: " + employeeDto.getEmployeeNumber()));
                            }
                            return Mono.just(convertToEntity(employeeDto));
                        })
                        .flatMap(employee -> {
                            Timer.Sample sample = Timer.start();
                            return employeeRepository.save(employee)
                                    .doOnSuccess(savedEmployee -> sample.stop(databaseOperationTimer))
                                    .doOnError(error -> sample.stop(databaseOperationTimer));
                        })
                        .map(this::convertToDto)
                        .doOnSuccess(createdEmployee -> {
                            long executionTime = System.currentTimeMillis() - startTime;
                            logger.info("Successfully created employee: {} with ID: {}", 
                                    createdEmployee.getEmployeeNumber(), createdEmployee.getEmployeeId());
                            LoggingUtil.logDataAccess("INSERT", "employees", createdEmployee.getEmployeeId());
                            
                            // Log audit
                            Map<String, Object> details = new HashMap<>();
                            details.put("employeeNumber", createdEmployee.getEmployeeNumber());
                            details.put("name", createdEmployee.getName());
                            details.put("furigana", createdEmployee.getFurigana());
                            details.put("birthday", createdEmployee.getBirthday());
                            
                            auditLogService.logDataAccess("CREATE", "employees", 
                                    createdEmployee.getEmployeeId().toString(), 
                                    "system", null, details);
                            
                            auditLogService.logBusinessOperation("CREATE_EMPLOYEE", "Employee", 
                                    createdEmployee.getEmployeeId().toString(), 
                                    "system", "SUCCESS", details);
                            
                            // Log database audit - using dynamically calculated execution time and affected rows
                            // INSERT operation usually affects 1 row (newly inserted record)
                            int affectedRows = createdEmployee.getEmployeeId() != null ? 1 : 0;
                            databaseAuditService.logInsertOperation(
                                    "employees", 
                                    createdEmployee.getEmployeeId().toString(),
                                    details,
                                    "INSERT INTO employees (employee_number, name, furigana, birthday, deleted) VALUES (?, ?, ?, ?, ?)",
                                    executionTime, // Dynamic execution time
                                    affectedRows   // Dynamic affected rows
                            ).subscribe();
                        })
                        .doOnError(error -> {
                            long executionTime = System.currentTimeMillis() - startTime;
                            logger.error("Failed to create employee: {}", employeeDto.getEmployeeNumber(), error);
                            LoggingUtil.logError("CREATE_EMPLOYEE", error, "Employee number: " + employeeDto.getEmployeeNumber());
                            
                            // Log error audit
                            Map<String, Object> errorDetails = new HashMap<>();
                            errorDetails.put("employeeNumber", employeeDto.getEmployeeNumber());
                            errorDetails.put("errorMessage", error.getMessage());
                            errorDetails.put("errorType", error.getClass().getSimpleName());
                            
                            auditLogService.logError("CREATE_EMPLOYEE_FAILED", "EmployeeService", 
                                    "Failed to create employee: " + employeeDto.getEmployeeNumber(), 
                                    "system", errorDetails);
                            
                            // Log failed database operation
                            databaseAuditService.logFailedOperation(
                                    "INSERT",
                                    "employees",
                                    employeeDto.getEmployeeNumber(),
                                    "INSERT INTO employees (employee_number, name, furigana, birthday, deleted) VALUES (?, ?, ?, ?, ?)",
                                    executionTime,
                                    error.getMessage()
                            ).subscribe();
                        })
        );
    }
    
    /**
     * Update employee information
     * @param id Employee ID
     * @param employeeDto Employee information
     * @return Mono<EmployeeDto>
     */
    @Transactional
    @CacheEvict(value = {"employees", "employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
    public Mono<EmployeeDto> updateEmployee(Long id, EmployeeDto employeeDto) {
        logger.info("Updating employee with ID: {}", id);
        LoggingUtil.logBusinessOperation("UPDATE_EMPLOYEE", "Employee ID: %s", id);
        employeeOperationCounter.increment();
        employeeUpdateCounter.increment();
        
        long startTime = System.currentTimeMillis();
        
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("Employee not found, ID: " + id)))
                .flatMap(existingEmployee -> {
                    // If employee number is changed, check if new number already exists
                    if (!existingEmployee.getEmployeeNumber().equals(employeeDto.getEmployeeNumber())) {
                        return employeeRepository.existsByEmployeeNumber(employeeDto.getEmployeeNumber())
                                .flatMap(exists -> exists 
                                    ? Mono.error(new DuplicateEmployeeNumberException("Employee number already exists: " + employeeDto.getEmployeeNumber()))
                                    : Mono.just(existingEmployee));
                    }
                    return Mono.just(existingEmployee);
                })
                .map(existingEmployee -> {
                    Employee updatedEmployee = convertToEntity(employeeDto);
                    updatedEmployee.setEmployeeId(id);
                    return updatedEmployee;
                })
                .flatMap(employeeRepository::save)
                .map(this::convertToDto)
                .doOnSuccess(updatedEmployee -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.info("Successfully updated employee: {} with ID: {}", 
                            updatedEmployee.getEmployeeNumber(), updatedEmployee.getEmployeeId());
                    LoggingUtil.logDataAccess("UPDATE", "employees", updatedEmployee.getEmployeeId());
                    
                    // Log database audit
                    Map<String, Object> oldValues = new HashMap<>();
                    oldValues.put("employeeNumber", employeeDto.getEmployeeNumber());
                    oldValues.put("name", employeeDto.getName());
                    oldValues.put("furigana", employeeDto.getFurigana());
                    oldValues.put("birthday", employeeDto.getBirthday());
                    
                    Map<String, Object> newValues = new HashMap<>();
                    newValues.put("employeeNumber", updatedEmployee.getEmployeeNumber());
                    newValues.put("name", updatedEmployee.getName());
                    newValues.put("furigana", updatedEmployee.getFurigana());
                    newValues.put("birthday", updatedEmployee.getBirthday());
                    
                    // UPDATE operation usually affects 1 row (updated record)
                    int affectedRows = updatedEmployee.getEmployeeId() != null ? 1 : 0;
                    databaseAuditService.logUpdateOperation(
                            "employees",
                            updatedEmployee.getEmployeeId().toString(),
                            oldValues,
                            newValues,
                            "UPDATE employees SET employee_number = ?, name = ?, furigana = ?, birthday = ? WHERE id = ?",
                            executionTime, // Dynamic execution time
                            affectedRows   // Dynamic affected rows
                    ).subscribe();
                })
                .doOnError(error -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.error("Failed to update employee with ID: {}", id, error);
                    LoggingUtil.logError("UPDATE_EMPLOYEE", error, "Employee ID: " + id);
                    
                    // Log failed database operation
                    databaseAuditService.logFailedOperation(
                            "UPDATE",
                            "employees",
                            id.toString(),
                            "UPDATE employees SET employee_number = ?, name = ?, furigana = ?, birthday = ? WHERE id = ?",
                            executionTime,
                            error.getMessage()
                    ).subscribe();
                });
    }
    
    /**
     * Delete employee by ID
     * @param id Employee ID
     * @return Mono<Void>
     */
    @Transactional
    @CacheEvict(value = {"employees", "employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
    public Mono<Void> deleteEmployeeById(Long id) {
        logger.info("Deleting employee with ID: {}", id);
        LoggingUtil.logBusinessOperation("DELETE_EMPLOYEE", "Employee ID: %s", id);
        employeeOperationCounter.increment();
        employeeDeleteCounter.increment();
        
        long startTime = System.currentTimeMillis();
        
        return employeeRepository.existsById(id)
                .flatMap(exists -> exists 
                    ? employeeRepository.deleteById(id)
                            .doOnSuccess(unused -> {
                                long executionTime = System.currentTimeMillis() - startTime;
                                logger.info("Successfully deleted employee with ID: {}", id);
                                LoggingUtil.logDataAccess("DELETE", "employees", id);
                                
                                // Log database audit
                                Map<String, Object> details = new HashMap<>();
                                details.put("employeeId", id);
                                
                                // DELETE operation usually affects 1 row (deleted record)
                                int affectedRows = 1; // Delete operation affects 1 row when successful
                                databaseAuditService.logDeleteOperation(
                                        "employees",
                                        id.toString(),
                                        details,
                                        "DELETE FROM employees WHERE id = ?",
                                        executionTime, // Dynamic execution time
                                        affectedRows   // Dynamic affected rows
                                ).subscribe();
                            })
                            .doOnError(error -> {
                                long executionTime = System.currentTimeMillis() - startTime;
                                logger.error("Failed to delete employee with ID: {}", id, error);
                                LoggingUtil.logError("DELETE_EMPLOYEE", error, "Employee ID: " + id);
                                
                                // Log failed database operation
                                databaseAuditService.logFailedOperation(
                                        "DELETE",
                                        "employees",
                                        id.toString(),
                                        "DELETE FROM employees WHERE id = ?",
                                        executionTime,
                                        error.getMessage()
                                ).subscribe();
                            })
                    : Mono.error(new EmployeeNotFoundException("Employee not found, ID: " + id)));
    }
    
    /**
     * Delete employee by employee number
     * @param employeeNumber Employee number
     * @return Mono<Void>
     */
    @Transactional
    @CacheEvict(value = {"employees", "employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
    public Mono<Void> deleteEmployeeByNumber(String employeeNumber) {
        logger.info("Deleting employee with number: {}", employeeNumber);
        LoggingUtil.logBusinessOperation("DELETE_EMPLOYEE", "Employee number: %s", employeeNumber);
        employeeOperationCounter.increment();
        employeeDeleteCounter.increment();
        
        long startTime = System.currentTimeMillis();
        
        return employeeRepository.existsByEmployeeNumber(employeeNumber)
                .flatMap(exists -> exists 
                    ? employeeRepository.deleteByEmployeeNumber(employeeNumber)
                            .doOnSuccess(unused -> {
                                long executionTime = System.currentTimeMillis() - startTime;
                                logger.info("Successfully deleted employee with number: {}", employeeNumber);
                                LoggingUtil.logDataAccess("DELETE", "employees", employeeNumber);
                                
                                // Log database audit
                                Map<String, Object> details = new HashMap<>();
                                details.put("employeeNumber", employeeNumber);
                                
                                // DELETE operation usually affects 1 row (deleted record)
                                int affectedRows = 1; // Delete operation affects 1 row when successful
                                databaseAuditService.logDeleteOperation(
                                        "employees",
                                        employeeNumber,
                                        details,
                                        "DELETE FROM employees WHERE employee_number = ?",
                                        executionTime, // Dynamic execution time
                                        affectedRows   // Dynamic affected rows
                                ).subscribe();
                            })
                            .doOnError(error -> {
                                long executionTime = System.currentTimeMillis() - startTime;
                                logger.error("Failed to delete employee with number: {}", employeeNumber, error);
                                LoggingUtil.logError("DELETE_EMPLOYEE", error, "Employee number: " + employeeNumber);
                                
                                // Log failed database operation
                                databaseAuditService.logFailedOperation(
                                        "DELETE",
                                        "employees",
                                        employeeNumber,
                                        "DELETE FROM employees WHERE employee_number = ?",
                                        executionTime,
                                        error.getMessage()
                                ).subscribe();
                            })
                    : Mono.error(new EmployeeNotFoundException("Employee not found, number: " + employeeNumber)));
    }
    
    /**
     * Convert Entity to DTO
     * @param employee Employee entity
     * @return EmployeeDto
     */
    EmployeeDto convertToDto(Employee employee) {
        return new EmployeeDto(
                employee.getEmployeeId(),
                employee.getEmployeeNumber(),
                employee.getName(),
                employee.getFurigana(),
                employee.getBirthday()
        );
    }
    
    /**
     * Convert DTO to Entity
     * @param employeeDto EmployeeDto
     * @return Employee
     */
    Employee convertToEntity(EmployeeDto employeeDto) {
        return new Employee(
                employeeDto.getEmployeeId(),
                employeeDto.getEmployeeNumber(),
                employeeDto.getName(),
                employeeDto.getFurigana(),
                employeeDto.getBirthday()
        );
    }
    
    /**
     * Get all employees with pagination
     * @param pageRequest Pagination request parameters
     * @return Mono<PageResponse<EmployeeDto>>
     */
    @Cacheable(value = "employeePagination", key = "#pageRequest.page + '_' + #pageRequest.size + '_' + #pageRequest.sortBy + '_' + #pageRequest.sortDirection", unless = "#result == null")
    public Mono<PageResponse<EmployeeDto>> getAllEmployeesWithPagination(PageRequest pageRequest) {
        logger.debug("Retrieving employees with pagination: {}", pageRequest);
        employeeQueryCounter.increment();
        
        String sortBy = validateAndNormalizeSortField(pageRequest.getSortBy());
        String sortDirection = pageRequest.getSortDirection().getValue();
        
        Flux<Employee> employeeFlux = getEmployeeFluxForSorting(sortBy, sortDirection, pageRequest.getOffset(), pageRequest.getSize());
        
        return Mono.zip(
                employeeFlux.map(this::convertToDto).collectList(),
                employeeRepository.countAllActive()
        ).map(tuple -> new PageResponse<>(
                tuple.getT1(),
                pageRequest.getPage(),
                pageRequest.getSize(),
                tuple.getT2(),
                sortBy,
                sortDirection
        )).doOnSuccess(response -> {
            logger.info("Successfully retrieved {} employees with pagination", response.getNumberOfElements());
            LoggingUtil.logDataAccess("SELECT", "employees", (long) response.getNumberOfElements());
        });
    }
    
    /**
     * Search employees with pagination (by name)
     * @param name Name keyword
     * @param pageRequest Pagination request parameters
     * @return Mono<PageResponse<EmployeeDto>>
     */
    @Cacheable(value = "employeeSearch", key = "'name:' + #name + '_' + #pageRequest.page + '_' + #pageRequest.size + '_' + #pageRequest.sortBy + '_' + #pageRequest.sortDirection", unless = "#result == null")
    public Mono<PageResponse<EmployeeDto>> searchEmployeesByNameWithPagination(String name, PageRequest pageRequest) {
        logger.debug("Searching employees by name '{}' with pagination: {}", name, pageRequest);
        employeeQueryCounter.increment();
        
        String sortBy = validateAndNormalizeSortField(pageRequest.getSortBy());
        String sortDirection = pageRequest.getSortDirection().getValue();
        String searchPattern = "%" + name + "%";
        
        Flux<Employee> employeeFlux = getEmployeeFluxForNameSearch(searchPattern, sortBy, sortDirection, pageRequest.getOffset(), pageRequest.getSize());
        
        return Mono.zip(
                employeeFlux.map(this::convertToDto).collectList(),
                employeeRepository.countByNameContaining(searchPattern)
        ).map(tuple -> new PageResponse<>(
                tuple.getT1(),
                pageRequest.getPage(),
                pageRequest.getSize(),
                tuple.getT2(),
                sortBy,
                sortDirection
        )).doOnSuccess(response -> {
            logger.info("Successfully found {} employees by name '{}' with pagination", response.getNumberOfElements(), name);
            LoggingUtil.logDataAccess("SELECT", "employees", (long) response.getNumberOfElements());
        });
    }
    
    /**
     * Search employees with pagination (by furigana)
     * @param furigana Furigana keyword
     * @param pageRequest Pagination request parameters
     * @return Mono<PageResponse<EmployeeDto>>
     */
    @Cacheable(value = "employeeSearch", key = "'furigana:' + #furigana + '_' + #pageRequest.page + '_' + #pageRequest.size + '_' + #pageRequest.sortBy + '_' + #pageRequest.sortDirection", unless = "#result == null")
    public Mono<PageResponse<EmployeeDto>> searchEmployeesByFuriganaWithPagination(String furigana, PageRequest pageRequest) {
        logger.debug("Searching employees by furigana '{}' with pagination: {}", furigana, pageRequest);
        employeeQueryCounter.increment();
        
        String sortBy = validateAndNormalizeSortField(pageRequest.getSortBy());
        String sortDirection = pageRequest.getSortDirection().getValue();
        String searchPattern = "%" + furigana + "%";
        
        Flux<Employee> employeeFlux = getEmployeeFluxForFuriganaSearch(searchPattern, sortBy, sortDirection, pageRequest.getOffset(), pageRequest.getSize());
        
        return Mono.zip(
                employeeFlux.map(this::convertToDto).collectList(),
                employeeRepository.countByFuriganaContaining(searchPattern)
        ).map(tuple -> new PageResponse<>(
                tuple.getT1(),
                pageRequest.getPage(),
                pageRequest.getSize(),
                tuple.getT2(),
                sortBy,
                sortDirection
        )).doOnSuccess(response -> {
            logger.info("Successfully found {} employees by furigana '{}' with pagination", response.getNumberOfElements(), furigana);
            LoggingUtil.logDataAccess("SELECT", "employees", (long) response.getNumberOfElements());
        });
    }
    
    /**
     * Get employee data stream based on sort field and direction
     * @param sortBy Sort field
     * @param sortDirection Sort direction
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    private Flux<Employee> getEmployeeFluxForSorting(String sortBy, String sortDirection, int offset, int limit) {
        if ("employee_id".equals(sortBy)) {
            return "ASC".equals(sortDirection) 
                ? employeeRepository.findAllWithPaginationByIdAsc(offset, limit)
                : employeeRepository.findAllWithPaginationByIdDesc(offset, limit);
        } else if ("employee_number".equals(sortBy)) {
            return "ASC".equals(sortDirection)
                ? employeeRepository.findAllWithPaginationByNumberAsc(offset, limit)
                : employeeRepository.findAllWithPaginationByNumberDesc(offset, limit);
        } else if ("name".equals(sortBy)) {
            return "ASC".equals(sortDirection)
                ? employeeRepository.findAllWithPaginationByNameAsc(offset, limit)
                : employeeRepository.findAllWithPaginationByNameDesc(offset, limit);
        } else {
            // Default sort by employee ID
            return "ASC".equals(sortDirection)
                ? employeeRepository.findAllWithPaginationByIdAsc(offset, limit)
                : employeeRepository.findAllWithPaginationByIdDesc(offset, limit);
        }
    }
    
    /**
     * Get employee data stream for name search based on sort field and direction
     * @param name Name keyword
     * @param sortBy Sort field
     * @param sortDirection Sort direction
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    private Flux<Employee> getEmployeeFluxForNameSearch(String name, String sortBy, String sortDirection, int offset, int limit) {
        if ("employee_id".equals(sortBy)) {
            return "ASC".equals(sortDirection)
                ? employeeRepository.findByNameContainingWithPaginationByIdAsc(name, offset, limit)
                : employeeRepository.findByNameContainingWithPaginationByIdDesc(name, offset, limit);
        } else if ("name".equals(sortBy)) {
            return "ASC".equals(sortDirection)
                ? employeeRepository.findByNameContainingWithPaginationByNameAsc(name, offset, limit)
                : employeeRepository.findByNameContainingWithPaginationByNameDesc(name, offset, limit);
        } else {
            // Default sort by employee ID
            return "ASC".equals(sortDirection)
                ? employeeRepository.findByNameContainingWithPaginationByIdAsc(name, offset, limit)
                : employeeRepository.findByNameContainingWithPaginationByIdDesc(name, offset, limit);
        }
    }
    
    /**
     * Get employee data stream for furigana search based on sort field and direction
     * @param furigana Furigana keyword
     * @param sortBy Sort field
     * @param sortDirection Sort direction
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    private Flux<Employee> getEmployeeFluxForFuriganaSearch(String furigana, String sortBy, String sortDirection, int offset, int limit) {
        if ("employee_id".equals(sortBy)) {
            return "ASC".equals(sortDirection)
                ? employeeRepository.findByFuriganaContainingWithPaginationByIdAsc(furigana, offset, limit)
                : employeeRepository.findByFuriganaContainingWithPaginationByIdDesc(furigana, offset, limit);
        } else if ("name".equals(sortBy)) {
            return "ASC".equals(sortDirection)
                ? employeeRepository.findByFuriganaContainingWithPaginationByNameAsc(furigana, offset, limit)
                : employeeRepository.findByFuriganaContainingWithPaginationByNameDesc(furigana, offset, limit);
        } else {
            // Default sort by employee ID
            return "ASC".equals(sortDirection)
                ? employeeRepository.findByFuriganaContainingWithPaginationByIdAsc(furigana, offset, limit)
                : employeeRepository.findByFuriganaContainingWithPaginationByIdDesc(furigana, offset, limit);
        }
    }
    
    /**
     * Validate and normalize sort field
     * @param sortBy Sort field
     * @return Normalized sort field
     */
    private String validateAndNormalizeSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "employee_id";
        }
        
        // Allowed sort fields
        String[] allowedFields = {
            "employee_id", "employee_number", "name", "furigana", "birthday", "created_at", "updated_at"
        };
        
        String normalizedField = sortBy.toLowerCase().trim();
        
        // Check if field is in allowed list
        for (String field : allowedFields) {
            if (field.equals(normalizedField)) {
                return field;
            }
        }
        
        // If field is not in allowed list, return default field
        logger.warn("Invalid sort field '{}', using default 'employee_id'", sortBy);
        return "employee_id";
    }
}