#!/bin/bash

# SSL 인증서 자동 갱신 스크립트
# crontab에 추가하여 자동 실행: 0 12 * * * /path/to/ssl-renew.sh

echo "SSL 인증서 갱신을 시작합니다..."

# 인증서 갱신
docker-compose run --rm certbot renew

# nginx 재시작
docker-compose restart web

echo "SSL 인증서 갱신이 완료되었습니다."
