-- 댓글 시스템 테스트를 위한 더미 댓글 데이터 생성

-- 1. 기존 댓글 데이터 정리 (테스트용)
-- DELETE FROM comment_reports;
-- DELETE FROM comment_reactions;
-- DELETE FROM comment;

-- 2. 사용자 데이터 확인 및 준비
-- admin과 user 계정이 있는지 확인하고, 없다면 생성
INSERT INTO users (username, email, password, name, role, created_at, updated_at)
SELECT 'admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '관리자', 'ADMIN', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, email, password, name, role, created_at, updated_at)
SELECT 'user', 'user@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '일반사용자', 'USER', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'user');

-- 사용자 ID 확인
SELECT 'admin_id' as user_type, id FROM users WHERE username = 'admin'
UNION ALL
SELECT 'user_id' as user_type, id FROM users WHERE username = 'user';

-- 3. 더미 댓글 데이터 삽입 (동적 user_id 사용)
INSERT INTO comment (content, created_at, updated_at, status, like_count, dislike_count, post_id, user_id, author)
SELECT '정말 유용한 정보네요! 감사합니다.', NOW(), NOW(), 'ACTIVE', 5, 0, 1, u.id, u.username
FROM users u WHERE u.username = 'admin'
UNION ALL
SELECT '이 내용으로 많은 도움이 되었어요.', NOW(), NOW(), 'ACTIVE', 3, 1, 1, u.id, u.username
FROM users u WHERE u.username = 'user'
UNION ALL
SELECT '추가로 궁금한 점이 있는데요...', NOW(), NOW(), 'ACTIVE', 2, 0, 1, u.id, u.username
FROM users u WHERE u.username = 'admin'
UNION ALL
SELECT '좋은 글 감사합니다!', NOW(), NOW(), 'ACTIVE', 4, 0, 1, u.id, u.username
FROM users u WHERE u.username = 'user'

UNION ALL

SELECT '이런 내용을 찾고 있었는데 정말 좋네요.', NOW(), NOW(), 'ACTIVE', 7, 0, 2, u.id, u.username
FROM users u WHERE u.username = 'admin'
UNION ALL
SELECT '실제로 적용해보니 효과가 좋았습니다.', NOW(), NOW(), 'ACTIVE', 6, 1, 2, u.id, u.username
FROM users u WHERE u.username = 'user'
UNION ALL
SELECT '더 자세한 설명이 필요해요.', NOW(), NOW(), 'ACTIVE', 1, 0, 2, u.id, u.username
FROM users u WHERE u.username = 'admin'
UNION ALL
SELECT '이 방법이 최신 트렌드인가요?', NOW(), NOW(), 'ACTIVE', 3, 2, 2, u.id, u.username
FROM users u WHERE u.username = 'user'

UNION ALL

SELECT '정말 깔끔하게 정리되어 있네요.', NOW(), NOW(), 'ACTIVE', 8, 0, 3, u.id, u.username
FROM users u WHERE u.username = 'admin'
UNION ALL
SELECT '이런 방식으로 접근하는 게 좋겠네요.', NOW(), NOW(), 'ACTIVE', 5, 1, 3, u.id, u.username
FROM users u WHERE u.username = 'user'
UNION ALL
SELECT '혹시 다른 방법도 있나요?', NOW(), NOW(), 'ACTIVE', 2, 0, 3, u.id, u.username
FROM users u WHERE u.username = 'admin'
UNION ALL
SELECT '실무에서 바로 적용 가능한 내용이네요.', NOW(), NOW(), 'ACTIVE', 9, 0, 3, u.id, u.username
FROM users u WHERE u.username = 'user'

UNION ALL

SELECT '이런 팁을 알게 되어서 좋네요.', NOW(), NOW(), 'ACTIVE', 4, 0, 4, u.id, u.username
FROM users u WHERE u.username = 'admin'
UNION ALL
SELECT '실제로 테스트해보니 정말 효과적이에요.', NOW(), NOW(), 'ACTIVE', 6, 1, 4, u.id, u.username
FROM users u WHERE u.username = 'user'
UNION ALL
SELECT '더 많은 예시가 있으면 좋겠어요.', NOW(), NOW(), 'ACTIVE', 3, 0, 4, u.id, u.username
FROM users u WHERE u.username = 'admin'
UNION ALL
SELECT '이런 내용을 찾고 있었는데 정말 감사합니다.', NOW(), NOW(), 'ACTIVE', 7, 0, 4, u.id, u.username
FROM users u WHERE u.username = 'user'

UNION ALL

SELECT '정말 유용한 정보네요!', NOW(), NOW(), 'ACTIVE', 5, 0, 5, u.id, u.username
FROM users u WHERE u.username = 'admin'
UNION ALL
SELECT '이 방법으로 문제를 해결할 수 있었어요.', NOW(), NOW(), 'ACTIVE', 8, 1, 5, u.id, u.username
FROM users u WHERE u.username = 'user'
UNION ALL
SELECT '추가 설명이 필요합니다.', NOW(), NOW(), 'ACTIVE', 2, 0, 5, u.id, u.username
FROM users u WHERE u.username = 'admin'
UNION ALL
SELECT '이런 접근 방식이 좋네요.', NOW(), NOW(), 'ACTIVE', 4, 0, 5, u.id, u.username
FROM users u WHERE u.username = 'user';

