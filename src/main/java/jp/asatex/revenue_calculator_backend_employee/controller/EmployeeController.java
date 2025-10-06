package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.dto.PageRequest;
import jp.asatex.revenue_calculator_backend_employee.dto.PageResponse;
import jp.asatex.revenue_calculator_backend_employee.dto.SortDirection;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
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
@RequestMapping("/api/v1/employee")
@CrossOrigin(origins = "*")
@Tag(name = "Employee Management", description = "Employee Management API")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    /**
     * Get all employees
     * GET /api/v1/employee
     * @return Flux<EmployeeDto>
     */
    @Operation(summary = "Get all employees", description = "Retrieve all employee information registered in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    @RateLimiter(name = "employee-pagination")
    public Flux<EmployeeDto> getAllEmployees() {
        return employeeService.getAllEmployees();
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
        return employeeService.getEmployeeById(id)
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
        return employeeService.getEmployeeByNumber(employeeNumber)
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
        return employeeService.searchEmployeesByName(name);
    }
    
    /**
     * Search employees by furigana
     * GET /api/v1/employee/search/furigana?q={furigana}
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
    public Flux<EmployeeDto> searchEmployeesByFurigana(
            @Parameter(description = "Furigana search keyword", required = true, example = "tanaka")
            @RequestParam @NotBlank(message = "Search keyword cannot be empty") @Size(min = 1, max = 200, message = "Search keyword length must be between 1-200 characters") String furigana) {
        return employeeService.searchEmployeesByFurigana(furigana);
    }
    
    /**
     * Get all employees with pagination
     * GET /api/v1/employee/paged
     * @param page Page number (starts from 0)
     * @param size Page size
     * @param sortBy Sort field
     * @param sortDirection Sort direction
     * @return Mono<PageResponse<EmployeeDto>>
     */
    @Operation(summary = "Get all employees with pagination", description = "Query employee list with pagination and sorting support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/paged")
    @RateLimiter(name = "employee-pagination")
    public Mono<PageResponse<EmployeeDto>> getAllEmployeesWithPagination(
            @Parameter(description = "Page number, starting from 0", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "employeeId") String sortBy,
            @Parameter(description = "Sort direction", example = "ASC")
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        PageRequest pageRequest = new PageRequest(page, size, sortBy, SortDirection.fromString(sortDirection));
        return employeeService.getAllEmployeesWithPagination(pageRequest);
    }
    
    /**
     * Search employees with pagination (by name)
     * GET /api/v1/employee/search/name/paged
     * @param name Name keyword
     * @param page Page number (starts from 0)
     * @param size Page size
     * @param sortBy Sort field
     * @param sortDirection Sort direction
     * @return Mono<PageResponse<EmployeeDto>>
     */
    @Operation(summary = "Search employees with pagination (by name)", description = "Name search with pagination and sorting support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/search/name/paged")
    @RateLimiter(name = "employee-search")
    public Mono<PageResponse<EmployeeDto>> searchEmployeesByNameWithPagination(
            @Parameter(description = "Name search keyword", required = true, example = "Tanaka")
            @RequestParam @NotBlank(message = "Search keyword cannot be empty") @Size(min = 1, max = 100, message = "Search keyword length must be between 1-100 characters") String name,
            @Parameter(description = "Page number, starting from 0", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "employeeId") String sortBy,
            @Parameter(description = "Sort direction", example = "ASC")
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        PageRequest pageRequest = new PageRequest(page, size, sortBy, SortDirection.fromString(sortDirection));
        return employeeService.searchEmployeesByNameWithPagination(name, pageRequest);
    }
    
    /**
     * Search employees with pagination (by furigana)
     * GET /api/v1/employee/search/furigana/paged
     * @param furigana Furigana keyword
     * @param page Page number (starts from 0)
     * @param size Page size
     * @param sortBy Sort field
     * @param sortDirection Sort direction
     * @return Mono<PageResponse<EmployeeDto>>
     */
    @Operation(summary = "Search employees with pagination (by furigana)", description = "Furigana search with pagination and sorting support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/search/furigana/paged")
    @RateLimiter(name = "employee-search")
    public Mono<PageResponse<EmployeeDto>> searchEmployeesByFuriganaWithPagination(
            @Parameter(description = "Furigana search keyword", required = true, example = "tanaka")
            @RequestParam @NotBlank(message = "Search keyword cannot be empty") @Size(min = 1, max = 200, message = "Search keyword length must be between 1-200 characters") String furigana,
            @Parameter(description = "Page number, starting from 0", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "employeeId") String sortBy,
            @Parameter(description = "Sort direction", example = "ASC")
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        PageRequest pageRequest = new PageRequest(page, size, sortBy, SortDirection.fromString(sortDirection));
        return employeeService.searchEmployeesByFuriganaWithPagination(furigana, pageRequest);
    }
    
    /**
     * Create new employee
     * POST /api/v1/employee
     * @param employeeDto Employee information
     * @return Mono<ResponseEntity<EmployeeDto>>
     */
    @Operation(summary = "Create new employee", description = "Register a new employee in the system")
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
        return employeeService.createEmployee(employeeDto)
                .map(createdEmployee -> ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee));
    }
    
    /**
     * Update employee information
     * PUT /api/v1/employee/{id}
     * @param id Employee ID
     * @param employeeDto Employee information
     * @return Mono<ResponseEntity<EmployeeDto>>
     */
    @Operation(summary = "Update employee information", description = "Update employee information for the specified ID")
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
        return employeeService.updateEmployee(id, employeeDto)
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
        return employeeService.deleteEmployeeById(id)
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
        return employeeService.deleteEmployeeByNumber(employeeNumber)
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
