package jp.asatex.revenue_calculator_backend_employee.common;

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
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @DisplayName("Default constructor should set default values")
        void testDefaultConstructor() {
            PageRequest pageRequest = new PageRequest();
            
            assertEquals(0, pageRequest.getPage());
            assertEquals(10, pageRequest.getSize());
            assertEquals("employeeId", pageRequest.getSortBy());
            assertEquals(SortDirection.ASC, pageRequest.getSortDirection());
            assertEquals(0, pageRequest.getOffset());
        }

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
    @DisplayName("Setter Tests")
    class SetterTests {

        @DisplayName("Setters should correctly update values")
        void testSetters() {
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
    @DisplayName("Validation Tests")
    class ValidationTests {

        @DisplayName("Valid PageRequest should pass validation")
        void testValidPageRequest() {
            PageRequest pageRequest = new PageRequest(0, 10, "name", SortDirection.ASC);
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            
            assertTrue(violations.isEmpty(), "Valid PageRequest should not have validation errors");
        }

        @DisplayName("Negative page should fail validation")
        void testNegativePage() {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(-1);
            
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            assertFalse(violations.isEmpty(), "Negative page should have validation errors");
        }

        @DisplayName("Zero size should fail validation")
        void testZeroSize() {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setSize(0);
            
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            assertFalse(violations.isEmpty(), "Zero size should have validation errors");
        }

        @DisplayName("Size exceeding maximum should fail validation")
        void testSizeExceedingMaximum() {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setSize(101);
            
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            assertFalse(violations.isEmpty(), "Size exceeding maximum should have validation errors");
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

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
