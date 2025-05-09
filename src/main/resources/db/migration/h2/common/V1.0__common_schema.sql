-- V1.0__common_schema.sql
-- 공통 스키마 생성 (예: 공통 시퀀스, 유틸리티 테이블 등)

-- 여기서는 공통 스키마를 정의하지 않지만, 실제 프로젝트에서는 필요에 따라 추가
-- 예시: 로그 테이블, 설정 테이블, 다국어 지원 테이블 등

CREATE TABLE IF NOT EXISTS app_version (
    id INT AUTO_INCREMENT PRIMARY KEY,
    version VARCHAR(50) NOT NULL,
    release_date TIMESTAMP NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_version (version, release_date, description)
VALUES ('1.0.0', CURRENT_TIMESTAMP, '초기 버전');
