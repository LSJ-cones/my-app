-- 댓글 시스템 고도화를 위한 데이터베이스 스키마 업데이트

-- 1. 기존 comment 테이블에 새로운 컬럼 추가
ALTER TABLE comment 
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE',
ADD COLUMN IF NOT EXISTS like_count INTEGER DEFAULT 0,
ADD COLUMN IF NOT EXISTS dislike_count INTEGER DEFAULT 0,
ADD COLUMN IF NOT EXISTS parent_id BIGINT,
ADD COLUMN IF NOT EXISTS user_id BIGINT;

-- 2. 기존 데이터의 updated_at을 created_at과 동일하게 설정
UPDATE comment SET updated_at = created_at WHERE updated_at IS NULL;

-- 3. 기존 댓글의 author를 users 테이블과 연결 (임시로 admin 사용자 연결)
UPDATE comment SET user_id = (SELECT id FROM users WHERE username = 'admin' LIMIT 1) WHERE user_id IS NULL;

-- 4. comment_reactions 테이블 생성
CREATE TABLE IF NOT EXISTS comment_reactions (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    UNIQUE(comment_id, user_id),
    FOREIGN KEY (comment_id) REFERENCES comment(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. comment_reports 테이블 생성
CREATE TABLE IF NOT EXISTS comment_reports (
    id BIGSERIAL PRIMARY KEY,
    reason VARCHAR(20) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    comment_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    FOREIGN KEY (comment_id) REFERENCES comment(id) ON DELETE CASCADE,
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_comment_post_id ON comment(post_id);
CREATE INDEX IF NOT EXISTS idx_comment_parent_id ON comment(parent_id);
CREATE INDEX IF NOT EXISTS idx_comment_user_id ON comment(user_id);
CREATE INDEX IF NOT EXISTS idx_comment_status ON comment(status);
CREATE INDEX IF NOT EXISTS idx_comment_reactions_comment_id ON comment_reactions(comment_id);
CREATE INDEX IF NOT EXISTS idx_comment_reactions_user_id ON comment_reactions(user_id);
CREATE INDEX IF NOT EXISTS idx_comment_reports_comment_id ON comment_reports(comment_id);
CREATE INDEX IF NOT EXISTS idx_comment_reports_reporter_id ON comment_reports(reporter_id);
CREATE INDEX IF NOT EXISTS idx_comment_reports_status ON comment_reports(status);

-- 7. 외래키 제약 조건 추가 (기존 제약 조건이 있는지 확인 후 추가)
DO $$
BEGIN
    -- parent_id 외래키 제약 조건 추가
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_comment_parent' 
        AND table_name = 'comment'
    ) THEN
        ALTER TABLE comment 
        ADD CONSTRAINT fk_comment_parent 
        FOREIGN KEY (parent_id) REFERENCES comment(id) ON DELETE CASCADE;
    END IF;
    
    -- user_id 외래키 제약 조건 추가
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_comment_user' 
        AND table_name = 'comment'
    ) THEN
        ALTER TABLE comment 
        ADD CONSTRAINT fk_comment_user 
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
END $$;

-- 8. 기존 댓글 데이터 상태 업데이트
UPDATE comment SET status = 'ACTIVE' WHERE status IS NULL;
UPDATE comment SET like_count = 0 WHERE like_count IS NULL;
UPDATE comment SET dislike_count = 0 WHERE dislike_count IS NULL;

-- 9. 테이블 정보 확인
SELECT 'comment' as table_name, COUNT(*) as row_count FROM comment
UNION ALL
SELECT 'comment_reactions' as table_name, COUNT(*) as row_count FROM comment_reactions
UNION ALL
SELECT 'comment_reports' as table_name, COUNT(*) as row_count FROM comment_reports;
