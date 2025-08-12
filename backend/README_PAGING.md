# 페이징 처리 기능 사용법

## 개요
Spring Boot 애플리케이션에 페이징 처리 기능이 구현되었습니다. 게시글과 댓글 조회 시 페이징을 적용할 수 있습니다.

## 구현된 기능

### 1. 게시글 페이징 조회
- **URL**: `GET /api/posts`
- **파라미터**:
  - `page`: 페이지 번호 (기본값: 0, 0부터 시작)
  - `size`: 페이지 크기 (기본값: 10)
  - `sortBy`: 정렬 기준 필드 (기본값: createdAt)
  - `sortDirection`: 정렬 방향 (기본값: desc, asc/desc)

**예시**:
```
GET /api/posts?page=0&size=5&sortBy=title&sortDirection=asc
```

### 2. 게시글 검색 (페이징)
- **URL**: `GET /api/posts/search`
- **필수 파라미터**: `keyword` (검색어)
- **선택 파라미터**: `page`, `size`, `sortBy`, `sortDirection`

**예시**:
```
GET /api/posts/search?keyword=스프링&page=0&size=10&sortBy=createdAt&sortDirection=desc
```

### 3. 댓글 페이징 조회
- **URL**: `GET /api/posts/{postId}/comments`
- **파라미터**: `page`, `size`, `sortBy`, `sortDirection`

**예시**:
```
GET /api/posts/1/comments?page=0&size=5&sortBy=createdAt&sortDirection=desc
```

## 응답 형식

페이징이 적용된 API는 다음과 같은 형식으로 응답합니다:

```json
{
  "content": [
    {
      "id": 1,
      "title": "게시글 제목",
      "content": "게시글 내용",
      "author": "작성자",
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 25,
  "totalPages": 3,
  "hasNext": true,
  "hasPrevious": false
}
```

## 기존 API 유지

페이징 기능을 추가하면서도 기존 API는 그대로 유지됩니다:

- `GET /api/posts/all` - 페이징 없는 전체 게시글 조회
- `GET /api/posts/search/all` - 페이징 없는 게시글 검색
- `GET /api/posts/{postId}/comments/all` - 페이징 없는 댓글 조회

## 정렬 가능한 필드

### 게시글 (Post)
- `id`: 게시글 ID
- `title`: 제목
- `content`: 내용
- `author`: 작성자
- `createdAt`: 생성일시
- `updatedAt`: 수정일시

### 댓글 (Comment)
- `id`: 댓글 ID
- `author`: 작성자
- `content`: 내용
- `createdAt`: 생성일시

## 사용 예시

### 1. 첫 번째 페이지 조회 (10개씩)
```
GET /api/posts?page=0&size=10
```

### 2. 두 번째 페이지 조회 (5개씩)
```
GET /api/posts?page=1&size=5
```

### 3. 제목으로 오름차순 정렬
```
GET /api/posts?sortBy=title&sortDirection=asc
```

### 4. 특정 게시글의 댓글을 최신순으로 조회
```
GET /api/posts/1/comments?sortBy=createdAt&sortDirection=desc
```

### 5. 키워드 검색 후 페이징
```
GET /api/posts/search?keyword=스프링&page=0&size=5
```
