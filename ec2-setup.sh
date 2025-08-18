#!/bin/bash

echo "🚀 EC2 인스턴스 초기 설정 시작..."

# 시스템 업데이트
echo "📦 시스템 업데이트 중..."
sudo yum update -y

# Docker 설치
echo "🐳 Docker 설치 중..."
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -a -G docker ec2-user

# Docker Compose 설치
echo "📋 Docker Compose 설치 중..."
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Git 설치
echo "📚 Git 설치 중..."
sudo yum install -y git

# Java 설치 (백엔드 빌드용)
echo "☕ Java 설치 중..."
sudo yum install -y java-17-amazon-corretto

# Node.js 설치 (프론트엔드 빌드용)
echo "🟢 Node.js 설치 중..."
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo yum install -y nodejs

# 방화벽 설정 (포트 80, 443, 8081 열기)
echo "🔥 방화벽 설정 중..."
sudo yum install -y firewalld
sudo systemctl start firewalld
sudo systemctl enable firewalld
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=443/tcp
sudo firewall-cmd --permanent --add-port=8081/tcp
sudo firewall-cmd --reload

# 보안 그룹 설정 (EC2 콘솔에서도 설정 필요)
echo "🔒 보안 그룹 설정 안내:"
echo "EC2 콘솔에서 다음 포트를 열어주세요:"
echo "- SSH (22): 현재 IP에서만"
echo "- HTTP (80): 모든 IP"
echo "- HTTPS (443): 모든 IP"
echo "- Custom TCP (8081): 모든 IP (API용)"

echo "✅ EC2 초기 설정 완료!"
echo "🔄 시스템을 재시작하거나 새 터미널 세션을 시작하세요."
echo "💡 다음 명령어로 Docker 권한을 확인하세요: docker ps"
