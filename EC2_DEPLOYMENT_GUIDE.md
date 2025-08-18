# EC2 배포 가이드

## 1. EC2 인스턴스 생성

### 1.1 EC2 인스턴스 사양 권장
- **인스턴스 타입**: t3.medium 이상 (2vCPU, 4GB RAM)
- **OS**: Amazon Linux 2 또는 Ubuntu 20.04 LTS
- **스토리지**: 최소 20GB (SSD 권장)
- **보안 그룹**: 다음 포트 열기
  - SSH (22): 현재 IP에서만
  - HTTP (80): 모든 IP (0.0.0.0/0)
  - HTTPS (443): 모든 IP (0.0.0.0/0)
  - Custom TCP (8081): 모든 IP (0.0.0.0/0) - API용

### 1.2 키 페어 생성
- EC2 콘솔에서 키 페어 생성
- `.pem` 파일을 안전한 곳에 저장

## 2. EC2 인스턴스 초기 설정

### 2.1 SSH 연결
```bash
ssh -i your-key.pem ec2-user@your-ec2-public-ip
```

### 2.2 초기 설정 스크립트 실행
```bash
# 스크립트 다운로드
curl -O https://raw.githubusercontent.com/LSJ-cones/my-app/main/ec2-setup.sh

# 실행 권한 부여
chmod +x ec2-setup.sh

# 스크립트 실행
./ec2-setup.sh
```

### 2.3 시스템 재시작 또는 새 세션
```bash
# 새 터미널 세션 시작
exit
ssh -i your-key.pem ec2-user@your-ec2-public-ip
```

## 3. 애플리케이션 배포

### 3.1 프로젝트 클론
```bash
# 프로젝트 디렉토리로 이동
cd /home/ec2-user

# GitHub에서 프로젝트 클론
git clone https://github.com/LSJ-cones/my-app.git
cd my-app
```

### 3.2 배포 스크립트 실행
```bash
# 실행 권한 부여
chmod +x deploy.sh

# 배포 실행
./deploy.sh
```

## 4. 배포 확인

### 4.1 컨테이너 상태 확인
```bash
docker-compose -f docker-compose.prod.yml ps
```

### 4.2 로그 확인
```bash
# 백엔드 로그
docker-compose -f docker-compose.prod.yml logs backend

# 프론트엔드 로그
docker-compose -f docker-compose.prod.yml logs web
```

### 4.3 접속 테스트
- 웹사이트: `http://your-ec2-public-ip`
- API 문서: `http://your-ec2-public-ip:8081/swagger-ui.html`

## 5. 도메인 연결 (선택사항)

### 5.1 Route 53 설정
1. AWS Route 53에서 도메인 등록 또는 기존 도메인 사용
2. A 레코드 생성: `your-domain.com` → EC2 퍼블릭 IP

### 5.2 SSL 인증서 설정 (권장)
```bash
# Certbot 설치 (Let's Encrypt)
sudo yum install -y certbot python3-certbot-nginx

# SSL 인증서 발급
sudo certbot --nginx -d your-domain.com
```

## 6. 모니터링 및 유지보수

### 6.1 로그 모니터링
```bash
# 실시간 로그 확인
docker-compose -f docker-compose.prod.yml logs -f

# 특정 서비스 로그
docker-compose -f docker-compose.prod.yml logs -f backend
```

### 6.2 컨테이너 재시작
```bash
# 전체 재시작
docker-compose -f docker-compose.prod.yml restart

# 특정 서비스 재시작
docker-compose -f docker-compose.prod.yml restart backend
```

### 6.3 업데이트 배포
```bash
# 최신 코드로 업데이트
git pull origin main
./deploy.sh
```

## 7. 백업 및 복구

### 7.1 데이터 백업
```bash
# 업로드된 파일 백업
tar -czf uploads-backup-$(date +%Y%m%d).tar.gz uploads/

# 데이터베이스 백업 (AWS RDS 사용 중이므로 콘솔에서 스냅샷 생성)
```

### 7.2 복구
```bash
# 파일 복구
tar -xzf uploads-backup-YYYYMMDD.tar.gz
```

## 8. 문제 해결

### 8.1 포트 확인
```bash
# 열린 포트 확인
sudo netstat -tlnp

# 방화벽 상태 확인
sudo firewall-cmd --list-all
```

### 8.2 Docker 상태 확인
```bash
# Docker 서비스 상태
sudo systemctl status docker

# 컨테이너 상태
docker ps -a
```

### 8.3 디스크 공간 확인
```bash
# 디스크 사용량 확인
df -h

# Docker 사용량 확인
docker system df
```

## 9. 보안 고려사항

### 9.1 방화벽 설정
- SSH 포트는 현재 IP에서만 접근 허용
- 불필요한 포트는 닫기

### 9.2 정기 업데이트
```bash
# 시스템 업데이트
sudo yum update -y

# Docker 이미지 정리
docker system prune -f
```

### 9.3 로그 로테이션
- 로그 파일 크기 제한 설정
- 정기적인 로그 정리

## 10. 성능 최적화

### 10.1 리소스 모니터링
```bash
# 시스템 리소스 확인
htop
free -h
df -h
```

### 10.2 Docker 최적화
```bash
# 사용하지 않는 이미지 정리
docker image prune -f

# 볼륨 정리
docker volume prune -f
```

---

## 주의사항

1. **보안**: 프로덕션 환경에서는 강력한 비밀번호와 JWT 시크릿 사용
2. **백업**: 정기적인 데이터 백업 필수
3. **모니터링**: 애플리케이션 상태 지속적 모니터링
4. **업데이트**: 보안 패치 및 기능 업데이트 정기 적용

## 지원

문제가 발생하면 다음을 확인하세요:
1. 로그 파일 확인
2. 네트워크 연결 상태
3. 리소스 사용량
4. 보안 그룹 설정
