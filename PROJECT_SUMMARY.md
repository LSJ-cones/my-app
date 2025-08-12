# 블로그 애플리케이션 프로젝트 요약

## 📋 프로젝트 개요
Spring Boot 기반의 블로그 애플리케이션으로, JWT 인증, 페이징, 카테고리/태그 시스템, 파일 업로드, 고급 검색, 댓글 시스템 등 다양한 기능을 제공합니다.

## 🏗️ 기술 스택
- **Backend**: Spring Boot 3.5.4, Spring Security, Spring Data JPA
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **API Documentation**: Swagger/OpenAPI 3.0 (SpringDoc 2.8.9)
- **Build Tool**: Gradle
- **Language**: Java 17

## 📁 프로젝트 구조
```
my-app/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/com/blog/toy/
│   │   ├── config/            # 설정 클래스들
│   │   ├── controller/        # REST API 컨트롤러
│   │   ├── domain/           # 엔티티 클래스들
│   │   ├── dto/              # 데이터 전송 객체들
│   │   ├── repository/       # JPA 리포지토리
│   │   ├── service/          # 비즈니스 로직 서비스
│   │   └── security/         # JWT 보안 관련
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── static/           # 정적 파일들
│   └── build.gradle
├── frontend/                  # React 프론트엔드
└── docker-compose.yml        # Docker 구성
```

## 🔧 주요 기능

### 1. 인증 및 보안 (JWT)
- **JWT 토큰 기반 인증**
- **Spring Security 설정**
- **사용자 역할 관리** (ADMIN, USER)
- **보안 필터 체인**

**API 엔드포인트:**
- `POST /api/auth/login` - 로그인
- `POST /api/auth/signup` - 회원가입

### 2. 게시글 관리
- **CRUD 작업**
- **페이징 처리**
- **카테고리/태그 시스템**
- **파일 업로드/다운로드**
- **조회수 관리**
- **게시글 상태 관리** (DRAFT, PUBLISHED, ARCHIVED)

**API 엔드포인트:**
- `GET /api/posts` - 게시글 목록 (페이징)
- `GET /api/posts/{id}` - 게시글 상세
- `POST /api/posts` - 게시글 생성
- `PUT /api/posts/{id}` - 게시글 수정
- `DELETE /api/posts/{id}` - 게시글 삭제

### 3. 카테고리/태그 시스템
- **카테고리 CRUD** (관리자 전용)
- **태그 CRUD** (관리자 전용)
- **카테고리별 게시글 조회**
- **태그별 게시글 조회**

**API 엔드포인트:**
- `GET /api/categories` - 카테고리 목록
- `POST /api/categories` - 카테고리 생성
- `GET /api/tags` - 태그 목록
- `POST /api/tags` - 태그 생성

### 4. 파일 업로드/다운로드
- **단일/다중 파일 업로드**
- **파일 타입 검증**
- **파일 크기 제한** (10MB)
- **지원 파일 형식**: jpg, jpeg, png, gif, pdf, doc, docx, txt, json, xml, csv

**API 엔드포인트:**
- `POST /api/files/upload` - 단일 파일 업로드
- `POST /api/files/upload-multiple` - 다중 파일 업로드
- `GET /api/files/download/{filename}` - 파일 다운로드
- `GET /api/files/post/{postId}` - 게시글별 파일 목록

### 5. 고급 검색 기능
- **키워드 검색**
- **카테고리별 검색**
- **태그별 검색**
- **상태별 검색**
- **정렬 옵션** (최신순, 인기순, 댓글순)
- **복합 검색 조건**

**API 엔드포인트:**
- `POST /api/posts/advanced-search` - 고급 검색
- `GET /api/posts/category/{categoryId}` - 카테고리별 검색
- `GET /api/posts/tags` - 태그별 검색
- `GET /api/posts/status/{status}` - 상태별 검색
- `GET /api/posts/popular` - 인기 게시글
- `GET /api/posts/recent` - 최신 게시글
- `GET /api/posts/most-commented` - 댓글 많은 게시글

### 6. 댓글 시스템 고도화 ⭐ NEW
- **대댓글 기능** (댓글의 댓글)
- **좋아요/싫어요 시스템**
- **댓글 신고 기능**
- **댓글 상태 관리** (ACTIVE, DELETED, REPORTED)
- **사용자별 댓글 관리**
- **관리자 신고 처리**

