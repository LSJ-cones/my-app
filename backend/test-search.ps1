# 검색 기능 테스트 스크립트
# PowerShell에서 실행

$baseUrl = "http://localhost:8081"

Write-Host "=== 블로그 검색 기능 테스트 ===" -ForegroundColor Green

# 1. 로그인하여 JWT 토큰 획득
Write-Host "`n1. 로그인 중..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    Write-Host "로그인 성공! 토큰 획득" -ForegroundColor Green
} catch {
    Write-Host "로그인 실패: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 2. 기본 키워드 검색
Write-Host "`n2. 기본 키워드 검색 테스트..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/search?keyword=스프링&page=0&size=5" -Method GET -Headers $headers
    Write-Host "키워드 검색 결과: $($response.content.Count)개 게시글" -ForegroundColor Green
} catch {
    Write-Host "키워드 검색 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. 고급 검색 테스트
Write-Host "`n3. 고급 검색 테스트..." -ForegroundColor Yellow
$advancedSearchBody = @{
    keyword = "Java"
    categoryId = 1
    tagNames = @("Spring Boot", "Java")
    status = "PUBLISHED"
    page = 0
    size = 5
    sortBy = "createdAt"
    sortDirection = "desc"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/advanced-search" -Method POST -Body $advancedSearchBody -Headers $headers
    Write-Host "고급 검색 결과: $($response.content.Count)개 게시글" -ForegroundColor Green
} catch {
    Write-Host "고급 검색 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. 인기 게시글 조회
Write-Host "`n4. 인기 게시글 조회 테스트..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/popular?page=0&size=5" -Method GET -Headers $headers
    Write-Host "인기 게시글 결과: $($response.content.Count)개 게시글" -ForegroundColor Green
} catch {
    Write-Host "인기 게시글 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 최근 게시글 조회
Write-Host "`n5. 최근 게시글 조회 테스트..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/recent?page=0&size=5" -Method GET -Headers $headers
    Write-Host "최근 게시글 결과: $($response.content.Count)개 게시글" -ForegroundColor Green
} catch {
    Write-Host "최근 게시글 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. 댓글 많은 순 조회
Write-Host "`n6. 댓글 많은 순 조회 테스트..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/most-commented?page=0&size=5" -Method GET -Headers $headers
    Write-Host "댓글 많은 순 결과: $($response.content.Count)개 게시글" -ForegroundColor Green
} catch {
    Write-Host "댓글 많은 순 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 7. 카테고리별 검색
Write-Host "`n7. 카테고리별 검색 테스트..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/category/1?page=0&size=5" -Method GET -Headers $headers
    Write-Host "카테고리별 검색 결과: $($response.content.Count)개 게시글" -ForegroundColor Green
} catch {
    Write-Host "카테고리별 검색 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 8. 태그별 검색
Write-Host "`n8. 태그별 검색 테스트..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/tags?tagNames=Spring Boot&tagNames=Java&page=0&size=5" -Method GET -Headers $headers
    Write-Host "태그별 검색 결과: $($response.content.Count)개 게시글" -ForegroundColor Green
} catch {
    Write-Host "태그별 검색 실패: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 검색 기능 테스트 완료 ===" -ForegroundColor Green
