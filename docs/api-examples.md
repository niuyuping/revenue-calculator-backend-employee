# API Examples

## Employee Data Model Examples

### Create Employee Request
```json
{
  "employeeNumber": "EMP006",
  "name": "Yamada Taro",
  "furigana": "yamada taro",
  "birthday": "1992-03-15",
  "email": "yamada.taro@example.com",
  "basicSalary": 320000.00,
  "dependentCount": 1,
  "healthInsuranceEnrolled": true,
  "welfarePensionEnrolled": false,
  "unitPrice": 4500.00,
  "individualBusinessAmount": 135000.00,
  "positionAllowance": 45000.00,
  "housingAllowance": 28000.00,
  "familyAllowance": 18000.00,
  "collectionFeeAmount": 4500.00,
  "paymentFeeAmount": 2800.00,
  "thirdPartyManagementRate": 4.50,
  "thirdPartyProfitDistributionRate": 2.80,
  "phoneNumber": "+81-3-1234-5678",
  "consumptionTaxRate": 10.00,
  "nonWorkingDeduction": 50000.00,
  "overtimeAllowance": 25000.00,
  "commutingAllowance": 15000.00,
  "remarks": "Special skills: Java, Spring Boot, React",
  "isDisabled": false,
  "isSingleParent": false,
  "isWidow": false,
  "isWorkingStudent": false,
  "disabledDependentCount": 0
}
```

### Create Employee Response
```json
{
  "employeeId": 6,
  "employeeNumber": "EMP006",
  "name": "Yamada Taro",
  "furigana": "yamada taro",
  "birthday": "1992-03-15",
  "email": "yamada.taro@example.com",
  "basicSalary": 320000.00,
  "dependentCount": 1,
  "healthInsuranceEnrolled": true,
  "welfarePensionEnrolled": false,
  "unitPrice": 4500.00,
  "individualBusinessAmount": 135000.00,
  "positionAllowance": 45000.00,
  "housingAllowance": 28000.00,
  "familyAllowance": 18000.00,
  "collectionFeeAmount": 4500.00,
  "paymentFeeAmount": 2800.00,
  "thirdPartyManagementRate": 4.50,
  "thirdPartyProfitDistributionRate": 2.80,
  "phoneNumber": "+81-3-1234-5678",
  "consumptionTaxRate": 10.00,
  "nonWorkingDeduction": 50000.00,
  "overtimeAllowance": 25000.00,
  "commutingAllowance": 15000.00,
  "remarks": "Special skills: Java, Spring Boot, React",
  "isDisabled": false,
  "isSingleParent": false,
  "isWidow": false,
  "isWorkingStudent": false,
  "disabledDependentCount": 0
}
```

### Update Employee Request
```json
{
  "employeeNumber": "EMP001",
  "name": "Tanaka Taro (Updated)",
  "furigana": "tanaka taro (updated)",
  "birthday": "1990-05-15",
  "email": "tanaka.updated@example.com",
  "basicSalary": 400000.00,
  "dependentCount": 3,
  "healthInsuranceEnrolled": true,
  "welfarePensionEnrolled": true,
  "unitPrice": 5500.00,
  "individualBusinessAmount": 165000.00,
  "phoneNumber": "+81-3-9876-5432",
  "consumptionTaxRate": 10.00,
  "nonWorkingDeduction": 30000.00,
  "overtimeAllowance": 20000.00,
  "commutingAllowance": 12000.00,
  "remarks": "Updated employee information"
}
```

### Paginated Employee List Response
```json
{
  "content": [
    {
      "employeeId": 1,
      "employeeNumber": "EMP001",
      "name": "Tanaka Taro",
      "furigana": "tanaka taro",
      "birthday": "1990-05-15",
      "email": "tanaka.taro@example.com",
      "basicSalary": 350000.00,
      "dependentCount": 2,
      "healthInsuranceEnrolled": true,
      "welfarePensionEnrolled": true,
      "unitPrice": 5000.00,
      "individualBusinessAmount": 150000.00
    },
    {
      "employeeId": 2,
      "employeeNumber": "EMP002",
      "name": "Sato Hanako",
      "furigana": "sato hanako",
      "birthday": "1985-12-03",
      "email": "sato.hanako@example.com",
      "basicSalary": 420000.00,
      "dependentCount": 1,
      "healthInsuranceEnrolled": true,
      "welfarePensionEnrolled": true,
      "unitPrice": 6000.00,
      "individualBusinessAmount": 180000.00
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 2,
  "sortBy": "name",
  "sortDirection": "ASC"
}
```

