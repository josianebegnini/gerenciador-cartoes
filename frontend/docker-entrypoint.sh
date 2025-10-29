#!/bin/sh

# Script para substituir variáveis de ambiente no runtime
# Substitui ${APP_GATEWAY_BASE_URL} pelo valor real da variável de ambiente

# Define valor padrão se a variável não estiver definida
APP_GATEWAY_BASE_URL=${APP_GATEWAY_BASE_URL:-http://localhost:8085}

echo "Configurando API URL: $APP_GATEWAY_BASE_URL"

# Encontra todos os arquivos JavaScript no diretório de build
find /usr/share/nginx/html -type f -name "*.js" -exec sed -i "s|\${APP_GATEWAY_BASE_URL}|$APP_GATEWAY_BASE_URL|g" {} \;

# Inicia o Nginx
exec nginx -g 'daemon off;'
