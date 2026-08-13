CREATE SCHEMA IF NOT EXISTS loans_schema;

CREATE TABLE loans_schema.loans (
    loan_id BIGSERIAL PRIMARY KEY,
    mobile_number VARCHAR(20) NOT NULL,
    loan_number VARCHAR(20) NOT NULL,
    loan_type VARCHAR(50) NOT NULL,
    total_loan INT NOT NULL,
    amount_paid INT NOT NULL DEFAULT 0,
    outstanding_amount INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_at TIMESTAMP,
    updated_by VARCHAR(50)
);

CREATE UNIQUE INDEX idx_loans_loan_number ON loans_schema.loans(loan_number);
CREATE INDEX idx_loans_mobile ON loans_schema.loans(mobile_number);
