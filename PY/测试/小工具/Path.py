import requests
import concurrent.futures

# 定义颜色
GREEN = '\033[92m'  # 绿色
RED = '\033[91m'  # 红色
YELLOW = '\033[93m'  # 黄色
RESET = '\033[0m'  # 重置颜色

base_url = 'http://192.168.71.6/'

# 允许用户输入想要显示的状态码类型，多个状态码用逗号分隔
status_codes_input = input("请输入你想显示的状态码 (用逗号分隔，例如: 200,404,500): ")
status_codes_to_show = [int(code.strip()) for code in status_codes_input.split(',')]

# 读取字典文件
with open('dir.txt', 'r', encoding='utf-8') as f:
    directories = f.readlines()

# 使用 ThreadPoolExecutor 进行多线程处理
with concurrent.futures.ThreadPoolExecutor(max_workers=500) as executor:
    # 提交任务到线程池
    futures = {executor.submit(requests.get, base_url + directory.strip().lstrip('/'), timeout=5): directory for
               directory in directories}

    # 使用 as_completed 逐个处理完成的任务，避免等待所有任务完成
    for future in concurrent.futures.as_completed(futures):
        directory = futures[future]
        url = base_url + directory.strip().lstrip('/')

        try:
            response = future.result()
            status_code = response.status_code

            # 根据用户输入的状态码筛选显示
            if status_code in status_codes_to_show:
                # 根据状态码显示不同颜色
                if status_code == 200:
                    print(f"{GREEN}[{status_code} OK] {url}{RESET}")
                elif status_code == 404:
                    print(f"{RED}[{status_code} Not Found] {url}{RESET}")
                else:
                    print(f"{YELLOW}[{status_code}] {url}{RESET}")  # 其他状态码

        except requests.exceptions.RequestException as e:
            print(f"{RED}[Error] 请求 {url} 失败: {e}{RESET}")
