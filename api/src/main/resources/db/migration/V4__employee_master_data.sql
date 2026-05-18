ALTER TABLE employee
    ADD COLUMN employee_code varchar(30),
    ADD COLUMN specialty varchar(100),
    ADD COLUMN employment_status varchar(20),
    ADD COLUMN mobile_phone varchar(30),
    ADD COLUMN emergency_contact_name varchar(140),
    ADD COLUMN emergency_contact_phone varchar(30),
    ADD COLUMN address varchar(300),
    ADD COLUMN city varchar(100),
    ADD COLUMN state varchar(2),
    ADD COLUMN postal_code varchar(20),
    ADD COLUMN cost_center varchar(80),
    ADD COLUMN company_name varchar(140),
    ADD COLUMN notes varchar(1000);

UPDATE employee
SET employee_code = COALESCE(employee_code, 'EMP-' || RIGHT(REPLACE(id::text, '-', ''), 8)),
    specialty = COALESCE(specialty, role),
    employment_status = COALESCE(employment_status, 'ACTIVE')
WHERE employee_code IS NULL
   OR specialty IS NULL
   OR employment_status IS NULL;

ALTER TABLE employee
    ALTER COLUMN employee_code SET NOT NULL,
    ALTER COLUMN specialty SET NOT NULL,
    ALTER COLUMN employment_status SET NOT NULL;

ALTER TABLE employee
    ADD CONSTRAINT uk_employee_employee_code UNIQUE (employee_code);
