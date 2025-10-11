package jp.asatex.revenue_calculator_backend_employee.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SortDirection test class
 */
@DisplayName("SortDirection Test")
class SortDirectionTest {

    @Nested
    @DisplayName("Enum Value Tests")
    class EnumValueTests {

        @Test
        @DisplayName("ASC should have correct value")
        void testAscValue() {
            assertEquals("ASC", SortDirection.ASC.getValue());
            assertEquals("ASC", SortDirection.ASC.toString());
        }

        @Test
        @DisplayName("DESC should have correct value")
        void testDescValue() {
            assertEquals("DESC", SortDirection.DESC.getValue());
            assertEquals("DESC", SortDirection.DESC.toString());
        }
    }

    @Nested
    @DisplayName("fromString Method Tests")
    class FromStringTests {

        @Test
        @DisplayName("null should return ASC")
        void testFromStringNull() {
            assertEquals(SortDirection.ASC, SortDirection.fromString(null));
        }

        @Test
        @DisplayName("empty string should return ASC")
        void testFromStringEmpty() {
            assertEquals(SortDirection.ASC, SortDirection.fromString(""));
        }

        @Test
        @DisplayName("'ASC' should return ASC")
        void testFromStringAsc() {
            assertEquals(SortDirection.ASC, SortDirection.fromString("ASC"));
        }

        @Test
        @DisplayName("'asc' should return ASC")
        void testFromStringAscLowercase() {
            assertEquals(SortDirection.ASC, SortDirection.fromString("asc"));
        }

        @Test
        @DisplayName("'DESC' should return DESC")
        void testFromStringDesc() {
            assertEquals(SortDirection.DESC, SortDirection.fromString("DESC"));
        }

        @Test
        @DisplayName("'desc' should return DESC")
        void testFromStringDescLowercase() {
            assertEquals(SortDirection.DESC, SortDirection.fromString("desc"));
        }

        @Test
        @DisplayName("invalid value should return ASC")
        void testFromStringInvalid() {
            assertEquals(SortDirection.ASC, SortDirection.fromString("INVALID"));
            assertEquals(SortDirection.ASC, SortDirection.fromString("random"));
            assertEquals(SortDirection.ASC, SortDirection.fromString("123"));
        }

        @Test
        @DisplayName("string with spaces should be handled correctly")
        void testFromStringWithSpaces() {
            assertEquals(SortDirection.ASC, SortDirection.fromString(" ASC "));
            assertEquals(SortDirection.DESC, SortDirection.fromString(" DESC "));
        }
    }
}
