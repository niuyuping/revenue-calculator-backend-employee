package jp.asatex.revenue_calculator_backend_employee.application;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.common.PageRequest;
import jp.asatex.revenue_calculator_backend_employee.common.PageResponse;
import jp.asatex.revenue_calculator_backend_employee.service.EmployeeService;
import jp.asatex.revenue_calculator_backend_employee.exception.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Employee Application Service
 * Application layer that orchestrates business use cases
 * Coordinates between presentation layer and domain services
 */
@Service
@Transactional
public class EmployeeApplicationService {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Create employee use case
     * Orchestrates the employee creation process
     * 
     * @param employeeDto Employee data
     * @return Created employee
     */
    public Mono<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
        // Application layer can add:
        // - Authorization checks
        // - Event publishing
        // - Audit logging
        // - Business rule validation
        
        return employeeService.createEmployee(employeeDto)
                .doOnSuccess(createdEmployee -> {
                    // Publish domain event if needed
                    // auditService.logEmployeeCreation(createdEmployee);
                })
                .onErrorMap(throwable -> {
                    // Don't wrap business exceptions, only wrap unexpected technical exceptions
                    if (!(throwable instanceof TransactionHandler) && 
                        !(throwable instanceof jp.asatex.revenue_calculator_backend_employee.exception.DuplicateEmployeeNumberHandler) &&
                        !(throwable instanceof jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundHandler)) {
                        return new TransactionHandler("Failed to create employee: " + employeeDto.getEmployeeNumber(), throwable);
                    }
                    return throwable;
                });
    }

    /**
     * Get employee by ID use case
     * 
     * @param id Employee ID
     * @return Employee information
     */
    @Transactional(readOnly = true)
    public Mono<EmployeeDto> getEmployeeById(Long id) {
        return employeeService.getEmployeeById(id);
    }

    /**
     * Get employee by number use case
     * 
     * @param employeeNumber Employee number
     * @return Employee information
     */
    @Transactional(readOnly = true)
    public Mono<EmployeeDto> getEmployeeByNumber(String employeeNumber) {
        return employeeService.getEmployeeByNumber(employeeNumber);
    }

    /**
     * Update employee use case
     * Orchestrates the employee update process
     * 
     * @param id Employee ID
     * @param employeeDto Updated employee data
     * @return Updated employee
     */
    public Mono<EmployeeDto> updateEmployee(Long id, EmployeeDto employeeDto) {
        return employeeService.updateEmployee(id, employeeDto)
                .doOnSuccess(updatedEmployee -> {
                    // Publish domain event if needed
                    // auditService.logEmployeeUpdate(updatedEmployee);
                })
                .onErrorMap(throwable -> {
                    // Don't wrap business exceptions, only wrap unexpected technical exceptions
                    if (!(throwable instanceof TransactionHandler) && 
                        !(throwable instanceof jp.asatex.revenue_calculator_backend_employee.exception.DuplicateEmployeeNumberHandler) &&
                        !(throwable instanceof jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundHandler)) {
                        return new TransactionHandler("Failed to update employee with ID: " + id, throwable);
                    }
                    return throwable;
                });
    }

    /**
     * Delete employee use case
     * Orchestrates the employee deletion process
     * 
     * @param id Employee ID
     */
    public Mono<Void> deleteEmployee(Long id) {
        return employeeService.deleteEmployeeById(id)
                .doOnSuccess(result -> {
                    // Publish domain event if needed
                    // auditService.logEmployeeDeletion(id);
                })
                .onErrorMap(throwable -> {
                    // Don't wrap business exceptions, only wrap unexpected technical exceptions
                    if (!(throwable instanceof TransactionHandler) && 
                        !(throwable instanceof jp.asatex.revenue_calculator_backend_employee.exception.DuplicateEmployeeNumberHandler) &&
                        !(throwable instanceof jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundHandler)) {
                        return new TransactionHandler("Failed to delete employee with ID: " + id, throwable);
                    }
                    return throwable;
                });
    }

    /**
     * Search employees by name use case
     * 
     * @param name Search keyword
     * @return List of matching employees
     */
    @Transactional(readOnly = true)
    public Flux<EmployeeDto> searchEmployeesByName(String name) {
        return employeeService.searchEmployeesByName(name);
    }

    /**
     * Search employees by furigana use case
     * 
     * @param furigana Search keyword
     * @return List of matching employees
     */
    @Transactional(readOnly = true)
    public Flux<EmployeeDto> searchEmployeesByFurigana(String furigana) {
        return employeeService.searchEmployeesByFurigana(furigana);
    }

    /**
     * Get employees with pagination use case
     * 
     * @param pageRequest Pagination parameters
     * @return Paginated employee list
     */
    @Transactional(readOnly = true)
    public Mono<PageResponse<EmployeeDto>> getEmployeesWithPagination(PageRequest pageRequest) {
        return employeeService.getEmployeesWithPagination(pageRequest);
    }

    /**
     * Delete employee by number use case
     * 
     * @param employeeNumber Employee number
     */
    public Mono<Void> deleteEmployeeByNumber(String employeeNumber) {
        return employeeService.deleteEmployeeByNumber(employeeNumber)
                .doOnSuccess(result -> {
                    // Publish domain event if needed
                    // auditService.logEmployeeDeletionByNumber(employeeNumber);
                })
                .onErrorMap(throwable -> {
                    // Don't wrap business exceptions, only wrap unexpected technical exceptions
                    if (!(throwable instanceof TransactionHandler) && 
                        !(throwable instanceof jp.asatex.revenue_calculator_backend_employee.exception.DuplicateEmployeeNumberHandler) &&
                        !(throwable instanceof jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundHandler)) {
                        return new TransactionHandler("Failed to delete employee with number: " + employeeNumber, throwable);
                    }
                    return throwable;
                });
    }
}
