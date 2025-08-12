# 파일 업로드 테스트 가이드

## 📋 테스트 준비사항

### 1. JWT 토큰 획득
먼저 로그인하여 JWT 토큰을 받아야 합니다.

```bash
# 로그인 요청
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 2. Swagger UI 접속
- URL: `http://localhost:8081/swagger-ui/index.html`
- Authorize 버튼 클릭
- JWT 토큰 입력: `Bearer {토큰값}`

## 🧪 테스트 시나리오

### 시나리오 1: 단일 파일 업로드
1. **파일 관리** → **단일 파일 업로드** 선택
2. **file**: `test.txt` 파일 선택
3. **postId**: (선택사항) 게시글 ID 입력
4. **Execute** 클릭

### 시나리오 2: 다중 파일 업로드
1. **파일 관리** → **다중 파일 업로드** 선택
2. **files**: `test.txt`, `sample.json` 파일들 선택
3. **postId**: (선택사항) 게시글 ID 입력
4. **Execute** 클릭

### 시나리오 3: 파일 목록 조회
1. **파일 관리** → **사용자별 파일 목록 조회** 선택
2. **Execute** 클릭

### 시나리오 4: 파일 다운로드
1. **파일 관리** → **파일 다운로드** 선택
2. **fileId**: 업로드된 파일의 ID 입력
3. **Execute** 클릭

### 시나리오 5: 파일 삭제
1. **파일 관리** → **파일 삭제** 선택
2. **fileId**: 삭제할 파일의 ID 입력
3. **Execute** 클릭

## 📁 테스트 파일 목록

- `test.txt`: 텍스트 파일 테스트
- `sample.json`: JSON 파일 테스트
- `README.md`: 이 가이드 파일

## 🔍 예상 결과

### 성공 응답 예시
```json
{
  "id": 1,
  "originalFileName": "test.txt",
  "storedFileName": "1733981234567_abc12345.txt",
  "fileType": "text/plain",
  "fileSize": 245,
  "createdAt": "2024-08-12T11:45:53.145"
}
```

### 에러 응답 예시
```json
{
  "timestamp": "2024-08-12T11:45:53.145",
  "status": 400,
  "error": "Bad Request",
  "message": "허용되지 않는 파일 형식입니다: exe"
}
```

## ⚠️ 주의사항

1. **파일 크기**: 최대 10MB까지 업로드 가능
2. **허용 확장자**: jpg, jpeg, png, gif, pdf, doc, docx, txt
3. **권한**: 파일 삭제는 파일 소유자 또는 ADMIN만 가능
4. **저장 경로**: `./uploads` 디렉토리에 저장됨
