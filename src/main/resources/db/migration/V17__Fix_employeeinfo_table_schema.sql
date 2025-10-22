-- This script checks for the existence of all columns and constraints in the employeeInfo table and adds them if they are missing.

-- V1 - Initial table creation columns
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='employee_id') THEN
        ALTER TABLE employeeInfo ADD COLUMN employee_id BIGSERIAL PRIMARY KEY;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='employee_number') THEN
        ALTER TABLE employeeInfo ADD COLUMN employee_number VARCHAR(20) NOT NULL UNIQUE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='name') THEN
        ALTER TABLE employeeInfo ADD COLUMN name VARCHAR(100) NOT NULL;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='furigana') THEN
        ALTER TABLE employeeInfo ADD COLUMN furigana VARCHAR(200);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='birthday') THEN
        ALTER TABLE employeeInfo ADD COLUMN birthday DATE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='created_at') THEN
        ALTER TABLE employeeInfo ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='updated_at') THEN
        ALTER TABLE employeeInfo ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
END;
$$;

-- V3 - Constraints
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_employee_number_format') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_employee_number_format CHECK (employee_number ~ '^[A-Za-z0-9_-]+$');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_employee_number_length') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_employee_number_length CHECK (LENGTH(employee_number) >= 1 AND LENGTH(employee_number) <= 20);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_name_length') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_name_length CHECK (LENGTH(name) >= 1 AND LENGTH(name) <= 100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_furigana_length') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_furigana_length CHECK (furigana IS NULL OR LENGTH(furigana) <= 200);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_birthday_past') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_birthday_past CHECK (birthday IS NULL OR birthday < CURRENT_DATE);
    END IF;
END;
$$;

-- V4 - Soft delete columns
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='is_deleted') THEN
        ALTER TABLE employeeInfo ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='deleted_at') THEN
        ALTER TABLE employeeInfo ADD COLUMN deleted_at TIMESTAMP;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='deleted_by') THEN
        ALTER TABLE employeeInfo ADD COLUMN deleted_by VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_deleted_by_length') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_deleted_by_length CHECK (deleted_by IS NULL OR LENGTH(deleted_by) <= 100);
    END IF;
END;
$$;

-- V5 - Email and basic salary
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='email') THEN
        ALTER TABLE employeeInfo ADD COLUMN email VARCHAR(255);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='basic_salary') THEN
        ALTER TABLE employeeInfo ADD COLUMN basic_salary DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_email_format') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_email_format CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_basic_salary_positive') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_basic_salary_positive CHECK (basic_salary IS NULL OR basic_salary >= 0);
    END IF;
END;
$$;