## Field Validation Examples

### Valid Email Formats
- `user@example.com`
- `user.name@company.co.jp`
- `user+tag@domain.org`

### Invalid Email Formats
- `invalid-email`
- `@domain.com`
- `user@`
- `user..name@domain.com`

### Valid Salary Values
- `300000.00` (300,000 JPY)
- `0.00` (zero salary)
- `9999999999.99` (maximum value)

### Invalid Salary Values
- `-1000.00` (negative values not allowed)
- `12345678901.00` (exceeds 10 integer digits)
- `123.456` (exceeds 2 decimal places)

### Valid Unit Price Values
- `5000.00` (5,000 JPY per hour/day)
- `0.01` (minimum positive value)
- `9999999999.99` (maximum value)

### Invalid Unit Price Values
- `0.00` (must be positive)
- `-100.00` (negative values not allowed)
- `12345678901.00` (exceeds 10 integer digits)

### Valid Dependent Count Values
- `0` (no dependents)
- `5` (5 dependents)
- `99` (maximum reasonable value)

### Invalid Dependent Count Values
- `-1` (negative values not allowed)
- `100` (exceeds reasonable limit)

### Valid Phone Number Formats
- `+81-3-1234-5678` (international format)
- `03-1234-5678` (domestic format)
- `090-1234-5678` (mobile format)
- `+1-555-123-4567` (US format)

### Invalid Phone Number Formats
- `invalid-phone` (contains letters)
- `123` (too short)
- `123456789012345678901` (exceeds 20 characters)

### Valid Consumption Tax Rate Values
- `10.00` (10% consumption tax)
- `0.00` (no tax)
- `100.00` (maximum tax rate)

### Invalid Consumption Tax Rate Values
- `-5.00` (negative values not allowed)
- `150.00` (exceeds 100% limit)
- `10.123` (exceeds 2 decimal places)

### Valid Allowance Values
- `50000.00` (50,000 JPY allowance)
- `0.00` (no allowance)
- `9999999999.99` (maximum value)

### Invalid Allowance Values
- `-1000.00` (negative values not allowed)
- `12345678901.00` (exceeds 10 integer digits)
- `123.456` (exceeds 2 decimal places)

### Valid Remarks
- `"Special skills: Java, Spring Boot"` (descriptive text)
- `"Team lead for backend development"` (role description)
- `""` (empty string allowed)

### Invalid Remarks
- Text exceeding 1000 characters (exceeds length limit)

## Error Response Examples

### Validation Error
```json
{
  "timestamp": "2024-12-19T10:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/employee",
  "details": [
    {
      "field": "email",
      "message": "Email format is invalid"
    },
    {
      "field": "basicSalary",
      "message": "Basic salary must be non-negative"
    },
    {
      "field": "unitPrice",
      "message": "Unit price must be positive"
    },
    {
      "field": "dependentCount",
      "message": "Dependent count must be non-negative"
    }
  ]
}
```

## 🏷️ Deduction Target Field Examples

### Valid Deduction Target Values
```json
{
  "isDisabled": true,
  "isSingleParent": false,
  "isWidow": false,
  "isWorkingStudent": true,
  "disabledDependentCount": 2
}
```

### Invalid Deduction Target Values
```json
{
  "isDisabled": "yes",  // ❌ Should be boolean
  "isSingleParent": null,  // ✅ null is allowed
  "isWidow": "true",  // ❌ Should be boolean
  "isWorkingStudent": false,  // ✅ Valid
  "disabledDependentCount": -1  // ❌ Should be non-negative
}
```

### Tax Deduction Scenarios
```json
// Scenario 1: Disabled employee with disabled dependents
{
  "isDisabled": true,
  "disabledDependentCount": 1
}

// Scenario 2: Single parent
{
  "isSingleParent": true,
  "disabledDependentCount": 0
}

// Scenario 3: Working student
{
  "isWorkingStudent": true,
  "disabledDependentCount": 0
}

// Scenario 4: Regular employee
{
  "isDisabled": false,
  "isSingleParent": false,
  "isWidow": false,
  "isWorkingStudent": false,
  "disabledDependentCount": 0
}
```

### Employee Not Found
```json
{
  "timestamp": "2024-12-19T10:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found, ID: 999",
  "path": "/api/v1/employee/999"
}
```
