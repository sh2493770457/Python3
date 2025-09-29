# -*- coding: utf-8 -*-
# TODO:@ModuleName: 图灵14扣js
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/09/28 15:40
import execjs
import requests
import time

total_sum = 0
for page in range(1, 21):
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',
        'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; v=QThpRG1lZmNPMmpaaFZpb1h1Mk8xNElobVQzZmNTd2JEdFFBLVlKNUZuODJsbVpqS29IOEMxN2wwSXZSMTc1Nzg5OTkwODQ4Nw==; _nano_fp=XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1758726296,1758727507,1759024851,1759028808; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1759028813',
    }

    # 加载 js
    with open("图灵14.js", "r", encoding="utf-8") as f:
        lines = f.read()
    ctx = execjs.compile(lines)
    params_value = ctx.call("get_m")  # 返回m的值
    # print(params_value)

    url = f"https://www.mashangpa.com/api/problem-detail/14/data/?m={params_value}"
    data = f'{{"page":{page}}}'
    response = requests.post(url, headers=headers, data=data)
    current_array = response.json()['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response.text.encode('utf-8').decode('unicode_escape'), end='\n\n')
    total_sum += num_sum
    time.sleep(0.1)
print("\n最终20页累加和为:", total_sum)
