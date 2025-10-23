# Etapa 1: Build da aplicação Angular
FROM node:20 AS builder
WORKDIR /app

# Copia os arquivos de configuração do npm
COPY package*.json ./

# Instala as dependências
RUN npm install

# Copia o restante do código do projeto
COPY . .

# Gera o build de produção do Angular
RUN npm run build --prod

# Etapa 2: Servir o app com Nginx
FROM nginx:alpine

# Copia os arquivos buildados para o servidor Nginx
COPY --from=builder /app/dist/gerenciador-cartoes-front /usr/share/nginx/html

# Expõe a porta padrão do Nginx
EXPOSE 80

# Comando para iniciar o servidor
CMD ["nginx", "-g", "daemon off;"]