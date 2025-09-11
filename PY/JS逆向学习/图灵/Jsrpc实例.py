# -*- encoding: utf-8 -*-
# TODO:@ModuleName:Jsrpc实例
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/06/10 20:16

import requests
import time
import hashlib

for page in range(1, 21):
    url = f"https://www.mashangpa.com/api/problem-detail/6/data/?page={page}"

    Tt = str(int(time.time() * 1000))
    S = hashlib.md5(("sssssbbbbb" + Tt).encode("utf-8")).hexdigest()

    headers = {
        "Cookie": "sessionid=lbhz9apisibqyvxil5xu0qqu44v6tyhh; Hm_lvt_b5d072258d61ab3cd6a9d485aac7f183=1747983150,1747992419,1748013053,1748070387; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1747962794,1749190454,1749553052; HMACCOUNT=EA832FD00F9EA5FD; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1749557767",
        "Tt": Tt,
        "Sec-Ch-Ua": "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
        "Accept": "*/*",
        "Sec-Ch-Ua-Platform": "\"Windows\"",
        "Priority": "u=1, i",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
        "Referer": "https://www.mashangpa.com/problem-detail/6/",
        "Sec-Fetch-Site": "same-origin",
        "Sec-Fetch-Dest": "empty",
        "Accept-Encoding": "gzip, deflate, br",
        "Sec-Fetch-Mode": "cors",
        "S": S,
        "Accept-Language": "zh-CN,zh;q=0.9",
        "Sec-Ch-Ua-Mobile": "?0"
    }

    response = requests.get(url, headers=headers).json()
    content = response['t']

    url = "http://127.0.0.1:12080/go"
    data = {
        "group": "zzz",
        "action": "hello3",
        "param": content
    }
    res = requests.post(url, data=data).json()
    text = res['data'].encode('utf-8')
    print(text.decode('unicode_escape'))
    print('\n')
