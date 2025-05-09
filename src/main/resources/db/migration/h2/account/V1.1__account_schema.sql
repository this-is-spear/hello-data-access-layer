-- V1.1__account_schema.sql
-- 계좌 관련 스키마 생성

CREATE TABLE IF NOT EXISTS account (
    account_number VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_account_account_number ON account(account_number);
