-- 카테고리 계층 구조 마이그레이션 스크립트

-- 1. 기존 테이블 백업
CREATE TABLE categories_backup AS SELECT * FROM categories;

-- 2. 기존 테이블 삭제
DROP TABLE categories CASCADE;

-- 3. 새로운 계층 구조 테이블 생성
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    display_order INTEGER,
    is_active BOOLEAN DEFAULT true,
    category_type VARCHAR(10) DEFAULT 'SUB',
    parent_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- 4. 인덱스 생성
CREATE INDEX idx_categories_parent_id ON categories(parent_id);
CREATE INDEX idx_categories_type ON categories(category_type);
CREATE INDEX idx_categories_active ON categories(is_active);

-- 5. 대분류 데이터 삽입
INSERT INTO categories (name, description, display_order, category_type, created_at, updated_at) VALUES
('개발', '소프트웨어 개발 관련', 1, 'MAIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('인프라', '서버, 클라우드, DevOps 관련', 2, 'MAIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('데이터', '데이터베이스, 빅데이터, AI/ML 관련', 3, 'MAIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('기타', '기타 기술 관련', 4, 'MAIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. 소분류 데이터 삽입
INSERT INTO categories (name, description, display_order, category_type, parent_id, created_at, updated_at) VALUES
-- 개발 > Java
('Java', 'Java 프로그래밍 언어', 1, 'SUB', (SELECT id FROM categories WHERE name = '개발' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Spring Boot', 'Spring Boot 프레임워크', 2, 'SUB', (SELECT id FROM categories WHERE name = '개발' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Web Development', '웹 개발 기술', 3, 'SUB', (SELECT id FROM categories WHERE name = '개발' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('JavaScript', 'JavaScript/TypeScript', 4, 'SUB', (SELECT id FROM categories WHERE name = '개발' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Python', 'Python 프로그래밍', 5, 'SUB', (SELECT id FROM categories WHERE name = '개발' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 인프라 > AWS
('AWS', 'Amazon Web Services', 1, 'SUB', (SELECT id FROM categories WHERE name = '인프라' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Docker', '컨테이너 기술', 2, 'SUB', (SELECT id FROM categories WHERE name = '인프라' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Kubernetes', '컨테이너 오케스트레이션', 3, 'SUB', (SELECT id FROM categories WHERE name = '인프라' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DevOps', 'DevOps 도구 및 방법론', 4, 'SUB', (SELECT id FROM categories WHERE name = '인프라' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 데이터 > Database
('Database', '데이터베이스 기술', 1, 'SUB', (SELECT id FROM categories WHERE name = '데이터' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Big Data', '빅데이터 기술', 2, 'SUB', (SELECT id FROM categories WHERE name = '데이터' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AI/ML', '인공지능 및 머신러닝', 3, 'SUB', (SELECT id FROM categories WHERE name = '데이터' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 기타
('기술 트렌드', '최신 기술 동향', 1, 'SUB', (SELECT id FROM categories WHERE name = '기타' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('개발 도구', '개발 도구 및 IDE', 2, 'SUB', (SELECT id FROM categories WHERE name = '기타' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 7. 기존 게시글의 카테고리 매핑 (예시)
-- 실제 데이터에 맞게 수정 필요
UPDATE posts SET category_id = (SELECT id FROM categories WHERE name = 'Java' AND category_type = 'SUB') 
WHERE category_id IN (SELECT id FROM categories_backup WHERE name = 'Java');

UPDATE posts SET category_id = (SELECT id FROM categories WHERE name = 'Spring Boot' AND category_type = 'SUB') 
WHERE category_id IN (SELECT id FROM categories_backup WHERE name = 'Spring Boot');

UPDATE posts SET category_id = (SELECT id FROM categories WHERE name = 'Web Development' AND category_type = 'SUB') 
WHERE category_id IN (SELECT id FROM categories_backup WHERE name = 'Web Development');

UPDATE posts SET category_id = (SELECT id FROM categories WHERE name = 'AWS' AND category_type = 'SUB') 
WHERE category_id IN (SELECT id FROM categories_backup WHERE name = 'AWS');

-- 8. 백업 테이블 삭제 (확인 후)
-- DROP TABLE categories_backup;
