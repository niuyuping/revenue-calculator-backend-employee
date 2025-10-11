package jp.asatex.revenue_calculator_backend_employee.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResponse test class
 */
@DisplayName("PageResponse Test")
class PageResponseTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @DisplayName("All-parameter constructor should correctly set all values")
        void testFullParameterConstructor() {
            List<String> content = Arrays.asList("item1", "item2");
            PageResponse<String> response = new PageResponse<>(content, 1, 10, 25L, "name", "ASC");
            
            assertEquals(2, response.getContent().size());
            assertEquals(1, response.getPage());
            assertEquals(10, response.getSize());
            assertEquals(25L, response.getTotalElements());
            assertEquals(3, response.getTotalPages()); // ceil(25/10) = 3
            assertFalse(response.isFirst());
            assertTrue(response.isLast());
            assertEquals(2, response.getNumberOfElements());
            assertEquals("name", response.getSortBy());
            assertEquals("ASC", response.getSortDirection());
        }

        @DisplayName("First page should be marked correctly")
        void testFirstPage() {
            List<String> content = Arrays.asList("item1", "item2", "item3");
            PageResponse<String> response = new PageResponse<>(content, 0, 10, 15L, "name", "ASC");
            
            assertTrue(response.isFirst());
            assertFalse(response.isLast());
            assertEquals(2, response.getTotalPages()); // ceil(15/10) = 2
        }

        @DisplayName("Last page should be marked correctly")
        void testLastPage() {
            List<String> content = Arrays.asList("item1");
            PageResponse<String> response = new PageResponse<>(content, 1, 10, 15L, "name", "ASC");
            
            assertFalse(response.isFirst());
            assertTrue(response.isLast());
        }

        @DisplayName("Empty content should be handled correctly")
        void testEmptyContent() {
            List<String> content = Arrays.asList();
            PageResponse<String> response = new PageResponse<>(content, 0, 10, 0L, "name", "ASC");
            
            assertEquals(0, response.getContent().size());
            assertEquals(0, response.getTotalElements());
            assertEquals(0, response.getTotalPages());
            assertEquals(0, response.getNumberOfElements());
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @DisplayName("Setters should correctly update values")
        void testSetters() {
            PageResponse<String> response = new PageResponse<>();
            
            List<String> newContent = Arrays.asList("new1", "new2");
            response.setContent(newContent);
            response.setPage(2);
            response.setSize(5);
            response.setTotalElements(20L);
            response.setTotalPages(4);
            response.setFirst(false);
            response.setLast(false);
            response.setNumberOfElements(2);
            response.setSortBy("id");
            response.setSortDirection("DESC");
            
            assertEquals(2, response.getContent().size());
            assertEquals(2, response.getPage());
            assertEquals(5, response.getSize());
            assertEquals(20L, response.getTotalElements());
            assertEquals(4, response.getTotalPages());
            assertFalse(response.isFirst());
            assertFalse(response.isLast());
            assertEquals(2, response.getNumberOfElements());
            assertEquals("id", response.getSortBy());
            assertEquals("DESC", response.getSortDirection());
        }

        @DisplayName("Setting null content should create empty list")
        void testSetNullContent() {
            PageResponse<String> response = new PageResponse<>();
            response.setContent(null);
            
            assertNotNull(response.getContent());
            assertTrue(response.getContent().isEmpty());
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @DisplayName("toString should return meaningful string")
        void testToString() {
            List<String> content = Arrays.asList("item1", "item2");
            PageResponse<String> response = new PageResponse<>(content, 1, 10, 25L, "name", "ASC");
            String result = response.toString();

            assertTrue(result.contains("content=2 items"));
            assertTrue(result.contains("page=1"));
            assertTrue(result.contains("size=10"));
            assertTrue(result.contains("totalElements=25"));
            assertTrue(result.contains("totalPages=3"));
            assertTrue(result.contains("first=false"));
            assertTrue(result.contains("last=true"));
            assertTrue(result.contains("numberOfElements=2"));
            assertTrue(result.contains("sortBy='name'"));
            assertTrue(result.contains("sortDirection='ASC'"));
        }
    }
}
