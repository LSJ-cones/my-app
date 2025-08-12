# 알림 목록 확인

$baseUrl = "http://localhost:8081"

Write-Host "=== 알림 목록 확인 ===" -ForegroundColor Green

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

# 알림 목록 조회
try {
    $notificationsResponse = Invoke-RestMethod -Uri "$baseUrl/api/notifications?page=0&size=10" -Method GET -Headers $headers
    Write-Host "알림 목록 조회 성공: $($notificationsResponse.content.length)개 알림" -ForegroundColor Green
    
    $notificationsResponse.content | ForEach-Object {
        Write-Host "  - 알림 ID: $($_.id), 타입: $($_.type), 제목: $($_.title)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "알림 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 읽지 않은 알림 확인
try {
    $unreadResponse = Invoke-RestMethod -Uri "$baseUrl/api/notifications/unread" -Method GET -Headers $headers
    Write-Host "읽지 않은 알림: $($unreadResponse.length)개" -ForegroundColor Yellow
} catch {
    Write-Host "읽지 않은 알림 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "확인 완료" -ForegroundColor Green

