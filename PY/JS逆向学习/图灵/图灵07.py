# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵07
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/06/11 01:27
import binascii
import hashlib
import json
import time
import requests
from Crypto.Cipher import AES


# Ts = str(int(time.time() * 1000))
# M = hashlib.md5(('xialuo'+Ts).encode("utf-8")).hexdigest()
# # TODO: sha256("xxoo"+M)
# x = hashlib.sha256(("xxoo"+M).encode("utf-8")).hexdigest()
# # print(M)
# url = f"https://www.mashangpa.com/api/problem-detail/7/data/?page=4&x={x}"
#
# headers = {
#     "Cookie": "sessionid=lbhz9apisibqyvxil5xu0qqu44v6tyhh; Hm_lvt_b5d072258d61ab3cd6a9d485aac7f183=1747983150,1747992419,1748013053,1748070387; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1747962794,1749190454,1749553052,1749575446; HMACCOUNT=EA832FD00F9EA5FD; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1749576092",
#     "Sec-Ch-Ua": "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
#     "X-Requested-With": "XMLHttpRequest",
#     "Accept": "application/json, text/javascript, */*; q=0.01",
#     "Sec-Ch-Ua-Platform": "\"Windows\"",
#     "Priority": "u=1, i",
#     "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
#     "Referer": "https://www.mashangpa.com/problem-detail/7/",
#     "Sec-Fetch-Site": "same-origin",
#     "Sec-Fetch-Dest": "empty",
#     "Pragma": "no-cache",
#     "Accept-Encoding": "gzip, deflate, br",
#     "M": M,
#     "Sec-Fetch-Mode": "cors",
#     "Cache-Control": "no-cache",
#     "Accept-Language": "zh-CN,zh;q=0.9",
#     "Sec-Ch-Ua-Mobile": "?0",
#     "Ts": Ts
# }
#
# response = requests.get(url, headers=headers).json()
# key = b'xxxxxxxxoooooooo'
# iv = b'0123456789ABCDEF'
#
#
# cipher = AES.new(key, AES.MODE_CBC, iv)
# encrypt_data = binascii.unhexlify(response['r'])
# decrypt_data = cipher.decrypt(encrypt_data)
# padding_len = decrypt_data[-1]
# plaintext = decrypt_data[:-padding_len]
# print(plaintext.decode('unicode_escape'))


class TuLinSeven:

    def __init__(self, Page, Key, Iv):
        Ts = str(int(time.time() * 1000))
        M = hashlib.md5(('xialuo' + Ts).encode("utf-8")).hexdigest()
        # TODO: sha256("xxoo"+M)
        x = hashlib.sha256(("xxoo" + M).encode("utf-8")).hexdigest()
        self.Key = Key
        self.Iv = Iv
        self.Page = Page
        self.url = f"https://www.mashangpa.com/api/problem-detail/7/data/?page={self.Page}&x={x}"
        self.headers = {
            "Cookie": 'Hm_lvt_b5d072258d61ab3cd6a9d485aac7f183=1747983150,1747992419,1748013053,1748070387; sessionid=mq3b8st9pc22iyxp7pdcweeybmkon4pv; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1752057934,1752564165,1752639045,1752822690; HMACCOUNT=EA832FD00F9EA5FD; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1752822700',
            "Sec-Ch-Ua": "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
            "X-Requested-With": "XMLHttpRequest",
            "Accept": "application/json, text/javascript, */*; q=0.01",
            "Sec-Ch-Ua-Platform": "\"Windows\"",
            "Priority": "u=1, i",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
            "Referer": "https://www.mashangpa.com/problem-detail/7/",
            "Sec-Fetch-Site": "same-origin",
            "Sec-Fetch-Dest": "empty",
            "Pragma": "no-cache",
            "Accept-Encoding": "gzip, deflate, br",
            "M": M,
            "Sec-Fetch-Mode": "cors",
            "Cache-Control": "no-cache",
            "Accept-Language": "zh-CN,zh;q=0.9",
            "Sec-Ch-Ua-Mobile": "?0",
            "Ts": Ts
        }

    def decrypt_data(self, encrypt_data):
        cipher = AES.new(self.Key, AES.MODE_CBC, self.Iv)
        decrypt_data = cipher.decrypt(encrypt_data)
        padding_len = decrypt_data[-1]
        plaintext = decrypt_data[:-padding_len]
        return plaintext.decode('unicode_escape')

    def req_url(self):
        response = requests.get(self.url, headers=self.headers)
        return response.json()

    def run(self):
        response_json = self.req_url()
        encrypt_data = binascii.unhexlify(response_json['r'])
        result = self.decrypt_data(encrypt_data)
        current_array = json.loads(result)['current_array']
        print(f"第{self.Page}页:", current_array, "--> 求和结果为: ", sum(current_array))
        print(result)
        print("\n")
        return sum(current_array)


if __name__ == '__main__':
    total_num = 0
    for page in range(1, 21):
        tulin = TuLinSeven(page, b'xxxxxxxxoooooooo', b'0123456789ABCDEF')
        total_num += tulin.run()
    print("20页总和为: ", total_num)
