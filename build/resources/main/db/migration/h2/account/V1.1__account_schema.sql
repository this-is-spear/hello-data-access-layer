-- V1.1__account_schema.sql
-- 계좌 관련 스키마 생성

-- Money 를 위한 임베디드 타입 대신 DECIMAL 사용
-- Account 테이블 생성
CREATE TABLE IF NOT EXISTS account (
    account_number VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 테이블 인덱스 생성
CREATE INDEX idx_account_account_number ON account(account_number);