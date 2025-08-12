# 간단한 댓글 생성 테스트

$baseUrl = "http://localhost:8081"

Write-Host "=== 간단한 댓글 생성 테스트 ===" -ForegroundColor Green

# 로그인
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    Write-Host "로그인 성공" -ForegroundColor Green
} catch {
    Write-Host "로그인 실패: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 댓글 생성
$commentBody = @{
    content = "알림 테스트용 댓글입니다!"
    postId = 1
} | ConvertTo-Json

try {
    $commentResponse = Invoke-RestMethod -Uri "$baseUrl/api/comments" -Method POST -Body $commentBody -Headers $headers
    Write-Host "댓글 생성 성공: ID=$($commentResponse.id)" -ForegroundColor Green
} catch {
    Write-Host "댓글 생성 실패: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "테스트 완료" -ForegroundColor Green

