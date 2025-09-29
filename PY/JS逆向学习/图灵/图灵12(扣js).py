# -*- coding: utf-8 -*-
# TODO:@ModuleName: 图灵12->扣js
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/09/22 11:27
import subprocess
import time
import requests

# 要调用的 Node 脚本路径
node_script = r"图灵12.js"

total_sum = 0
for page in range(1, 21):
    # 执行 Node 脚本
    result = subprocess.run(
        ["node", node_script, str(page)],
        capture_output=True,  # 捕获输出
        text=True  # 输出以字符串形式返回
    )

    path = result.stdout.strip().splitlines()[-1]

    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',
        'Accept': 'application/json, text/javascript, */*; q=0.01',
        'Referer': 'https://www.mashangpa.com/problem-detail/12/',
        'Priority': 'u=1, i',
        'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; v=QThpRG1lZmNPMmpaaFZpb1h1Mk8xNElobVQzZmNTd2JEdFFBLVlKNUZuODJsbVpqS29IOEMxN2wwSXZSMTc1Nzg5OTkwODQ4Nw==; _nano_fp=XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1758017588,1758075595,1758261042,1758505179; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1758511623',
    }
    base_url = 'https://www.mashangpa.com'
    url = base_url + path
    response = requests.get(
        url,
        headers=headers,
    )
    current_array = response.json()['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response.text.encode('utf-8').decode('unicode_escape'), end='\n\n')
    total_sum += num_sum
    time.sleep(0.1)
print("\n最终20页累加和为:", total_sum)

