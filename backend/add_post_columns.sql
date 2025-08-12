-- Post 테이블에 필요한 컬럼들 추가
-- PostgreSQL에서 직접 실행

-- 1. status 컬럼 추가
ALTER TABLE post ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'DRAFT';

-- 2. view_count 컬럼 추가
ALTER TABLE post ADD COLUMN IF NOT EXISTS view_count INTEGER DEFAULT 0;

-- 3. category_id 컬럼 추가
ALTER TABLE post ADD COLUMN IF NOT EXISTS category_id BIGINT;

-- 4. 기존 데이터의 status를 DRAFT로 설정
UPDATE post SET status = 'DRAFT' WHERE status IS NULL;

-- 5. 기존 데이터의 view_count를 0으로 설정
UPDATE post SET view_count = 0 WHERE view_count IS NULL;

-- 6. status 컬럼을 NOT NULL로 변경
ALTER TABLE post ALTER COLUMN status SET NOT NULL;

-- 7. view_count 컬럼을 NOT NULL로 변경
ALTER TABLE post ALTER COLUMN view_count SET NOT NULL;

-- 8. category_id에 외래키 제약조건 추가 (categories 테이블이 존재할 때)
-- ALTER TABLE post ADD CONSTRAINT fk_post_category FOREIGN KEY (category_id) REFERENCES categories(id);

-- 9. 결과 확인
SELECT 'Post 테이블 컬럼 추가 완료!' as message;
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'post' 
ORDER BY ordinal_position;
