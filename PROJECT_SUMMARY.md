# 블로그 프로젝트 개발 완료 보고서

## 📋 프로젝트 개요
- **프로젝트명**: MY-APP 블로그 시스템
- **기술 스택**: Spring Boot 3.5.4, PostgreSQL, JWT, Spring Security
- **개발 기간**: 2025년 8월
- **상태**: 기본 기능 구현 완료

---

## 🚀 구현된 주요 기능

### 1. 기본 블로그 시스템 ✅
- **Post 엔티티**: 제목, 내용, 작성자, 생성/수정일시
- **Comment 엔티티**: 댓글 기능 (Post와 1:N 관계)
- **페이징 시스템**: 게시글과 댓글 목록 조회 시 페이징 지원
- **REST API**: CRUD 엔드포인트 구현

### 2. JWT 인증 시스템 ✅
- **User 엔티티**: 사용자 정보 (username, email, password, role)
- **Spring Security**: 인증/인가 시스템
- **JWT 토큰**: 로그인/회원가입, 토큰 기반 인증
- **Role 기반 권한**: ADMIN, USER 역할 구분
- **BCrypt 암호화**: 비밀번호 암호화

### 3. 카테고리/태그 시스템 ✅
- **Category 엔티티**: 게시글 분류 (Spring Boot, Java, Web Development 등)
- **Tag 엔티티**: 게시글 태깅 (Many-to-Many 관계)
- **Post Status**: DRAFT, PUBLISHED, ARCHIVED 상태 관리
- **조회수 기능**: 게시글 조회수 추적

### 4. 파일 업로드 시스템 ✅
- **File 엔티티**: 파일 정보 저장 (원본명, 저장명, 경로, 크기 등)
- **단일/다중 파일 업로드**: MultipartFile 처리
- **파일 다운로드**: Resource 기반 다운로드
- **파일 검증**: 확장자, 크기 제한 (jpg, jpeg, png, gif, pdf, doc, docx, txt, json, xml, csv)
- **로컬 저장소**: `./uploads` 디렉토리에 파일 저장

### 5. Swagger UI ✅
- **OpenAPI 3.0**: API 문서화
- **Authorize 버튼**: JWT 토큰 인증 지원
- **파일 업로드 UI**: Choose File 버튼 지원

### 6. 데이터베이스 ✅
- **PostgreSQL**: 메인 데이터베이스
- **초기 데이터**: 200개 더미 게시글, 3323개 댓글
- **사용자 계정**: admin/admin123, user/user123
- **카테고리**: Spring Boot, Java, Web Development

---

## 📁 프로젝트 구조

