# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵03
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/5/23 15:46

import time
import requests

total_sum = 0
for page in range(1, 21):
    url = f'https://www.mashangpa.com/api/problem-detail/3/data/?page={page}'
    headers = {
        'Cookie': 'Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1747962794; Hm_lvt_b5d072258d61ab3cd6a9d485aac7f183=1747983150; sessionid=wr66pa8fxujqpm9wza6c01sieknbvgn0; Hm_lpvt_b5d072258d61ab3cd6a9d485aac7f183=1747986359',
        'Sec-Ch-Ua': '"Chromium";v="136", "Google Chrome";v="136", "Not.A/Brand";v="99"',
        'Accept': '*/*',
        'Sec-Ch-Ua-Platform': '"Windows"',
        'Priority': 'u=1, i',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36',
        'Referer': 'https://www.mashangpa.com/problem-detail/3/',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-Dest': 'empty',
        'Accept-Encoding': 'gzip, deflate, br',
        'Sec-Fetch-Mode': 'cors',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Sec-Ch-Ua-Mobile': '?0'
    }

    params = {'page': {page}}
    response = requests.get(url, headers=headers, params=params).json()
    current_array = response['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response, end='\n\n')
    total_sum += num_sum
    time.sleep(1)
print("\n最终20页累加和为:", total_sum)

