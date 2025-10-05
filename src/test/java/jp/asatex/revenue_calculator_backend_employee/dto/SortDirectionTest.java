package jp.asatex.revenue_calculator_backend_employee.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SortDirection 测试类
 */
@DisplayName("SortDirection 测试")
class SortDirectionTest {

    @Nested
    @DisplayName("枚举值测试")
    class EnumValueTests {

        @Test
        @DisplayName("ASC 应该有正确的值")
        void testAscValue() {
            assertEquals("ASC", SortDirection.ASC.getValue());
            assertEquals("ASC", SortDirection.ASC.toString());
        }

        @Test
        @DisplayName("DESC 应该有正确的值")
        void testDescValue() {
            assertEquals("DESC", SortDirection.DESC.getValue());
            assertEquals("DESC", SortDirection.DESC.toString());
        }
    }

    @Nested
    @DisplayName("fromString 方法测试")
    class FromStringTests {

        @Test
        @DisplayName("null 应该返回 ASC")
        void testFromStringNull() {
            assertEquals(SortDirection.ASC, SortDirection.fromString(null));
        }

        @Test
        @DisplayName("空字符串应该返回 ASC")
        void testFromStringEmpty() {
            assertEquals(SortDirection.ASC, SortDirection.fromString(""));
        }

        @Test
        @DisplayName("'ASC' 应该返回 ASC")
        void testFromStringAsc() {
            assertEquals(SortDirection.ASC, SortDirection.fromString("ASC"));
        }

        @Test
        @DisplayName("'asc' 应该返回 ASC")
        void testFromStringAscLowercase() {
            assertEquals(SortDirection.ASC, SortDirection.fromString("asc"));
        }

        @Test
        @DisplayName("'DESC' 应该返回 DESC")
        void testFromStringDesc() {
            assertEquals(SortDirection.DESC, SortDirection.fromString("DESC"));
        }

        @Test
        @DisplayName("'desc' 应该返回 DESC")
        void testFromStringDescLowercase() {
            assertEquals(SortDirection.DESC, SortDirection.fromString("desc"));
        }

        @Test
        @DisplayName("无效值应该返回 ASC")
        void testFromStringInvalid() {
            assertEquals(SortDirection.ASC, SortDirection.fromString("INVALID"));
            assertEquals(SortDirection.ASC, SortDirection.fromString("random"));
            assertEquals(SortDirection.ASC, SortDirection.fromString("123"));
        }

        @Test
        @DisplayName("带空格的字符串应该正确处理")
        void testFromStringWithSpaces() {
            assertEquals(SortDirection.ASC, SortDirection.fromString(" ASC "));
            assertEquals(SortDirection.DESC, SortDirection.fromString(" DESC "));
        }
    }
}
