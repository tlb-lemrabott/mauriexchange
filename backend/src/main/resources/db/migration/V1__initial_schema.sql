-- Initial schema for Mauriexchange application
-- V1__initial_schema.sql

-- Create banks table
CREATE TABLE banks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    website_url VARCHAR(500),
    api_endpoint VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create exchange_rates table
CREATE TABLE exchange_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bank_id BIGINT NOT NULL,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    buy_rate DECIMAL(10, 4) NOT NULL,
    sell_rate DECIMAL(10, 4) NOT NULL,
    rate_date DATE NOT NULL,
    rate_timestamp TIMESTAMP NOT NULL,
    source VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (bank_id) REFERENCES banks(id),
    INDEX idx_currency_pair (from_currency, to_currency),
    INDEX idx_rate_date (rate_date),
    INDEX idx_bank_currency_date (bank_id, from_currency, to_currency, rate_date)
);

-- Create currency_conversions table for tracking conversions
CREATE TABLE currency_conversions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    converted_amount DECIMAL(15, 2) NOT NULL,
    exchange_rate DECIMAL(10, 4) NOT NULL,
    bank_id BIGINT,
    user_ip VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bank_id) REFERENCES banks(id),
    INDEX idx_conversion_date (created_at),
    INDEX idx_currency_pair (from_currency, to_currency)
);

-- Create api_requests table for monitoring and rate limiting
CREATE TABLE api_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    endpoint VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    user_ip VARCHAR(45),
    user_agent TEXT,
    response_time_ms INT,
    status_code INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_request_date (created_at),
    INDEX idx_user_ip (user_ip),
    INDEX idx_endpoint (endpoint)
);

-- Insert default banks
INSERT INTO banks (name, code, website_url, is_active) VALUES
('Central Bank of Mauritania', 'BCM', 'https://www.bcm.mr', TRUE),
('Banque Nationale de Mauritanie', 'BNM', 'https://www.bnm.mr', TRUE),
('Banque Mauritanienne pour le Commerce International', 'BMCI', 'https://www.bmci.mr', TRUE),
('Banque Al Baraka Mauritanie', 'BABM', 'https://www.albaraka.mr', TRUE),
('Société Générale Mauritanie', 'SGM', 'https://www.sgci.mr', TRUE);

-- Insert sample exchange rates (for development/testing)
INSERT INTO exchange_rates (bank_id, from_currency, to_currency, buy_rate, sell_rate, rate_date, rate_timestamp, source) VALUES
(1, 'USD', 'MRU', 35.50, 35.80, CURDATE(), NOW(), 'BCM_API'),
(1, 'EUR', 'MRU', 38.20, 38.50, CURDATE(), NOW(), 'BCM_API'),
(1, 'XOF', 'MRU', 0.058, 0.060, CURDATE(), NOW(), 'BCM_API'),
(2, 'USD', 'MRU', 35.45, 35.85, CURDATE(), NOW(), 'BNM_API'),
(2, 'EUR', 'MRU', 38.15, 38.55, CURDATE(), NOW(), 'BNM_API'),
(3, 'USD', 'MRU', 35.55, 35.75, CURDATE(), NOW(), 'BMCI_API'),
(3, 'EUR', 'MRU', 38.25, 38.45, CURDATE(), NOW(), 'BMCI_API');
