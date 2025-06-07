# cmd python server.py运行，client.py运行，输入内容，回车，查看结果
#案例 构建socket网络服务
import socket
#创建服务器服务
server=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind(('localhost',6999))
server.listen(5) #最大连接数
while True:
#conn表示客户端连接，addr表示客户端地址
    conn,addr=server.accept()
    print(conn,addr)
    try:
        data=conn.recv(1024)
        print('recive:',data.decode())
        conn.send(data.upper())
        conn.close()
    except:
        print('关闭了正在占线的链接')
        break