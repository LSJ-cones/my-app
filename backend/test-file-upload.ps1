# 파일 업로드 테스트 스크립트
# PowerShell에서 실행

# JWT 토큰 설정
$token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc1NDk2NzAwOSwiZXhwIjoxNzU1MDUzNDA5fQ.k3LZ40gCsKBd3zDDxAlavtEcOGNixMig_-Nh8uTHK8H5khk0Xp9RED398ZypGCzitkX01NqXNiGxVEJKY5_yiQ"

# 헤더 설정
$headers = @{
    "Authorization" = "Bearer $token"
}

# API 기본 URL
$baseUrl = "http://localhost:8081"

Write-Host "=== 파일 업로드 테스트 시작 ===" -ForegroundColor Green

# 1. 현재 파일 목록 조회
Write-Host "`n1. 현재 파일 목록 조회..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/files/my-files" -Method GET -Headers $headers
    Write-Host "현재 파일 개수: $($response.Count)" -ForegroundColor Cyan
    if ($response.Count -gt 0) {
        $response | ForEach-Object { Write-Host "  - $($_.originalFileName) (ID: $($_.id))" -ForegroundColor White }
    }
} catch {
    Write-Host "파일 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 2. 단일 파일 업로드
Write-Host "`n2. 단일 파일 업로드 테스트..." -ForegroundColor Yellow
$testFile = "test-files\test.txt"
if (Test-Path $testFile) {
    try {
        $form = @{
            file = Get-Item $testFile
        }
        $response = Invoke-RestMethod -Uri "$baseUrl/api/files/upload" -Method POST -Headers $headers -Form $form
        Write-Host "파일 업로드 성공!" -ForegroundColor Green
        Write-Host "  파일 ID: $($response.id)" -ForegroundColor Cyan
        Write-Host "  원본 파일명: $($response.originalFileName)" -ForegroundColor Cyan
        Write-Host "  저장 파일명: $($response.storedFileName)" -ForegroundColor Cyan
        Write-Host "  파일 크기: $($response.fileSize) bytes" -ForegroundColor Cyan
        
        $uploadedFileId = $response.id
    } catch {
        Write-Host "파일 업로드 실패: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "테스트 파일을 찾을 수 없습니다: $testFile" -ForegroundColor Red
}

# 3. 업로드된 파일 다운로드 테스트
if ($uploadedFileId) {
    Write-Host "`n3. 파일 다운로드 테스트..." -ForegroundColor Yellow
    try {
        $downloadResponse = Invoke-WebRequest -Uri "$baseUrl/api/files/download/$uploadedFileId" -Method GET -Headers $headers
        Write-Host "파일 다운로드 성공!" -ForegroundColor Green
        Write-Host "  다운로드된 파일 크기: $($downloadResponse.Content.Length) bytes" -ForegroundColor Cyan
        
        # 다운로드된 파일 저장
        $downloadPath = "test-files\downloaded_test.txt"
        $downloadResponse.Content | Out-File -FilePath $downloadPath -Encoding UTF8
        Write-Host "  파일이 저장됨: $downloadPath" -ForegroundColor Cyan
    } catch {
        Write-Host "파일 다운로드 실패: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 4. 업로드된 파일 삭제 테스트
if ($uploadedFileId) {
    Write-Host "`n4. 파일 삭제 테스트..." -ForegroundColor Yellow
    try {
        Invoke-RestMethod -Uri "$baseUrl/api/files/$uploadedFileId" -Method DELETE -Headers $headers
        Write-Host "파일 삭제 성공!" -ForegroundColor Green
    } catch {
        Write-Host "파일 삭제 실패: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 5. 최종 파일 목록 확인
Write-Host "`n5. 최종 파일 목록 확인..." -ForegroundColor Yellow
try {
    $finalResponse = Invoke-RestMethod -Uri "$baseUrl/api/files/my-files" -Method GET -Headers $headers
    Write-Host "최종 파일 개수: $($finalResponse.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "파일 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 파일 업로드 테스트 완료 ===" -ForegroundColor Green
