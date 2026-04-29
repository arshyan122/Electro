FROM node:20-alpine

WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm install --omit=dev --no-audit --no-fund

COPY . .

ENV NODE_ENV=production
ENV PORT=8080
EXPOSE 8080

# On first boot, seed the SQLite DB if it doesn't exist, then start.
CMD ["sh", "-c", "[ -f ./electro.db ] || npm run seed; npm start"]
