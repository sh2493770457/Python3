#客户端发送一个数据再接收一个
import socket
#声明一个socket对象，同时生成套接字对象
client=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#连接服务器
client.connect(('localhost',6999))
msg='欢迎新同学'
client.send(msg.encode('utf-8'))
data=client.recv(1024)
print('recv:',data.decode())
client.close()