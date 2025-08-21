# 📋 **프로젝트 전체 정리 및 기술 스택**

## 🎯 **프로젝트 개요**
- **프로젝트명**: 블로그 플랫폼 (Toy Blog)
- **목적**: 계층형 카테고리 시스템을 갖춘 블로그 플랫폼 개발
- **배포 환경**: AWS EC2 (13.158.29.215)
- **개발 환경**: Docker + Docker Compose

---

## 🛠️ **기술 스택**

### **Backend**
- **Framework**: Spring Boot 3.5.4
- **Language**: Java 17
- **Build Tool**: Gradle 8.2.1
- **Database**: PostgreSQL 17.4
- **ORM**: Hibernate 6.6.22
- **Security**: Spring Security + JWT
- **API Documentation**: Swagger/OpenAPI
- **WebSocket**: Spring WebSocket (알림 시스템)

### **Frontend**
- **Framework**: React 18
- **Language**: JavaScript (ES6+)
- **Build Tool**: Create React App (CRA)
- **Styling**: Tailwind CSS
- **State Management**: React Context API
- **HTTP Client**: Axios
- **UI Components**: Lucide React (아이콘)

### **Infrastructure**
- **Containerization**: Docker + Docker Compose
- **Web Server**: Nginx (Reverse Proxy)
- **Cloud Platform**: AWS EC2
- **Database**: PostgreSQL (Docker Container)

### **Development Tools**
- **IDE**: IntelliJ IDEA / VS Code
- **Version Control**: Git
- **Package Manager**: npm (Frontend), Gradle (Backend)

---

## 🔧 **주요 기능 구현**

### **1. 인증 및 보안 시스템**
```java
// JWT 토큰 기반 인증
- 토큰 만료 시간: 30분 (1800000ms)
- 브라우저 종료시 자동 로그아웃 (sessionStorage 사용)
- Spring Security 설정
```

### **2. 계층형 카테고리 시스템**
```sql
-- 대분류 (MAIN)
- 개발 (소프트웨어 개발 관련)
- 인프라 (서버 및 인프라 관련)  
- 데이터 (데이터 분석 및 처리)
- 기타 (기타 기술 관련)

-- 소분류 (SUB)
- Java, Spring Boot, JavaScript, Python
- AWS, Docker, Kubernetes
- Big Data, AI/ML, Database
- Tech Trends, DevOps, Web Development
```

### **3. 게시글 관리 시스템**
```java
// 게시글 상태 관리
public enum PostStatus {
    DRAFT,      // 임시저장
    PUBLISHED,  // 발행
    ARCHIVED    // 보관
}

// 반응 시스템
- 좋아요/싫어요 기능
- 조회수 카운팅
- 댓글 시스템
```

### **4. 알림 시스템**
```javascript
// WebSocket 기반 실시간 알림
- 댓글 알림
- 좋아요 알림
- 시스템 알림
```

---

## 📁 **프로젝트 구조**

```
my-app/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/
│   │   └── com/blog/toy/
│   │       ├── config/         # 설정 클래스들
│   │       ├── controller/     # REST API 컨트롤러
│   │       ├── domain/         # 엔티티 클래스들
│   │       ├── dto/           # 데이터 전송 객체
│   │       ├── repository/    # JPA 리포지토리
│   │       ├── security/      # JWT 보안 설정
│   │       └── service/       # 비즈니스 로직
│   └── src/main/resources/
│       └── application.properties
├── frontend/                   # React 프론트엔드
│   ├── src/
│   │   ├── components/        # 재사용 컴포넌트
│   │   ├── contexts/          # React Context
│   │   ├── pages/            # 페이지 컴포넌트
│   │   ├── services/         # API 서비스
│   │   └── utils/            # 유틸리티 함수
│   └── package.json
├── nginx/                     # Nginx 설정
├── docker-compose.yml         # Docker Compose 설정
└── README.md
```

---

## 🚀 **배포 및 운영**

### **Docker Compose 서비스**
```yaml
services:
  backend:    # Spring Boot 애플리케이션 (포트 8081)
  web:        # Nginx + React (포트 80)
  db:         # PostgreSQL 데이터베이스
```

### **네트워크 구성**
```
Internet → Nginx (80) → React App
                ↓
            Backend API (8081)
                ↓
            PostgreSQL DB
```

