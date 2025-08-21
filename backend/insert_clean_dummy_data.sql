-- 사용자 데이터
INSERT INTO users (username, email, password, name, role, is_enabled, created_at, updated_at) VALUES
('admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '관리자', 'ADMIN', true, NOW(), NOW()),
('user1', 'user1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '사용자1', 'USER', true, NOW(), NOW()),
('user2', 'user2@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '사용자2', 'USER', true, NOW(), NOW());

-- 대분류 카테고리
INSERT INTO categories (name, description, category_type, is_active, display_order, created_at, updated_at) VALUES
('개발', '소프트웨어 개발 관련', 'MAIN', true, 1, NOW(), NOW()),
('인프라', '서버 및 인프라 관련', 'MAIN', true, 2, NOW(), NOW()),
('데이터', '데이터 분석 및 처리', 'MAIN', true, 3, NOW(), NOW()),
('기타', '기타 기술 관련', 'MAIN', true, 4, NOW(), NOW());

-- 소분류 카테고리
INSERT INTO categories (name, description, category_type, parent_id, is_active, display_order, created_at, updated_at) VALUES
-- 개발 대분류 하위
('Java', 'Java 개발', 'SUB', 1, true, 1, NOW(), NOW()),
('Spring Boot', 'Spring Boot 프레임워크', 'SUB', 1, true, 2, NOW(), NOW()),
('JavaScript', 'JavaScript 개발', 'SUB', 1, true, 3, NOW(), NOW()),
('Python', 'Python 개발', 'SUB', 1, true, 4, NOW(), NOW()),

-- 인프라 대분류 하위
('AWS', 'Amazon Web Services', 'SUB', 2, true, 1, NOW(), NOW()),
('Docker', '컨테이너 기술', 'SUB', 2, true, 2, NOW(), NOW()),
('Kubernetes', '쿠버네티스', 'SUB', 2, true, 3, NOW(), NOW()),

-- 데이터 대분류 하위
('Big Data', '빅데이터 처리', 'SUB', 3, true, 1, NOW(), NOW()),
('AI/ML', '인공지능 및 머신러닝', 'SUB', 3, true, 2, NOW(), NOW()),
('Database', '데이터베이스', 'SUB', 3, true, 3, NOW(), NOW()),

-- 기타 대분류 하위
('Tech Trends', '기술 트렌드', 'SUB', 4, true, 1, NOW(), NOW()),
('DevOps', '개발 운영', 'SUB', 4, true, 2, NOW(), NOW()),
('Web Development', '웹 개발', 'SUB', 4, true, 3, NOW(), NOW());

-- 태그 데이터
INSERT INTO tags (name, description, is_active, created_at, updated_at) VALUES
('Java', 'Java 관련', true, NOW(), NOW()),
('Spring', 'Spring 프레임워크', true, NOW(), NOW()),
('AWS', 'AWS 서비스', true, NOW(), NOW()),
('Docker', 'Docker 컨테이너', true, NOW(), NOW()),
('Python', 'Python 언어', true, NOW(), NOW()),
('JavaScript', 'JavaScript 언어', true, NOW(), NOW()),
('Database', '데이터베이스', true, NOW(), NOW()),
('AI', '인공지능', true, NOW(), NOW()),
('DevOps', '개발운영', true, NOW(), NOW()),
('Web', '웹 개발', true, NOW(), NOW());

-- 게시글 데이터
INSERT INTO post (title, content, author, author_id, category_id, status, created_at, updated_at) VALUES
('Java 개발 환경 설정 가이드', 'Java 개발을 위한 환경 설정 방법을 안내합니다...', '관리자', 1, 5, 'PUBLISHED', NOW(), NOW()),
('Spring Boot 시작하기', 'Spring Boot 프로젝트 생성 및 기본 설정...', '관리자', 1, 6, 'PUBLISHED', NOW(), NOW()),
('AWS EC2 인스턴스 생성', 'AWS EC2 인스턴스를 생성하고 설정하는 방법...', '사용자1', 2, 9, 'PUBLISHED', NOW(), NOW()),
('Docker 컨테이너 기초', 'Docker 컨테이너의 기본 개념과 사용법...', '사용자2', 3, 10, 'PUBLISHED', NOW(), NOW()),
('Python 데이터 분석', 'Python을 사용한 데이터 분석 기초...', '관리자', 1, 8, 'PUBLISHED', NOW(), NOW()),
('JavaScript ES6+ 문법', 'JavaScript ES6 이상의 새로운 문법들...', '사용자1', 2, 7, 'PUBLISHED', NOW(), NOW()),
('데이터베이스 설계 원칙', '효율적인 데이터베이스 설계 방법...', '사용자2', 3, 11, 'PUBLISHED', NOW(), NOW()),
('AI 머신러닝 입문', '머신러닝의 기본 개념과 실습...', '관리자', 1, 12, 'PUBLISHED', NOW(), NOW());

-- 게시글-태그 연결
INSERT INTO post_tags (post_id, tag_id) VALUES
(1, 1), (1, 2),  -- Java 개발 환경 설정 가이드
(2, 2),          -- Spring Boot 시작하기
(3, 3),          -- AWS EC2 인스턴스 생성
(4, 4),          -- Docker 컨테이너 기초
(5, 5),          -- Python 데이터 분석
(6, 6),          -- JavaScript ES6+ 문법
(7, 7),          -- 데이터베이스 설계 원칙
(8, 8);          -- AI 머신러닝 입문

-- 댓글 데이터
INSERT INTO comments (content, author, author_id, post_id, status, created_at, updated_at) VALUES
('정말 유용한 정보네요!', '사용자1', 2, 1, 'ACTIVE', NOW(), NOW()),
('감사합니다. 도움이 많이 되었어요.', '사용자2', 3, 1, 'ACTIVE', NOW(), NOW()),
('추가 설명이 필요합니다.', '사용자1', 2, 2, 'ACTIVE', NOW(), NOW()),
('좋은 글 감사합니다!', '사용자2', 3, 3, 'ACTIVE', NOW(), NOW());
