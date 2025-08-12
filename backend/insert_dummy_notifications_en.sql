-- Test dummy notification data

-- Create admin and user accounts if they don't exist
INSERT INTO users (username, email, password, name, role, created_at, updated_at)
SELECT 'admin', 'admin@example.com', '$2a$10$rAM0QxKqQxKqQxKqQxKqQO', 'Administrator', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, email, password, name, role, created_at, updated_at)
SELECT 'user', 'user@example.com', '$2a$10$rAM0QxKqQxKqQxKqQxKqQO', 'Regular User', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'user');

-- Insert dummy notification data
INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'COMMENT', 'New Comment', 'user has left a comment on your post.', 
       u1.id, u2.id, p.id, c.id, 'UNREAD', CURRENT_TIMESTAMP - INTERVAL '1 hour'
FROM users u1, users u2, post p, comment c
WHERE u1.username = 'admin' AND u2.username = 'user' 
  AND p.id = 1 AND c.id = 1
LIMIT 1;

INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'REPLY', 'New Reply', 'admin has replied to your comment.', 
       u1.id, u2.id, p.id, c.id, 'UNREAD', CURRENT_TIMESTAMP - INTERVAL '30 minutes'
FROM users u1, users u2, post p, comment c
WHERE u1.username = 'user' AND u2.username = 'admin' 
  AND p.id = 1 AND c.id = 2
LIMIT 1;

INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'LIKE', 'New Like', 'user liked your comment.', 
       u1.id, u2.id, p.id, c.id, 'UNREAD', CURRENT_TIMESTAMP - INTERVAL '15 minutes'
FROM users u1, users u2, post p, comment c
WHERE u1.username = 'admin' AND u2.username = 'user' 
  AND p.id = 1 AND c.id = 1
LIMIT 1;

INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'POST_UPDATE', 'Post Updated', 'Your post has been updated.', 
       u.id, NULL, p.id, NULL, 'READ', CURRENT_TIMESTAMP - INTERVAL '2 hours'
FROM users u, post p
WHERE u.username = 'admin' AND p.id = 1
LIMIT 1;

INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'SYSTEM', 'System Notification', 'Blog system has been updated.', 
       u.id, NULL, NULL, NULL, 'UNREAD', CURRENT_TIMESTAMP - INTERVAL '5 minutes'
FROM users u
WHERE u.username = 'admin'
LIMIT 1;

INSERT INTO notification (type, title, content, recipient_id, sender_id, post_id, comment_id, status, created_at)
SELECT 'SYSTEM', 'System Notification', 'New features have been added.', 
       u.id, NULL, NULL, NULL, 'UNREAD', CURRENT_TIMESTAMP - INTERVAL '10 minutes'
FROM users u
WHERE u.username = 'user'
LIMIT 1;
