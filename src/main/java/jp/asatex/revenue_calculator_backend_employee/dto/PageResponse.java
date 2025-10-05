package jp.asatex.revenue_calculator_backend_employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 分页响应结果
 */
@Schema(description = "分页响应结果")
public class PageResponse<T> {
    
    @Schema(description = "数据列表")
    private List<T> content;
    
    @Schema(description = "当前页码", example = "0")
    private int page;
    
    @Schema(description = "每页大小", example = "10")
    private int size;
    
    @Schema(description = "总元素数量", example = "100")
    private long totalElements;
    
    @Schema(description = "总页数", example = "10")
    private int totalPages;
    
    @Schema(description = "是否为第一页", example = "true")
    private boolean first;
    
    @Schema(description = "是否为最后一页", example = "false")
    private boolean last;
    
    @Schema(description = "当前页元素数量", example = "10")
    private int numberOfElements;
    
    @Schema(description = "排序字段", example = "name")
    private String sortBy;
    
    @Schema(description = "排序方向", example = "ASC")
    private String sortDirection;
    
    // 默认构造函数
    public PageResponse() {}
    
    // 全参数构造函数
    public PageResponse(List<T> content, int page, int size, long totalElements, 
                       String sortBy, String sortDirection) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        
        // 计算派生属性
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 0;
        this.last = page >= totalPages - 1;
        this.numberOfElements = content != null ? content.size() : 0;
    }
    
    // Getter 和 Setter 方法
    public List<T> getContent() {
        return content;
    }
    
    public void setContent(List<T> content) {
        this.content = content;
    }
    
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
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public boolean isFirst() {
        return first;
    }
    
    public void setFirst(boolean first) {
        this.first = first;
    }
    
    public boolean isLast() {
        return last;
    }
    
    public void setLast(boolean last) {
        this.last = last;
    }
    
    public int getNumberOfElements() {
        return numberOfElements;
    }
    
    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    @Override
    public String toString() {
        return "PageResponse{" +
                "content=" + (content != null ? content.size() : 0) + " items" +
                ", page=" + page +
                ", size=" + size +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", first=" + first +
                ", last=" + last +
                ", numberOfElements=" + numberOfElements +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