-- V7 & V16 - Insurance and payment
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='dependent_count') THEN
        ALTER TABLE employeeInfo ADD COLUMN dependent_count INTEGER DEFAULT 0;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='no_health_insurance') THEN
        -- Original name was health_insurance_enrolled
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='health_insurance_enrolled') THEN
            ALTER TABLE employeeInfo ADD COLUMN no_health_insurance BOOLEAN DEFAULT FALSE;
        ELSE
            ALTER TABLE employeeInfo RENAME COLUMN health_insurance_enrolled TO no_health_insurance;
        END IF;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='no_pension_insurance') THEN
        -- Original name was welfare_pension_enrolled
         IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='welfare_pension_enrolled') THEN
            ALTER TABLE employeeInfo ADD COLUMN no_pension_insurance BOOLEAN DEFAULT FALSE;
        ELSE
            ALTER TABLE employeeInfo RENAME COLUMN welfare_pension_enrolled TO no_pension_insurance;
        END IF;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='unit_price') THEN
        ALTER TABLE employeeInfo ADD COLUMN unit_price DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='individual_business_amount') THEN
        ALTER TABLE employeeInfo ADD COLUMN individual_business_amount DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_dependent_count_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_dependent_count_non_negative CHECK (dependent_count IS NULL OR dependent_count >= 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_unit_price_positive') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_unit_price_positive CHECK (unit_price IS NULL OR unit_price > 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_individual_business_amount_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_individual_business_amount_non_negative CHECK (individual_business_amount IS NULL OR individual_business_amount >= 0);
    END IF;
END;
$$;

-- V9, V11, V13 - Allowances and fees
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='position_allowance') THEN
        ALTER TABLE employeeInfo ADD COLUMN position_allowance DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='housing_allowance') THEN
        ALTER TABLE employeeInfo ADD COLUMN housing_allowance DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='family_allowance') THEN
        ALTER TABLE employeeInfo ADD COLUMN family_allowance DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='collection_fee_amount') THEN
        ALTER TABLE employeeInfo ADD COLUMN collection_fee_amount DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='payment_fee_amount') THEN
        ALTER TABLE employeeInfo ADD COLUMN payment_fee_amount DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='third_party_management_rate') THEN
        ALTER TABLE employeeInfo ADD COLUMN third_party_management_rate DECIMAL(5,2);
    ELSE
        ALTER TABLE employeeInfo ALTER COLUMN third_party_management_rate TYPE DECIMAL(5,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='third_party_profit_distribution_rate') THEN
        ALTER TABLE employeeInfo ADD COLUMN third_party_profit_distribution_rate DECIMAL(5,2);
    ELSE
        ALTER TABLE employeeInfo ALTER COLUMN third_party_profit_distribution_rate TYPE DECIMAL(5,2);
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_third_party_management_rate_range') THEN
        ALTER TABLE employeeInfo DROP CONSTRAINT chk_third_party_management_rate_range;
    END IF;
    ALTER TABLE employeeInfo ADD CONSTRAINT chk_third_party_management_rate_range CHECK (third_party_management_rate IS NULL OR (third_party_management_rate >= 0 AND third_party_management_rate <= 100));
    IF EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_third_party_profit_distribution_rate_range') THEN
        ALTER TABLE employeeInfo DROP CONSTRAINT chk_third_party_profit_distribution_rate_range;
    END IF;
    ALTER TABLE employeeInfo ADD CONSTRAINT chk_third_party_profit_distribution_rate_range CHECK (third_party_profit_distribution_rate IS NULL OR (third_party_profit_distribution_rate >= 0 AND third_party_profit_distribution_rate <= 100));

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_position_allowance_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_position_allowance_non_negative CHECK (position_allowance IS NULL OR position_allowance >= 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_housing_allowance_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_housing_allowance_non_negative CHECK (housing_allowance IS NULL OR housing_allowance >= 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_family_allowance_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_family_allowance_non_negative CHECK (family_allowance IS NULL OR family_allowance >= 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_collection_fee_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_collection_fee_non_negative CHECK (collection_fee_amount IS NULL OR collection_fee_amount >= 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_payment_fee_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_payment_fee_non_negative CHECK (payment_fee_amount IS NULL OR payment_fee_amount >= 0);
    END IF;
END;
$$;


-- V14 - Phone, tax, etc.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='phone_number') THEN
        ALTER TABLE employeeInfo ADD COLUMN phone_number VARCHAR(20);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='consumption_tax_rate') THEN
        ALTER TABLE employeeInfo ADD COLUMN consumption_tax_rate DECIMAL(5,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='non_working_deduction') THEN
        ALTER TABLE employeeInfo ADD COLUMN non_working_deduction DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='overtime_allowance') THEN
        ALTER TABLE employeeInfo ADD COLUMN overtime_allowance DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='commuting_allowance') THEN
        ALTER TABLE employeeInfo ADD COLUMN commuting_allowance DECIMAL(12,2);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='remarks') THEN
        ALTER TABLE employeeInfo ADD COLUMN remarks TEXT;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_non_working_deduction_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_non_working_deduction_non_negative CHECK (non_working_deduction IS NULL OR non_working_deduction >= 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_overtime_allowance_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_overtime_allowance_non_negative CHECK (overtime_allowance IS NULL OR overtime_allowance >= 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_commuting_allowance_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_commuting_allowance_non_negative CHECK (commuting_allowance IS NULL OR commuting_allowance >= 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_consumption_tax_rate_range') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_consumption_tax_rate_range CHECK (consumption_tax_rate IS NULL OR (consumption_tax_rate >= 0 AND consumption_tax_rate <= 100));
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_phone_number_format') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_phone_number_format CHECK (phone_number IS NULL OR phone_number ~ '^[+]?[0-9\\s\\-\\(\\)]+$');
    END IF;
END;
$$;

-- V15 - Deduction targets
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='is_disabled') THEN
        ALTER TABLE employeeInfo ADD COLUMN is_disabled BOOLEAN DEFAULT FALSE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='is_single_parent') THEN
        ALTER TABLE employeeInfo ADD COLUMN is_single_parent BOOLEAN DEFAULT FALSE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='is_widow') THEN
        ALTER TABLE employeeInfo ADD COLUMN is_widow BOOLEAN DEFAULT FALSE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='is_working_student') THEN
        ALTER TABLE employeeInfo ADD COLUMN is_working_student BOOLEAN DEFAULT FALSE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employeeinfo' AND column_name='disabled_dependent_count') THEN
        ALTER TABLE employeeInfo ADD COLUMN disabled_dependent_count INTEGER DEFAULT 0;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='employeeinfo' AND constraint_name='chk_disabled_dependent_count_non_negative') THEN
        ALTER TABLE employeeInfo ADD CONSTRAINT chk_disabled_dependent_count_non_negative CHECK (disabled_dependent_count IS NULL OR disabled_dependent_count >= 0);
    END IF;
