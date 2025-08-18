-- 카테고리 계층 구조 마이그레이션 스크립트 (UTF-8)

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
('Development', 'Software development related', 1, 'MAIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Infrastructure', 'Server, Cloud, DevOps related', 2, 'MAIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Data', 'Database, Big Data, AI/ML related', 3, 'MAIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Others', 'Other technology related', 4, 'MAIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. 소분류 데이터 삽입
INSERT INTO categories (name, description, display_order, category_type, parent_id, created_at, updated_at) VALUES
-- Development > Java
('Java', 'Java programming language', 1, 'SUB', (SELECT id FROM categories WHERE name = 'Development' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Spring Boot', 'Spring Boot framework', 2, 'SUB', (SELECT id FROM categories WHERE name = 'Development' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Web Development', 'Web development technology', 3, 'SUB', (SELECT id FROM categories WHERE name = 'Development' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('JavaScript', 'JavaScript/TypeScript', 4, 'SUB', (SELECT id FROM categories WHERE name = 'Development' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Python', 'Python programming', 5, 'SUB', (SELECT id FROM categories WHERE name = 'Development' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Infrastructure > AWS
('AWS', 'Amazon Web Services', 1, 'SUB', (SELECT id FROM categories WHERE name = 'Infrastructure' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Docker', 'Container technology', 2, 'SUB', (SELECT id FROM categories WHERE name = 'Infrastructure' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Kubernetes', 'Container orchestration', 3, 'SUB', (SELECT id FROM categories WHERE name = 'Infrastructure' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DevOps', 'DevOps tools and methodology', 4, 'SUB', (SELECT id FROM categories WHERE name = 'Infrastructure' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Data > Database
('Database', 'Database technology', 1, 'SUB', (SELECT id FROM categories WHERE name = 'Data' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Big Data', 'Big data technology', 2, 'SUB', (SELECT id FROM categories WHERE name = 'Data' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AI/ML', 'Artificial Intelligence and Machine Learning', 3, 'SUB', (SELECT id FROM categories WHERE name = 'Data' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Others
('Tech Trends', 'Latest technology trends', 1, 'SUB', (SELECT id FROM categories WHERE name = 'Others' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Development Tools', 'Development tools and IDE', 2, 'SUB', (SELECT id FROM categories WHERE name = 'Others' AND category_type = 'MAIN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
