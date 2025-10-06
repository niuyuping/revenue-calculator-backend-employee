package jp.asatex.revenue_calculator_backend_employee.repository;

import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Employee reactive repository interface
 * Provides CRUD operations for employee data
 */
@Repository
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {
    
    /**
     * Find employee by employee number
     * @param employeeNumber Employee number
     * @return Mono<Employee>
     */
    @Query("SELECT * FROM employees WHERE employee_number = :employeeNumber")
    Mono<Employee> findByEmployeeNumber(String employeeNumber);
    
    /**
     * Find employees by name containing keyword
     * @param name Name keyword
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE name LIKE :name")
    Flux<Employee> findByNameContaining(String name);
    
    /**
     * Find employees by furigana containing keyword
     * @param furigana Furigana keyword
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE furigana LIKE :furigana")
    Flux<Employee> findByFuriganaContaining(String furigana);
    
    /**
     * Check if employee number exists
     * @param employeeNumber Employee number
     * @return Mono<Boolean>
     */
    @Query("SELECT COUNT(*) > 0 FROM employees WHERE employee_number = :employeeNumber")
    Mono<Boolean> existsByEmployeeNumber(String employeeNumber);
    
    /**
     * Delete employee by employee number
     * @param employeeNumber Employee number
     * @return Mono<Void>
     */
    @Query("DELETE FROM employees WHERE employee_number = :employeeNumber")
    Mono<Void> deleteByEmployeeNumber(String employeeNumber);
    
    /**
     * Paginated query for all employees (excluding deleted) - ordered by employee ID ascending
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY employee_id ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByIdAsc(int offset, int limit);
    
    /**
     * Paginated query for all employees (excluding deleted) - ordered by employee ID descending
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY employee_id DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByIdDesc(int offset, int limit);
    
    /**
     * Paginated query for all employees (excluding deleted) - ordered by name ascending
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY name ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByNameAsc(int offset, int limit);
    
    /**
     * Paginated query for all employees (excluding deleted) - ordered by name descending
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY name DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByNameDesc(int offset, int limit);
    
    /**
     * Paginated query for all employees (excluding deleted) - ordered by employee number ascending
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY employee_number ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByNumberAsc(int offset, int limit);
    
    /**
     * Paginated query for all employees (excluding deleted) - ordered by employee number descending
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY employee_number DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByNumberDesc(int offset, int limit);
    
    /**
     * Count total number of all employees (excluding deleted)
     * @return Mono<Long>
     */
    @Query("SELECT COUNT(*) FROM employees WHERE is_deleted = false")
    Mono<Long> countAllActive();
    
    /**
     * Paginated query for employees searched by name - ordered by employee ID ascending
     * @param name Name keyword
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE name LIKE :name AND is_deleted = false ORDER BY employee_id ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByNameContainingWithPaginationByIdAsc(String name, int offset, int limit);
    
    /**
     * Paginated query for employees searched by name - ordered by employee ID descending
     * @param name Name keyword
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE name LIKE :name AND is_deleted = false ORDER BY employee_id DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByNameContainingWithPaginationByIdDesc(String name, int offset, int limit);
    
    /**
     * Paginated query for employees searched by name - ordered by name ascending
     * @param name Name keyword
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE name LIKE :name AND is_deleted = false ORDER BY name ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByNameContainingWithPaginationByNameAsc(String name, int offset, int limit);
    
    /**
     * Paginated query for employees searched by name - ordered by name descending
     * @param name Name keyword
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE name LIKE :name AND is_deleted = false ORDER BY name DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByNameContainingWithPaginationByNameDesc(String name, int offset, int limit);
    
    /**
     * Count total number of employees searched by name
     * @param name Name keyword
     * @return Mono<Long>
     */
    @Query("SELECT COUNT(*) FROM employees WHERE name LIKE :name AND is_deleted = false")
    Mono<Long> countByNameContaining(String name);
    
    /**
     * Paginated query for employees searched by furigana - ordered by employee ID ascending
     * @param furigana Furigana keyword
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE furigana LIKE :furigana AND is_deleted = false ORDER BY employee_id ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByFuriganaContainingWithPaginationByIdAsc(String furigana, int offset, int limit);
    
    /**
     * Paginated query for employees searched by furigana - ordered by employee ID descending
     * @param furigana Furigana keyword
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE furigana LIKE :furigana AND is_deleted = false ORDER BY employee_id DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByFuriganaContainingWithPaginationByIdDesc(String furigana, int offset, int limit);
    
    /**
     * Paginated query for employees searched by furigana - ordered by name ascending
     * @param furigana Furigana keyword
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE furigana LIKE :furigana AND is_deleted = false ORDER BY name ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByFuriganaContainingWithPaginationByNameAsc(String furigana, int offset, int limit);
    
    /**
     * Paginated query for employees searched by furigana - ordered by name descending
     * @param furigana Furigana keyword
     * @param offset Offset
     * @param limit Limit
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE furigana LIKE :furigana AND is_deleted = false ORDER BY name DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByFuriganaContainingWithPaginationByNameDesc(String furigana, int offset, int limit);
    
    /**
     * Count total number of employees searched by furigana
     * @param furigana Furigana keyword
     * @return Mono<Long>
     */
    @Query("SELECT COUNT(*) FROM employees WHERE furigana LIKE :furigana AND is_deleted = false")
    Mono<Long> countByFuriganaContaining(String furigana);
}
