-- 페이징 테스트를 위한 더미 데이터 삽입 스크립트
-- PostgreSQL에 직접 실행

-- 기존 데이터 삭제 (선택사항)
-- DELETE FROM comment;
-- DELETE FROM post;
-- ALTER SEQUENCE post_id_seq RESTART WITH 1;
-- ALTER SEQUENCE comment_id_seq RESTART WITH 1;

-- 게시글 데이터 삽입 (200개)
INSERT INTO post (title, content, author, created_at, updated_at) VALUES
('Spring Boot 시작하기 - Part 1', '이 글은 Spring Boot에 대한 시작하기 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #1번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.', '김개발', NOW(), NOW()),
('JPA 완벽 가이드 - Part 2', '이 글은 JPA에 대한 완벽 가이드 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #2번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.', '이코딩', NOW(), NOW()),
('REST API 실전 예제 - Part 3', '이 글은 REST API에 대한 실전 예제 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #3번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.', '박웹개발', NOW(), NOW()),
('데이터베이스 최적화 기법 - Part 4', '이 글은 데이터베이스에 대한 최적화 기법 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #4번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.', '최DB', NOW(), NOW()),
('Docker 배포 방법 - Part 5', '이 글은 Docker에 대한 배포 방법 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #5번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.', '정DevOps', NOW(), NOW()),
('마이크로서비스 설계 패턴 - Part 6', '이 글은 마이크로서비스에 대한 설계 패턴 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #6번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.', '한아키텍트', NOW(), NOW()),
('React 연동 방법 - Part 7', '이 글은 React에 대한 연동 방법 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #7번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.', '전풀스택', NOW(), NOW()),
('보안 보안 기법 - Part 8', '이 글은 보안에 대한 보안 기법 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #8번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.', '강보안', NOW(), NOW()),
('테스트 테스트 방법 - Part 9', '이 글은 테스트에 대한 테스트 방법 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #9번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.', '윤테스터', NOW(), NOW()),
('클라우드 클라우드 배포 - Part 10', '이 글은 클라우드에 대한 클라우드 배포 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #10번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.', '조클라우드', NOW(), NOW());

-- 나머지 190개 게시글 삽입 (반복 패턴)
DO $$
DECLARE
    i INTEGER;
    topics TEXT[] := ARRAY['Spring Boot', 'JPA', 'REST API', '데이터베이스', 'Docker', '마이크로서비스', 'React', '보안', '테스트', '클라우드', '모니터링', 'CI/CD', '성능', '코드리뷰', '애자일'];
    authors TEXT[] := ARRAY['김개발', '이코딩', '박웹개발', '최DB', '정DevOps', '한아키텍트', '전풀스택', '강보안', '윤테스터', '조클라우드', '임모니터', '백DevOps', '성튜너', '리뷰어', '애자일마스터'];
    actions TEXT[] := ARRAY['시작하기', '완벽 가이드', '실전 예제', '최적화 기법', '배포 방법', '설계 패턴', '연동 방법', '보안 기법', '테스트 방법', '클라우드 배포', '모니터링 시스템', '자동화 파이프라인', '성능 튜닝', '리뷰 가이드', '개발 방법론'];
BEGIN
    FOR i IN 11..200 LOOP
        INSERT INTO post (title, content, author, created_at, updated_at) VALUES (
            topics[1 + (i-1) % array_length(topics, 1)] || ' ' || actions[1 + (i-1) % array_length(actions, 1)] || ' - Part ' || i,
            '이 글은 ' || topics[1 + (i-1) % array_length(topics, 1)] || '에 대한 ' || actions[1 + (i-1) % array_length(actions, 1)] || ' 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #' || i || '번째 게시글입니다. 실제 프로젝트에서는 이런 내용들이 포함될 것입니다. Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 다양한 주제로 구성되어 있습니다.',
            authors[1 + (i-1) % array_length(authors, 1)],
            NOW(),
            NOW()
        );
    END LOOP;
END $$;

-- 댓글 데이터 삽입 (각 게시글당 3-8개)
DO $$
DECLARE
    post_id INTEGER;
    comment_count INTEGER;
    j INTEGER;
    comment_contents TEXT[] := ARRAY['좋은 글이네요!', '도움이 많이 되었습니다.', '추가 설명이 필요해요.', '실습해보겠습니다.', '궁금한 점이 있어요.', '정말 유용한 정보입니다.', '다음 글도 기대합니다.', '코드 예제가 더 있으면 좋겠어요.', '실무에서 바로 적용할 수 있겠네요.', '오타가 있는 것 같습니다.', '더 자세한 설명 부탁드립니다.', '관련 자료도 있나요?', '성능 측정 결과는 어떻게 되나요?', '다른 방법도 있나요?', '실제 프로젝트에 적용해보겠습니다.', '이해가 잘 되네요!', '추천합니다!', '꼭 읽어보세요!', '정말 좋은 내용입니다!'];
    comment_authors TEXT[] := ARRAY['댓글러1', '댓글러2', '댓글러3', '댓글러4', '댓글러5', '댓글러6', '댓글러7', '댓글러8', '댓글러9', '댓글러10', '댓글러11', '댓글러12', '댓글러13', '댓글러14', '댓글러15', '댓글러16', '댓글러17', '댓글러18', '댓글러19', '댓글러20'];
BEGIN
    FOR post_id IN SELECT id FROM post LOOP
        comment_count := 3 + (post_id % 6); -- 3-8개
        FOR j IN 1..comment_count LOOP
            INSERT INTO comment (post_id, content, author, created_at) VALUES (
                post_id,
                comment_contents[1 + (random() * (array_length(comment_contents, 1) - 1))::INTEGER],
                comment_authors[1 + (random() * (array_length(comment_authors, 1) - 1))::INTEGER],
                NOW()
            );
        END LOOP;
    END LOOP;
END $$;

-- 결과 확인
SELECT '게시글 수: ' || COUNT(*) FROM post;
SELECT '댓글 수: ' || COUNT(*) FROM comment;
SELECT '페이징 테스트 준비 완료!' as message;
