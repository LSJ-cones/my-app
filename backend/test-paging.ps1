# Paging API Test Script (PowerShell)
# Usage: .\test-paging.ps1

$BASE_URL = "http://localhost:8081"

Write-Host "========================================" -ForegroundColor Green
Write-Host "Paging API Test Start" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# 1. Post Paging Test
Write-Host "1. Post Paging Test" -ForegroundColor Yellow
Write-Host "First page (5 items):" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/posts?page=0&size=5" -Method Get
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "Second page (5 items):" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/posts?page=1&size=5" -Method Get
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "Sort by title (ascending):" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/posts?page=0&size=5&sortBy=title&sortDirection=asc" -Method Get
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

# 2. Post Search + Paging Test
Write-Host "2. Post Search + Paging Test" -ForegroundColor Yellow
Write-Host "Search keyword 'Spring' (first page):" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/posts/search?keyword=Spring&page=0&size=3" -Method Get
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "Search keyword 'Development' (first page):" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/posts/search?keyword=Development&page=0&size=3" -Method Get
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

# 3. Comment Paging Test
Write-Host "3. Comment Paging Test" -ForegroundColor Yellow
Write-Host "Comments for post ID 1 (first page):" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/posts/1/comments?page=0&size=5" -Method Get
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "Comments for post ID 1 (second page):" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/posts/1/comments?page=1&size=5" -Method Get
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

# 4. Original API Test (no paging)
Write-Host "4. Original API Test (no paging)" -ForegroundColor Yellow
Write-Host "All posts:" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/posts/all" -Method Get
    Write-Host "Total posts: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "Search keyword 'Spring' (no paging):" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/posts/search/all?keyword=Spring" -Method Get
    Write-Host "Found posts: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "All comments for post ID 1:" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/posts/1/comments/all" -Method Get
    Write-Host "Total comments: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Green
Write-Host "Test Complete" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "For visual testing, open this URL in your browser:" -ForegroundColor Yellow
Write-Host "$BASE_URL/api/test/paging-demo" -ForegroundColor Cyan
Write-Host ""
Read-Host "Press Enter to continue"
