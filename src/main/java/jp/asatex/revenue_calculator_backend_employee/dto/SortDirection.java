package jp.asatex.revenue_calculator_backend_employee.dto;

/**
 * Sort direction enumeration
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
     * Create sort direction from string
     * @param direction Sort direction string
     * @return Sort direction enumeration
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
        
        return ASC; // Default ascending order
    }
}
