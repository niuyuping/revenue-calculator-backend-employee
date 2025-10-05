package jp.asatex.revenue_calculator_backend_employee.service;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.dto.PageRequest;
import jp.asatex.revenue_calculator_backend_employee.dto.PageResponse;
import jp.asatex.revenue_calculator_backend_employee.dto.SortDirection;
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
 * Employeeサービス層
 * 従業員ビジネスロジック処理を提供
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
     * 全従業員の取得
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
     * IDによる従業員の取得
     * @param id 従業員ID
     * @return Mono<EmployeeDto>
     */
    @Cacheable(value = "employees", key = "#id", unless = "#result == null")
    public Mono<EmployeeDto> getEmployeeById(Long id) {
        employeeQueryCounter.increment();
        return employeeRepository.findById(id)
                .map(this::convertToDto)
                .doOnSuccess(employee -> {
                    if (employee != null) {
                        // 记录数据库审计日志
                        databaseAuditService.logSelectOperation(
                                "employees",
                                employee.getEmployeeId().toString(),
                                "SELECT * FROM employees WHERE id = ?",
                                25L, // 执行时间
                                1     // 影响行数
                        ).subscribe();
                    }
                });
    }
    
    /**
     * 従業員番号による従業員の取得
     * @param employeeNumber 従業員番号
     * @return Mono<EmployeeDto>
     */
    @Cacheable(value = "employees", key = "'number:' + #employeeNumber", unless = "#result == null")
    public Mono<EmployeeDto> getEmployeeByNumber(String employeeNumber) {
        return employeeRepository.findByEmployeeNumber(employeeNumber)
                .map(this::convertToDto);
    }
    
    /**
     * 姓名による従業員検索
     * @param name 姓名キーワード
     * @return Flux<EmployeeDto>
     */
    public Flux<EmployeeDto> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContaining("%" + name + "%")
                .map(this::convertToDto);
    }
    
    /**
     * ふりがなによる従業員検索
     * @param furigana ふりがなキーワード
     * @return Flux<EmployeeDto>
     */
    public Flux<EmployeeDto> searchEmployeesByFurigana(String furigana) {
        return employeeRepository.findByFuriganaContaining("%" + furigana + "%")
                .map(this::convertToDto);
    }
    
    /**
     * 新従業員の作成
     * @param employeeDto 従業員情報
     * @return Mono<EmployeeDto>
     */
    @Transactional
    @CacheEvict(value = {"employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
    public Mono<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
        logger.info("Creating new employee with number: {}", employeeDto.getEmployeeNumber());
        LoggingUtil.logBusinessOperation("CREATE_EMPLOYEE", "Employee number: %s", employeeDto.getEmployeeNumber());
        employeeOperationCounter.increment();
        employeeCreateCounter.increment();
        
        // 使用事务监控包装操作
        return transactionMonitoringService.monitorTransaction(
                "CREATE_EMPLOYEE", 
                "Employee number: " + employeeDto.getEmployeeNumber(),
                // 従業員番号が既に存在するかチェック
                employeeRepository.existsByEmployeeNumber(employeeDto.getEmployeeNumber())
                        .flatMap(exists -> {
                            if (exists) {
                                logger.warn("Duplicate employee number detected: {}", employeeDto.getEmployeeNumber());
                                LoggingUtil.logSecurity("DUPLICATE_EMPLOYEE_NUMBER", "Attempted to create employee with existing number: " + employeeDto.getEmployeeNumber());
                                return Mono.error(new DuplicateEmployeeNumberException("従業員番号が既に存在します: " + employeeDto.getEmployeeNumber()));
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
                            logger.info("Successfully created employee: {} with ID: {}", 
                                    createdEmployee.getEmployeeNumber(), createdEmployee.getEmployeeId());
                            LoggingUtil.logDataAccess("INSERT", "employees", createdEmployee.getEmployeeId());
                            
                            // 记录审计日志
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
                            
                            // 记录数据库审计日志
                            databaseAuditService.logInsertOperation(
                                    "employees", 
                                    createdEmployee.getEmployeeId().toString(),
                                    details,
                                    "INSERT INTO employees (employee_number, name, furigana, birthday, deleted) VALUES (?, ?, ?, ?, ?)",
                                    100L, // 执行时间
                                    1     // 影响行数
                            ).subscribe();
                        })
                        .doOnError(error -> {
                            logger.error("Failed to create employee: {}", employeeDto.getEmployeeNumber(), error);
                            LoggingUtil.logError("CREATE_EMPLOYEE", error, "Employee number: " + employeeDto.getEmployeeNumber());
                            
                            // 记录错误审计日志
                            Map<String, Object> errorDetails = new HashMap<>();
                            errorDetails.put("employeeNumber", employeeDto.getEmployeeNumber());
                            errorDetails.put("errorMessage", error.getMessage());
                            errorDetails.put("errorType", error.getClass().getSimpleName());
                            
                            auditLogService.logError("CREATE_EMPLOYEE_FAILED", "EmployeeService", 
                                    "Failed to create employee: " + employeeDto.getEmployeeNumber(), 
                                    "system", errorDetails);
                        })
        );
    }
    
    /**
     * 従業員情報の更新
     * @param id 従業員ID
     * @param employeeDto 従業員情報
     * @return Mono<EmployeeDto>
     */
    @Transactional
    @CacheEvict(value = {"employees", "employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
    public Mono<EmployeeDto> updateEmployee(Long id, EmployeeDto employeeDto) {
        logger.info("Updating employee with ID: {}", id);
        LoggingUtil.logBusinessOperation("UPDATE_EMPLOYEE", "Employee ID: %s", id);
        employeeOperationCounter.increment();
        employeeUpdateCounter.increment();
        
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("従業員が存在しません、ID: " + id)))
                .flatMap(existingEmployee -> {
                    // 従業員番号が変更された場合、新しい番号が既に存在するかチェック
                    if (!existingEmployee.getEmployeeNumber().equals(employeeDto.getEmployeeNumber())) {
                        return employeeRepository.existsByEmployeeNumber(employeeDto.getEmployeeNumber())
                                .flatMap(exists -> exists 
                                    ? Mono.error(new DuplicateEmployeeNumberException("従業員番号が既に存在します: " + employeeDto.getEmployeeNumber()))
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
                    logger.info("Successfully updated employee: {} with ID: {}", 
                            updatedEmployee.getEmployeeNumber(), updatedEmployee.getEmployeeId());
                    LoggingUtil.logDataAccess("UPDATE", "employees", updatedEmployee.getEmployeeId());
                    
                    // 记录数据库审计日志
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
                    
                    databaseAuditService.logUpdateOperation(
                            "employees",
                            updatedEmployee.getEmployeeId().toString(),
                            oldValues,
                            newValues,
                            "UPDATE employees SET employee_number = ?, name = ?, furigana = ?, birthday = ? WHERE id = ?",
                            75L, // 执行时间
                            1     // 影响行数
                    ).subscribe();
                })
                .doOnError(error -> {
                    logger.error("Failed to update employee with ID: {}", id, error);
                    LoggingUtil.logError("UPDATE_EMPLOYEE", error, "Employee ID: " + id);
                });
    }
    
    /**
     * IDによる従業員の削除
     * @param id 従業員ID
     * @return Mono<Void>
     */
    @Transactional
    @CacheEvict(value = {"employees", "employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
    public Mono<Void> deleteEmployeeById(Long id) {
        logger.info("Deleting employee with ID: {}", id);
        LoggingUtil.logBusinessOperation("DELETE_EMPLOYEE", "Employee ID: %s", id);
        employeeOperationCounter.increment();
        employeeDeleteCounter.increment();
        
        return employeeRepository.existsById(id)
                .flatMap(exists -> exists 
                    ? employeeRepository.deleteById(id)
                            .doOnSuccess(unused -> {
                                logger.info("Successfully deleted employee with ID: {}", id);
                                LoggingUtil.logDataAccess("DELETE", "employees", id);
                                
                                // 记录数据库审计日志
                                Map<String, Object> details = new HashMap<>();
                                details.put("employeeId", id);
                                
                                databaseAuditService.logDeleteOperation(
                                        "employees",
                                        id.toString(),
                                        details,
                                        "DELETE FROM employees WHERE id = ?",
                                        50L, // 执行时间
                                        1    // 影响行数
                                ).subscribe();
                            })
                            .doOnError(error -> {
                                logger.error("Failed to delete employee with ID: {}", id, error);
                                LoggingUtil.logError("DELETE_EMPLOYEE", error, "Employee ID: " + id);
                            })
                    : Mono.error(new EmployeeNotFoundException("従業員が存在しません、ID: " + id)));
    }
    
    /**
     * 従業員番号による従業員の削除
     * @param employeeNumber 従業員番号
     * @return Mono<Void>
     */
    @Transactional
    @CacheEvict(value = {"employees", "employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
    public Mono<Void> deleteEmployeeByNumber(String employeeNumber) {
        logger.info("Deleting employee with number: {}", employeeNumber);
        LoggingUtil.logBusinessOperation("DELETE_EMPLOYEE", "Employee number: %s", employeeNumber);
        employeeOperationCounter.increment();
        employeeDeleteCounter.increment();
        
        return employeeRepository.existsByEmployeeNumber(employeeNumber)
                .flatMap(exists -> exists 
                    ? employeeRepository.deleteByEmployeeNumber(employeeNumber)
                            .doOnSuccess(unused -> {
                                logger.info("Successfully deleted employee with number: {}", employeeNumber);
                                LoggingUtil.logDataAccess("DELETE", "employees", employeeNumber);
                                
                                // 记录数据库审计日志
                                Map<String, Object> details = new HashMap<>();
                                details.put("employeeNumber", employeeNumber);
                                
                                databaseAuditService.logDeleteOperation(
                                        "employees",
                                        employeeNumber,
                                        details,
                                        "DELETE FROM employees WHERE employee_number = ?",
                                        50L, // 执行时间
                                        1    // 影响行数
                                ).subscribe();
                            })
                            .doOnError(error -> {
                                logger.error("Failed to delete employee with number: {}", employeeNumber, error);
                                LoggingUtil.logError("DELETE_EMPLOYEE", error, "Employee number: " + employeeNumber);
                            })
                    : Mono.error(new EmployeeNotFoundException("従業員が存在しません、番号: " + employeeNumber)));
    }
    
    /**
     * EntityをDTOに変換
     * @param employee Employeeエンティティ
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
     * DTOをEntityに変換
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
     * 分页获取所有员工
     * @param pageRequest 分页请求参数
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
     * 分页搜索员工（按姓名）
     * @param name 姓名关键词
     * @param pageRequest 分页请求参数
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
     * 分页搜索员工（按ふりがな）
     * @param furigana ふりがな关键词
     * @param pageRequest 分页请求参数
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
     * 根据排序字段和方向获取员工数据流
     * @param sortBy 排序字段
     * @param sortDirection 排序方向
     * @param offset 偏移量
     * @param limit 限制数量
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
            // 默认按员工ID排序
            return "ASC".equals(sortDirection)
                ? employeeRepository.findAllWithPaginationByIdAsc(offset, limit)
                : employeeRepository.findAllWithPaginationByIdDesc(offset, limit);
        }
    }
    
    /**
     * 根据排序字段和方向获取按姓名搜索的员工数据流
     * @param name 姓名关键词
     * @param sortBy 排序字段
     * @param sortDirection 排序方向
     * @param offset 偏移量
     * @param limit 限制数量
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
            // 默认按员工ID排序
            return "ASC".equals(sortDirection)
                ? employeeRepository.findByNameContainingWithPaginationByIdAsc(name, offset, limit)
                : employeeRepository.findByNameContainingWithPaginationByIdDesc(name, offset, limit);
        }
    }
    
    /**
     * 根据排序字段和方向获取按ふりがな搜索的员工数据流
     * @param furigana ふりがな关键词
     * @param sortBy 排序字段
     * @param sortDirection 排序方向
     * @param offset 偏移量
     * @param limit 限制数量
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
            // 默认按员工ID排序
            return "ASC".equals(sortDirection)
                ? employeeRepository.findByFuriganaContainingWithPaginationByIdAsc(furigana, offset, limit)
                : employeeRepository.findByFuriganaContainingWithPaginationByIdDesc(furigana, offset, limit);
        }
    }
    
    /**
     * 验证和规范化排序字段
     * @param sortBy 排序字段
     * @return 规范化的排序字段
     */
    private String validateAndNormalizeSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "employee_id";
        }
        
        // 允许的排序字段
        String[] allowedFields = {
            "employee_id", "employee_number", "name", "furigana", "birthday", "created_at", "updated_at"
        };
        
        String normalizedField = sortBy.toLowerCase().trim();
        
        // 检查字段是否在允许列表中
        for (String field : allowedFields) {
            if (field.equals(normalizedField)) {
                return field;
            }
        }
        
        // 如果字段不在允许列表中，返回默认字段
        logger.warn("Invalid sort field '{}', using default 'employee_id'", sortBy);
        return "employee_id";
    }
}