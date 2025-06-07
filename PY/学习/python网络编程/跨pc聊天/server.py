import socket

# 创建一个 TCP/IP 套接字
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# 绑定套接字到地址和端口
server_address = ('0.0.0.0', 65432)  # '0.0.0.0' 表示接受来自所有网络接口的连接
server_socket.bind(server_address)

# 启动监听
server_socket.listen(1)

print('等待客户端连接...')

# 等待客户端连接
connection, client_address = server_socket.accept()

try:
    print(f'客户端已连接: {client_address}')

    # 接收数据
    while True:
        data = connection.recv(1024)
        if not data:
            break
        print(f'接收到数据: {data.decode()}')

        # 发送数据
        message = '数据已接收'
        connection.sendall(message.encode())

finally:
    # 清理连接
    connection.close()