```
my-app/
├── backend/
│   ├── src/main/java/com/blog/toy/
│   │   ├── domain/
│   │   │   ├── Post.java          # 게시글 엔티티
│   │   │   ├── Comment.java       # 댓글 엔티티
│   │   │   ├── User.java          # 사용자 엔티티
│   │   │   ├── Category.java      # 카테고리 엔티티
│   │   │   ├── Tag.java           # 태그 엔티티
│   │   │   └── File.java          # 파일 엔티티
│   │   ├── controller/
│   │   │   ├── PostController.java    # 게시글 API
│   │   │   ├── CommentController.java # 댓글 API
│   │   │   ├── AuthController.java    # 인증 API
│   │   │   ├── CategoryController.java # 카테고리 API
│   │   │   ├── TagController.java     # 태그 API
│   │   │   └── FileController.java    # 파일 API
│   │   ├── service/
│   │   │   ├── PostService.java       # 게시글 서비스
│   │   │   ├── CommentService.java    # 댓글 서비스
│   │   │   ├── AuthService.java       # 인증 서비스
│   │   │   ├── CategoryService.java   # 카테고리 서비스
│   │   │   ├── TagService.java        # 태그 서비스
│   │   │   ├── FileService.java       # 파일 서비스
│   │   │   └── CustomUserDetailsService.java # 사용자 상세 서비스
│   │   ├── repository/
│   │   │   ├── PostRepository.java    # 게시글 리포지토리
│   │   │   ├── CommentRepository.java # 댓글 리포지토리
│   │   │   ├── UserRepository.java    # 사용자 리포지토리
│   │   │   ├── CategoryRepository.java # 카테고리 리포지토리
│   │   │   ├── TagRepository.java     # 태그 리포지토리
│   │   │   └── FileRepository.java    # 파일 리포지토리
│   │   ├── dto/
│   │   │   ├── PostRequestDto.java    # 게시글 요청 DTO
│   │   │   ├── PostResponseDto.java   # 게시글 응답 DTO
│   │   │   ├── CommentResponseDto.java # 댓글 응답 DTO
│   │   │   ├── PageRequestDto.java    # 페이징 요청 DTO
│   │   │   ├── PageResponseDto.java   # 페이징 응답 DTO
│   │   │   ├── auth/                  # 인증 관련 DTO
│   │   │   ├── category/              # 카테고리 관련 DTO
│   │   │   ├── tag/                   # 태그 관련 DTO
│   │   │   └── file/                  # 파일 관련 DTO
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java  # JWT 토큰 생성/검증
│   │   │   └── JwtAuthenticationFilter.java # JWT 인증 필터
│   │   └── config/
│   │       ├── SecurityConfig.java    # Spring Security 설정
│   │       ├── SwaggerConfig.java     # Swagger UI 설정
│   │       ├── WebConfig.java         # Web MVC 설정
│   │       └── DataInitializer.java   # 초기 데이터 로딩
│   └── src/main/resources/
│       └── application.properties     # 애플리케이션 설정
├── frontend/                          # React 프론트엔드 (기본 구조)
├── nginx/                             # Nginx 설정
└── docker-compose.yml                 # Docker 구성
```

---

## ⚙️ 주요 설정

### application.properties
```properties
# 서버 포트
server.port=8081

# 데이터베이스
spring.datasource.url=jdbc:postgresql://database-2.cxw0w0g0cyap.ap-northeast-1.rds.amazonaws.com:5432/my_app
spring.datasource.username=postgres
spring.datasource.password=diRlqkq99*

# JPA 설정
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT 설정
app.jwt.secret=mySecretKeyForBlogApplicationJWTTokenGenerationAndValidation2024
app.jwt.expiration=864000000

# 파일 업로드 설정
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.enabled=true
app.file.upload-dir=./uploads
app.file.allowed-extensions=jpg,jpeg,png,gif,pdf,doc,docx,txt,json,xml,csv

# Swagger UI 설정
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.api-docs.enabled=true
```

