# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵05(扣js)
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/9/12 15:02
import json
import execjs
import requests
import time

total_sum = 0
for page in range(1, 21):
    url = 'https://www.mashangpa.com/api/problem-detail/5/data/'
    with open("图灵05.js", "r", encoding="utf-8") as f:
        lines = f.read()
    ctx = execjs.compile(lines)
    headers = {
        'Sec-Ch-Ua': '"Chromium";v="136", "Google Chrome";v="136", "Not.A/Brand";v="99"',
        'Accept': '*/*',
        'Sec-Ch-Ua-Platform': '"Windows"',
        'Priority': 'u=1, i',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36',
        'Referer': 'https://www.mashangpa.com/problem-detail/4/',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-Dest': 'empty',
        'Pragma': 'no-cache',
        'Accept-Encoding': 'gzip, deflate, br',
        'Sec-Fetch-Mode': 'cors',
        'Cache-Control': 'no-cache',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Sec-Ch-Ua-Mobile': '?0',
        'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757566712,1757571135,1757597958,1757640983; HMACCOUNT=5ADB897338667860; s=51b351b351b351b370b0f0b090d0f09090307030f0; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757658597',
    }

    orgin_data = {
        "page": page,
        "_ts": int(time.time() * 1000)
    }
    encrypt_data = ctx.call("encrypt", json.dumps(orgin_data))
    data = {"xl": encrypt_data}
    response = requests.post(url, headers=headers, json=data).json()

    current_array = response['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response, end='\n\n')
    total_sum += num_sum
    time.sleep(1)
print("\n最终20页累加和为:", total_sum)
