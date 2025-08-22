#!/bin/bash

# 배포 스크립트 - 프로덕션 환경용

echo "🚀 프로덕션 배포 시작..."

# Git pull
echo "📥 최신 코드 가져오기..."
git pull origin main

# 기존 컨테이너 중지 및 제거
echo "🛑 기존 컨테이너 중지..."
docker compose -f docker-compose.yml -f docker-compose.prod.yml down

# 새로운 이미지 빌드
echo "🔨 새로운 이미지 빌드..."
docker compose -f docker-compose.yml -f docker-compose.prod.yml build --no-cache

# 컨테이너 시작
echo "▶️ 컨테이너 시작..."
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# 상태 확인
echo "✅ 배포 완료! 컨테이너 상태 확인..."
docker compose -f docker-compose.yml -f docker-compose.prod.yml ps

echo "🎉 배포가 완료되었습니다!"
