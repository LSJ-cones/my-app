-- 카테고리 테이블에서 category_type 컬럼 제거
-- PostgreSQL에서 직접 실행

-- 1. category_type 컬럼 제거
ALTER TABLE categories DROP COLUMN IF EXISTS category_type;

-- 2. 결과 확인
SELECT 'Category 테이블 스키마 업데이트 완료!' as message;
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'categories' 
ORDER BY ordinal_position;
