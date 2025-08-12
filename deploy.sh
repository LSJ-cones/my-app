#!/bin/bash

echo "🚀 Lcones Blog 배포 시작..."

# 기존 컨테이너 정리
echo "📦 기존 컨테이너 정리 중..."
docker-compose down
docker system prune -f

# 최신 코드 가져오기
echo "📥 최신 코드 가져오기..."
git pull origin main

# 프로덕션 환경으로 빌드 및 실행
echo "🔨 프로덕션 환경 빌드 중..."
docker-compose -f docker-compose.prod.yml up -d --build

# 배포 완료 확인
echo "✅ 배포 완료!"
echo "🌐 접속 주소: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)"
echo "📚 API 문서: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4):8081/swagger-ui.html"

# 컨테이너 상태 확인
echo "📊 컨테이너 상태:"
docker-compose -f docker-compose.prod.yml ps
