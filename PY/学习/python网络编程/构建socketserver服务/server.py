import socketserver
class MyTCPHandler(socketserver.BaseRequestHandler):
    def handle(self):
        try:
            while True:
                self.data=self.request.recv(1024)
                print('{}发送的消息：'.format(self.client_address),self.data)
                if not self.data:
                    print('{}断开连接'.format(self.client_address))
                    break
                self.request.sendall(self.data.upper())
        except Exception as e:
            print(self.client_address,'连接断开')
        finally:
            self.request.close()
    def setup(self):
        print('{}已连接'.format(self.client_address))
    def finish(self):
        print('{}已断开'.format(self.client_address))
if __name__=='__main__':
    HOST,PORT='localhost',9999
    server=socketserver.TCPServer((HOST,PORT),MyTCPHandler)
    server.serve_forever()