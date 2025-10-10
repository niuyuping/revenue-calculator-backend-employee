package jp.asatex.revenue_calculator_backend_employee.service;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.dto.PageRequest;
import jp.asatex.revenue_calculator_backend_employee.dto.PageResponse;
import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import jp.asatex.revenue_calculator_backend_employee.exception.DuplicateEmployeeNumberException;
import jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundException;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
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
    private Counter employeeOperationCounter;
    
    @Autowired
    private Counter employeeQueryCounter;
    
    @Autowired
    private Counter employeeCreateCounter;
    
    @Autowired
    private Counter employeeUpdateCounter;
    
    @Autowired
    private Counter employeeDeleteCounter;
    
    @Autowired
    private Timer employeeOperationTimer;
    
    /**
     * Get all employees
     * @return Flux<EmployeeDto>
     */
    public Flux<EmployeeDto> getAllEmployees() {
        logger.debug("Retrieving all employees");
        employeeQueryCounter.increment();
        
        return employeeRepository.findAll()
                .map(this::convertToDto)
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
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("Employee not found with ID: " + id)));
    }
    
    /**
     * Get employee by employee number
     * @param employeeNumber Employee number
     * @return Mono<EmployeeDto>
     */
    @Cacheable(value = "employees", key = "#employeeNumber")
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
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("Employee not found with number: " + employeeNumber)));
    }
    
    /**
     * Create new employee
     * @param employeeDto Employee data
     * @return Mono<EmployeeDto>
     */
    @Transactional
    @CacheEvict(value = {"employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
    public Mono<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
        logger.info("Creating new employee with number: {}", employeeDto.getEmployeeNumber());
        employeeOperationCounter.increment();
        employeeCreateCounter.increment();
        
        long startTime = System.currentTimeMillis();
        
        return employeeRepository.existsByEmployeeNumber(employeeDto.getEmployeeNumber())
                .flatMap(exists -> {
                    if (exists) {
                        logger.warn("Duplicate employee number detected: {}", employeeDto.getEmployeeNumber());
                        return Mono.error(new DuplicateEmployeeNumberException("Employee number already exists: " + employeeDto.getEmployeeNumber()));
                    }
                    return Mono.just(convertToEntity(employeeDto));
                })
                .flatMap(employeeRepository::save)
                .map(this::convertToDto)
                .doOnSuccess(createdEmployee -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.info("Successfully created employee: {} with ID: {}", 
                            createdEmployee.getEmployeeNumber(), createdEmployee.getEmployeeId());
                })
                .doOnError(error -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.error("Failed to create employee: {}", employeeDto.getEmployeeNumber(), error);
                });
    }
    
    /**
     * Update employee
     * @param id Employee ID
     * @param employeeDto Updated employee data
     * @return Mono<EmployeeDto>
     */
    @Transactional
    @CacheEvict(value = {"employees", "employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
    public Mono<EmployeeDto> updateEmployee(Long id, EmployeeDto employeeDto) {
        logger.info("Updating employee with ID: {}", id);
        employeeOperationCounter.increment();
        employeeUpdateCounter.increment();
        
        long startTime = System.currentTimeMillis();
        
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("Employee not found with ID: " + id)))
                .flatMap(existingEmployee -> {
                    Employee updatedEmployee = convertToEntity(employeeDto);
                    updatedEmployee.setEmployeeId(id);
                    return employeeRepository.save(updatedEmployee);
                })
                .map(this::convertToDto)
                .doOnSuccess(updatedEmployee -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.info("Successfully updated employee: {} with ID: {}", 
                            updatedEmployee.getEmployeeNumber(), updatedEmployee.getEmployeeId());
                })
                .doOnError(error -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.error("Failed to update employee with ID: {}", id, error);
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
        employeeOperationCounter.increment();
        employeeDeleteCounter.increment();
        
        long startTime = System.currentTimeMillis();
        
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("Employee not found with ID: " + id)))
                .flatMap(employeeRepository::delete)
                .doOnSuccess(unused -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.info("Successfully deleted employee with ID: {}", id);
                })
                .doOnError(error -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.error("Failed to delete employee with ID: {}", id, error);
                });
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
        employeeOperationCounter.increment();
        employeeDeleteCounter.increment();
        
        long startTime = System.currentTimeMillis();
        
        return employeeRepository.findByEmployeeNumber(employeeNumber)
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("Employee not found with number: " + employeeNumber)))
                .flatMap(employeeRepository::delete)
                .doOnSuccess(unused -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.info("Successfully deleted employee with number: {}", employeeNumber);
                })
                .doOnError(error -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.error("Failed to delete employee with number: {}", employeeNumber, error);
                });
    }
    
    /**
     * Search employees by name
     * @param name Employee name
     * @return Flux<EmployeeDto>
     */
    @Cacheable(value = "employeeSearch", key = "#name")
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
    @Cacheable(value = "employeeSearch", key = "#furigana")
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
    @Cacheable(value = "employeePagination", key = "#pageRequest.page + '_' + #pageRequest.size + '_' + #pageRequest.sortBy + '_' + #pageRequest.sortDirection")
    public Mono<PageResponse<EmployeeDto>> getEmployeesWithPagination(PageRequest pageRequest) {
        logger.debug("Retrieving employees with pagination: page={}, size={}", pageRequest.getPage(), pageRequest.getSize());
        employeeQueryCounter.increment();
        
        return employeeRepository.findAll()
                .map(this::convertToDto)
                .collectList()
                .map(employees -> {
                    int totalElements = employees.size();
                    int totalPages = (int) Math.ceil((double) totalElements / pageRequest.getSize());
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