### **환경 설정**
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://db:5432/my_app
app.jwt.secret=mySecretKeyForBlogApplicationJWTTokenGenerationAndValidation2024
app.jwt.expiration=1800000
```

---

## 🔄 **최근 주요 작업 내용**

### **1. 데이터 초기화 및 정리**
- ✅ 기존 434개 게시글 → 3개 샘플 게시글로 정리
- ✅ 계층형 카테고리 구조 구현
- ✅ 외래키 제약조건 고려한 데이터 삭제 순서 수정

### **2. 프론트엔드 UI 개선**
- ✅ 계층형 카테고리 선택 UI 구현
  - 대분류 선택 (파란색 강조)
  - 소분류 선택 (빨간색 강조)
- ✅ 파일 업로드 기능 비활성화
- ✅ 세션 관리 개선 (localStorage → sessionStorage)

### **3. 보안 강화**
- ✅ JWT 토큰 만료 시간 단축 (24시간 → 30분)
- ✅ 브라우저 종료시 자동 로그아웃
- ✅ 토큰 만료시 자동 로그아웃 및 리다이렉트

---

## 📊 **데이터베이스 스키마**

### **주요 테이블**
```sql
users           # 사용자 정보
categories      # 계층형 카테고리 (parent_id로 계층 구조)
post           # 게시글
comments       # 댓글
post_reactions # 게시글 반응 (좋아요/싫어요)
comment_reactions # 댓글 반응
files          # 파일 업로드 (현재 비활성화)
notifications  # 알림 시스템
```

### **관계 구조**
```
User (1) ←→ (N) Post
Post (1) ←→ (N) Comment
Post (1) ←→ (N) PostReaction
Category (1) ←→ (N) Category (parent-child)
```

---

## 🎨 **UI/UX 특징**

### **디자인 시스템**
- **색상**: 다크 테마 기반
- **컴포넌트**: Glass morphism 효과
- **반응형**: 모바일/데스크톱 대응
- **아이콘**: Lucide React 아이콘 라이브러리

### **사용자 경험**
- **직관적인 카테고리 선택**: 2단계 선택 (대분류 → 소분류)
- **실시간 알림**: WebSocket 기반 푸시 알림
- **반응형 디자인**: 모든 디바이스에서 최적화된 경험

---

## 🔧 **개발 환경 설정**

### **로컬 개발**
```bash
# 프로젝트 클론
git clone [repository-url]
cd my-app

# Docker Compose로 실행
docker compose up -d --build

# 접속
http://localhost
```

### **서버 배포**
```bash
# 서버 접속
ssh -i key.pem ubuntu@13.158.29.215

# 애플리케이션 실행
docker compose up -d --build

# 접속
http://13.158.29.215
```

---

## ⚡ **성능 최적화**

### **백엔드 최적화**
- JPA N+1 쿼리 방지
- HikariCP 커넥션 풀 사용
- JWT 토큰 캐싱

### **프론트엔드 최적화**
- React.memo를 통한 불필요한 리렌더링 방지
- Axios 인터셉터를 통한 토큰 관리
- Tailwind CSS를 통한 최적화된 CSS 번들

---

## 🔮 **향후 개선 계획**

### **기능 개선**
- [ ] 파일 업로드 기능 재활성화
- [ ] 검색 기능 고도화
- [ ] 태그 시스템 구현
- [ ] 사용자 프로필 관리

### **기술 개선**
- [ ] Redis 캐싱 도입
- [ ] Elasticsearch 검색 엔진 연동
- [ ] CI/CD 파이프라인 구축
- [ ] 모니터링 시스템 구축

---

## 📝 **발표용 핵심 포인트**

### **기술적 특징**
1. **계층형 카테고리 시스템**: 직관적인 2단계 선택 UI
2. **실시간 알림**: WebSocket 기반 푸시 알림
3. **보안 강화**: JWT + 세션 관리 최적화
4. **컨테이너화**: Docker를 통한 일관된 배포 환경

### **비즈니스 가치**
1. **사용자 경험**: 직관적이고 반응형인 UI/UX
2. **확장성**: 마이크로서비스 아키텍처 준비
3. **유지보수성**: 모듈화된 코드 구조
4. **성능**: 최적화된 데이터베이스 쿼리 및 프론트엔드 렌더링

---

## 🎯 **프로젝트 성과**

### **완료된 기능**
- ✅ 사용자 인증 및 권한 관리
- ✅ 계층형 카테고리 시스템
- ✅ 게시글 CRUD 및 반응 시스템
- ✅ 댓글 시스템
- ✅ 실시간 알림 시스템
- ✅ 반응형 웹 디자인
- ✅ Docker 컨테이너화
- ✅ AWS EC2 배포

### **기술적 성과**
- ✅ Spring Boot + React 풀스택 개발
- ✅ JWT 기반 보안 시스템 구현
- ✅ WebSocket 실시간 통신
- ✅ PostgreSQL 계층형 데이터 모델링
- ✅ Docker 기반 CI/CD 파이프라인

---

## 📞 **연락처 및 참고 자료**

### **프로젝트 정보**
- **GitHub Repository**: [repository-url]
- **배포 URL**: http://13.158.29.215
- **개발 기간**: 2024년 8월
- **개발 언어**: Java, JavaScript
- **프레임워크**: Spring Boot, React

### **주요 기술 문서**
- Spring Boot 공식 문서
- React 공식 문서
- Docker 공식 문서
- PostgreSQL 공식 문서

---

*이 문서는 프로젝트의 전체적인 구조와 기술적 특징을 정리한 것입니다.*
*발표나 포트폴리오 작성시 참고하시기 바랍니다.*
