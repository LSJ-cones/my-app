# 간단한 알림 시스템 테스트 스크립트

Write-Host "🔔 알림 시스템 테스트 시작" -ForegroundColor Green

# 서버 상태 확인
Write-Host "1. 서버 상태 확인..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -Method GET -TimeoutSec 5
    Write-Host "✅ 서버가 정상적으로 실행 중입니다." -ForegroundColor Green
} catch {
    Write-Host "❌ 서버가 실행되지 않고 있습니다." -ForegroundColor Red
    Write-Host "서버를 먼저 시작해주세요: .\gradlew.bat bootRun" -ForegroundColor Yellow
    exit 1
}

# 로그인하여 JWT 토큰 획득
Write-Host "2. 로그인하여 JWT 토큰 획득..." -ForegroundColor Yellow
try {
    $loginBody = @{
        username = "admin"
        password = "admin123"
    } | ConvertTo-Json

    $loginResponse = Invoke-WebRequest -Uri "http://localhost:8081/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $loginResult = $loginResponse.Content | ConvertFrom-Json
    $token = $loginResult.token
    
    Write-Host "✅ 로그인 성공, 토큰 획득" -ForegroundColor Green
} catch {
    Write-Host "❌ 로그인 실패: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 알림 목록 조회
Write-Host "3. 알림 목록 조회..." -ForegroundColor Yellow
try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $notificationsResponse = Invoke-WebRequest -Uri "http://localhost:8081/api/notifications?page=0&size=10" -Method GET -Headers $headers
    $notifications = $notificationsResponse.Content | ConvertFrom-Json
    
    Write-Host "✅ 알림 목록 조회 성공" -ForegroundColor Green
    Write-Host "   - 전체 알림 개수: $($notifications.totalElements)" -ForegroundColor Cyan
    Write-Host "   - 현재 페이지 알림 개수: $($notifications.content.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ 알림 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 읽지 않은 알림 개수 조회
Write-Host "4. 읽지 않은 알림 개수 조회..." -ForegroundColor Yellow
try {
    $unreadCountResponse = Invoke-WebRequest -Uri "http://localhost:8081/api/notifications/unread/count" -Method GET -Headers $headers
    $unreadCount = $unreadCountResponse.Content
    
    Write-Host "✅ 읽지 않은 알림 개수 조회 성공" -ForegroundColor Green
    Write-Host "   - 읽지 않은 알림 개수: $unreadCount" -ForegroundColor Cyan
} catch {
    Write-Host "❌ 읽지 않은 알림 개수 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 모든 알림 읽음 처리
Write-Host "5. 모든 알림 읽음 처리..." -ForegroundColor Yellow
try {
    $readAllResponse = Invoke-WebRequest -Uri "http://localhost:8081/api/notifications/read-all" -Method PUT -Headers $headers
    Write-Host "✅ 모든 알림 읽음 처리 성공" -ForegroundColor Green
} catch {
    Write-Host "❌ 모든 알림 읽음 처리 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 읽지 않은 알림 개수 재확인
Write-Host "6. 읽지 않은 알림 개수 재확인..." -ForegroundColor Yellow
try {
    $unreadCountResponse2 = Invoke-WebRequest -Uri "http://localhost:8081/api/notifications/unread/count" -Method GET -Headers $headers
    $unreadCount2 = $unreadCountResponse2.Content
    
    Write-Host "✅ 읽지 않은 알림 개수 재확인 성공" -ForegroundColor Green
    Write-Host "   - 읽지 않은 알림 개수: $unreadCount2" -ForegroundColor Cyan
} catch {
    Write-Host "❌ 읽지 않은 알림 개수 재확인 실패: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "🎉 알림 시스템 테스트 완료!" -ForegroundColor Green

