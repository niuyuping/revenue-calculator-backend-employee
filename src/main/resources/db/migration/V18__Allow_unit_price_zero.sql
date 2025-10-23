-- Allow unit_price to be zero (non-negative)
-- Drop old constraint if it exists, then add the new one

ALTER TABLE employeeInfo DROP CONSTRAINT IF EXISTS chk_unit_price_positive;

ALTER TABLE employeeInfo ADD CONSTRAINT chk_unit_price_non_negative
CHECK (unit_price IS NULL OR unit_price >= 0);
