# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵08
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/6/11 16:27
import requests
import base64
import time


class TuLinEight:
    def __init__(self, Page, Prefix="oooooo"):
        self.Prefix = Prefix
        self.Page = Page
        Ts = str(int(time.time() * 1000))
        self.T = base64.b64encode(Ts.encode()).decode()
        self.url = "https://www.mashangpa.com/api/problem-detail/8/data/"
        # TODO: 构造加密字符串
        encrypt_input = f"{self.Prefix}{Ts}{self.Page}"
        self.M = self.oooo_encrypt(encrypt_input, self.Prefix)
        self.headers = {
            "Cookie": "sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757325019,1757337823,1757566712,1757571135; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757582433; s=51b351b351b351b370b0f0b0f05110d030907151b0",
            "Origin": "https://www.mashangpa.com",
            "Sec-Ch-Ua": "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
            "X-Requested-With": "XMLHttpRequest",
            "Accept": "*/*",
            "Sec-Ch-Ua-Platform": "\"Windows\"",
            "Priority": "u=1, i",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
            "Referer": "https://www.mashangpa.com/problem-detail/8/",
            "Sec-Fetch-Site": "same-origin",
            "Sec-Fetch-Dest": "empty",
            "Pragma": "no-cache",
            "Accept-Encoding": "gzip, deflate, br",
            "M": self.M,
            "Sec-Fetch-Mode": "cors",
            "Cache-Control": "no-cache",
            "T": self.T,
            "Accept-Language": "zh-CN,zh;q=0.9",
            "Sec-Ch-Ua-Mobile": "?0",
            "Content-Type": "application/json"
        }

    @staticmethod
    def oooo_encrypt(input_string, key_string):
        """ TODO: 加密函数 """
        result_chars = []
        for i in range(0, len(input_string), 4):
            block = list(input_string[i:i + 4])
            for j, char in enumerate(block):
                c = ord(char)
                k = ord(key_string[j % len(key_string)])
                block[j] = chr((c + k) % 256)
            result_chars.extend(block)
        return ''.join(f'{ord(c):02x}' for c in result_chars)

    def req_url(self):
        req_data = {"page": self.Page}
        response = requests.post(self.url, json=req_data, headers=self.headers)
        return response.json()

    def run(self):
        response_json = self.req_url()
        current_array = response_json['current_array']
        print(f"第{self.Page}页:", current_array, "--> 求和结果为: ", sum(current_array))
        print(response_json)
        print('\n')
        return sum(current_array)


if __name__ == '__main__':
    total_sum = 0
    for page in range(1, 21):
        tulin = TuLinEight(page)
        total_sum += tulin.run()
    print("20页总和为: ", total_sum)
