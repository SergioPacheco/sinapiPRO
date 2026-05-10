-- Auxiliary registrations: clients, employees, payment methods, bank accounts, units of measure
CREATE TABLE client (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    document varchar(20),
    email varchar(200),
    phone varchar(30),
    address varchar(400),
    city varchar(100),
    state varchar(2),
    notes varchar(500),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE employee (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    document varchar(20),
    role varchar(80) NOT NULL,
    type varchar(20) NOT NULL DEFAULT 'EMPLOYEE',
    email varchar(200),
    phone varchar(30),
    hourly_rate numeric(14,4),
    admission_date date,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE unit_of_measure (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    symbol varchar(10) NOT NULL UNIQUE,
    description varchar(100) NOT NULL
);

CREATE TABLE payment_method (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL UNIQUE,
    installments integer NOT NULL DEFAULT 1,
    active boolean NOT NULL DEFAULT true
);

CREATE TABLE bank_account (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    bank_code varchar(10) NOT NULL,
    bank_name varchar(100) NOT NULL,
    agency varchar(20) NOT NULL,
    account_number varchar(30) NOT NULL,
    account_type varchar(20) NOT NULL DEFAULT 'CHECKING',
    holder_name varchar(200),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

-- Seed common units of measure
INSERT INTO unit_of_measure (id, symbol, description) VALUES
    (gen_random_uuid(), 'm', 'Metro'),
    (gen_random_uuid(), 'm2', 'Metro quadrado'),
    (gen_random_uuid(), 'm3', 'Metro cúbico'),
    (gen_random_uuid(), 'kg', 'Quilograma'),
    (gen_random_uuid(), 'un', 'Unidade'),
    (gen_random_uuid(), 'vb', 'Verba'),
    (gen_random_uuid(), 'h', 'Hora'),
    (gen_random_uuid(), 'l', 'Litro'),
    (gen_random_uuid(), 't', 'Tonelada'),
    (gen_random_uuid(), 'mês', 'Mês');
