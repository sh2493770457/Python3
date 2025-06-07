import socket
import requests
import concurrent.futures
from requests.exceptions import RequestException

# 定义颜色
GREEN = '\033[92m'  # 绿色
RED = '\033[91m'  # 红色
YELLOW = '\033[93m'  # 黄色
RESET = '\033[0m'  # 重置颜色
CYAN = '\033[96m'

print(f"{RED}------把回忆拼好丢了------{RESET}")
print(fr'''{CYAN} _    __  ____   __   ____                  
| |   \ \/ /\ \ / /  / ___|  ___ __ _ _ __  
| |    \  /  \ V /___\___ \ / __/ _` | '_ \ 
| |___ /  \   | |_____|__) | (_| (_| | | | |
|_____/_/\_\  |_|    |____/ \___\__,_|_| |_|{CYAN}''')


# 生成C段IP地址
def generate_ips(base_ip):
    base_ip = base_ip.replace('*', '')  # 替换掉输入中的*
    if base_ip.endswith('.'):
        return [f"{base_ip}{i}" for i in range(2, 255)]  # 正确拼接IP地址
    else:
        raise ValueError("IP地址格式错误，应以'.'结尾。")


# 扫描IP的端口
def scan_ip(ip, ports=None):
    if ports is None:
        ports = [80, 8080, 8081, 8082, 8000, 81, 82, 83, 84, 9000, 9999, 8888, 4444, 7777, 6666]
    open_ports = []
    for port in ports:
        try:
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
                sock.settimeout(2)  # 增加超时
                result = sock.connect_ex((ip, port))
                if result == 0:
                    # 尝试发送和接收少量数据来进一步验证端口开放
                    sock.send(b"GET / HTTP/1.1\r\nHost: " + ip.encode() + b"\r\n\r\n")
                    data = sock.recv(1024)
                    if b"HTTP" in data:
                        open_ports.append(port)  # 记录开放的端口
        except socket.error:
            pass
    return open_ports  # 返回开放端口列表


# 保存结果到文件（批量写入）
def save_ips(ips_with_ports, file_path):
    with open(file_path, "w") as f:
        for ip, ports in ips_with_ports.items():
            for port in ports:  # 针对每个端口，保存为ip:port的格式
                f.write(f"{ip}:{port}\n")


# 多线程端口扫描
def scan_ips_concurrently(ips):
    open_ips = {}
    with concurrent.futures.ThreadPoolExecutor(max_workers=400) as executor:
        futures = {executor.submit(scan_ip, ip): ip for ip in ips}
        for future in concurrent.futures.as_completed(futures):
            ip = futures[future]
            try:
                open_ports = future.result()
                if open_ports:
                    print(f"[开放] {ip} HTTP服务开启: {open_ports}")
                    open_ips[ip] = open_ports
                else:
                    print(f"[关闭] {ip} HTTP服务关闭")
            except Exception as e:
                pass
                # print(f"{RED}[ERROR] {ip} - 扫描失败: {e}{RESET}")
    return open_ips


# 爆破目录
def bruteforce_directories_for_ip(ip, port, directories, status_codes_to_show):
    base_url = f'http://{ip}:{port}'
    print(f"\n开始爆破 {base_url} ...")

    with concurrent.futures.ThreadPoolExecutor(max_workers=400) as executor:
        futures = {
            executor.submit(requests.get, base_url + directory.strip(), timeout=2): directory for directory in
            directories
        }
        for future in concurrent.futures.as_completed(futures):
            directory = futures[future]
            url = base_url + directory.strip()
            try:
                response = future.result()
                status_code = response.status_code
                if status_code in status_codes_to_show:
                    if status_code == 200:
                        print(f"{GREEN}[{status_code} OK] {url}{RESET}")
                    elif status_code == 404:
                        print(f"{RED}[{status_code} Not Found] {url}{RESET}")
                    elif status_code == 403:
                        print(f"{YELLOW}[{status_code} Forbidden] {url}{RESET}")
                    elif status_code == 500:
                        print(f"{RED}[{status_code} Internal Server Error] {url}{RESET}")
                    else:
                        print(f"{YELLOW}[{status_code}] {url}{RESET}")
            except RequestException as e:
                pass
                # print(f"{RED}[ERROR] {url} - 请求失败: {e}{RESET}")


# 生成IP地址
base_ip = "192.168.62."
ips = generate_ips(base_ip)

# 使用多线程扫描开放的端口
open_ips_with_ports = scan_ips_concurrently(ips)

# 保存开放端口的IP和端口
save_ips(open_ips_with_ports, "open_ips.txt")
print(f"开放端口的IP和端口已写入open_ips.txt")

# 读取字典文件
with open('dir.txt', 'r', encoding='utf-8') as f:
    directories = f.readlines()

# 获取用户输入的状态码
status_codes_input = "200,403,500"
status_codes_to_show = [int(code.strip()) for code in status_codes_input.split(',')]

# 读取开放的IP地址并逐个爆破目录
with open("open_ips.txt", "r") as f:
    for line in f.readlines():
        ip, port = line.strip().split(':')  # 分离IP和端口
        bruteforce_directories_for_ip(ip, port, directories, status_codes_to_show)

# 结束程序
input("按任意键退出程序...")
