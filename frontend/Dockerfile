# 1단계: 빌드 스테이지
FROM node:18 AS build

WORKDIR /app

COPY package*.json ./

RUN npm install

# ✅ react-scripts에 실행 권한 부여
RUN chmod +x node_modules/.bin/react-scripts

COPY . .

RUN npm run build

# 2단계: 실제 실행 환경
FROM nginx:alpine

COPY --from=build /app/build /usr/share/nginx/html

