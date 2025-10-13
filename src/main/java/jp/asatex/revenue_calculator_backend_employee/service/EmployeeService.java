package jp.asatex.revenue_calculator_backend_employee.service;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.common.PageRequest;
import jp.asatex.revenue_calculator_backend_employee.common.PageResponse;
import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import jp.asatex.revenue_calculator_backend_employee.exception.DuplicateEmployeeNumberHandler;
import jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundHandler;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    private SystemMonitoringService systemMonitoringService;
    
    @Autowired
    private Counter employeeOperationCounter;
    
    @Autowired
    private Counter employeeQueryCounter;
    
    @Autowired
    private Counter employeeCreateCounter;
    
    @Autowired
    private Counter employeeUpdateCounter;
    
    @Autowired
    private Counter employeeDeleteCounter;
    
    /**
     * Get employee by ID
     * @param id Employee ID
     * @return Mono<EmployeeDto>
     */
    @Cacheable(value = "employees", key = "#id")
    public Mono<EmployeeDto> getEmployeeById(Long id) {
        logger.debug("Retrieving employee with ID: {}", id);
        employeeQueryCounter.increment();
        
        return employeeRepository.findById(id)
                .map(this::convertToDto)
                .doOnSuccess(employee -> {
                    if (employee != null) {
                        logger.info("Successfully retrieved employee: {}", employee.getEmployeeNumber());
                    }
                })
                .doOnError(error -> logger.error("Failed to retrieve employee with ID: {}", id, error))
                .switchIfEmpty(Mono.error(new EmployeeNotFoundHandler("Employee not found with ID: " + id)));
    }
    
    /**
     * Get employee by employee number
     * @param employeeNumber Employee number
     * @return Mono<EmployeeDto>
     */
    @Cacheable(value = "employees", key = "'number:' + #employeeNumber")
    public Mono<EmployeeDto> getEmployeeByNumber(String employeeNumber) {
        logger.debug("Retrieving employee with number: {}", employeeNumber);
        employeeQueryCounter.increment();
        
        return employeeRepository.findByEmployeeNumber(employeeNumber)
                .map(this::convertToDto)
                .doOnSuccess(employee -> {
                    if (employee != null) {
                        logger.info("Successfully retrieved employee: {}", employee.getEmployeeNumber());
                    }
                })
                .doOnError(error -> logger.error("Failed to retrieve employee with number: {}", employeeNumber, error))
                .switchIfEmpty(Mono.error(new EmployeeNotFoundHandler("Employee not found with number: " + employeeNumber)));
    }
    
    /**
     * Create new employee
     * @param employeeDto Employee data
     * @return Mono<EmployeeDto>
     */
    @Transactional
    public Mono<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
        logger.info("Creating new employee with number: {}", employeeDto.getEmployeeNumber());
        employeeOperationCounter.increment();
        employeeCreateCounter.increment();
        
        return systemMonitoringService.monitorTransaction(
                "CREATE_EMPLOYEE",
                "Creating employee: " + employeeDto.getEmployeeNumber(),
                employeeRepository.existsByEmployeeNumber(employeeDto.getEmployeeNumber())
                        .flatMap(exists -> {
                            if (exists) {
                                logger.warn("Duplicate employee number detected: {}", employeeDto.getEmployeeNumber());
                                return Mono.error(new DuplicateEmployeeNumberHandler("Employee number already exists: " + employeeDto.getEmployeeNumber()));
                            }
                            return Mono.just(convertToEntity(employeeDto));
                        })
                        .flatMap(employeeRepository::save)
                        .map(this::convertToDto)
                        .doOnSuccess(createdEmployee -> {
                            logger.info("Successfully created employee: {} with ID: {}", 
                                    createdEmployee.getEmployeeNumber(), createdEmployee.getEmployeeId());
                        })
                        .doOnError(error -> {
                            logger.error("Failed to create employee: {}", employeeDto.getEmployeeNumber(), error);
                        })
        );
    }
    
    /**
     * Update employee
     * @param id Employee ID
     * @param employeeDto Updated employee data
     * @return Mono<EmployeeDto>
     */
    @Transactional
    @CachePut(value = "employees", key = "#id")
    public Mono<EmployeeDto> updateEmployee(Long id, EmployeeDto employeeDto) {
        logger.info("Updating employee with ID: {}", id);
        employeeOperationCounter.increment();
        employeeUpdateCounter.increment();
        
        return systemMonitoringService.monitorTransaction(
                "UPDATE_EMPLOYEE",
                "Updating employee ID: " + id,
                employeeRepository.findById(id)
                        .switchIfEmpty(Mono.error(new EmployeeNotFoundHandler("Employee not found with ID: " + id)))
                        .flatMap(existingEmployee -> {
                            Employee updatedEmployee = convertToEntity(employeeDto);
                            updatedEmployee.setEmployeeId(id);
                            return employeeRepository.save(updatedEmployee);
                        })
                        .map(this::convertToDto)
                        .doOnSuccess(updatedEmployee -> {
                            logger.info("Successfully updated employee: {} with ID: {}", 
                                    updatedEmployee.getEmployeeNumber(), updatedEmployee.getEmployeeId());
                        })
                        .doOnError(error -> {
                            logger.error("Failed to update employee with ID: {}", id, error);
                        })
        );
    }
    
    /**
     * Delete employee by ID
     * @param id Employee ID
     * @return Mono<Void>
     */
    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public Mono<Void> deleteEmployeeById(Long id) {
        logger.info("Deleting employee with ID: {}", id);
        employeeOperationCounter.increment();
        employeeDeleteCounter.increment();
        
        return systemMonitoringService.monitorTransaction(
                "DELETE_EMPLOYEE_BY_ID",
                "Deleting employee ID: " + id,
                employeeRepository.findById(id)
                        .switchIfEmpty(Mono.error(new EmployeeNotFoundHandler("Employee not found with ID: " + id)))
                        .flatMap(employeeRepository::delete)
                        .doOnSuccess(unused -> {
                            logger.info("Successfully deleted employee with ID: {}", id);
                        })
                        .doOnError(error -> {
                            logger.error("Failed to delete employee with ID: {}", id, error);
                        })
        );
    }
    
    /**
     * Delete employee by employee number
     * @param employeeNumber Employee number
     * @return Mono<Void>
     */
    @Transactional
    @CacheEvict(value = "employees", key = "'number:' + #employeeNumber")
    public Mono<Void> deleteEmployeeByNumber(String employeeNumber) {
        logger.info("Deleting employee with number: {}", employeeNumber);
        employeeOperationCounter.increment();
        employeeDeleteCounter.increment();
        
        return systemMonitoringService.monitorTransaction(
                "DELETE_EMPLOYEE_BY_NUMBER",
                "Deleting employee number: " + employeeNumber,
                employeeRepository.findByEmployeeNumber(employeeNumber)
                        .switchIfEmpty(Mono.error(new EmployeeNotFoundHandler("Employee not found with number: " + employeeNumber)))
                        .flatMap(employeeRepository::delete)
                        .doOnSuccess(unused -> {
                            logger.info("Successfully deleted employee with number: {}", employeeNumber);
                        })
                        .doOnError(error -> {
                            logger.error("Failed to delete employee with number: {}", employeeNumber, error);
                        })
        );
    }
    
    /**
     * Search employees by name
     * @param name Employee name
     * @return Flux<EmployeeDto>
     */
    public Flux<EmployeeDto> searchEmployeesByName(String name) {
        logger.debug("Searching employees by name: {}", name);
        employeeQueryCounter.increment();
        
        return employeeRepository.findByNameContaining("%" + name + "%")
                .map(this::convertToDto)
                .doOnComplete(() -> logger.info("Successfully searched employees by name: {}", name))
                .doOnError(error -> logger.error("Failed to search employees by name: {}", name, error));
    }
    
    /**
     * Search employees by furigana
     * @param furigana Employee furigana
     * @return Flux<EmployeeDto>
     */
    public Flux<EmployeeDto> searchEmployeesByFurigana(String furigana) {
        logger.debug("Searching employees by furigana: {}", furigana);
        employeeQueryCounter.increment();
        
        return employeeRepository.findByFuriganaContaining("%" + furigana + "%")
                .map(this::convertToDto)
                .doOnComplete(() -> logger.info("Successfully searched employees by furigana: {}", furigana))
                .doOnError(error -> logger.error("Failed to search employees by furigana: {}", furigana, error));
    }
    
    /**
     * Get employees with pagination
     * @param pageRequest Page request
     * @return Mono<PageResponse<EmployeeDto>>
     */
    public Mono<PageResponse<EmployeeDto>> getEmployeesWithPagination(PageRequest pageRequest) {
        logger.debug("Retrieving employees with pagination: page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        employeeQueryCounter.increment();
        
        return employeeRepository.findAll()
                .map(this::convertToDto)
                .collectList()
                .map(employees -> {
                    // Apply sorting
                    employees.sort((e1, e2) -> {
                        String sortBy = pageRequest.getSortBy().toLowerCase();
                        boolean isDesc = pageRequest.getSortDirection().toString().equals("DESC");
                        
                        int comparison = 0;
                        switch (sortBy) {
                            case "employeeid":
                            case "employee_id":
                            case "id":
                                comparison = e1.getEmployeeId().compareTo(e2.getEmployeeId());
                                break;
                            case "name":
                                comparison = e1.getName().compareTo(e2.getName());
                                break;
                            case "employeenumber":
                            case "employee_number":
                            case "number":
                                comparison = e1.getEmployeeNumber().compareTo(e2.getEmployeeNumber());
                                break;
                            default:
                                comparison = e1.getEmployeeId().compareTo(e2.getEmployeeId());
                        }
                        
                        return isDesc ? -comparison : comparison;
                    });
                    
                    int totalElements = employees.size();
                    int startIndex = pageRequest.getPage() * pageRequest.getSize();
                    int endIndex = Math.min(startIndex + pageRequest.getSize(), totalElements);
                    
                    return new PageResponse<>(
                            employees.subList(startIndex, endIndex),
                            pageRequest.getPage(),
                            pageRequest.getSize(),
                            totalElements,
                            pageRequest.getSortBy(),
                            pageRequest.getSortDirection().toString()
                    );
                })
                .doOnSuccess(pageResponse -> logger.info("Successfully retrieved employees with pagination: {} items", pageResponse.getContent().size()))
                .doOnError(error -> logger.error("Failed to retrieve employees with pagination", error));
    }
    
    /**
     * Check if employee exists by employee number
     * @param employeeNumber Employee number
     * @return Mono<Boolean>
     */
    public Mono<Boolean> existsByEmployeeNumber(String employeeNumber) {
        logger.debug("Checking if employee exists with number: {}", employeeNumber);
        return employeeRepository.existsByEmployeeNumber(employeeNumber);
    }
    
    /**
     * Get total employee count
     * @return Mono<Long>
     */
    public Mono<Long> getEmployeeCount() {
        logger.debug("Getting total employee count");
        return employeeRepository.count()
                .doOnSuccess(count -> logger.info("Total employee count: {}", count))
                .doOnError(error -> logger.error("Failed to get employee count", error));
    }
    
    /**
     * Convert Employee entity to EmployeeDto
     * @param employee Employee entity
     * @return EmployeeDto
     */
    private EmployeeDto convertToDto(Employee employee) {
        if (employee == null) {
            return null;
        }
        
        EmployeeDto dto = new EmployeeDto();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setEmployeeNumber(employee.getEmployeeNumber());
        dto.setName(employee.getName());
        dto.setFurigana(employee.getFurigana());
        dto.setBirthday(employee.getBirthday());
        
        return dto;
    }
    
    /**
     * Convert EmployeeDto to Employee entity
     * @param dto EmployeeDto
     * @return Employee
     */
    private Employee convertToEntity(EmployeeDto dto) {
        if (dto == null) {
            return null;
        }
        
        Employee employee = new Employee();
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setEmployeeNumber(dto.getEmployeeNumber());
        employee.setName(dto.getName());
        employee.setFurigana(dto.getFurigana());
        employee.setBirthday(dto.getBirthday());
        
        return employee;
    }
}