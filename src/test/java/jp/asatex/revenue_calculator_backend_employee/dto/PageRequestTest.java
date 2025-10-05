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
 * PageRequest 测试类
 */
@DisplayName("PageRequest 测试")
class PageRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("默认构造函数创建的对象应该有默认值")
        void testDefaultConstructor() {
            PageRequest pageRequest = new PageRequest();
            
            assertEquals(0, pageRequest.getPage());
            assertEquals(10, pageRequest.getSize());
            assertEquals("employeeId", pageRequest.getSortBy());
            assertEquals(SortDirection.ASC, pageRequest.getSortDirection());
            assertEquals(0, pageRequest.getOffset());
        }

        @Test
        @DisplayName("全参数构造函数应该正确设置所有值")
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
    @DisplayName("Getter 和 Setter 测试")
    class GetterSetterTests {

        @Test
        @DisplayName("所有 getter 和 setter 应该正常工作")
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
    @DisplayName("验证测试")
    class ValidationTests {

        @Test
        @DisplayName("有效的 PageRequest 应该通过验证")
        void testValidPageRequest() {
            PageRequest pageRequest = new PageRequest(0, 10, "name", SortDirection.ASC);
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            
            assertTrue(violations.isEmpty(), "有效的 PageRequest 不应该有验证错误");
        }

        @Test
        @DisplayName("页码为负数应该验证失败")
        void testNegativePage() {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(-1);
            
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            assertFalse(violations.isEmpty(), "负数页码应该验证失败");
        }

        @Test
        @DisplayName("每页大小为0应该验证失败")
        void testZeroSize() {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setSize(0);
            
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            assertFalse(violations.isEmpty(), "每页大小为0应该验证失败");
        }

        @Test
        @DisplayName("每页大小超过100应该验证失败")
        void testSizeTooLarge() {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setSize(101);
            
            Set<ConstraintViolation<PageRequest>> violations = validator.validate(pageRequest);
            assertFalse(violations.isEmpty(), "每页大小超过100应该验证失败");
        }
    }

    @Nested
    @DisplayName("toString 测试")
    class ToStringTests {

        @Test
        @DisplayName("toString 应该返回有意义的字符串")
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
