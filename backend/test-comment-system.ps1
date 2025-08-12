# 댓글 시스템 테스트 스크립트

$baseUrl = "http://localhost:8081"
$token = ""

Write-Host "=== 댓글 시스템 테스트 시작 ===" -ForegroundColor Green

# 1. 로그인하여 토큰 획득
Write-Host "`n1. 로그인 중..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    Write-Host "✅ 로그인 성공: $token" -ForegroundColor Green
} catch {
    Write-Host "❌ 로그인 실패: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 2. 게시글 1의 댓글 목록 조회
Write-Host "`n2. 게시글 1의 댓글 목록 조회..." -ForegroundColor Yellow
try {
    $commentsResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments/post/1" -Method GET -Headers $headers
    Write-Host "✅ 댓글 목록 조회 성공: $($commentsResponse.content.length)개 댓글" -ForegroundColor Green
    $commentsResponse.content | ForEach-Object {
        Write-Host "  - 댓글 ID: $($_.id), 내용: $($_.content.Substring(0, [Math]::Min(30, $_.content.Length)))..." -ForegroundColor Cyan
    }
} catch {
    Write-Host "❌ 댓글 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. 새 댓글 생성
Write-Host "`n3. 새 댓글 생성..." -ForegroundColor Yellow
$newCommentBody = @{
    content = "테스트 댓글입니다. 댓글 시스템이 잘 작동하는지 확인해보겠습니다."
    postId = 1
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments" -Method POST -Body $newCommentBody -Headers $headers
    Write-Host "✅ 댓글 생성 성공: ID=$($createResponse.id)" -ForegroundColor Green
    $newCommentId = $createResponse.id
} catch {
    Write-Host "❌ 댓글 생성 실패: $($_.Exception.Message)" -ForegroundColor Red
    $newCommentId = 1  # 기본값 사용
}

# 4. 대댓글 생성
Write-Host "`n4. 대댓글 생성..." -ForegroundColor Yellow
$replyBody = @{
    content = "이것은 대댓글입니다. 부모 댓글에 대한 답변입니다."
    postId = 1
    parentId = $newCommentId
} | ConvertTo-Json

try {
    $replyResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments" -Method POST -Body $replyBody -Headers $headers
    Write-Host "✅ 대댓글 생성 성공: ID=$($replyResponse.id), 부모ID=$($replyResponse.parentId)" -ForegroundColor Green
} catch {
    Write-Host "❌ 대댓글 생성 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 댓글 좋아요
Write-Host "`n5. 댓글 좋아요..." -ForegroundColor Yellow
$likeBody = @{
    commentId = $newCommentId
    type = "LIKE"
} | ConvertTo-Json

try {
    $likeResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments/$newCommentId/reaction" -Method POST -Body $likeBody -Headers $headers
    Write-Host "✅ 댓글 좋아요 성공: 좋아요 수=$($likeResponse.likeCount)" -ForegroundColor Green
} catch {
    Write-Host "❌ 댓글 좋아요 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. 댓글 싫어요
Write-Host "`n6. 댓글 싫어요..." -ForegroundColor Yellow
$dislikeBody = @{
    commentId = $newCommentId
    type = "DISLIKE"
} | ConvertTo-Json

try {
    $dislikeResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments/$newCommentId/reaction" -Method POST -Body $dislikeBody -Headers $headers
    Write-Host "✅ 댓글 싫어요 성공: 싫어요 수=$($dislikeResponse.dislikeCount)" -ForegroundColor Green
} catch {
    Write-Host "❌ 댓글 싫어요 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 7. 댓글 신고
Write-Host "`n7. 댓글 신고..." -ForegroundColor Yellow
$reportBody = @{
    commentId = $newCommentId
    reason = "SPAM"
    description = "테스트용 신고입니다."
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$baseUrl/api/comments/$newCommentId/report" -Method POST -Body $reportBody -Headers $headers
    Write-Host "✅ 댓글 신고 성공" -ForegroundColor Green
} catch {
    Write-Host "❌ 댓글 신고 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 8. 대댓글 목록 조회
Write-Host "`n8. 대댓글 목록 조회..." -ForegroundColor Yellow
try {
    $repliesResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments/$newCommentId/replies" -Method GET -Headers $headers
    Write-Host "✅ 대댓글 목록 조회 성공: $($repliesResponse.length)개 대댓글" -ForegroundColor Green
    $repliesResponse | ForEach-Object {
        Write-Host "  - 대댓글 ID: $($_.id), 내용: $($_.content.Substring(0, [Math]::Min(30, $_.content.Length)))..." -ForegroundColor Cyan
    }
} catch {
    Write-Host "❌ 대댓글 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 9. 사용자 댓글 목록 조회
Write-Host "`n9. 사용자 댓글 목록 조회..." -ForegroundColor Yellow
try {
    $userCommentsResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments/user/1" -Method GET -Headers $headers
    Write-Host "✅ 사용자 댓글 목록 조회 성공: $($userCommentsResponse.content.length)개 댓글" -ForegroundColor Green
} catch {
    Write-Host "❌ 사용자 댓글 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 10. 댓글 수정
Write-Host "`n10. 댓글 수정..." -ForegroundColor Yellow
$updateBody = @{
    content = "수정된 테스트 댓글입니다. 댓글 수정 기능이 잘 작동합니다."
    postId = 1
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments/$newCommentId" -Method PUT -Body $updateBody -Headers $headers
    Write-Host "✅ 댓글 수정 성공: $($updateResponse.content)" -ForegroundColor Green
} catch {
    Write-Host "❌ 댓글 수정 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 11. 신고된 댓글 목록 조회 (관리자용)
Write-Host "`n11. 신고된 댓글 목록 조회 (관리자용)..." -ForegroundColor Yellow
try {
    $reportedResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments/reported" -Method GET -Headers $headers
    Write-Host "✅ 신고된 댓글 목록 조회 성공: $($reportedResponse.content.length)개 신고된 댓글" -ForegroundColor Green
} catch {
    Write-Host "❌ 신고된 댓글 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 12. 댓글 삭제 (소프트 삭제)
Write-Host "`n12. 댓글 삭제..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/api/comments/$newCommentId" -Method DELETE -Headers $headers
    Write-Host "✅ 댓글 삭제 성공" -ForegroundColor Green
} catch {
    Write-Host "❌ 댓글 삭제 실패: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 댓글 시스템 테스트 완료 ===" -ForegroundColor Green
Write-Host "모든 테스트가 완료되었습니다. Swagger UI에서 추가 테스트를 진행할 수 있습니다." -ForegroundColor Cyan
