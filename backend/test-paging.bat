@echo off
chcp 65001 >nul
REM Paging API Test Script (Windows)
REM Usage: test-paging.bat

set BASE_URL=http://localhost:8081

echo ========================================
echo Paging API Test Start
echo ========================================
echo.

REM 1. Post Paging Test
echo 1. Post Paging Test
echo First page (5 items):
curl -s "%BASE_URL%/api/posts?page=0&size=5"
echo.
echo.

echo Second page (5 items):
curl -s "%BASE_URL%/api/posts?page=1&size=5"
echo.
echo.

echo Sort by title (ascending):
curl -s "%BASE_URL%/api/posts?page=0&size=5&sortBy=title&sortDirection=asc"
echo.
echo.

REM 2. Post Search + Paging Test
echo 2. Post Search + Paging Test
echo Search keyword 'Spring' (first page):
curl -s "%BASE_URL%/api/posts/search?keyword=Spring&page=0&size=3"
echo.
echo.

echo Search keyword 'Development' (first page):
curl -s "%BASE_URL%/api/posts/search?keyword=Development&page=0&size=3"
echo.
echo.

REM 3. Comment Paging Test
echo 3. Comment Paging Test
echo Comments for post ID 1 (first page):
curl -s "%BASE_URL%/api/posts/1/comments?page=0&size=5"
echo.
echo.

echo Comments for post ID 1 (second page):
curl -s "%BASE_URL%/api/posts/1/comments?page=1&size=5"
echo.
echo.

REM 4. Original API Test (no paging)
echo 4. Original API Test (no paging)
echo All posts:
curl -s "%BASE_URL%/api/posts/all"
echo.

echo Search keyword 'Spring' (no paging):
curl -s "%BASE_URL%/api/posts/search/all?keyword=Spring"
echo.

echo All comments for post ID 1:
curl -s "%BASE_URL%/api/posts/1/comments/all"
echo.
echo.

echo ========================================
echo Test Complete
echo ========================================
echo.
echo For visual testing, open this URL in your browser:
echo %BASE_URL%/api/test/paging-demo
echo.
pause
