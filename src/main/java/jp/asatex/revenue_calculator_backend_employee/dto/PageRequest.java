package jp.asatex.revenue_calculator_backend_employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 分页请求参数
 */
@Schema(description = "分页请求参数")
public class PageRequest {
    
    @Schema(description = "页码，从0开始", example = "0", minimum = "0")
    @Min(value = 0, message = "页码不能小于0")
    private int page = 0;
    
    @Schema(description = "每页大小", example = "10", minimum = "1", maximum = "100")
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private int size = 10;
    
    @Schema(description = "排序字段", example = "name")
    private String sortBy = "employeeId";
    
    @Schema(description = "排序方向", example = "ASC", allowableValues = {"ASC", "DESC"})
    private SortDirection sortDirection = SortDirection.ASC;
    
    // 默认构造函数
    public PageRequest() {}
    
    // 全参数构造函数
    public PageRequest(int page, int size, String sortBy, SortDirection sortDirection) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
    
    // Getter 和 Setter 方法
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
    
    // 计算偏移量
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
