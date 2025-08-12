# 페이징 기능 테스트 환경

## 개요
페이징 기능을 테스트할 수 있는 완전한 환경이 구축되었습니다. 테스트 데이터 자동 생성, 웹 기반 테스트 UI, 그리고 커맨드라인 테스트 스크립트를 제공합니다.

## 테스트 환경 구성

### 1. 자동 테스트 데이터 생성
- **파일**: `src/main/java/com/blog/toy/config/DataInitializer.java`
- **기능**: 애플리케이션 시작 시 자동으로 테스트 데이터 생성
- **생성 데이터**:
  - 15개의 게시글 (다양한 주제)
  - 각 게시글당 15개의 댓글
  - 총 225개의 댓글

### 2. 웹 기반 테스트 UI
- **URL**: `http://localhost:8081/api/test/paging-demo`
- **기능**: 
  - 게시글 페이징 조회 테스트
  - 게시글 검색 + 페이징 테스트
  - 실시간 페이지네이션
  - 정렬 기능 테스트

### 3. 커맨드라인 테스트 스크립트
- **Linux/Mac**: `./test-paging.sh`
- **Windows**: `test-paging.bat`
- **PowerShell**: `test-paging.ps1`

## 테스트 환경 실행 방법

### 1. 애플리케이션 실행
```bash
# backend 디렉토리에서
./gradlew bootRun
```

### 2. 데이터베이스 확인
애플리케이션 시작 시 콘솔에서 다음과 같은 메시지를 확인할 수 있습니다:
```
테스트 데이터를 생성합니다...
테스트 데이터 생성 완료!
생성된 게시글 수: 15
생성된 댓글 수: 225
```

### 3. 웹 브라우저 테스트
브라우저에서 다음 URL 접속:
```
http://localhost:8081/api/test/paging-demo
```

### 4. 커맨드라인 테스트
```bash
# Windows CMD
test-paging.bat

# PowerShell
.\test-paging.ps1

# Linux/Mac
chmod +x test-paging.sh
./test-paging.sh
```

## 테스트 시나리오

### 시나리오 1: 기본 페이징 테스트
1. 웹 UI에서 페이지 크기를 5로 설정
2. "게시글 조회" 버튼 클릭
3. 첫 번째 페이지 결과 확인
4. 페이지네이션 버튼으로 다음 페이지 이동

### 시나리오 2: 정렬 테스트
1. 정렬 필드를 "제목"으로 변경
2. 정렬 방향을 "오름차순"으로 설정
3. "게시글 조회" 버튼 클릭
4. 제목 순으로 정렬된 결과 확인

### 시나리오 3: 검색 + 페이징 테스트
1. 검색어에 "스프링" 입력
2. 페이지 크기를 3으로 설정
3. "검색" 버튼 클릭
4. 검색 결과의 페이징 확인

### 시나리오 4: 댓글 페이징 테스트
```bash
# API 직접 호출
curl "http://localhost:8081/api/posts/1/comments?page=0&size=5"
curl "http://localhost:8081/api/posts/1/comments?page=1&size=5"
```

## API 테스트 예시

### 게시글 페이징 조회
```bash
# 첫 번째 페이지 (5개씩)
curl "http://localhost:8081/api/posts?page=0&size=5"

# 두 번째 페이지 (5개씩)
curl "http://localhost:8081/api/posts?page=1&size=5"

# 제목으로 오름차순 정렬
curl "http://localhost:8081/api/posts?page=0&size=5&sortBy=title&sortDirection=asc"
```

### 게시글 검색 + 페이징
```bash
# 키워드 검색
curl "http://localhost:8081/api/posts/search?keyword=스프링&page=0&size=3"
```

### 댓글 페이징 조회
```bash
# 특정 게시글의 댓글
curl "http://localhost:8081/api/posts/1/comments?page=0&size=5"
```

## 예상 결과

### 페이징 응답 형식
```json
{
  "content": [
    {
      "id": 1,
      "title": "Spring Boot 시작하기",
      "content": "Spring Boot를 처음 시작하는 방법에 대해 알아보겠습니다.",
      "author": "김개발",
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ],
  "pageNumber": 0,
  "pageSize": 5,
  "totalElements": 15,
  "totalPages": 3,
  "hasNext": true,
  "hasPrevious": false
}
```

### 테스트 데이터 정보
- **총 게시글 수**: 15개
- **총 댓글 수**: 225개 (게시글당 15개씩)
- **페이지 크기 5일 때**: 게시글 3페이지, 댓글 45페이지
- **페이지 크기 10일 때**: 게시글 2페이지, 댓글 23페이지

## 문제 해결

### 1. 데이터가 생성되지 않는 경우
- 데이터베이스 연결 확인
- 애플리케이션 로그에서 오류 메시지 확인
- 기존 데이터가 있으면 자동으로 건너뛰므로, 필요시 데이터베이스 초기화

### 2. API 응답이 없는 경우
- 애플리케이션이 정상 실행 중인지 확인
- 포트 8080이 사용 가능한지 확인
- CORS 설정 확인 (필요시)

### 3. 페이징이 작동하지 않는 경우
- PageRequestDto와 PageResponseDto 클래스 확인
- Repository 메서드가 올바르게 정의되었는지 확인
- Service 레이어의 페이징 로직 확인

## 추가 테스트

### 성능 테스트
```bash
# 대량 데이터로 성능 테스트
# DataInitializer에서 더 많은 테스트 데이터 생성 후 테스트
```

### 스트레스 테스트
```bash
# 동시 요청 테스트
for i in {1..10}; do
  curl "http://localhost:8081/api/posts?page=0&size=5" &
done
wait
```

이제 완전한 테스트 환경이 준비되었습니다! 애플리케이션을 실행하고 다양한 방법으로 페이징 기능을 테스트해보세요.
