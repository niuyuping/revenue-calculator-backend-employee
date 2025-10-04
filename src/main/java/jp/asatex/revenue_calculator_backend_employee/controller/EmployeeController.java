package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

/**
 * Employeeコントローラー
 * 従業員管理のREST APIエンドポイントを提供
 */
@RestController
@RequestMapping("/api/v1/employee")
@CrossOrigin(origins = "*")
@Tag(name = "Employee Management", description = "従業員管理API")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    /**
     * 全従業員の取得
     * GET /api/v1/employee
     * @return Flux<EmployeeDto>
     */
    @Operation(summary = "全従業員の取得", description = "システムに登録されている全ての従業員情報を取得します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    @GetMapping
    @RateLimiter(name = "employee-api")
    public Flux<EmployeeDto> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
    
    /**
     * IDによる従業員の取得
     * GET /api/v1/employee/{id}
     * @param id 従業員ID
     * @return Mono<ResponseEntity<EmployeeDto>>
     */
    @Operation(summary = "IDによる従業員の取得", description = "指定されたIDの従業員情報を取得します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "404", description = "従業員が見つかりません"),
            @ApiResponse(responseCode = "400", description = "無効なID"),
            @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EmployeeDto>> getEmployeeById(
            @Parameter(description = "従業員ID", required = true, example = "1")
            @PathVariable @NotNull @Positive(message = "従業員IDは正数である必要があります") Long id) {
        return employeeService.getEmployeeById(id)
                .map(employee -> ResponseEntity.ok(employee))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * 従業員番号による従業員の取得
     * GET /api/v1/employee/number/{employeeNumber}
     * @param employeeNumber 従業員番号
     * @return Mono<ResponseEntity<EmployeeDto>>
     */
    @Operation(summary = "従業員番号による従業員の取得", description = "指定された従業員番号の従業員情報を取得します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "404", description = "従業員が見つかりません"),
            @ApiResponse(responseCode = "400", description = "無効な従業員番号"),
            @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    @GetMapping("/number/{employeeNumber}")
    public Mono<ResponseEntity<EmployeeDto>> getEmployeeByNumber(
            @Parameter(description = "従業員番号", required = true, example = "EMP001")
            @PathVariable @NotBlank(message = "従業員番号は空にできません") @Size(min = 1, max = 20, message = "従業員番号の長さは1-20文字の間である必要があります") @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "従業員番号は英字、数字、アンダースコア、ハイフンのみを含むことができます") String employeeNumber) {
        return employeeService.getEmployeeByNumber(employeeNumber)
                .map(employee -> ResponseEntity.ok(employee))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * 姓名による従業員検索
     * GET /api/v1/employee/search/name?q={name}
     * @param name 姓名キーワード
     * @return Flux<EmployeeDto>
     */
    @Operation(summary = "姓名による従業員検索", description = "姓名にキーワードを含む従業員を検索します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "無効な検索キーワード"),
            @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    @GetMapping("/search/name")
    @RateLimiter(name = "employee-search")
    public Flux<EmployeeDto> searchEmployeesByName(
            @Parameter(description = "検索キーワード", required = true, example = "田中")
            @RequestParam @NotBlank(message = "検索キーワードは空にできません") @Size(min = 1, max = 100, message = "検索キーワードの長さは1-100文字の間である必要があります") String name) {
        return employeeService.searchEmployeesByName(name);
    }
    
    /**
     * ふりがなによる従業員検索
     * GET /api/v1/employee/search/furigana?q={furigana}
     * @param furigana ふりがなキーワード
     * @return Flux<EmployeeDto>
     */
    @Operation(summary = "ふりがなによる従業員検索", description = "ふりがなにキーワードを含む従業員を検索します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "無効な検索キーワード"),
            @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    @GetMapping("/search/furigana")
    public Flux<EmployeeDto> searchEmployeesByFurigana(
            @Parameter(description = "ふりがな検索キーワード", required = true, example = "たなか")
            @RequestParam @NotBlank(message = "検索キーワードは空にできません") @Size(min = 1, max = 200, message = "検索キーワードの長さは1-200文字の間である必要があります") String furigana) {
        return employeeService.searchEmployeesByFurigana(furigana);
    }
    
    /**
     * 新従業員の作成
     * POST /api/v1/employee
     * @param employeeDto 従業員情報
     * @return Mono<ResponseEntity<EmployeeDto>>
     */
    @Operation(summary = "新従業員の作成", description = "新しい従業員をシステムに登録します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "従業員が正常に作成されました", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "無効なリクエストデータ"),
            @ApiResponse(responseCode = "409", description = "従業員番号が既に存在します"),
            @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    @PostMapping
    @RateLimiter(name = "employee-write")
    public Mono<ResponseEntity<EmployeeDto>> createEmployee(
            @Parameter(description = "従業員情報", required = true)
            @RequestBody @Valid EmployeeDto employeeDto) {
        return employeeService.createEmployee(employeeDto)
                .map(createdEmployee -> ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee));
    }
    
    /**
     * 従業員情報の更新
     * PUT /api/v1/employee/{id}
     * @param id 従業員ID
     * @param employeeDto 従業員情報
     * @return Mono<ResponseEntity<EmployeeDto>>
     */
    @Operation(summary = "従業員情報の更新", description = "指定されたIDの従業員情報を更新します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "従業員情報が正常に更新されました", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "無効なリクエストデータ"),
            @ApiResponse(responseCode = "404", description = "従業員が見つかりません"),
            @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EmployeeDto>> updateEmployee(
            @Parameter(description = "従業員ID", required = true, example = "1")
            @PathVariable @NotNull @Positive(message = "従業員IDは正数である必要があります") Long id, 
            @Parameter(description = "更新する従業員情報", required = true)
            @RequestBody @Valid EmployeeDto employeeDto) {
        return employeeService.updateEmployee(id, employeeDto)
                .map(updatedEmployee -> ResponseEntity.ok(updatedEmployee))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * IDによる従業員の削除
     * DELETE /api/v1/employee/{id}
     * @param id 従業員ID
     * @return Mono<ResponseEntity<Void>>
     */
    @Operation(summary = "IDによる従業員の削除", description = "指定されたIDの従業員を削除します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "従業員が正常に削除されました"),
            @ApiResponse(responseCode = "400", description = "無効なID"),
            @ApiResponse(responseCode = "404", description = "従業員が見つかりません"),
            @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEmployeeById(
            @Parameter(description = "従業員ID", required = true, example = "1")
            @PathVariable @NotNull @Positive(message = "従業員IDは正数である必要があります") Long id) {
        return employeeService.deleteEmployeeById(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
    
    /**
     * 従業員番号による従業員の削除
     * DELETE /api/v1/employee/number/{employeeNumber}
     * @param employeeNumber 従業員番号
     * @return Mono<ResponseEntity<Void>>
     */
    @Operation(summary = "従業員番号による従業員の削除", description = "指定された従業員番号の従業員を削除します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "従業員が正常に削除されました"),
            @ApiResponse(responseCode = "400", description = "無効な従業員番号"),
            @ApiResponse(responseCode = "404", description = "従業員が見つかりません"),
            @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    @DeleteMapping("/number/{employeeNumber}")
    public Mono<ResponseEntity<Void>> deleteEmployeeByNumber(
            @Parameter(description = "従業員番号", required = true, example = "EMP001")
            @PathVariable @NotBlank(message = "従業員番号は空にできません") @Size(min = 1, max = 20, message = "従業員番号の長さは1-20文字の間である必要があります") @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "従業員番号は英字、数字、アンダースコア、ハイフンのみを含むことができます") String employeeNumber) {
        return employeeService.deleteEmployeeByNumber(employeeNumber)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
    
    /**
     * ヘルスチェックエンドポイント
     * GET /api/v1/employee/health
     * @return Mono<ResponseEntity<String>>
     */
    @Operation(summary = "ヘルスチェック", description = "APIの稼働状況を確認します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "APIが正常に稼働中"),
            @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    @GetMapping("/health")
    public Mono<ResponseEntity<String>> healthCheck() {
        return Mono.just(ResponseEntity.ok("Employee API is running"));
    }
}
