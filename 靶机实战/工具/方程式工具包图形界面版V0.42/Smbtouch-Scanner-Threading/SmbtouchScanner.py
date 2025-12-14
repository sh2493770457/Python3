import os
import fileinput
import sys
import threading
import time
import socket
import struct


lock = threading.RLock()
sem = threading.Semaphore(int(sys.argv[1]))

def smb_touch(ip):
     with  sem:
          Command = 'Smbtouch-1.1.1.exe --TargetIp ' + ip

          Output = os.popen(Command).read()
          Output = Output[0:Output.find('<config', 1)]
          Flag = Output.find('[-] Touch failed')
          if Flag == -1:
               print '[+] Touch success:	' + ip + '\r\n'

               lock.acquire()
               fp_success.writelines('\r\n--------------------------------------------\r\n' + Output)
               lock.release()

          else:
               print '[-] Touch failed:	' + ip + '\r\n'

               lock.acquire()
               fp_failed.writelines('\r\n--------------------------------------------\r\n' + Output)
               lock.release()


def GetIpList(BeginIP, EndIP):
     ipList = []

     int_BeginIp = struct.unpack('!I', socket.inet_aton(BeginIP))[0]
     int_EndIp = struct.unpack('!I', socket.inet_aton(EndIP))[0]

     for int_ip in range(int_BeginIp, int_EndIp + 1):
          str_ip = socket.inet_ntoa(struct.pack('!I', int_ip))
          ipList.append(str_ip)

     return ipList


if __name__=="__main__":
     fp_success = open('log_success.txt', 'w+')
     fp_failed = open('log_failed.txt', 'w+')
     threads = []


     ipList = GetIpList(sys.argv[2], sys.argv[3])
     MaxThreadNum = int(sys.argv[1])

     for ip in ipList:
          t = threading.Thread(target=smb_touch,args=(ip,))
          t.setDaemon(True)
          threads.append(t)

     for t in threads:
          t.start()

     for t in threads:
          t.join()

     fp_success.close()
     fp_failed.close()
'''
     task_pool = threadpool.ThreadPool(int(sys.argv[1]))
     requests = threadpool.makeRequests(smb_touch, ipList)
     for req in requests:
          task_pool.putRequest(req)

     task_pool.wait()
'''








