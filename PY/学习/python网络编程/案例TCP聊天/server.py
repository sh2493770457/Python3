import socket

host = ''  # 监听所有网络接口
port = 8888

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host, port))
s.listen(5)
print('服务器处于监听中...\r\n')
sock, addr = s.accept()
print(f'客户端 {addr} 已连接...\r\n')
print('***提示，如果要退出聊天，请输入esc后回车***\r\n')

while True:
    info = sock.recv(1024).decode()
    if info == 'esc':
        break
    if info:
        print('客户端：' + info)
    send_data = input('服务器：')
    sock.send(send_data.encode())
    if send_data == 'esc':
        break

sock.close()
s.close()