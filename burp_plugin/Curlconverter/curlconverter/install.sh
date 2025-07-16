#!/bin/bash

# CurlConverter 快速部署脚本
# 简化版，适合快速部署

echo " 开始部署 CurlConverter..."

# 检查镜像文件
if [ ! -f "curlconverter.tar" ]; then
    echo "错误：curlconverter.tar 文件不存在"
    exit 1
fi

# 加载镜像
echo "加载 Docker 镜像..."
docker load -i curlconverter.tar

# 停止并删除现有容器
echo "清理现有容器..."
docker stop curlconverter 2>/dev/null || true
docker rm curlconverter 2>/dev/null || true

# 运行新容器
echo "启动新容器..."
docker run -d \
  --name curlconverter \
  --restart unless-stopped \
  -p 3000:3000 \
  curlconverter-curlconverter:latest

# 检查状态
echo "查部署状态..."
sleep 5

if docker ps | grep -q curlconverter; then
    echo "部署成功！"
    
    # 获取服务器IP
    SERVER_IP=$(curl -s ipecho.net/plain 2>/dev/null || echo "localhost")
    
    echo ""
    echo "===================="
    echo "部署信息"
    echo "===================="
    echo "访问地址："
    echo "   http://$SERVER_IP:3000"
    echo ""
    echo "API 接口："
    echo "   健康检查：http://$SERVER_IP:3000/health"
    echo "   支持语言：http://$SERVER_IP:3000/languages"
    echo "   转换接口：http://$SERVER_IP:3000/convert"
    echo ""
    echo "管理命令："
    echo "   查看日志：docker logs curlconverter"
    echo "   重启服务：docker restart curlconverter"
    echo "   停止服务：docker stop curlconverter"
    echo ""
    echo "如果无法访问，请开放 3000 端口："
    echo "   firewall-cmd --permanent --add-port=3000/tcp && firewall-cmd --reload"
    echo "   或 ufw allow 3000"
    echo ""
else
    echo "部署失败，请检查日志："
    docker logs curlconverter
fi 