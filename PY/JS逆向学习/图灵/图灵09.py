# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵09
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/06/12 11:34
import requests
import base64
import hashlib
import hmac
import time


# url = "https://www.mashangpa.com/api/problem-detail/9/data/"
#
# key = "xxxooo"
# prefix = "9527"  # 🔑 关键发现：必须加上这个前缀！
# timestamp = int(time.time() * 1000)
#
# # 正确的算法
# tt = base64.b64encode(str(timestamp).encode('utf-8')).decode('utf-8')
# data = prefix + str(timestamp)  # "9527" + timestamp
# m = hmac.new(key.encode('utf-8'), data.encode('utf-8'), hashlib.sha1).hexdigest()
#
#
# headers = {
#     "Cookie": "sessionid=lbhz9apisibqyvxil5xu0qqu44v6tyhh; Hm_lvt_b5d072258d61ab3cd6a9d485aac7f183=1747983150,1747992419,1748013053,1748070387; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1749604690,1749612243,1749692636,1749697642; HMACCOUNT=EA832FD00F9EA5FD; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1749699269",
#     "Origin": "https://www.mashangpa.com",
#     "Sec-Ch-Ua": "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
#     "X-Requested-With": "XMLHttpRequest",
#     "Accept": "*/*",
#     "Sec-Ch-Ua-Platform": "\"Windows\"",
#     "Priority": "u=1, i",
#     "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
#     "Referer": "https://www.mashangpa.com/problem-detail/9/",
#     "Sec-Fetch-Site": "same-origin",
#     "Sec-Fetch-Dest": "empty",
#     "Accept-Encoding": "gzip, deflate, br",
#     "Sec-Fetch-Mode": "cors",
#     "Accept-Language": "zh-CN,zh;q=0.9",
#     "Sec-Ch-Ua-Mobile": "?0",
#     "Content-Type": "application/json"
# }
#
# data = {
#     "tt": tt,
#     "m": m,
#     "page": 3
# }
#
# response = requests.post(url, headers=headers, json=data)
#
# print('[状态码]:', response.status_code)
# print('[响应正文]:\n', response.text.encode('utf-8').decode('unicode_escape'))

class TuLinNine:
    def __init__(self, Page, Prefix, Key):
        self.url = "https://www.mashangpa.com/api/problem-detail/9/data/"
        self.Prefix = Prefix
        self.Key = Key
        self.Page = Page
        self.T = int(time.time() * 1000)
        self.headers = {
            "Cookie": "sessionid=lbhz9apisibqyvxil5xu0qqu44v6tyhh; Hm_lvt_b5d072258d61ab3cd6a9d485aac7f183=1747983150,1747992419,1748013053,1748070387; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1749604690,1749612243,1749692636,1749697642; HMACCOUNT=EA832FD00F9EA5FD; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1749699269",
            "Origin": "https://www.mashangpa.com",
            "Sec-Ch-Ua": "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
            "X-Requested-With": "XMLHttpRequest",
            "Accept": "*/*",
            "Sec-Ch-Ua-Platform": "\"Windows\"",
            "Priority": "u=1, i",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
            "Referer": "https://www.mashangpa.com/problem-detail/9/",
            "Sec-Fetch-Site": "same-origin",
            "Sec-Fetch-Dest": "empty",
            "Accept-Encoding": "gzip, deflate, br",
            "Sec-Fetch-Mode": "cors",
            "Accept-Language": "zh-CN,zh;q=0.9",
            "Sec-Ch-Ua-Mobile": "?0",
            "Content-Type": "application/json"
        }

    @staticmethod
    def encrypt_data(self):
        timestamp = int(time.time() * 1000)
        tt = base64.b64encode(str(timestamp).encode('utf-8')).decode('utf-8')
        data = self.Prefix + str(timestamp)  # "9527" + timestamp
        m = hmac.new(self.Key.encode('utf-8'), data.encode('utf-8'), hashlib.sha1).hexdigest()
        req_data = {
            "tt": tt,
            "m": m,
            "page": self.Page
        }
        return req_data

    def req_url(self):
        proxy = {
            "http": "http://127.0.0.1:7890",
            "https": "http://127.0.0.1:7890"
        }
        response = requests.post(self.url, headers=self.headers, json=self.encrypt_data(self), proxies=proxy)
        return response.json()


if __name__ == '__main__':
    total_sum = 0
    for page in range(1, 21):
        tulin = TuLinNine(page, "9527", "xxxooo")
        result = tulin.req_url()
        current_array = result.get("current_array", [])
        page_sum = sum(current_array)
        print(f"第{page}页: {current_array} --> 求和结果为: {page_sum}")
        print(result)
        print('\n')
        total_sum += page_sum
    print("20页总和为: ", total_sum)
