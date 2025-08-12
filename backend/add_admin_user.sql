-- Admin 사용자 추가
-- PostgreSQL에서 직접 실행

-- 1. Admin 사용자 추가 (BCrypt로 암호화된 비밀번호: admin123)
INSERT INTO users (username, email, password, name, role, is_enabled, created_at, updated_at) 
VALUES (
    'admin',
    'admin@example.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    '관리자',
    'ADMIN',
    true,
    NOW(),
    NOW()
) ON CONFLICT (username) DO NOTHING;

-- 2. 일반 사용자 추가 (BCrypt로 암호화된 비밀번호: user123)
INSERT INTO users (username, email, password, name, role, is_enabled, created_at, updated_at) 
VALUES (
    'user',
    'user@example.com',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', -- user123
    '일반사용자',
    'USER',
    true,
    NOW(),
    NOW()
) ON CONFLICT (username) DO NOTHING;

-- 3. 결과 확인
SELECT '사용자 추가 완료!' as message;
SELECT username, email, name, role, is_enabled FROM users;
