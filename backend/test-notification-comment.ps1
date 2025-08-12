# 알림 테스트 페이지 댓글 생성 기능 테스트 스크립트

$baseUrl = "http://localhost:8081"
$token = ""

Write-Host "=== 알림 테스트 페이지 댓글 생성 기능 테스트 ===" -ForegroundColor Green

# 1. 로그인하여 토큰 획득
Write-Host "`n1. 로그인 중..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    Write-Host "✅ 로그인 성공: $($token.Substring(0, 20))..." -ForegroundColor Green
} catch {
    Write-Host "❌ 로그인 실패: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 2. 게시글 목록 확인
Write-Host "`n2. 게시글 목록 확인..." -ForegroundColor Yellow
try {
    $postsResponse = Invoke-RestMethod -Uri "$baseUrl/api/posts?page=0&size=5" -Method GET -Headers $headers
    Write-Host "✅ 게시글 목록 조회 성공: $($postsResponse.content.length)개 게시글" -ForegroundColor Green
    $postsResponse.content | ForEach-Object {
        Write-Host "  - 게시글 ID: $($_.id), 제목: $($_.title.Substring(0, [Math]::Min(30, $_.title.Length)))..." -ForegroundColor Cyan
    }
    $testPostId = $postsResponse.content[0].id
} catch {
    Write-Host "❌ 게시글 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
    $testPostId = 1  # 기본값 사용
}

# 3. 일반 댓글 생성 (알림 테스트용)
Write-Host "`n3. 일반 댓글 생성 (알림 테스트용)..." -ForegroundColor Yellow
$commentBody = @{
    content = "알림 테스트를 위한 댓글입니다! 이 댓글로 알림이 생성되는지 확인해보겠습니다."
    postId = $testPostId
} | ConvertTo-Json

try {
    $commentResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments" -Method POST -Body $commentBody -Headers $headers
    Write-Host "✅ 일반 댓글 생성 성공: ID=$($commentResponse.id)" -ForegroundColor Green
    $commentId = $commentResponse.id
} catch {
    Write-Host "❌ 일반 댓글 생성 실패: $($_.Exception.Message)" -ForegroundColor Red
    $commentId = 1  # 기본값 사용
}

# 4. 대댓글 생성 (알림 테스트용)
Write-Host "`n4. 대댓글 생성 (알림 테스트용)..." -ForegroundColor Yellow
$replyBody = @{
    content = "이것은 대댓글입니다. 부모 댓글에 대한 답변으로 알림이 생성됩니다."
    postId = $testPostId
    parentId = $commentId
} | ConvertTo-Json

try {
    $replyResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments" -Method POST -Body $replyBody -Headers $headers
    Write-Host "✅ 대댓글 생성 성공: ID=$($replyResponse.id), 부모ID=$($replyResponse.parentId)" -ForegroundColor Green
} catch {
    Write-Host "❌ 대댓글 생성 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 댓글 좋아요 (알림 테스트용)
Write-Host "`n5. 댓글 좋아요 (알림 테스트용)..." -ForegroundColor Yellow
$likeBody = @{
    commentId = $commentId
    type = "LIKE"
} | ConvertTo-Json

try {
    $likeResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments/$commentId/reaction" -Method POST -Body $likeBody -Headers $headers
    Write-Host "✅ 댓글 좋아요 성공: 좋아요 수=$($likeResponse.likeCount)" -ForegroundColor Green
} catch {
    Write-Host "❌ 댓글 좋아요 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. 알림 목록 확인
Write-Host "`n6. 알림 목록 확인..." -ForegroundColor Yellow
try {
    $notificationsResponse = Invoke-RestMethod -Uri "$baseUrl/api/notifications?page=0&size=10" -Method GET -Headers $headers
    Write-Host "✅ 알림 목록 조회 성공: $($notificationsResponse.content.length)개 알림" -ForegroundColor Green
    
    $notificationsResponse.content | ForEach-Object {
        Write-Host "  - 알림 ID: $($_.id), 타입: $($_.type), 제목: $($_.title)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "❌ 알림 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 7. 읽지 않은 알림 확인
Write-Host "`n7. 읽지 않은 알림 확인..." -ForegroundColor Yellow
try {
    $unreadResponse = Invoke-RestMethod -Uri "$baseUrl/api/notifications/unread" -Method GET -Headers $headers
    Write-Host "✅ 읽지 않은 알림 조회 성공: $($unreadResponse.length)개" -ForegroundColor Green
} catch {
    Write-Host "❌ 읽지 않은 알림 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 테스트 완료 ===" -ForegroundColor Green
Write-Host "이제 브라우저에서 http://localhost:8081/notification-test.html 에 접속하여" -ForegroundColor Cyan
Write-Host "웹소켓 연결 후 실시간 알림을 확인할 수 있습니다." -ForegroundColor Cyan
Write-Host "JWT 토큰: $($token.Substring(0, 20))..." -ForegroundColor Yellow

