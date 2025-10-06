package jp.asatex.revenue_calculator_backend_employee.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageRequest test class
 */
@DisplayName("PageRequest Test")
class PageRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Nested
    @DisplayName("Constructor Test")
    class ConstructorTests {

        @Test
        @DisplayName("Object created with default constructor should have default values")
        void testDefaultConstructor() {
            PageRequest pageRequest = new PageRequest();
            
            assertEquals(0, pageRequest.getPage());
            assertEquals(10, pageRequest.getSize());
            assertEquals("employeeId", pageRequest.getSortBy());
            assertEquals(SortDirection.ASC, pageRequest.getSortDirection());
            assertEquals(0, pageRequest.getOffset());
        }

        @Test
        @DisplayName("All-parameter constructor should correctly set all values")
        void testFullParameterConstructor() {
            PageRequest pageRequest = new PageRequest(2, 20, "name", SortDirection.DESC);
            
            assertEquals(2, pageRequest.getPage());
            assertEquals(20, pageRequest.getSize());
            assertEquals("name", pageRequest.getSortBy());
            assertEquals(SortDirection.DESC, pageRequest.getSortDirection());
            assertEquals(40, pageRequest.getOffset()); // 2 * 20
        }
    }

    @Nested
    @DisplayName("Getter and Setter Test")
    class GetterSetterTests {

        @Test
        @DisplayName("All getters and setters should work correctly")
        void testGettersAndSetters() {
            PageRequest pageRequest = new PageRequest();
            
            pageRequest.setPage(3);
            pageRequest.setSize(15);
            pageRequest.setSortBy("furigana");
            pageRequest.setSortDirection(SortDirection.DESC);
            
            assertEquals(3, pageRequest.getPage());
            assertEquals(15, pageRequest.getSize());
            assertEquals("furigana", pageRequest.getSortBy());
            assertEquals(SortDirection.DESC, pageRequest.getSortDirection());
            assertEquals(45, pageRequest.getOffset()); // 3 * 15
        }
    }

    @Nested
    @DisplayName("Validation Test")
    class ValidationTests {

        @Test
        @DisplayName("Valid PageRequest should pass validation")
        void testValidPageRequest() {
            PageRequest pageRequest = new PageRequest(0, 10, "name", SortDirection.ASC);
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            
            assertTrue(violations.isEmpty(), "Valid PageRequest should not have validation errors");
        }

        @Test
        @DisplayName("Negative page number should fail validation")
        void testNegativePage() {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(-1);
            
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            assertFalse(violations.isEmpty(), "Negative page number should fail validation");
        }

        @Test
        @DisplayName("Page size of 0 should fail validation")
        void testZeroSize() {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setSize(0);
            
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            assertFalse(violations.isEmpty(), "Page size of 0 should fail validation");
        }

        @Test
        @DisplayName("Page size over 100 should fail validation")
        void testSizeTooLarge() {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setSize(101);
            
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            assertFalse(violations.isEmpty(), "Page size over 100 should fail validation");
        }
    }

    @Nested
    @DisplayName("toString Test")
    class ToStringTests {

        @Test
        @DisplayName("toString should return meaningful string")
        void testToString() {
            PageRequest pageRequest = new PageRequest(1, 5, "name", SortDirection.DESC);
            String result = pageRequest.toString();
            
            assertTrue(result.contains("page=1"));
            assertTrue(result.contains("size=5"));
            assertTrue(result.contains("sortBy='name'"));
            assertTrue(result.contains("sortDirection=DESC"));
        }
    }
}
