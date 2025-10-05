package jp.asatex.revenue_calculator_backend_employee.dto;

/**
 * 排序方向枚举
 */
public enum SortDirection {
    ASC("ASC"),
    DESC("DESC");
    
    private final String value;
    
    SortDirection(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    /**
     * 从字符串创建排序方向
     * @param direction 排序方向字符串
     * @return 排序方向枚举
     */
    public static SortDirection fromString(String direction) {
        if (direction == null) {
            return ASC;
        }
        
        String upperDirection = direction.trim().toUpperCase();
        for (SortDirection dir : values()) {
            if (dir.value.equals(upperDirection)) {
                return dir;
            }
        }
        
        return ASC; // 默认升序
    }
}
