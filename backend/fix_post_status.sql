-- 기존 post 테이블의 status 컬럼 수정
-- PostgreSQL에서 직접 실행

-- 1. 기존 status 컬럼이 NULL인 경우 기본값으로 'DRAFT' 설정
UPDATE post SET status = 'DRAFT' WHERE status IS NULL;

-- 2. status 컬럼을 NOT NULL로 변경
ALTER TABLE post ALTER COLUMN status SET NOT NULL;

-- 3. view_count 컬럼이 NULL인 경우 기본값으로 0 설정
UPDATE post SET view_count = 0 WHERE view_count IS NULL;

-- 4. view_count 컬럼을 NOT NULL로 변경
ALTER TABLE post ALTER COLUMN view_count SET NOT NULL;

-- 5. 결과 확인
SELECT 'Post 테이블 수정 완료!' as message;
SELECT COUNT(*) as total_posts FROM post;
SELECT status, COUNT(*) as count FROM post GROUP BY status;
