package com.blog.toy.controller;

import com.blog.toy.dto.PageRequestDto;
import com.blog.toy.dto.PageResponseDto;
import com.blog.toy.dto.PostResponseDto;
import com.blog.toy.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@Tag(name = "테스트 도구", description = "페이징 기능 테스트를 위한 웹 UI")
public class TestController {

    private final PostService postService;

    public TestController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "페이징 테스트 UI", description = "페이징 기능을 테스트할 수 있는 웹 인터페이스를 제공합니다.")
    @GetMapping("/paging-demo")
    public String getPagingDemo() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>페이징 테스트</title>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .container { max-width: 1200px; margin: 0 auto; }
                    .test-section { margin: 20px 0; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }
                    .result { background: #f5f5f5; padding: 10px; margin: 10px 0; border-radius: 3px; }
                    button { padding: 10px 15px; margin: 5px; background: #007bff; color: white; border: none; border-radius: 3px; cursor: pointer; }
                    button:hover { background: #0056b3; }
                    input, select { padding: 5px; margin: 5px; }
                    .pagination { margin: 10px 0; }
                    .pagination button { margin: 2px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>페이징 기능 테스트</h1>
                    
                    <div class="test-section">
                        <h2>1. 게시글 페이징 조회</h2>
                        <div>
                            <label>페이지 번호: <input type="number" id="page" value="0" min="0"></label>
                            <label>페이지 크기: <input type="number" id="size" value="5" min="1" max="20"></label>
                            <label>정렬 필드: 
                                <select id="sortBy">
                                    <option value="createdAt">생성일시</option>
                                    <option value="title">제목</option>
                                    <option value="author">작성자</option>
                                </select>
                            </label>
                            <label>정렬 방향: 
                                <select id="sortDirection">
                                    <option value="desc">내림차순</option>
                                    <option value="asc">오름차순</option>
                                </select>
                            </label>
                            <button onclick="loadPosts()">게시글 조회</button>
                        </div>
                        <div id="postsResult" class="result"></div>
                        <div id="postsPagination" class="pagination"></div>
                    </div>
                    
                    <div class="test-section">
                        <h2>2. 게시글 검색 + 페이징</h2>
                        <div>
                            <label>검색어: <input type="text" id="keyword" placeholder="검색할 키워드를 입력하세요"></label>
                            <label>페이지 번호: <input type="number" id="searchPage" value="0" min="0"></label>
                            <label>페이지 크기: <input type="number" id="searchSize" value="5" min="1" max="20"></label>
                            <button onclick="searchPosts()">검색</button>
                        </div>
                        <div id="searchResult" class="result"></div>
                        <div id="searchPagination" class="pagination"></div>
                    </div>
                    
                    <div class="test-section">
                        <h2>3. API 엔드포인트 정보</h2>
                        <div class="result">
                            <h3>게시글 페이징 조회:</h3>
                            <code>GET /api/posts?page=0&size=10&sortBy=createdAt&sortDirection=desc</code>
                            
                            <h3>게시글 검색 + 페이징:</h3>
                            <code>GET /api/posts/search?keyword=스프링&page=0&size=5</code>
                            
                            <h3>댓글 페이징 조회:</h3>
                            <code>GET /api/posts/{postId}/comments?page=0&size=5</code>
                            
                            <h3>기존 API (페이징 없음):</h3>
                            <code>GET /api/posts/all</code><br>
                            <code>GET /api/posts/search/all?keyword=스프링</code><br>
                            <code>GET /api/posts/{postId}/comments/all</code>
                        </div>
                    </div>
                </div>
                
                <script>
                    function loadPosts() {
                        const page = document.getElementById('page').value;
                        const size = document.getElementById('size').value;
                        const sortBy = document.getElementById('sortBy').value;
                        const sortDirection = document.getElementById('sortDirection').value;
                        
                        const url = `/api/posts?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`;
                        
                        fetch(url)
                            .then(response => response.json())
                            .then(data => {
                                displayPosts(data, 'postsResult', 'postsPagination');
                            })
                            .catch(error => {
                                document.getElementById('postsResult').innerHTML = '오류: ' + error.message;
                            });
                    }
                    
                    function searchPosts() {
                        const keyword = document.getElementById('keyword').value;
                        const page = document.getElementById('searchPage').value;
                        const size = document.getElementById('searchSize').value;
                        
                        if (!keyword.trim()) {
                            alert('검색어를 입력해주세요.');
                            return;
                        }
                        
                        const url = `/api/posts/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`;
                        
                        fetch(url)
                            .then(response => response.json())
                            .then(data => {
                                displayPosts(data, 'searchResult', 'searchPagination');
                            })
                            .catch(error => {
                                document.getElementById('searchResult').innerHTML = '오류: ' + error.message;
                            });
                    }
                    
                    function displayPosts(data, resultId, paginationId) {
                        const resultDiv = document.getElementById(resultId);
                        const paginationDiv = document.getElementById(paginationId);
                        
                        let html = '<h3>페이징 정보:</h3>';
                        html += `<p>현재 페이지: ${data.pageNumber + 1} / ${data.totalPages}</p>`;
                        html += `<p>페이지 크기: ${data.pageSize}</p>`;
                        html += `<p>전체 요소 수: ${data.totalElements}</p>`;
                        html += `<p>이전 페이지: ${data.hasPrevious ? '있음' : '없음'}</p>`;
                        html += `<p>다음 페이지: ${data.hasNext ? '있음' : '없음'}</p>`;
                        
                        html += '<h3>게시글 목록:</h3>';
                        if (data.content && data.content.length > 0) {
                            data.content.forEach(post => {
                                html += `<div style="border: 1px solid #ccc; margin: 10px 0; padding: 10px; border-radius: 3px;">`;
                                html += `<h4>${post.title}</h4>`;
                                html += `<p><strong>작성자:</strong> ${post.author}</p>`;
                                html += `<p><strong>내용:</strong> ${post.content}</p>`;
                                html += `<p><strong>생성일시:</strong> ${post.createdAt}</p>`;
                                html += `</div>`;
                            });
                        } else {
                            html += '<p>게시글이 없습니다.</p>';
                        }
                        
                        resultDiv.innerHTML = html;
                        
                        // 페이지네이션 버튼 생성
                        let paginationHtml = '';
                        if (data.totalPages > 1) {
                            paginationHtml += '<h4>페이지 이동:</h4>';
                            
                            // 이전 페이지 버튼
                            if (data.hasPrevious) {
                                paginationHtml += `<button onclick="goToPage(${data.pageNumber - 1}, '${resultId === 'postsResult' ? 'posts' : 'search'}')">이전</button>`;
                            }
                            
                            // 페이지 번호 버튼들
                            for (let i = 0; i < data.totalPages; i++) {
                                const isCurrent = i === data.pageNumber;
                                paginationHtml += `<button onclick="goToPage(${i}, '${resultId === 'postsResult' ? 'posts' : 'search'}')" ${isCurrent ? 'style="background: #28a745;"' : ''}>${i + 1}</button>`;
                            }
                            
                            // 다음 페이지 버튼
                            if (data.hasNext) {
                                paginationHtml += `<button onclick="goToPage(${data.pageNumber + 1}, '${resultId === 'postsResult' ? 'posts' : 'search'}')">다음</button>`;
                            }
                        }
                        
                        paginationDiv.innerHTML = paginationHtml;
                    }
                    
                    function goToPage(page, type) {
                        if (type === 'posts') {
                            document.getElementById('page').value = page;
                            loadPosts();
                        } else if (type === 'search') {
                            document.getElementById('searchPage').value = page;
                            searchPosts();
                        }
                    }
                    
                    // 페이지 로드 시 첫 번째 페이지 조회
                    window.onload = function() {
                        loadPosts();
                    };
                </script>
            </body>
            </html>
            """;
    }
}
