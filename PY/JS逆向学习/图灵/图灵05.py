# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵05
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/5/23 18:27
import hashlib
import time
import requests
from Crypto.Cipher import AES
from Crypto.Util.Padding import unpad, pad
import binascii
import json

key = b"jo8j9wGw%6HbxfFn"
iv = b"0123456789ABCDEF"

# # TODO: 来自服务器响应的 hex 密文
# hex_data = "5a8bf85c47362330192855aee8b84b7792ace31e7c8edf6d55ac79b048e8bb2c"
#
# # TODO: 转换为字节串
# ciphertext = binascii.unhexlify(hex_data)
#
# # TODO: 创建 AES 解密器并解密
# cipher = AES.new(key, AES.MODE_CBC, iv)
#
# decrypted = cipher.decrypt(ciphertext)
# # TODO: 去除填充
# plaintext = unpad(decrypted, AES.block_size)
#
# # TODO: 打印明文 --> {"page":5,"_ts":1747995681123}
# print(plaintext.decode('utf-8'))

total_sum = 0
for page in range(1, 21):
    url = 'https://www.mashangpa.com/api/problem-detail/5/data/'
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

    orgin_data = {
        "page": page,
        "_ts": time.time() * 1000
    }

    # TODO: 转换为 JSON 字符串并加密
    raw = json.dumps(orgin_data).encode('utf-8')
    cipher = AES.new(key, AES.MODE_CBC, iv)
    encrypted = cipher.encrypt(pad(raw, AES.block_size))
    hex_cipher = encrypted.hex()

    print("加密后的内容:", hex_cipher)
    data = {"xl": hex_cipher}

    response = requests.post(url, headers=headers, json=data).json()
    current_array = response['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response, end='\n\n')
    total_sum += num_sum
    time.sleep(1)
print("\n最终20页累加和为:", total_sum)
