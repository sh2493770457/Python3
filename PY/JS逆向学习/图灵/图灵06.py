# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵06
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/6/6 14:37
import binascii
import hashlib
import json
import time
import requests
from Crypto.Cipher import AES
from PY.diy_func import timer


# url = "https://www.mashangpa.com/api/problem-detail/6/data/?page=2"
# # TODO: 时间戳
# Tt = str(int(time.time() * 1000))
# # TODO: md5(字符串+时间戳)
# S = hashlib.md5(("sssssbbbbb" + Tt).encode("utf-8")).hexdigest()
# headers = {
#     "Cookie": "sessionid=lbhz9apisibqyvxil5xu0qqu44v6tyhh; Hm_lvt_b5d072258d61ab3cd6a9d485aac7f183=1747983150,1747992419,1748013053,1748070387; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1747962794,1749190454; HMACCOUNT=EA832FD00F9EA5FD; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1749190511",
#     "Tt": Tt,
#     "Sec-Ch-Ua": "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
#     "Accept": "*/*",
#     "Sec-Ch-Ua-Platform": "\"Windows\"",
#     "Priority": "u=1, i",
#     "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
#     "Referer": "https://www.mashangpa.com/problem-detail/6/",
#     "Sec-Fetch-Site": "same-origin",
#     "Sec-Fetch-Dest": "empty",
#     "Accept-Encoding": "gzip, deflate, br",
#     "Sec-Fetch-Mode": "cors",
#     "S": S,
#     "Accept-Language": "zh-CN,zh;q=0.9",
#     "Sec-Ch-Ua-Mobile": "?0"
# }
#
# response = requests.get(url, headers=headers)
# response_json = response.json()
# # TODO: AES解密响应内容
# key = b'xxxxxxxxoooooooo'
# iv = b'0123456789ABCDEF'
#
# cipher = AES.new(key, AES.MODE_CBC, iv)
# encrypt_data = binascii.unhexlify(response_json['t'])
# decrypt_data = cipher.decrypt(encrypt_data)
# padding_len = decrypt_data[-1]
# plaintext = decrypt_data[:-padding_len]
# print(plaintext.decode('unicode_escape'))


class TuLinSix:

    def __init__(self, page, Key, Iv, Cookie):
        self.Cookie = Cookie
        self.key = Key
        self.iv = Iv
        self.page = page
        self.Tt = str(int(time.time() * 1000))
        self.S = hashlib.md5(("sssssbbbbb" + self.Tt).encode("utf-8")).hexdigest()
        self.url = f"https://www.mashangpa.com/api/problem-detail/6/data/?page={page}"
        self.headers = {
            "Cookie": self.Cookie,
            "Tt": self.Tt, "Sec-Ch-Ua": "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
            "Accept": "*/*", "Sec-Ch-Ua-Platform": "\"Windows\"", "Priority": "u=1, i",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
            "Referer": "https://www.mashangpa.com/problem-detail/6/", "Sec-Fetch-Site": "same-origin",
            "Sec-Fetch-Dest": "empty", "Accept-Encoding": "gzip, deflate, br", "Sec-Fetch-Mode": "cors", "S": self.S,
            "Accept-Language": "zh-CN,zh;q=0.9", "Sec-Ch-Ua-Mobile": "?0"}

    def req_url(self):
        # proxy = {"http": "http://127.0.0.1:7890", "https": "http://127.0.0.1:7890"}
        response = requests.get(self.url, headers=self.headers)
        response_json = response.json()
        return response_json

    @staticmethod
    def decrypt_data(response_json):
        cipher = AES.new(key, AES.MODE_CBC, iv)
        encrypt_data = binascii.unhexlify(response_json['t'])
        decrypt_data = cipher.decrypt(encrypt_data)
        padding_len = decrypt_data[-1]
        plaintext = decrypt_data[:-padding_len]
        print(plaintext)
        return plaintext.decode('unicode_escape')

    def run(self):
        response_json = self.req_url()
        result = self.decrypt_data(response_json)
        current_array = json.loads(result)['current_array']
        print(f"第{self.page}页:", current_array, "--> 求和结果为: ", sum(current_array))
        print(result)
        print("\n")
        return sum(current_array)


if __name__ == '__main__':
    end_time = timer.exec_time()
    key = b'xxxxxxxxoooooooo'
    iv = b'0123456789ABCDEF'
    cookie = "sessionid=lbhz9apisibqyvxil5xu0qqu44v6tyhh; Hm_lvt_b5d072258d61ab3cd6a9d485aac7f183=1747983150,1747992419,1748013053,1748070387; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1747962794,1749190454,1749553052; HMACCOUNT=EA832FD00F9EA5FD; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1749557767"
    total_num = 0
    for i in range(1, 21):
        tulin = TuLinSix(i, key, iv, cookie)
        total_num += tulin.run()
    print("20页总和为: ", total_num)
    end_time()
