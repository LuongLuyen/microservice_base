CREATE SCHEMA IF NOT EXISTS accounts_schema;

CREATE TABLE accounts_schema.customer (
    customer_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    tenant_id VARCHAR(50) NOT NULL DEFAULT 'default',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_at TIMESTAMP,
    updated_by VARCHAR(50)
);

CREATE UNIQUE INDEX idx_customer_mobile ON accounts_schema.customer(mobile_number);

CREATE TABLE accounts_schema.accounts (
    account_number BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    branch_address VARCHAR(200) NOT NULL,
    communication_sw BOOLEAN DEFAULT FALSE,
    tenant_id VARCHAR(50) NOT NULL DEFAULT 'default',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_at TIMESTAMP,
    updated_by VARCHAR(50),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES accounts_schema.customer(customer_id)
);
