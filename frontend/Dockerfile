# Stage 1 - Build React App
FROM node:22-alpine AS builder

WORKDIR /app

COPY package*.json ./

RUN npm ci

COPY . .

# Build with empty VITE_API_BASE_URL to enable relative path API calls in production
ENV VITE_API_BASE_URL=""
RUN npm run build

# Stage 2 - Serve using Nginx
FROM nginx:alpine

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]