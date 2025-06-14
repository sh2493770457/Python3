# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 未授权批量验证
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/6/14 20:40
import json
import requests
import urllib3


class Unauthorized:
    # TODO: 关闭警告(部分https不被信任的情况)
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

    def __init__(self, Json_path, Headers):
        self.json_path = Json_path
        self.headers = Headers

    def read_api_json(self):
        with open(self.json_path, 'r', encoding='utf-8') as f:
            api_json = json.load(f)
        return api_json

    def request_api(self):
        api_json = self.read_api_json()
        # TODO: 遍历接口,对键值对进行判断,值为空get请求,否则post
        for url, json_data in api_json.items():
            if json_data is None:
                print(f"[GET]: {url}")
                response = requests.get(url, headers=self.headers, verify=False)
                print(f"响应状态码: {response.status_code}")
                print(f"响应内容: {response.text}")
                print("\n")
            else:
                print(f"[POST]: {url}")
                response = requests.post(url, headers=self.headers, json=json_data, verify=False)
                print(f"响应状态码: {response.status_code}")
                print(f"响应内容: {response.text}")
                print("\n")


if __name__ == '__main__':
    headers = {
        "Sec-Ch-Ua": "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
        "Accept": "application/json, text/plain, */*",
        "Sec-Ch-Ua-Platform": "\"Windows\"",
        "Priority": "u=1, i",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
        "Sec-Fetch-Site": "same-origin",
        "Sec-Fetch-Dest": "empty",
        "Token": "xxxxxxxxxxxxx",
        "Accept-Encoding": "gzip, deflate, br",
        "Sec-Fetch-Mode": "cors",
        "Accept-Language": "zh-CN,zh;q=0.9",
        "Sec-Ch-Ua-Mobile": "?0"
    }
    json_path = r"C:\Users\xxxx\Desktop\api_20250614_202825.json"
    Unauthorized = Unauthorized(json_path, headers)
    Unauthorized.request_api()
