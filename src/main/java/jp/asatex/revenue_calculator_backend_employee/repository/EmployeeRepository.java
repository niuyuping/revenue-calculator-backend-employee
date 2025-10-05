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
    
    /**
     * 分页查询所有员工（排除已删除的）- 按员工ID升序
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY employee_id ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByIdAsc(int offset, int limit);
    
    /**
     * 分页查询所有员工（排除已删除的）- 按员工ID降序
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY employee_id DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByIdDesc(int offset, int limit);
    
    /**
     * 分页查询所有员工（排除已删除的）- 按姓名升序
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY name ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByNameAsc(int offset, int limit);
    
    /**
     * 分页查询所有员工（排除已删除的）- 按姓名降序
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY name DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByNameDesc(int offset, int limit);
    
    /**
     * 分页查询所有员工（排除已删除的）- 按员工编号升序
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY employee_number ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByNumberAsc(int offset, int limit);
    
    /**
     * 分页查询所有员工（排除已删除的）- 按员工编号降序
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE is_deleted = false ORDER BY employee_number DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findAllWithPaginationByNumberDesc(int offset, int limit);
    
    /**
     * 统计所有员工总数（排除已删除的）
     * @return Mono<Long>
     */
    @Query("SELECT COUNT(*) FROM employees WHERE is_deleted = false")
    Mono<Long> countAllActive();
    
    /**
     * 分页查询按姓名搜索的员工 - 按员工ID升序
     * @param name 姓名关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE name LIKE :name AND is_deleted = false ORDER BY employee_id ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByNameContainingWithPaginationByIdAsc(String name, int offset, int limit);
    
    /**
     * 分页查询按姓名搜索的员工 - 按员工ID降序
     * @param name 姓名关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE name LIKE :name AND is_deleted = false ORDER BY employee_id DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByNameContainingWithPaginationByIdDesc(String name, int offset, int limit);
    
    /**
     * 分页查询按姓名搜索的员工 - 按姓名升序
     * @param name 姓名关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE name LIKE :name AND is_deleted = false ORDER BY name ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByNameContainingWithPaginationByNameAsc(String name, int offset, int limit);
    
    /**
     * 分页查询按姓名搜索的员工 - 按姓名降序
     * @param name 姓名关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE name LIKE :name AND is_deleted = false ORDER BY name DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByNameContainingWithPaginationByNameDesc(String name, int offset, int limit);
    
    /**
     * 统计按姓名搜索的员工总数
     * @param name 姓名关键词
     * @return Mono<Long>
     */
    @Query("SELECT COUNT(*) FROM employees WHERE name LIKE :name AND is_deleted = false")
    Mono<Long> countByNameContaining(String name);
    
    /**
     * 分页查询按ふりがな搜索的员工 - 按员工ID升序
     * @param furigana ふりがな关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE furigana LIKE :furigana AND is_deleted = false ORDER BY employee_id ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByFuriganaContainingWithPaginationByIdAsc(String furigana, int offset, int limit);
    
    /**
     * 分页查询按ふりがな搜索的员工 - 按员工ID降序
     * @param furigana ふりがな关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE furigana LIKE :furigana AND is_deleted = false ORDER BY employee_id DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByFuriganaContainingWithPaginationByIdDesc(String furigana, int offset, int limit);
    
    /**
     * 分页查询按ふりがな搜索的员工 - 按姓名升序
     * @param furigana ふりがな关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE furigana LIKE :furigana AND is_deleted = false ORDER BY name ASC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByFuriganaContainingWithPaginationByNameAsc(String furigana, int offset, int limit);
    
    /**
     * 分页查询按ふりがな搜索的员工 - 按姓名降序
     * @param furigana ふりがな关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Flux<Employee>
     */
    @Query("SELECT * FROM employees WHERE furigana LIKE :furigana AND is_deleted = false ORDER BY name DESC LIMIT :limit OFFSET :offset")
    Flux<Employee> findByFuriganaContainingWithPaginationByNameDesc(String furigana, int offset, int limit);
    
    /**
     * 统计按ふりがな搜索的员工总数
     * @param furigana ふりがな关键词
     * @return Mono<Long>
     */
    @Query("SELECT COUNT(*) FROM employees WHERE furigana LIKE :furigana AND is_deleted = false")
    Mono<Long> countByFuriganaContaining(String furigana);
}
