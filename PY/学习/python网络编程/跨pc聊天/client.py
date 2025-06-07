import socket

# 创建一个 TCP/IP 套接字
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# 连接到服务器
server_address = ('127.0.0.1', 65432)
client_socket.connect(server_address)

try:
    # 发送数据
    message = '你好，服务器!'
    client_socket.sendall(message.encode())

    # 接收数据
    data = client_socket.recv(1024)
    print(f'收到来自服务器的消息: {data.decode()}')

finally:
    # 关闭套接字
    client_socket.close()