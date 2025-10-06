package jp.asatex.revenue_calculator_backend_employee.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResponse test class
 */
@DisplayName("PageResponse Test")
class PageResponseTest {

    @Nested
    @DisplayName("Constructor Test")
    class ConstructorTests {

        @Test
        @DisplayName("All-parameter constructor should correctly set all values")
        void testFullParameterConstructor() {
            List<String> content = Arrays.asList("item1", "item2", "item3");
            PageResponse<String> response = new PageResponse<>(content, 1, 10, 25L, "name", "ASC");
            
            assertEquals(content, response.getContent());
            assertEquals(1, response.getPage());
            assertEquals(10, response.getSize());
            assertEquals(25L, response.getTotalElements());
            assertEquals(3, response.getTotalPages()); // Math.ceil(25/10) = 3
            assertFalse(response.isFirst()); // page 1 is not first
            assertFalse(response.isLast()); // page 1 is not last (totalPages = 3)
            assertEquals(3, response.getNumberOfElements());
            assertEquals("name", response.getSortBy());
            assertEquals("ASC", response.getSortDirection());
        }

        @Test
        @DisplayName("First page should be correctly identified")
        void testFirstPage() {
            List<String> content = Arrays.asList("item1", "item2");
            PageResponse<String> response = new PageResponse<>(content, 0, 10, 15L, "name", "ASC");
            
            assertTrue(response.isFirst());
            assertFalse(response.isLast());
        }

        @Test
        @DisplayName("Last page should be correctly identified")
        void testLastPage() {
            List<String> content = Arrays.asList("item1", "item2");
            PageResponse<String> response = new PageResponse<>(content, 1, 10, 15L, "name", "ASC");
            
            assertFalse(response.isFirst());
            assertTrue(response.isLast());
        }

        @Test
        @DisplayName("Empty content should be handled correctly")
        void testEmptyContent() {
            List<String> content = Collections.emptyList();
            PageResponse<String> response = new PageResponse<>(content, 0, 10, 0L, "name", "ASC");
            
            assertEquals(0, response.getNumberOfElements());
            assertTrue(response.isFirst());
            assertTrue(response.isLast());
            assertEquals(0, response.getTotalPages());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Test")
    class GetterSetterTests {

        @Test
        @DisplayName("All getters and setters should work correctly")
        void testGettersAndSetters() {
            PageResponse<String> response = new PageResponse<>();
            
            List<String> content = Arrays.asList("test");
            response.setContent(content);
            response.setPage(2);
            response.setSize(5);
            response.setTotalElements(12L);
            response.setTotalPages(3);
            response.setFirst(false);
            response.setLast(false);
            response.setNumberOfElements(1);
            response.setSortBy("test");
            response.setSortDirection("DESC");
            
            assertEquals(content, response.getContent());
            assertEquals(2, response.getPage());
            assertEquals(5, response.getSize());
            assertEquals(12L, response.getTotalElements());
            assertEquals(3, response.getTotalPages());
            assertFalse(response.isFirst());
            assertFalse(response.isLast());
            assertEquals(1, response.getNumberOfElements());
            assertEquals("test", response.getSortBy());
            assertEquals("DESC", response.getSortDirection());
        }
    }

    @Nested
    @DisplayName("toString Test")
    class ToStringTests {

        @Test
        @DisplayName("toString should return meaningful string")
        void testToString() {
            List<String> content = Arrays.asList("item1", "item2");
            PageResponse<String> response = new PageResponse<>(content, 1, 10, 25L, "name", "ASC");
            String result = response.toString();
            
            assertTrue(result.contains("2 items"));
            assertTrue(result.contains("page=1"));
            assertTrue(result.contains("size=10"));
            assertTrue(result.contains("totalElements=25"));
            assertTrue(result.contains("totalPages=3"));
            assertTrue(result.contains("sortBy='name'"));
            assertTrue(result.contains("sortDirection='ASC'"));
        }
    }
}
