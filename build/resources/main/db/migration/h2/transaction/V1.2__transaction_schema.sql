-- V1.2__transaction_schema.sql
-- 거래 관련 스키마 생성

-- Transaction 테이블 생성
CREATE TABLE IF NOT EXISTS transaction (
    transaction_id VARCHAR(255) PRIMARY KEY,
    source_account_number VARCHAR(255) NOT NULL,
    target_account_number VARCHAR(255) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    transaction_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 제약 조건
    CONSTRAINT ck_different_accounts CHECK (source_account_number != target_account_number)
);

-- 인덱스 생성
CREATE INDEX idx_transaction_source_account ON transaction(source_account_number);
CREATE INDEX idx_transaction_target_account ON transaction(target_account_number);
CREATE INDEX idx_transaction_date ON transaction(transaction_date);

-- 외래 키 제약 조건
ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_source_account
    FOREIGN KEY (source_account_number)
    REFERENCES account(account_number);
    
ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_target_account
    FOREIGN KEY (target_account_number)
    REFERENCES account(account_number);