-- 4. 대댓글 데이터 삽입 (동적 user_id 사용)
-- 첫 번째 댓글에 대한 대댓글들
INSERT INTO comment (content, created_at, updated_at, status, like_count, dislike_count, post_id, user_id, author, parent_id)
SELECT '동감합니다! 정말 도움이 되었어요.', NOW(), NOW(), 'ACTIVE', 2, 0, 1, u.id, u.username, c.id
FROM users u, comment c 
WHERE u.username = 'user' AND c.author = 'admin' AND c.post_id = 1 AND c.parent_id IS NULL
LIMIT 1;

INSERT INTO comment (content, created_at, updated_at, status, like_count, dislike_count, post_id, user_id, author, parent_id)
SELECT '저도 같은 생각이에요.', NOW(), NOW(), 'ACTIVE', 1, 0, 1, u.id, u.username, c.id
FROM users u, comment c 
WHERE u.username = 'admin' AND c.author = 'admin' AND c.post_id = 1 AND c.parent_id IS NULL
LIMIT 1;

INSERT INTO comment (content, created_at, updated_at, status, like_count, dislike_count, post_id, user_id, author, parent_id)
SELECT '추가로 궁금한 점이 있으시면 언제든 말씀해주세요!', NOW(), NOW(), 'ACTIVE', 3, 0, 1, u.id, u.username, c.id
FROM users u, comment c 
WHERE u.username = 'admin' AND c.author = 'admin' AND c.post_id = 1 AND c.parent_id IS NULL
LIMIT 1;

-- 두 번째 댓글에 대한 대댓글들
INSERT INTO comment (content, created_at, updated_at, status, like_count, dislike_count, post_id, user_id, author, parent_id)
SELECT '저도 같은 경험이 있어요.', NOW(), NOW(), 'ACTIVE', 2, 0, 1, u.id, u.username, c.id
FROM users u, comment c 
WHERE u.username = 'admin' AND c.author = 'user' AND c.post_id = 1 AND c.parent_id IS NULL
LIMIT 1;

INSERT INTO comment (content, created_at, updated_at, status, like_count, dislike_count, post_id, user_id, author, parent_id)
SELECT '어떤 부분에서 도움이 되었나요?', NOW(), NOW(), 'ACTIVE', 1, 0, 1, u.id, u.username, c.id
FROM users u, comment c 
WHERE u.username = 'user' AND c.author = 'user' AND c.post_id = 1 AND c.parent_id IS NULL
LIMIT 1;

-- 5. 댓글 반응 데이터 삽입 (동적 ID 사용)
INSERT INTO comment_reactions (type, created_at, comment_id, user_id)
SELECT 'LIKE', NOW(), c.id, u.id
FROM comment c, users u 
WHERE c.author = 'admin' AND c.post_id = 1 AND c.parent_id IS NULL AND u.username = 'user'
LIMIT 1;

INSERT INTO comment_reactions (type, created_at, comment_id, user_id)
SELECT 'LIKE', NOW(), c.id, u.id
FROM comment c, users u 
WHERE c.author = 'admin' AND c.post_id = 1 AND c.parent_id IS NULL AND u.username = 'admin'
LIMIT 1;

INSERT INTO comment_reactions (type, created_at, comment_id, user_id)
SELECT 'LIKE', NOW(), c.id, u.id
FROM comment c, users u 
WHERE c.author = 'user' AND c.post_id = 1 AND c.parent_id IS NULL AND u.username = 'admin'
LIMIT 1;

INSERT INTO comment_reactions (type, created_at, comment_id, user_id)
SELECT 'DISLIKE', NOW(), c.id, u.id
FROM comment c, users u 
WHERE c.author = 'user' AND c.post_id = 1 AND c.parent_id IS NULL AND u.username = 'user'
LIMIT 1;

-- 6. 댓글 신고 데이터 삽입 (테스트용, 동적 ID 사용)
INSERT INTO comment_reports (reason, description, created_at, status, comment_id, reporter_id)
SELECT 'SPAM', '스팸 댓글 같습니다.', NOW(), 'PENDING', c.id, u.id
FROM comment c, users u 
WHERE c.author = 'user' AND c.post_id = 2 AND c.parent_id IS NULL AND u.username = 'admin'
LIMIT 1;

INSERT INTO comment_reports (reason, description, created_at, status, comment_id, reporter_id)
SELECT 'INAPPROPRIATE', '부적절한 내용입니다.', NOW(), 'PENDING', c.id, u.id
FROM comment c, users u 
WHERE c.author = 'admin' AND c.post_id = 3 AND c.parent_id IS NULL AND u.username = 'user'
LIMIT 1;

INSERT INTO comment_reports (reason, description, created_at, status, comment_id, reporter_id)
SELECT 'HARASSMENT', '괴롭힘 성격의 댓글입니다.', NOW(), 'PENDING', c.id, u.id
FROM comment c, users u 
WHERE c.author = 'user' AND c.post_id = 4 AND c.parent_id IS NULL AND u.username = 'admin'
LIMIT 1;

-- 7. 댓글 통계 확인
SELECT 
    '총 댓글 수' as metric,
    COUNT(*) as value
FROM comment
UNION ALL
SELECT 
    '총 대댓글 수' as metric,
    COUNT(*) as value
FROM comment WHERE parent_id IS NOT NULL
UNION ALL
SELECT 
    '총 좋아요 수' as metric,
    SUM(like_count) as value
FROM comment
UNION ALL
SELECT 
    '총 싫어요 수' as metric,
    SUM(dislike_count) as value
FROM comment
UNION ALL
SELECT 
    '총 신고 수' as metric,
    COUNT(*) as value
FROM comment_reports;
