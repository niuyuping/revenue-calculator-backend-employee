package jp.asatex.revenue_calculator_backend_employee.service;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<EmployeeDto> getEmployeeById(Long id) {
        employeeQueryCounter.increment();
        return employeeRepository.findById(id)
                .map(this::convertToDto);
    }
    
    /**
     * 従業員番号による従業員の取得
     * @param employeeNumber 従業員番号
     * @return Mono<EmployeeDto>
     */
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
    public Mono<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
        logger.info("Creating new employee with number: {}", employeeDto.getEmployeeNumber());
        LoggingUtil.logBusinessOperation("CREATE_EMPLOYEE", "Employee number: %s", employeeDto.getEmployeeNumber());
        employeeOperationCounter.increment();
        employeeCreateCounter.increment();
        
        // 従業員番号が既に存在するかチェック
        return employeeRepository.existsByEmployeeNumber(employeeDto.getEmployeeNumber())
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
                })
                .doOnError(error -> {
                    logger.error("Failed to create employee: {}", employeeDto.getEmployeeNumber(), error);
                    LoggingUtil.logError("CREATE_EMPLOYEE", error, "Employee number: " + employeeDto.getEmployeeNumber());
                });
    }
    
    /**
     * 従業員情報の更新
     * @param id 従業員ID
     * @param employeeDto 従業員情報
     * @return Mono<EmployeeDto>
     */
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
}