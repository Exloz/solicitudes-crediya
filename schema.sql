CREATE TABLE IF NOT EXISTS loan_type (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY key,
    name VARCHAR(255) NOT NULL UNIQUE,
    min_amount DECIMAL(15,2) not NULL,
    max_amount DECIMAL(15,2) not NULL,
    interest_rate DECIMAL(15,2) not null,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS state (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description text,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS loan_application (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id VARCHAR(255) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    term INTEGER NOT NULL,
    loan_type_id BIGINT NOT NULL REFERENCES loan_type(id),
    status BIGINT NOT null references state(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO loan_type (name, min_amount, max_amount, interest_rate) VALUES
('Personal Loan', 1000.00, 50000.00, 12.5),
('Home Loan', 10000.00, 200000.00, 8.5),
('Car Loan', 5000.00, 80000.00, 10.0),
('Business Loan', 15000.00, 300000.00, 9.5)
ON CONFLICT (name) DO NOTHING;

INSERT INTO state (name, description) VALUES
('Pending review', 'The application has been received but not yet reviewed'),
('Under review', 'The application is currently being evaluated'),
('Approved', 'The application has been accepted and approved'),
('Rejected', 'The application has been denied'),
('Cancelled', 'The application was withdrawn or cancelled by the applicant')
ON CONFLICT (name) DO NOTHING;

