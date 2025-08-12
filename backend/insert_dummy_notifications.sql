-- 테스트용 더미 알림 데이터 생성

-- admin과 user 계정이 없으면 생성
INSERT INTO users (username, email, password, name, role, created_at, updated_at)
SELECT 'admin', 'admin@example.com', '$2a$10$rAM0QxKqQxKqQxKqQxKqQO', '관리자', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, email, password, name, role, created_at, updated_at)
SELECT 'user', 'user@example.com', '$2a$10$rAM0QxKqQxKqQxKqQxKqQO', '일반사용자', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'user');

-- 더미 알림 데이터 삽입
INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'COMMENT', '새로운 댓글', 'user님이 회원님의 게시글에 댓글을 남겼습니다.', 
       u1.id, u2.id, p.id, c.id, 'UNREAD', CURRENT_TIMESTAMP - INTERVAL '1 hour'
FROM users u1, users u2, post p, comment c
WHERE u1.username = 'admin' AND u2.username = 'user' 
  AND p.id = 1 AND c.id = 1
LIMIT 1;

INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'REPLY', '새로운 대댓글', 'admin님이 회원님의 댓글에 대댓글을 남겼습니다.', 
       u1.id, u2.id, p.id, c.id, 'UNREAD', CURRENT_TIMESTAMP - INTERVAL '30 minutes'
FROM users u1, users u2, post p, comment c
WHERE u1.username = 'user' AND u2.username = 'admin' 
  AND p.id = 1 AND c.id = 2
LIMIT 1;

INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'LIKE', '새로운 좋아요', 'user님이 회원님의 댓글에 좋아요를 눌렀습니다.', 
       u1.id, u2.id, p.id, c.id, 'UNREAD', CURRENT_TIMESTAMP - INTERVAL '15 minutes'
FROM users u1, users u2, post p, comment c
WHERE u1.username = 'admin' AND u2.username = 'user' 
  AND p.id = 1 AND c.id = 1
LIMIT 1;

INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'POST_UPDATE', '게시글 업데이트', '회원님의 게시글이 업데이트되었습니다.', 
       u.id, NULL, p.id, NULL, 'READ', CURRENT_TIMESTAMP - INTERVAL '2 hours'
FROM users u, post p
WHERE u.username = 'admin' AND p.id = 1
LIMIT 1;

INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'SYSTEM', '시스템 알림', '블로그 시스템이 업데이트되었습니다.', 
       u.id, NULL, NULL, NULL, 'UNREAD', CURRENT_TIMESTAMP - INTERVAL '5 minutes'
FROM users u
WHERE u.username = 'admin'
LIMIT 1;

INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'SYSTEM', '시스템 알림', '새로운 기능이 추가되었습니다.', 
       u.id, NULL, NULL, NULL, 'UNREAD', CURRENT_TIMESTAMP - INTERVAL '10 minutes'
FROM users u
WHERE u.username = 'user'
LIMIT 1;
