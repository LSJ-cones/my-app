-- 알림 시스템 스키마 생성

-- 알림 테이블 생성
CREATE TABLE IF NOT EXISTS notification (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    recipient_id BIGINT NOT NULL,
    sender_id BIGINT,
    post_id BIGINT,
    comment_id BIGINT,
    status VARCHAR(20) DEFAULT 'UNREAD',
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_notification_recipient ON notification(recipient_id);
CREATE INDEX IF NOT EXISTS idx_notification_status ON notification(status);
CREATE INDEX IF NOT EXISTS idx_notification_created_at ON notification(created_at);
CREATE INDEX IF NOT EXISTS idx_notification_type ON notification(type);

-- 외래키 제약조건 추가
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_notification_recipient'
    ) THEN
        ALTER TABLE notification 
        ADD CONSTRAINT fk_notification_recipient 
        FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_notification_sender'
    ) THEN
        ALTER TABLE notification 
        ADD CONSTRAINT fk_notification_sender 
        FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL;
    END IF;
END $$;

DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_notification_post'
    ) THEN
        ALTER TABLE notification 
        ADD CONSTRAINT fk_notification_post 
        FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_notification_comment'
    ) THEN
        ALTER TABLE notification 
        ADD CONSTRAINT fk_notification_comment 
        FOREIGN KEY (comment_id) REFERENCES comment(id) ON DELETE CASCADE;
    END IF;
END $$;

-- 알림 타입 체크 제약조건
ALTER TABLE notification 
ADD CONSTRAINT chk_notification_type 
CHECK (type IN ('COMMENT', 'REPLY', 'LIKE', 'POST_UPDATE', 'SYSTEM'));

-- 알림 상태 체크 제약조건
ALTER TABLE notification 
ADD CONSTRAINT chk_notification_status 
CHECK (status IN ('UNREAD', 'READ'));
