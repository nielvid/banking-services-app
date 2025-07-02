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


CREATE TABLE accounts (
	id serial4 PRIMARY KEY,
	user_id varchar NOT NULL,
	account_number varchar NOT NUL UNIQUE,
	account_name varchar(255) NOT NULL,
	balance numeric(20, 2) DEFAULT 0.00 NOT NULL,
	account_type varchar(50) NOT NULL,
	status varchar(50) NOT NULL,
	pin varchar(255) NULL,
	FOREIGN KEY (user_id) REFERENCES users(client_id)

);

