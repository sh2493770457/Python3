FROM node:20-alpine

# 设置工作目录
WORKDIR /app

# 创建非root用户（先创建，后面使用）
RUN addgroup -g 1001 -S nodejs
RUN adduser -S curlconverter -u 1001

# 复制 package 文件
COPY package*.json ./

# 直接复制本地的 node_modules（如果存在）
COPY node_modules ./node_modules

# 复制源码
COPY . .

# 确保 dist 目录存在并设置正确权限
RUN mkdir -p dist && chown -R curlconverter:nodejs /app

# 切换到非root用户进行编译
USER curlconverter

# 编译 TypeScript 代码
RUN npm run compile

# 暴露端口
EXPOSE 3000

# 设置环境变量
ENV NODE_ENV=production

# 启动应用
CMD ["npm", "run", "api"] 