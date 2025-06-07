import socket
client=socket.socket()
client.connect(('localhost',9999))
while True:
    cmd=input('是否推出(Y/N)>>').strip()
    if len(cmd)==0:
        continue
    if cmd=='Y'or cmd=='y':
        break
    client.send(cmd.encode())
    data=client.recv(1024)
    print(data.decode())
client.close()