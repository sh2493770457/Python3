# -*- coding: utf-8 -*-
# TODO:@ModuleName: 图灵17->字体加密
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/09/28 17:18
import requests
import time
import json

total_sum = 0
for page in range(1, 21):
    cookies = {
        'sessionid': 'ub2p253ntgy23lyqfl2pkm50jrwkrmyi',
        '_nano_fp': 'XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj',
        'Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0': '1758726296,1758727507,1759024851,1759028808',
        'HMACCOUNT': '5ADB897338667860',
        'v': 'QS1SVnhZbzdYOVFkMGFSRFF5eEFGbDhidGVuVGZRa0x5cUtjSmY0RWNfZHlnSXJYSm8zWWR4cXhiS2ROMTc1OTA0NzEyOTI1NQ==',
        'Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0': '1759050431',
    }

    headers = {
        'Host': 'www.mashangpa.com',
        'Pragma': 'no-cache',
        'Cache-Control': 'no-cache',
        'Sec-Ch-Ua-Platform': '"Windows"',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',
        'Sec-Ch-Ua': '"Chromium";v="140", "Not=A?Brand";v="24", "Google Chrome";v="140"',
        'Sec-Ch-Ua-Mobile': '?0',
        'Accept': '*/*',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Dest': 'empty',
        'Referer': 'https://www.mashangpa.com/problem-detail/17/',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Priority': 'u=1, i',
    }

    params = {
        'page': page,
    }

    response = requests.get(
        'https://www.mashangpa.com/api/problem-detail/17/data/',
        params=params,
        cookies=cookies,
        headers=headers,
    )

    FONT_DECRYPT_MAP = {
        "ꙮ": "0",
        "ઊ": "1",
        "સ": "2",
        "ત": "3",
        "ধ": "4",
        "ન": "5",
        "પ": "6",
        "ફ": "7",
        "બ": "8",
        "ભ": "9",
    }

    # 解密 current_array
    decrypted = []
    json_data = response.json()  # 只调用一次
    for item in json_data["current_array"]:
        digits = []
        for ch in item:
            digits.append(FONT_DECRYPT_MAP.get(ch, "?"))
        decrypted.append(int("".join(digits)))  # 转为数字

    json_data['current_array'] = decrypted
    current_array = json_data['current_array']

    # 对列表内容求和
    num_sum = sum(current_array)
    total_sum += num_sum

    print(f"第{page}页的累加和为:", num_sum)
    print(json.dumps(json_data, ensure_ascii=False), end='\n\n')

    time.sleep(0.1)

print("\n最终20页累加和为:", total_sum)
