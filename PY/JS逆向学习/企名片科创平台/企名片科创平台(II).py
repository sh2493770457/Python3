# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 企名片科创平台(II) -->面向对象
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/4/6 00:44

import requests
from py_mini_racer import py_mini_racer


class QMPDecryptor:
    """
    加载js解密
    """

    def __init__(self, js_file: str):
        self.js_file = js_file
        self.ctx = py_mini_racer.MiniRacer()
        with open(js_file, encoding='utf-8') as f:
            js_code = f.read()
        self.ctx.eval(js_code)

    def decrypt(self, encrypt_data: str):
        """
        :param encrypt_data:
        :return:
        """
        return self.ctx.call('Fc', encrypt_data)


class QMPRequest:
    """
    QMP “企名片” 科创平台 API 客户端，封装请求和解密流程。
    """
    BASE_URL = 'https://wyiosapi.qmpsee.com/Web/getCaDetail'

    def __init__(self, decryptor):
        self.decryptor = decryptor
        self.headers = {
            'Accept': 'application/json, text/plain, */*',
            'Accept-Language': 'zh-CN,zh;q=0.9',
            'Cache-Control': 'no-cache',
            'Connection': 'keep-alive',
            'Content-Type': 'application/x-www-form-urlencoded',
            'Origin': 'https://wx.qmpsee.com',
            'Platform': 'web',
            'Pragma': 'no-cache',
            'Sec-Fetch-Dest': 'empty',
            'Sec-Fetch-Mode': 'cors',
            'Sec-Fetch-Site': 'same-site',
            'Source': 'see',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) '
                          'AppleWebKit/537.36 (KHTML, like Gecko) '
                          'Chrome/134.0.0.0 Safari/537.36',
            'sec-ch-ua': '"Chromium";v="134", "Not:A-Brand";v="24", "Google Chrome";v="134"',
            'sec-ch-ua-mobile': '?0',
            'sec-ch-ua-platform': '"Windows"',
        }

    def get_detail(self, page: int, num: int, ca_uuid: str) -> dict:
        """
        请求 getCaDetail 接口并返回解密后的数据。

        :param page: 页码
        :param num: 每页数量
        :param ca_uuid: ca_uuid 参数
        :return: 解密后的 Python dict
        """
        payload = {
            'page': str(page),
            'num': str(num),
            'ca_uuid': ca_uuid,
        }
        resp = requests.post(self.BASE_URL, headers=self.headers, data=payload)
        resp.raise_for_status()
        body = resp.json()
        encrypt_data = body.get('encrypt_data')
        if not encrypt_data:
            raise ValueError("接口返回数据中缺少 'encrypt_data'")
        return self.decryptor.decrypt(encrypt_data)


if __name__ == '__main__':
    # 1. 初始化解密器，传入 JS 文件路径
    decrypt = QMPDecryptor('企名片科创平台.js')
    # 2. 初始化请求客户端
    client = QMPRequest(decrypt)
    # 3. 调用接口并打印解密结果
    result = client.get_detail(1, 20, 'feef62bfdac45a94b9cd89aed5c235be')
    print(result)
