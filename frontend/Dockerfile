# Etapa 1: build do Angular
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration production

# Etapa 2: servir com Nginx
FROM nginx:alpine
# ⚠️ Copia a pasta de build Angular para o diretório que o Nginx serve
#COPY --from=build /app/dist/gerenciador-cartoes-front /usr/share/nginx/html
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
# Copia o arquivo de configuração customizado do Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
