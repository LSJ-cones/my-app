-- 게시글 반응 테이블 생성
CREATE TABLE IF NOT EXISTS post_reactions (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('LIKE', 'DISLIKE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 게시글 테이블에 좋아요/싫어요 컬럼 추가
ALTER TABLE post ADD COLUMN IF NOT EXISTS like_count INTEGER DEFAULT 0;
ALTER TABLE post ADD COLUMN IF NOT EXISTS dislike_count INTEGER DEFAULT 0;

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_post_reactions_post_id ON post_reactions(post_id);
CREATE INDEX IF NOT EXISTS idx_post_reactions_user_id ON post_reactions(user_id);
CREATE INDEX IF NOT EXISTS idx_post_reactions_type ON post_reactions(type);

-- 기존 게시글의 좋아요/싫어요 수를 0으로 초기화
UPDATE post SET like_count = 0, dislike_count = 0 WHERE like_count IS NULL OR dislike_count IS NULL;

-- 완료 메시지
SELECT '게시글 반응 테이블 생성 완료!' as message;
