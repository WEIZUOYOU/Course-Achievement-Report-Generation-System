FROM node:18-alpine AS builder
WORKDIR /app
COPY ruoyi-ui/package*.json ./
RUN npm install
COPY ruoyi-ui/ .
RUN npm run build:prod

FROM nginx:stable-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY ruoyi-ui/nginx.conf.template /etc/nginx/templates/default.conf.template
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]