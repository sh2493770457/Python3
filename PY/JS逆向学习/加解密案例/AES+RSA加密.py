# -*- encoding: utf-8 -*-
# TODO:@ModuleName: AES+RSA加密
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/8/17 16:15
from Crypto.Cipher import PKCS1_v1_5, AES
from Crypto.PublicKey import RSA
from Crypto.Util.Padding import pad
import base64


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

public_key = """
-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDRvA7giwinEkaTYllDYCkzujvi
NH+up0XAKXQot8RixKGpB7nr8AdidEvuo+wVCxZwDK3hlcRGrrqt0Gxqwc11btlM
DSj92Mr3xSaJcshZU8kfj325L8DRh9jpruphHBfh955ihvbednGAvOHOrz3Qy3Cb
ocDbsNeCwNpRxwjIdQIDAQAB
-----END PUBLIC KEY-----
"""
key = "bb219e386d2a07dd5a87e6d212c6aaed"
iv = "3f59fd8e58ff23bcd8f81ae45f608de4"
origin_data = '{"username":"admin","password":"123456"}'
json_data = {
    'encryptedData': aes_encrypt(origin_data, key, iv),
    'encryptedKey': 'JV/oFaNivNRLMkhVLY7HZBHzAV0xoFaylBlbphydVFAm6wLZjAGHaaCjYYMEbxWvsaaFEw/WgXlk+2byuJPrKEGn6eaVK4pH/8brLMUU8ANqKFmESjU1f24rgJFaz242uK6OZOkM7FKsEJyAUNeruW6C3BkUzrwZv5nuiab553M=',
    'encryptedIv': 'hb/MFgOtJE1W4UNAqbGHD0WzW3inzKzdOQ3mywkMUvl2Ppr1f53pxT/MBbCJCprmTMZWUOkD65vo/5gPyySVz4b2Lpzwv+fmwbQs4Vb22l90QtkY09J9uuyJyW7Jz5TLL2XoDqKak+YG6o2aJrwkKZjCXdPjUlN4tsLNmkolh2M=',
}
