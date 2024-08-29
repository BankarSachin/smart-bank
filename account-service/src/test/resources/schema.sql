DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS transactions CASCADE;

DROP TYPE IF EXISTS account_type;
DROP TYPE IF EXISTS account_status;

CREATE TABLE IF NOT EXISTS customers (
	customer_id INTEGER AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	email VARCHAR(100) NOT NULL UNIQUE,
	phone_number VARCHAR(15) NOT NULL UNIQUE,
	password VARCHAR(500) NOT NULL,
	created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS account_number_seq
    INCREMENT BY 1
    START WITH 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

--ALTER SEQUENCE customers_customer_id_seq RESTART WITH 123456;

CREATE TABLE IF NOT EXISTS accounts(
	account_number CHAR(10) PRIMARY KEY,
	customer_id BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    branch_code VARCHAR(20),
    account_status VARCHAR(10) NOT NULL,
    current_balance DECIMAL(10, 2),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lock INTEGER,
	CONSTRAINT fk_customer_id FOREIGN KEY(customer_id) REFERENCES customers(customer_id),
    UNIQUE (customer_id, account_number)
);

CREATE TABLE IF NOT EXISTS permissions(
	permission_id SMALLINT AUTO_INCREMENT PRIMARY KEY,
	permission VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS customer_permission_mappings(
    customer_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id),
    UNIQUE (customer_id, permission_id)
);

--CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS transactions (
    utr_number UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    transaction_summary TEXT,
	transaction_status VARCHAR(15) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transaction_entries (
    transaction_entry_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
	utr_number UUID NOT NULL,
	transaction_type VARCHAR(2) NOT NULL,
	transaction_amount DECIMAL(10,2) NOT NULL,
	account_number CHAR(10) NOT NULL,
	closing_balance DECIMAL(10,2) NOT NULL,
	CONSTRAINT fk_account_number FOREIGN KEY(account_number) REFERENCES accounts(account_number),
	CONSTRAINT fk_utr_number FOREIGN KEY(utr_number) REFERENCES transactions(utr_number)
);

CREATE TABLE IF NOT EXISTS transfers(
	transfer_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
	utr_number UUID NOT NULL,
	source_account_number CHAR(10) NOT NULL,
	destination_account_number CHAR(10) NOT NULL,
	CONSTRAINT fk_src_account_number FOREIGN KEY(source_account_number) REFERENCES accounts(account_number),
	CONSTRAINT fk_det_account_number FOREIGN KEY(destination_account_number) REFERENCES accounts(account_number),
	CONSTRAINT fk_transaction_id FOREIGN KEY(utr_number) REFERENCES transactions(utr_number)
);

INSERT INTO permissions(permission) VALUES ('ADMIN');
INSERT INTO permissions(permission) VALUES ('VIEW_BALANCE');
INSERT INTO permissions(permission) VALUES ('DEPOSIT_FUNDS');
INSERT INTO permissions(permission) VALUES ('WITHDRAW_FUNDS');
