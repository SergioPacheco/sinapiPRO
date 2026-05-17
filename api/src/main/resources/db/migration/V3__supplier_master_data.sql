ALTER TABLE supplier
    ADD COLUMN contact_name varchar(140),
    ADD COLUMN website varchar(200),
    ADD COLUMN category varchar(40),
    ADD COLUMN qualification_status varchar(30),
    ADD COLUMN payment_term_days integer,
    ADD COLUMN lead_time_days integer,
    ADD COLUMN address varchar(300),
    ADD COLUMN city varchar(100),
    ADD COLUMN state varchar(2),
    ADD COLUMN postal_code varchar(20),
    ADD COLUMN notes varchar(1000);

UPDATE supplier
SET category = 'GENERAL',
    qualification_status = 'APPROVED',
    payment_term_days = 28,
    lead_time_days = 7
WHERE category IS NULL
   OR qualification_status IS NULL
   OR payment_term_days IS NULL
   OR lead_time_days IS NULL;

ALTER TABLE supplier
    ALTER COLUMN category SET NOT NULL,
    ALTER COLUMN qualification_status SET NOT NULL,
    ALTER COLUMN payment_term_days SET NOT NULL,
    ALTER COLUMN lead_time_days SET NOT NULL;
