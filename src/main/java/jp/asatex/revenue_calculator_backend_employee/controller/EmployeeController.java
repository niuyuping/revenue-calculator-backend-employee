package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.common.PageRequest;
import jp.asatex.revenue_calculator_backend_employee.common.PageResponse;
import jp.asatex.revenue_calculator_backend_employee.application.EmployeeApplicationService;
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
 * Employee controller
 * Provides REST API endpoints for employee management
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/employee")
@Tag(name = "Employee Management", description = "Employee Management API with comprehensive allowance, fee, and rate tracking")
public class EmployeeController {
    
    @Autowired
    private EmployeeApplicationService employeeApplicationService;
    
    /**
     * Get employees with pagination
     * GET /api/v1/employee?page=0&size=10&sortBy=name&sortDirection=ASC
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Sort field
     * @param sortDirection Sort direction (ASC/DESC)
     * @return Mono<PageResponse<EmployeeDto>>
     */
    @Operation(summary = "Get employees with pagination", description = "Retrieve employee information with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    @RateLimiter(name = "employee-pagination")
    public Mono<PageResponse<EmployeeDto>> getEmployeesWithPagination(
            @Parameter(description = "Pagination parameters") 
            @Valid PageRequest pageRequest) {
        
        return employeeApplicationService.getEmployeesWithPagination(pageRequest);
    }
    
    /**
     * Get employee by ID
     * GET /api/v1/employee/{id}
     * @param id Employee ID
     * @return Mono<ResponseEntity<EmployeeDto>>
     */
    @Operation(summary = "Get employee by ID", description = "Retrieve employee information by specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EmployeeDto>> getEmployeeById(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable @NotNull @Positive(message = "Employee ID must be positive") Long id) {
        return employeeApplicationService.getEmployeeById(id)
                .map(employee -> ResponseEntity.ok(employee))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Get employee by employee number
     * GET /api/v1/employee/number/{employeeNumber}
     * @param employeeNumber Employee number
     * @return Mono<ResponseEntity<EmployeeDto>>
     */
    @Operation(summary = "Get employee by employee number", description = "Retrieve employee information by specified employee number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "400", description = "Invalid employee number"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/number/{employeeNumber}")
    public Mono<ResponseEntity<EmployeeDto>> getEmployeeByNumber(
            @Parameter(description = "Employee number", required = true, example = "EMP001")
            @PathVariable @NotBlank(message = "Employee numbercannot be empty") @Size(min = 1, max = 20, message = "Employee numberlength must be between 1-20 characters") @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Employee numbercan only contain letters, numbers, underscores, and hyphens") String employeeNumber) {
        return employeeApplicationService.getEmployeeByNumber(employeeNumber)
                .map(employee -> ResponseEntity.ok(employee))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Search employees by name
     * GET /api/v1/employee/search/name?q={name}
     * @param name Name keyword
     * @return Flux<EmployeeDto>
     */
    @Operation(summary = "Search employees by name", description = "Search for employees whose name contains the keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search keyword"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/search/name")
    @RateLimiter(name = "employee-search")
    public Flux<EmployeeDto> searchEmployeesByName(
            @Parameter(description = "Search keyword", required = true, example = "Tanaka")
            @RequestParam @NotBlank(message = "Search keyword cannot be empty") @Size(min = 1, max = 100, message = "Search keyword length must be between 1-100 characters") String name) {
        return employeeApplicationService.searchEmployeesByName(name);
    }
    
    /**
     * Search employees by furigana
     * GET /api/v1/employee/search/furigana?furigana={furigana}
     * @param furigana Furigana keyword
     * @return Flux<EmployeeDto>
     */
    @Operation(summary = "Search employees by furigana", description = "Search for employees whose furigana contains the keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search keyword"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/search/furigana")
    @RateLimiter(name = "employee-search")
    public Flux<EmployeeDto> searchEmployeesByFurigana(
            @Parameter(description = "Search keyword", required = true, example = "tanaka")
            @RequestParam @NotBlank(message = "Search keyword cannot be empty") @Size(min = 1, max = 100, message = "Search keyword length must be between 1-100 characters") String furigana) {
        return employeeApplicationService.searchEmployeesByFurigana(furigana);
    }
    
    
    
    
    
    /**
     * Create new employee
     * POST /api/v1/employee
     * @param employeeDto Employee information
     * @return Mono<ResponseEntity<EmployeeDto>>
     */
    @Operation(summary = "Create new employee", description = "Register a new employee in the system with comprehensive allowance, fee, and rate information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Employee number already exists"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    @RateLimiter(name = "employee-create")
    public Mono<ResponseEntity<EmployeeDto>> createEmployee(
            @Parameter(description = "Employee information", required = true)
            @RequestBody @Valid EmployeeDto employeeDto) {
        return employeeApplicationService.createEmployee(employeeDto)
                .map(createdEmployee -> ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee));
    }
    
    /**
     * Update employee information
     * PUT /api/v1/employee/{id}
     * @param id Employee ID
     * @param employeeDto Employee information
     * @return Mono<ResponseEntity<EmployeeDto>>
     */
    @Operation(summary = "Update employee information", description = "Update employee information including allowance, fee, and rate details for the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee information updated successfully", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{id}")
    @RateLimiter(name = "employee-update")
    public Mono<ResponseEntity<EmployeeDto>> updateEmployee(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable @NotNull @Positive(message = "Employee ID must be positive") Long id, 
            @Parameter(description = "Employee information to update", required = true)
            @RequestBody @Valid EmployeeDto employeeDto) {
        return employeeApplicationService.updateEmployee(id, employeeDto)
                .map(updatedEmployee -> ResponseEntity.ok(updatedEmployee))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete employee by ID
     * DELETE /api/v1/employee/{id}
     * @param id Employee ID
     * @return Mono<ResponseEntity<Void>>
     */
    @Operation(summary = "Delete employee by ID", description = "Delete employee for the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/{id}")
    @RateLimiter(name = "employee-delete")
    public Mono<ResponseEntity<Void>> deleteEmployeeById(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable @NotNull @Positive(message = "Employee ID must be positive") Long id) {
        return employeeApplicationService.deleteEmployee(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
    
    /**
     * Delete employee by employee number
     * DELETE /api/v1/employee/number/{employeeNumber}
     * @param employeeNumber Employee number
     * @return Mono<ResponseEntity<Void>>
     */
    @Operation(summary = "Delete employee by employee number", description = "Delete employee for the specified employee number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid employee number"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/number/{employeeNumber}")
    @RateLimiter(name = "employee-delete")
    public Mono<ResponseEntity<Void>> deleteEmployeeByNumber(
            @Parameter(description = "Employee number", required = true, example = "EMP001")
            @PathVariable @NotBlank(message = "Employee numbercannot be empty") @Size(min = 1, max = 20, message = "Employee numberlength must be between 1-20 characters") @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Employee numbercan only contain letters, numbers, underscores, and hyphens") String employeeNumber) {
        return employeeApplicationService.deleteEmployeeByNumber(employeeNumber)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
    
    /**
     * Health check endpoint
     * GET /api/v1/employee/health
     * @return Mono<ResponseEntity<String>>
     */
    @Operation(summary = "Health check", description = "Check API operational status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API is running normally"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/health")
    public Mono<ResponseEntity<String>> healthCheck() {
        return Mono.just(ResponseEntity.ok("Employee API is running"));
    }
}
