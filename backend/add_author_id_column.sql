-- post 테이블에 author_id 컬럼 추가
-- PostgreSQL에서 직접 실행

-- 1. author_id 컬럼 추가
ALTER TABLE post ADD COLUMN IF NOT EXISTS author_id BIGINT;

-- 2. 기존 게시글들의 author_id를 설정 (admin 사용자 ID로 설정)
UPDATE post SET author_id = (SELECT id FROM users WHERE username = 'admin' LIMIT 1) WHERE author_id IS NULL;

-- 3. author_id 컬럼을 NOT NULL로 설정
ALTER TABLE post ALTER COLUMN author_id SET NOT NULL;

-- 4. 결과 확인
SELECT 'author_id 컬럼 추가 완료!' as message;
SELECT id, title, author, author_id FROM post LIMIT 5;
