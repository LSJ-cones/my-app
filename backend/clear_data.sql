-- 기존 데이터 삭제 (순서 중요)
DELETE FROM post_reactions;
DELETE FROM comment_reactions;
DELETE FROM comment_reports;
DELETE FROM comments;
DELETE FROM post_tags;
DELETE FROM files;
DELETE FROM post;
DELETE FROM tags;
DELETE FROM categories;
DELETE FROM notifications;
DELETE FROM users;

-- 시퀀스 리셋
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE categories_id_seq RESTART WITH 1;
ALTER SEQUENCE tags_id_seq RESTART WITH 1;
ALTER SEQUENCE post_id_seq RESTART WITH 1;
ALTER SEQUENCE comments_id_seq RESTART WITH 1;
ALTER SEQUENCE files_id_seq RESTART WITH 1;
ALTER SEQUENCE notifications_id_seq RESTART WITH 1;
