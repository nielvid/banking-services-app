CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    clientId VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    phone VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    zip VARCHAR(20),
    country VARCHAR(100),
    role VARCHAR(200),
    UNIQUE (email)
);

CREATE INDEX idx_email ON users(email);

CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL, 
    account_number BIGINT NOT NULL UNIQUE,
    account_name VARCHAR(255) NOT NULL,
    balance NUMERIC(20, 2) NOT NULL DEFAULT 0.00,
    account_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    pin VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- CREATE UNIQUE INDEX idx_account_number ON accounts(account_number); //automatically created a unique index on the account_number.