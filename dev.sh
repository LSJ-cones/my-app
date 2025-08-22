#!/bin/bash

# 로컬 개발 환경 스크립트

echo "🚀 로컬 개발 환경 시작..."

# 기존 컨테이너 중지 및 제거
echo "🛑 기존 컨테이너 중지..."
docker compose -f docker-compose.yml -f docker-compose.dev.yml down

# 새로운 이미지 빌드
echo "🔨 새로운 이미지 빌드..."
docker compose -f docker-compose.yml -f docker-compose.dev.yml build --no-cache

# 컨테이너 시작
echo "▶️ 컨테이너 시작..."
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# 상태 확인
echo "✅ 개발 환경 시작 완료! 컨테이너 상태 확인..."
docker compose -f docker-compose.yml -f docker-compose.dev.yml ps

echo "🎉 로컬 개발 환경이 시작되었습니다!"
echo "🌐 접속 주소: http://localhost"
