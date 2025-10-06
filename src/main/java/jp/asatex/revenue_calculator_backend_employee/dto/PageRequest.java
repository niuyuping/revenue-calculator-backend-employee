package jp.asatex.revenue_calculator_backend_employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Pagination request parameters
 */
@Schema(description = "Pagination request parameters")
public class PageRequest {
    
    @Schema(description = "Page number, starting from 0", example = "0", minimum = "0")
    @Min(value = 0, message = "Page number cannot be less than 0")
    private int page = 0;
    
    @Schema(description = "Page size", example = "10", minimum = "1", maximum = "100")
    @Min(value = 1, message = "Page size cannot be less than 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int size = 10;
    
    @Schema(description = "Sort field", example = "name")
    private String sortBy = "employeeId";
    
    @Schema(description = "Sort direction", example = "ASC", allowableValues = {"ASC", "DESC"})
    private SortDirection sortDirection = SortDirection.ASC;
    
    // Default constructor
    public PageRequest() {}
    
    // All parameters constructor
    public PageRequest(int page, int size, String sortBy, SortDirection sortDirection) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
    
    // Getter and Setter methods
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public SortDirection getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    // Calculate offset
    public int getOffset() {
        return page * size;
    }
    
    @Override
    public String toString() {
        return "PageRequest{" +
                "page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection=" + sortDirection +
                '}';
    }
}
