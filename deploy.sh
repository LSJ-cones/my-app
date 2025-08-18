#!/bin/bash

echo "🚀 Lcones Blog EC2 배포 시작..."

# 현재 디렉토리 확인
echo "📁 현재 디렉토리: $(pwd)"

# 기존 컨테이너 정리
echo "📦 기존 컨테이너 정리 중..."
docker-compose -f docker-compose.prod.yml down
docker system prune -f

# 최신 코드 가져오기
echo "📥 최신 코드 가져오기..."
git pull origin main

# 업로드 디렉토리 생성
echo "📁 업로드 디렉토리 생성..."
mkdir -p uploads

# 프로덕션 환경으로 빌드 및 실행
echo "🔨 프로덕션 환경 빌드 중..."
docker-compose -f docker-compose.prod.yml up -d --build

# 배포 완료 확인
echo "✅ 배포 완료!"

# EC2 퍼블릭 IP 가져오기
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)
if [ -z "$PUBLIC_IP" ]; then
    echo "⚠️  퍼블릭 IP를 가져올 수 없습니다. EC2 콘솔에서 확인해주세요."
else
    echo "🌐 접속 주소: http://$PUBLIC_IP"
    echo "📚 API 문서: http://$PUBLIC_IP:8081/swagger-ui.html"
fi

# 컨테이너 상태 확인
echo "📊 컨테이너 상태:"
docker-compose -f docker-compose.prod.yml ps

# 로그 확인
echo "📋 최근 로그 (백엔드):"
docker-compose -f docker-compose.prod.yml logs --tail=10 backend

echo "📋 최근 로그 (프론트엔드):"
docker-compose -f docker-compose.prod.yml logs --tail=10 web

echo "🎉 배포가 완료되었습니다!"
