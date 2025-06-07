# 提取C:/User/24937/Desktop/shell.txt中的url的ip
import re
with open(r'C:/Users/24937/Desktop/shell.txt', 'r') as f:
    lines = f.readlines()
    for line in lines:
        if 'http' in line:
            url = re.findall(r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\$\$,]|(?:%[0-9a-fA-F][0-9a-fA-F]))+', line)
            if url:
                ip = re.findall(r'(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)', url[0])
                if ip:
                    print(ip[0])
                    #保存到桌面
                    with open(r'C:/Users/24937/Desktop/re_ip.txt', 'a') as f:
                        f.write(ip[0]+'\n')

                    