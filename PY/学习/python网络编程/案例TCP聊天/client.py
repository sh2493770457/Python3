import socket

s = socket.socket()
host = '127.0.0.1'  # 例如 '192.168.1.5'
port = 8888
s.connect((host, port))
print('连接服务器成功')
print('***提示，如果要退出，请输入esc后回车。\r\n')

while True:
    send_data = input('客户端说：')
    s.send(send_data.encode())
    if send_data == 'esc':
        break
    if send_data != 'esc':
        info = s.recv(1024).decode()
        print('服务器说：' + info)

s.close()

