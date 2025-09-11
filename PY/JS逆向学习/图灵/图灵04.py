# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵4
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/5/23 16:02

import requests
import hashlib
import time

total_sum = 0
for page in range(1, 21):
    timestamp = str(int(time.time() * 1000))
    orgin_sign = 'tuling' + timestamp + str(page)
    sign = hashlib.md5(orgin_sign.encode()).hexdigest()
    url = f'https://www.mashangpa.com/api/problem-detail/4/data/?page={page}&sign={sign}&_ts={timestamp}'
    headers = {
        'Cookie': 'Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1747962794; Hm_lvt_b5d072258d61ab3cd6a9d485aac7f183=1747983150,1747992419,1748013053; HMACCOUNT=EA832FD00F9EA5FD; sessionid=fdcm2n18gdgmdwbw7x1uguc7tifez4d5; Hm_lpvt_b5d072258d61ab3cd6a9d485aac7f183=1748014311',
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
        'Sec-Ch-Ua-Mobile': '?0'
    }

    params = {'sign': sign, 'page': page, '_ts': timestamp}
    response = requests.get(url, headers=headers, params=params).json()
    current_array = response['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response, end='\n\n')
    total_sum += num_sum
    time.sleep(1)
print("\n最终20页累加和为:", total_sum)