END;
$$;

-- Create indexes if they don't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_employee_number' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_employee_number ON employeeInfo(employee_number);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_name' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_name ON employeeInfo(name);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_furigana' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_furigana ON employeeInfo(furigana);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_is_deleted' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_is_deleted ON employeeInfo(is_deleted);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_deleted_at' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_deleted_at ON employeeInfo(deleted_at);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_email' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_email ON employeeInfo(email);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_dependent_count' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_dependent_count ON employeeInfo(dependent_count);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_no_health_insurance' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_no_health_insurance ON employeeInfo(no_health_insurance);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_no_pension_insurance' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_no_pension_insurance ON employeeInfo(no_pension_insurance);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_position_allowance' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_position_allowance ON employeeInfo(position_allowance);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_housing_allowance' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_housing_allowance ON employeeInfo(housing_allowance);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_family_allowance' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_family_allowance ON employeeInfo(family_allowance);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_collection_fee' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_collection_fee ON employeeInfo(collection_fee_amount);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_payment_fee' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_payment_fee ON employeeInfo(payment_fee_amount);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_phone_number' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_phone_number ON employeeInfo(phone_number);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_consumption_tax_rate' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_consumption_tax_rate ON employeeInfo(consumption_tax_rate);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_is_disabled' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_is_disabled ON employeeInfo(is_disabled);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_is_single_parent' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_is_single_parent ON employeeInfo(is_single_parent);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_is_widow' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_is_widow ON employeeInfo(is_widow);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_is_working_student' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_is_working_student ON employeeInfo(is_working_student);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_employeeinfo_disabled_dependent_count' AND n.nspname = 'public') THEN
        CREATE INDEX idx_employeeInfo_disabled_dependent_count ON employeeInfo(disabled_dependent_count);
    END IF;
END;
$$;

-- Update trigger for updated_at
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'update_updated_at_column') THEN
        CREATE OR REPLACE FUNCTION update_updated_at_column()
        RETURNS TRIGGER AS $func$
        BEGIN
            NEW.updated_at = CURRENT_TIMESTAMP;
            RETURN NEW;
        END;
        $func$ language 'plpgsql';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'update_employeeinfo_updated_at') THEN
        CREATE TRIGGER update_employeeInfo_updated_at
            BEFORE UPDATE ON employeeInfo
            FOR EACH ROW
            EXECUTE FUNCTION update_updated_at_column();
    END IF;
END;
$$;
