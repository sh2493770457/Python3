import subprocess
import threading

# TODO:生成ip.txt,方便后续操作
base_ip = input("请输入需要生成的起始ip地址(例如192.168.71.*):")
last_num = int(base_ip.rsplit('.', 1)[-1])  # TODO:获取最后一个数字 *
base_ip = base_ip.rsplit('.', 1)[0] + '.'  # TODO:获取除了最后一个数字的部分 192.168.71. 然后进行拼接得到新的ip
ips = [base_ip + str(i) for i in range(last_num, 255)]
# TODO:写入文件
with open("C:/Users/24937/Desktop/ip.txt", "w") as f:  # TODO:这里用w实现覆盖,避免重复运行时ip重复
    for ip in ips:
        print(ip)
        f.write(ip + "\n")
print('写入成功 >_<')

# # TODO:把需要的用到的命令写入到列表,可以自定义
# cmds = [
#     'nmap -iL C:\\Users\\24937\\Desktop\\ip2.txt -p 80,8080,22,3306,6379,3389,1433,445 -sV -O',
#     'fscan.exe -hf C:\\Users\\24937\\Desktop\\ip2.txt -p 80,8080,22,3306,6379,3389,1433,445',
#     'sqlmap -u "www.baidu.com" --batch --random-agent --threads 10 --level 3 --risk 3 --banner --dbs --batch',
#     'D:\\naabu\\naabu.exe -host 192.168.41.145 -p 1-65535',
#     'D:\\masscan\\masscan.exe --ports 1-65535 192.168.41.145'
# ]
# # TODO:使用多线程,这里用subprocess,因为os不好用,容易卡
# threads = []
# for cmd in cmds:
#     # TODO:这里使用了个匿名函数lamda,把cmd传进去
#     thread = threading.Thread(target=lambda c=cmd: subprocess.run(c, shell=True, check=True))
#     threads.append(thread)
#     thread.start()
# for thread in threads:
#     thread.join()
# print("所有命令执行完毕 >_<")



