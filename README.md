# 🗡️ Lcones Blog - 귀멸의 칼날 스타일 기술 블로그

> Spring Boot + React + Docker로 구현된 기술 블로그 플랫폼

## ✨ 주요 기능

- 🎨 **귀멸의 칼날 테마**: 다크 그라데이션과 빨간색 액센트
- 🔐 **인증 시스템**: 로그인/로그아웃, 프로필 관리
- 📝 **게시글 CRUD**: 작성, 조회, 수정, 삭제
- 🏷️ **카테고리 필터링**: Java, Spring, JavaScript, React
- 🔍 **게시글 검색**: 키워드 기반 검색
- ❤️ **반응 시스템**: 좋아요/싫어요 기능
- 💬 **댓글 시스템**: 댓글 및 대댓글
- 📎 **파일 업로드**: 이미지, PDF, 문서, 압축파일
- 📱 **반응형 디자인**: 모바일/데스크톱 지원
- ⚡ **애니메이션**: 페이지 전환 효과

## 🛠️ 기술 스택

### Backend
- **Spring Boot 3.x**
- **Spring Data JPA**
- **PostgreSQL**
- **Spring Security**
- **Swagger/OpenAPI**

### Frontend
- **React 18**
- **Tailwind CSS**
- **Lucide React**
- **React Router**
- **React Hot Toast**

### Infrastructure
- **Docker & Docker Compose**
- **Nginx (Reverse Proxy)**
- **PostgreSQL**

## 🚀 빠른 시작

### 로컬 개발 환경

```bash
# 1. 프로젝트 클론
git clone https://github.com/LSJ-cones/my-app.git
cd my-app

# 2. Docker Compose로 실행
docker-compose up -d

# 3. 접속
# 메인 사이트: http://localhost
# API 문서: http://localhost:8081/swagger-ui.html
```

### EC2 배포

```bash
# 1. EC2 인스턴스 준비
sudo yum update -y
sudo yum install -y docker git
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -a -G docker ec2-user

# 2. 프로젝트 클론
git clone https://github.com/LSJ-cones/my-app.git
cd my-app

# 3. 배포 스크립트 실행
chmod +x deploy.sh
./deploy.sh
```

## 📁 프로젝트 구조

```
my-app/
├── backend/                 # Spring Boot 백엔드
│   ├── src/main/java/
│   │   └── com/blog/toy/
│   │       ├── controller/  # REST API 컨트롤러
│   │       ├── service/     # 비즈니스 로직
│   │       ├── repository/  # 데이터 접근 계층
│   │       ├── domain/      # 엔티티 클래스
│   │       └── dto/         # 데이터 전송 객체
│   └── Dockerfile
├── frontend/                # React 프론트엔드
│   ├── src/
│   │   ├── components/      # 재사용 컴포넌트
│   │   ├── pages/          # 페이지 컴포넌트
│   │   ├── contexts/       # React Context
│   │   └── services/       # API 서비스
│   └── Dockerfile
├── nginx/                   # Nginx 설정
│   └── nginx.conf
├── docker-compose.yml       # 개발 환경
├── docker-compose.prod.yml  # 프로덕션 환경
└── deploy.sh               # 배포 스크립트
```

## 🔧 환경 설정

### 개발 환경
- **포트**: 80 (Nginx), 8081 (Spring Boot)
- **데이터베이스**: PostgreSQL (Docker)
- **환경**: `dev`

### 프로덕션 환경
- **포트**: 80 (Nginx), 8081 (Spring Boot)
- **데이터베이스**: PostgreSQL (Docker)
- **환경**: `prod`

## 📚 API 문서

- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8081/v3/api-docs`

## 🎨 디자인 시스템

### 색상 팔레트
- **Primary**: `#dc2626` (빨간색)
- **Background**: `#111827` (다크 그레이)
- **Surface**: `#1f2937` (글래스 효과)
- **Text**: `#f9fafb` (밝은 그레이)

### 컴포넌트
- **Glass Effect**: `glass-dark` 클래스
- **Gradient Text**: `text-gradient` 클래스
- **Demon Slayer Shadow**: `shadow-demon` 클래스

## 🔄 배포 프로세스

1. **코드 변경** → Git 커밋
2. **EC2에서** → `git pull` + `./deploy.sh`
3. **자동 빌드** → Docker 이미지 생성
4. **서비스 재시작** → 새 버전 배포

## 📊 모니터링

```bash
# 컨테이너 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs -f [service-name]

# 리소스 사용량
docker stats
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 👨‍💻 개발자

**Lcones** - [GitHub](https://github.com/LSJ-cones)

---

⭐ 이 프로젝트가 도움이 되었다면 스타를 눌러주세요!
