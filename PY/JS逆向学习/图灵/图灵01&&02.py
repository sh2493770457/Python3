# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵01-02
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/5/23 09:12
import time
import requests

total_sum = 0
for page in range(1, 21):
    url = f'https://www.mashangpa.com/api/problem-detail/1/data/?page={page}'
    headers = {
        'Cookie': '_nano_fp=XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj; v=QS1SVnhZbzdYOVFkMGFSRFF5eEFGbDhidGVuVGZRa0x5cUtjSmY0RWNfZHlnSXJYSm8zWWR4cXhiS2ROMTc1OTA0NzEyOTI1NQ==; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1760588635,1763018957; HMACCOUNT=5ADB897338667860; sessionid=ah0iz8hna8g7ou7si0xk2z9o5hpiot6q; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1763019093',
        'Accept': '*/*',
        'Sec-Ch-Ua-Platform': '"Windows"',
        'Priority': 'u=1, i',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36',
        'Referer': 'https://www.mashangpa.com/problem-detail/2/',
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
