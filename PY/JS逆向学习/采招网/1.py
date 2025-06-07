import csv
import requests
import base64
from Crypto.Cipher import AES
import json

# 定义请求头
headers = {
    'accept': 'application/json, text/javascript, */*; q=0.01',
    'accept-language': 'zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6',
    'cache-control': 'no-cache',
    'content-type': 'application/x-www-form-urlencoded; charset=UTF-8',
    'origin': 'https://search.bidcenter.com.cn',
    'pragma': 'no-cache',
    'priority': 'u=1, i',
    'referer': 'https://search.bidcenter.com.cn/',
    'sec-ch-ua': '"Not/A)Brand";v="8", "Chromium";v="126", "Microsoft Edge";v="126"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
    'sec-fetch-dest': 'empty',
    'sec-fetch-mode': 'cors',
    'sec-fetch-site': 'same-site',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0',
}

data = 'from=6137&location=6138&guid=Fri+Jul+05+2024+05%3A18%3A58+GMT%2B0800+(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)&token='

response = requests.post('https://interface.bidcenter.com.cn/index/TuijianKeywordsHandler.ashx', headers=headers,
                         data=data)

# 获取响应数据
response_data = response.text

# 检查响应数据是否为JSON格式
try:
    json_data = json.loads(response_data)
    print("JSON Data:", json_data)

    # 如果有需要解密的字段，这里示例假设字段名为 "encrypted_data"
    if 'encrypted_data' in json_data:
        base64_encrypt_data = json_data['encrypted_data']

        # 解码base64数据
        encrypt_data = base64.b64decode(base64_encrypt_data)

        # AES解密所需的密钥和初始向量
        key = '3zKzyf6eEfuDjAG3'.encode()
        iv = 'fyUANZ0qSNZhhNCV'.encode()

        # 创建AES解密器
        aes = AES.new(key, AES.MODE_CBC, iv)
        decrypted_data = aes.decrypt(encrypt_data)

        print(decrypted_data.decode())
    else:
        print("未找到加密数据字段")
except json.JSONDecodeError:
    print("响应数据不是有效的JSON格式:", response_data)


