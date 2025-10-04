package jp.asatex.revenue_calculator_backend_employee;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class, 
         jp.asatex.revenue_calculator_backend_employee.config.TestContainersConfig.class})
class RevenueCalculatorBackendEmployeeApplicationTests {

	@Test
	void contextLoads() {
	}

}
