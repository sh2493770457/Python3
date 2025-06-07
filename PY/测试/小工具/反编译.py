import socket
import requests
import concurrent.futures
from requests.exceptions import RequestException

GREEN = '\033[92m'  # 绿色
RED = '\033[91m'  # 红色
YELLOW = '\033[93m'  # 黄色
RESET = '\033[0m'  # 重置颜色
CYAN = '\033[96m'

print(f'{GREEN}------把回忆拼好丢了------{RESET}')
print(
    f'{CYAN} _    __  ____   __   ____                  \n| |   \\ \\/ /\\ \\ / /  / ___|  ___ __ _ _ __  \n| |    \\  /  \\ V /___\\___ \\ / __/ _` | \'_ \\ \n| |___ /  \\   | |_____|__) | (_| (_| | | | |\n|_____/_/\\_\\  |_|    |____/ \\___\\__,_|_| |_|{CYAN}')


def generate_ips(base_ip):
    base_ip = base_ip.replace('*', '')
    if base_ip.endswith('.'):
        return [f'{base_ip}{i}' for i in range(2, 255)]
    else:
        raise ValueError('IP地址格式错误，应以\'.\'结尾。')


def scan_ip(ip, ports=None):
    if ports is None:
        ports = [80, 8080, 8081, 8082, 8000, 81, 82, 83, 84, 9000, 9999, 8888, 4444]
    open_ports = []
    for port in ports:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
            sock.settimeout(3)
            try:
                result = sock.connect_ex((ip, port))
                if result == 0:  # 端口开放
                    sock.send(b'GET / HTTP/1.1\r\nHost: ' + ip.encode() + b'\r\n\r\n')
                    data = sock.recv(1024)
                    if b'HTTP' in data:
                        open_ports.append(port)
            except socket.error:
                pass
    return open_ports


def save_ips(ips_with_ports, file_path):
    with open(file_path, 'w') as f:
        for ip, ports in ips_with_ports.items():
            for port in ports:
                f.write(f'{ip}:{port}\n')


def scan_ips_concurrently(ips):
    open_ips = {}
    with concurrent.futures.ThreadPoolExecutor(max_workers=400) as executor:
        futures = {executor.submit(scan_ip, ip): ip for ip in ips}
        for future in concurrent.futures.as_completed(futures):
            ip = futures[future]
            try:
                open_ports = future.result()
                if open_ports:
                    print(f'[开放] {ip} HTTP服务开启: {open_ports}')
                    open_ips[ip] = open_ports
                else:
                    print(f'[关闭] {ip} HTTP服务关闭')
            except Exception as e:
                print(f'[错误] 扫描 {ip} 时出错: {e}')
    return open_ips


def bruteforce_directories_for_ip(ip, port, directories, status_codes_to_show):
    base_url = f'http://{ip}:{port}'
    print(f'\n开始爆破 {base_url} ...')
    with concurrent.futures.ThreadPoolExecutor(max_workers=400) as executor:
        futures = {executor.submit(requests.get, base_url + directory.strip(), timeout=2): directory for directory in directories}
        for future in concurrent.futures.as_completed(futures):
            directory = futures[future]
            url = base_url + directory.strip()
            try:
                response = future.result()
                status_code = response.status_code
                if status_code in status_codes_to_show:
                    if status_code == 200:
                        print(f'{GREEN}[{status_code} OK] {url}{RESET}')
                    elif status_code == 404:
                        print(f'{RED}[{status_code} Not Found] {url}{RESET}')
                    elif status_code == 403:
                        print(f'{YELLOW}[{status_code} Forbidden] {url}{RESET}')
                    elif status_code == 500:
                        print(f'{RED}[{status_code} Internal Server Error] {url}{RESET}')
                    else:
                        print(f'{YELLOW}[{status_code}] {url}{RESET}')
            except RequestException:
                continue


base_ip = '192.168.71.'
ips = generate_ips(base_ip)
open_ips_with_ports = scan_ips_concurrently(ips)
save_ips(open_ips_with_ports, '../open_ips.txt')
print('开放端口的IP和端口已写入open_ips.txt')

with open('dir.txt', 'r', encoding='utf-8') as f:
    directories = f.readlines()

status_codes_input = '200,403,500'
status_codes_to_show = [int(code.strip()) for code in status_codes_input.split(',')]

with open('../open_ips.txt', 'r') as f:
    for line in f.readlines():
        ip, port = line.strip().split(':')
        bruteforce_directories_for_ip(ip, port, directories, status_codes_to_show)

input('按任意键退出程序...')
