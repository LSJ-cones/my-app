@echo off
echo === 댓글 생성 기능 테스트 ===

REM 로그인하여 토큰 획득
echo 1. 로그인 중...
curl -X POST http://localhost:8081/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}" > login_response.json

REM 토큰 추출 (간단한 방법)
for /f "tokens=*" %%a in ('type login_response.json ^| findstr "token"') do set token_line=%%a
set token=%token_line:"token":"=%
set token=%token:","=%

echo 로그인 성공: %token:~0,20%...

REM 댓글 생성
echo 2. 댓글 생성 중...
curl -X POST http://localhost:8081/api/comments -H "Authorization: Bearer %token%" -H "Content-Type: application/json" -d "{\"content\":\"알림 테스트용 댓글입니다!\",\"postId\":1}" > comment_response.json

echo 댓글 생성 완료

REM 알림 목록 확인
echo 3. 알림 목록 확인 중...
curl -X GET http://localhost:8081/api/notifications?page=0^&size=5 -H "Authorization: Bearer %token%" > notifications_response.json

echo 알림 목록 확인 완료

echo === 테스트 완료 ===
echo 이제 브라우저에서 http://localhost:8081/notification-test.html 에 접속하여 확인하세요.

pause