**API 엔드포인트:**
- `GET /api/comments/post/{postId}` - 게시글 댓글 목록
- `POST /api/comments` - 댓글 생성
- `PUT /api/comments/{id}` - 댓글 수정
- `DELETE /api/comments/{id}` - 댓글 삭제 (소프트 삭제)
- `POST /api/comments/{id}/reaction` - 댓글 좋아요/싫어요
- `POST /api/comments/{id}/report` - 댓글 신고
- `GET /api/comments/{id}/replies` - 대댓글 목록
- `GET /api/comments/user/{userId}` - 사용자 댓글 목록
- `GET /api/comments/reported` - 신고된 댓글 목록 (관리자용)
- `PUT /api/comments/reports/{reportId}` - 신고 처리 (관리자용)

## 🗄️ 데이터베이스 스키마

### 주요 테이블
1. **users** - 사용자 정보
2. **posts** - 게시글
3. **comments** - 댓글 (고도화됨)
4. **comment_reactions** - 댓글 반응 (좋아요/싫어요)
5. **comment_reports** - 댓글 신고
6. **categories** - 카테고리
7. **tags** - 태그
8. **post_tags** - 게시글-태그 연결
9. **files** - 파일 정보

### 댓글 시스템 관련 테이블
```sql
-- 댓글 테이블 (확장됨)
comment (
    id, content, created_at, updated_at, status,
    like_count, dislike_count, parent_id, post_id, user_id, author
)

-- 댓글 반응 테이블
comment_reactions (
    id, type, created_at, comment_id, user_id
)

-- 댓글 신고 테이블
comment_reports (
    id, reason, description, created_at, status, comment_id, reporter_id
)
```

## 🔐 보안 설정

### JWT 설정
```properties
app.jwt.secret=mySecretKeyForBlogApplicationJWTTokenGenerationAndValidation2024
app.jwt.expiration=864000000
```

### Spring Security 설정
- **공개 경로**: `/api/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`, `/api/hello`
- **인증 필요**: 모든 다른 경로
- **관리자 전용**: 카테고리/태그 관리, 신고 처리

## 📊 페이징 처리
- **기본 페이지 크기**: 10
- **정렬 옵션**: createdAt, title, viewCount
- **정렬 방향**: asc, desc

## 🚀 실행 방법

### 1. 데이터베이스 설정
```sql
-- PostgreSQL 연결 후 스키마 업데이트
\i update_comment_schema.sql
\i insert_dummy_comments.sql
```

### 2. 애플리케이션 실행
```bash
cd backend
./gradlew bootRun
```

### 3. API 문서 확인
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs

## 🧪 테스트

### 댓글 시스템 테스트
```powershell
# PowerShell에서 실행
.\test-comment-system.ps1
```

### 검색 기능 테스트
```powershell
# PowerShell에서 실행
.\test-search.ps1
```

## 📈 성능 최적화
- **데이터베이스 인덱스** 설정
- **JPA 페이징** 활용
- **Lazy Loading** 적용
- **캐싱 전략** (향후 Redis 도입 예정)

## 🔄 향후 개선 계획
1. **실시간 알림 시스템** (WebSocket)
2. **이메일 알림**
3. **사용자 프로필 관리**
4. **게시글 좋아요/북마크**
5. **API 버전 관리**
6. **Redis 캐싱**
7. **이미지 썸네일 생성**
8. **SEO 최적화**

## 🐛 해결된 주요 이슈
1. **Swagger Base64 인코딩 문제** - WebConfig 수정으로 해결
2. **JWT 토큰 만료** - 만료 시간 연장 (10일)
3. **파일 업로드 버튼 누락** - Swagger UI 파라미터 설정으로 해결
4. **PowerShell 인코딩 문제** - Windows 환경 특성으로 인식
5. **데이터베이스 스키마 변경** - SQL 스크립트로 해결

## 📝 개발 노트
- **JWT 인증**이 모든 API에 적용됨
- **관리자 권한**이 필요한 기능들은 `@PreAuthorize("hasRole('ADMIN')")` 적용
- **댓글 시스템**은 대댓글, 좋아요/싫어요, 신고 기능을 모두 포함
- **파일 업로드**는 로컬 저장소 사용 (향후 클라우드 스토리지 고려)
- **검색 기능**은 복합 조건을 지원하여 유연한 검색 가능

---

**최종 업데이트**: 2024년 8월 12일
**버전**: 1.0.0
**개발자**: Blog Team
