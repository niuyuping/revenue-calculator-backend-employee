package jp.asatex.revenue_calculator_backend_employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Pagination response result
 */
@Schema(description = "Pagination response result")
public class PageResponse<T> {
    
    @Schema(description = "Data list")
    private List<T> content;
    
    @Schema(description = "Current page number", example = "0")
    private int page;
    
    @Schema(description = "Page size", example = "10")
    private int size;
    
    @Schema(description = "Total number of elements", example = "100")
    private long totalElements;
    
    @Schema(description = "Total number of pages", example = "10")
    private int totalPages;
    
    @Schema(description = "Whether it is the first page", example = "true")
    private boolean first;
    
    @Schema(description = "Whether it is the last page", example = "false")
    private boolean last;
    
    @Schema(description = "Number of elements in current page", example = "10")
    private int numberOfElements;
    
    @Schema(description = "Sort field", example = "name")
    private String sortBy;
    
    @Schema(description = "Sort direction", example = "ASC")
    private String sortDirection;
    
    // Default constructor
    public PageResponse() {}
    
    // All parameters constructor
    public PageResponse(List<T> content, int page, int size, long totalElements, 
                       String sortBy, String sortDirection) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        
        // Calculate derived properties
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 0;
        this.last = page >= totalPages - 1;
        this.numberOfElements = content != null ? content.size() : 0;
    }
    
    // Getter and Setter methods
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