### build.gradle (주요 의존성)
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
    
    // Swagger/OpenAPI
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9'
    
    // 파일 업로드
    implementation 'commons-io:commons-io:2.11.0'
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    
    // 데이터베이스
    runtimeOnly 'org.postgresql:postgresql'
}
```

---

## 🔌 API 엔드포인트

### 인증 API
- `POST /api/auth/login` - 로그인
- `POST /api/auth/signup` - 회원가입

### 게시글 API
- `GET /api/posts` - 게시글 목록 (페이징)
- `GET /api/posts/{id}` - 게시글 상세
- `POST /api/posts` - 게시글 생성
- `PUT /api/posts/{id}` - 게시글 수정
- `DELETE /api/posts/{id}` - 게시글 삭제
- `GET /api/posts/category/{categoryId}` - 카테고리별 게시글
- `GET /api/posts/tags` - 태그별 게시글
- `GET /api/posts/status/{status}` - 상태별 게시글

### 댓글 API
- `GET /api/posts/{postId}/comments` - 게시글 댓글 목록
- `POST /api/posts/{postId}/comments` - 댓글 작성
- `PUT /api/comments/{commentId}` - 댓글 수정
- `DELETE /api/comments/{commentId}` - 댓글 삭제

### 파일 API
- `POST /api/files/upload` - 단일 파일 업로드
- `POST /api/files/upload/multiple` - 다중 파일 업로드
- `GET /api/files/download/{fileId}` - 파일 다운로드
- `GET /api/files/my-files` - 내 파일 목록
- `GET /api/files/post/{postId}` - 게시글별 파일 목록
- `DELETE /api/files/{fileId}` - 파일 삭제

### 카테고리 API
- `GET /api/categories` - 카테고리 목록
- `POST /api/categories` - 카테고리 생성 (ADMIN)
- `PUT /api/categories/{id}` - 카테고리 수정 (ADMIN)
- `DELETE /api/categories/{id}` - 카테고리 삭제 (ADMIN)

### 태그 API
- `GET /api/tags` - 태그 목록
- `POST /api/tags` - 태그 생성 (ADMIN)
- `PUT /api/tags/{id}` - 태그 수정 (ADMIN)
- `DELETE /api/tags/{id}` - 태그 삭제 (ADMIN)

---

## 🗄️ 데이터베이스 스키마

### 주요 테이블
1. **users** - 사용자 정보
2. **posts** - 게시글
3. **comments** - 댓글
4. **categories** - 카테고리
5. **tags** - 태그
6. **post_tags** - 게시글-태그 관계
7. **files** - 파일 정보

### 초기 데이터
- **사용자**: admin/admin123, user/user123
- **게시글**: 200개 더미 데이터
- **댓글**: 3323개 더미 데이터
- **카테고리**: Spring Boot, Java, Web Development

---

## 🔧 개발 과정에서 해결한 주요 이슈

### 1. Swagger UI 문제
- **문제**: Base64 인코딩으로 인한 JSON 응답 깨짐
- **해결**: WebConfig에서 MappingJackson2HttpMessageConverter 제거

### 2. JWT 토큰 만료
- **문제**: 토큰 만료 시간이 너무 짧음
- **해결**: application.properties에서 expiration을 864000000ms로 증가

### 3. 파일 업로드 권한
- **문제**: 403 Forbidden 에러
- **해결**: JWT 토큰 갱신 및 SecurityConfig 설정 확인

### 4. 파일 형식 검증
- **문제**: JSON 파일 업로드 시 "지원하지 않는 파일 형식" 에러
- **해결**: allowed-extensions에 json, xml, csv 추가

### 5. Swagger UI Authorize 버튼
- **문제**: Authorize 버튼이 보이지 않음
- **해결**: SwaggerConfig에 SecurityScheme 및 SecurityRequirement 추가

---

## 🎯 현재 상태

### ✅ 완료된 기능
- [x] 기본 CRUD 기능
- [x] 페이징 시스템
- [x] JWT 인증/인가
- [x] 카테고리/태그 시스템
- [x] 파일 업로드/다운로드
- [x] Swagger UI 문서화
- [x] 초기 데이터 로딩

### 🔄 다음 단계 고려사항
- [ ] 검색 기능 (제목, 내용, 태그별 검색)
- [ ] 통계 기능 (조회수, 댓글수 통계)
- [ ] 프론트엔드 React 연동
- [ ] 권한 관리 세분화
- [ ] 이미지 썸네일 생성
- [ ] 파일 압축/압축해제

---

## 🚀 실행 방법

### 1. 애플리케이션 시작
```bash
cd backend
./gradlew bootRun
```

### 2. Swagger UI 접속
```
http://localhost:8081/swagger-ui.html
```

### 3. 로그인 테스트
```bash
# 로그인
curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 토큰으로 API 호출
curl -X GET "http://localhost:8081/api/posts" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

---

## 📝 개발자 정보
- **개발자**: LSJ
- **이메일**: mistake@kakao.com
- **프로젝트**: MY-APP 블로그 시스템
- **버전**: 1.0.0
- **라이선스**: MIT License

---

*이 문서는 2025년 8월 12일 기준으로 작성되었습니다.*
