#!/bin/bash

# SSL 인증서 설정 스크립트
# 사용법: ./ssl-setup.sh your-domain.com your-email@example.com

DOMAIN=$1
EMAIL=$2

if [ -z "$DOMAIN" ] || [ -z "$EMAIL" ]; then
    echo "사용법: ./ssl-setup.sh your-domain.com your-email@example.com"
    exit 1
fi

echo "도메인: $DOMAIN"
echo "이메일: $EMAIL"

# certbot 디렉토리 생성
mkdir -p certbot/conf
mkdir -p certbot/www

# nginx 설정 파일에서 도메인 교체
sed -i "s/your-domain.com/$DOMAIN/g" nginx/nginx.conf
sed -i "s/your-email@example.com/$EMAIL/g" docker-compose.yml

# certbot 명령어에서 도메인과 이메일 교체
sed -i "s/tltlgns6@gmail.com/$EMAIL/g" docker-compose.yml
sed -i "s/leecone.blog/$DOMAIN/g" docker-compose.yml

# 초기 nginx 설정 (SSL 없이)
echo "초기 nginx 설정을 적용합니다..."
docker-compose up -d web

# SSL 인증서 발급
echo "SSL 인증서를 발급받습니다..."
docker-compose run --rm certbot certonly --webroot --webroot-path=/var/www/certbot --email $EMAIL --agree-tos --no-eff-email -d $DOMAIN -d www.$DOMAIN

# nginx 재시작 (SSL 설정 적용)
echo "SSL 설정을 적용하여 nginx를 재시작합니다..."
docker-compose restart web

echo "SSL 설정이 완료되었습니다!"
echo "https://$DOMAIN 으로 접속해보세요."
