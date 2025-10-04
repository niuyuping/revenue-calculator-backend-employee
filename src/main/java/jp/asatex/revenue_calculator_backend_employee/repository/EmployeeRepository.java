package jp.asatex.revenue_calculator_backend_employee.repository;

import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * EmployeeリアクティブRepositoryインターフェース
 * 従業員データのCRUD操作を提供
 */
@Repository
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {
    
    /**
     * 従業員番号による従業員の検索
     * @param employeeNumber 従業員番号
     * @return Mono<Employee>
     */
    @Query("SELECT * FROM employees WHERE employee_number = :employeeNumber")
    Mono<Employee> findByEmployeeNumber(String employeeNumber);
    
    /**
     * 姓名による従業員の曖昧検索
     * @param name 姓名キーワード
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE name LIKE :name")
    Flux<Employee> findByNameContaining(String name);
    
    /**
     * ふりがなによる従業員の曖昧検索
     * @param furigana ふりがなキーワード
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE furigana LIKE :furigana")
    Flux<Employee> findByFuriganaContaining(String furigana);
    
    /**
     * 従業員番号の存在チェック
     * @param employeeNumber 従業員番号
     * @return Mono<Boolean>
     */
    @Query("SELECT COUNT(*) > 0 FROM employees WHERE employee_number = :employeeNumber")
    Mono<Boolean> existsByEmployeeNumber(String employeeNumber);
    
    /**
     * 従業員番号による従業員の削除
     * @param employeeNumber 従業員番号
     * @return Mono<Void>
     */
    @Query("DELETE FROM employees WHERE employee_number = :employeeNumber")
    Mono<Void> deleteByEmployeeNumber(String employeeNumber);
}
