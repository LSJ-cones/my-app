# SSL 인증서 및 도메인 설정 가이드

## 사전 준비사항

1. **도메인 소유권**: 도메인을 구매하고 DNS 설정 권한이 있어야 합니다.
2. **서버 접근 권한**: EC2 인스턴스에 SSH 접근 권한이 있어야 합니다.
3. **포트 설정**: EC2 보안 그룹에서 80번과 443번 포트가 열려있어야 합니다.

## 단계별 설정 방법

### 1. 도메인 DNS 설정

도메인 제공업체의 DNS 관리 페이지에서 다음 A 레코드를 추가하세요:

```
A    @    13.158.29.215
A    www  13.158.29.215
```

### 2. 스크립트 실행 권한 부여

```bash
chmod +x ssl-setup.sh
chmod +x ssl-renew.sh
```

### 3. SSL 인증서 발급 및 설정

```bash
./ssl-setup.sh your-domain.com your-email@example.com
```

예시:
```bash
./ssl-setup.sh myblog.com admin@myblog.com
```

### 4. 자동 갱신 설정 (선택사항)

SSL 인증서는 90일마다 갱신이 필요합니다. 자동 갱신을 위해 crontab에 추가하세요:

```bash
crontab -e
```

다음 줄을 추가:
```
0 12 * * * /path/to/your/project/ssl-renew.sh
```

## 문제 해결

### 1. 인증서 발급 실패
- 도메인이 올바르게 DNS에 설정되었는지 확인
- 80번 포트가 열려있는지 확인
- 방화벽 설정 확인

### 2. HTTPS 접속 안됨
- 443번 포트가 열려있는지 확인
- nginx 설정 파일 문법 확인
- Docker 컨테이너 로그 확인: `docker-compose logs web`

### 3. 인증서 갱신 실패
- certbot 로그 확인: `docker-compose logs certbot`
- 수동 갱신 시도: `docker-compose run --rm certbot renew --dry-run`

## 보안 설정 추가 (권장)

### 1. HTTP Strict Transport Security (HSTS) 추가

nginx.conf의 HTTPS 서버 블록에 다음을 추가:

```nginx
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
```

### 2. 보안 헤더 추가

```nginx
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
```

## 모니터링

### 1. 인증서 만료일 확인
```bash
docker-compose run --rm certbot certificates
```

### 2. nginx 상태 확인
```bash
docker-compose ps
docker-compose logs web
```

## 참고사항

- Let's Encrypt 인증서는 무료이며 90일마다 갱신이 필요합니다.
- 도메인 변경 시 nginx.conf와 docker-compose.yml의 도메인을 모두 수정해야 합니다.
- 프로덕션 환경에서는 더 강력한 보안 설정을 권장합니다.
