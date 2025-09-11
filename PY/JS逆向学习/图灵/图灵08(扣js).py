# -*- coding: utf-8 -*-
# TODO:@ModuleName: 图灵08-->扣js代码实现
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/09/11 17:21
import execjs
import requests
import time
import base64


# total_sum = 0
# for page in range(1, 21):
#     # TODO: 获取当前时间戳（毫秒）
#     timestamp = str(int(time.time() * 1000))
#     # TODO: 将时间戳进行 Base64 编码，用于请求头 T
#     base64_ts = base64.b64encode(timestamp.encode()).decode()
#     print(base64_ts)
#     # TODO: 读取 JS 文件
#     with open("图灵08.js", "r", encoding="utf-8") as f:
#         js_code = f.read()
#     # TODO: 编译 JS
#     ctx = execjs.compile(js_code)
#     # TODO: 调用 OOOoOo 函数生成 M 参数
#     M = ctx.call("OOOoOo", f"oooooo{timestamp}{page}", "oooooo")
#     print("[生成M]:", M)
#
#     # TODO: 构造请求头
#     headers = {
#         'Host': 'www.mashangpa.com',
#         'Pragma': 'no-cache',
#         'Cache-Control': 'no-cache',
#         'Sec-Ch-Ua-Platform': '"Windows"',
#         'Sec-Ch-Ua': '"Not;A=Brand";v="99", "Google Chrome";v="139", "Chromium";v="139"',
#         'Sec-Ch-Ua-Mobile': '?0',
#         'M': M,
#         'X-Requested-With': 'XMLHttpRequest',
#         'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36',
#         'Accept': '*/*',
#         'T': base64_ts,
#         'Content-Type': 'application/json',
#         'Origin': 'https://www.mashangpa.com',
#         'Sec-Fetch-Site': 'same-origin',
#         'Sec-Fetch-Mode': 'cors',
#         'Sec-Fetch-Dest': 'empty',
#         'Referer': 'https://www.mashangpa.com/problem-detail/8/',
#         'Accept-Language': 'zh-CN,zh;q=0.9',
#         'Priority': 'u=1, i',
#         'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757325019,1757337823,1757566712,1757571135; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757582433; s=51b351b351b351b370b0f0b0f05110d030907151b0',
#     }
#
#     # TODO: 请求体
#     json_data = {
#         'page': page,
#     }
#
#     # TODO: 发送 POST 请求
#     response = requests.post(
#         'https://www.mashangpa.com/api/problem-detail/8/data/',
#         headers=headers,
#         json=json_data,
#     ).json()
#     current_array = response['current_array']
#     # TODO: 对列表内容求和
#     num_sum = sum(current_array)
#     print(f"第{page}页的累加和为:", num_sum)
#     print(response, end='\n\n')
#     total_sum += num_sum
# print("\n最终20页累加和为:", total_sum)


class TuLinEightJS:
    """
    通过调用外部 JS 文件来解决图灵08问题
    """

    def __init__(self):
        # 编译 JS, 只执行一次以提高效率
        with open("图灵08.js", "r", encoding="utf-8") as f:
            js_code = f.read()
        self.ctx = execjs.compile(js_code)
        self.session = requests.Session()
        self.url = 'https://www.mashangpa.com/api/problem-detail/8/data/'

    def get_params(self, page: int) -> tuple:
        """
        根据页码生成请求所需的 M 和 T 参数
        """
        # 1. 获取当前时间戳（毫秒）
        timestamp = str(int(time.time() * 1000))

        # 2. 生成 T 参数 (Base64 编码的时间戳)
        t_param = base64.b64encode(timestamp.encode()).decode()

        # --- 问题修复点 ---
        # 3. 构造正确的加密输入字符串，必须包含 Prefix, timestamp 和 page
        # 原始错误代码: f"oooooo{timestamp}"
        # 正确代码: f"oooooo{timestamp}{page}"
        encrypt_input = f"oooooo{timestamp}{page}"

        # 4. 调用 JS 函数生成 M 参数
        m_param = self.ctx.call("OOOoOo", encrypt_input, "oooooo")

        return m_param, t_param

    def run(self):
        total_sum = 0
        for page in range(1, 21):
            m, t = self.get_params(page)

            headers = {
                'M': str(m),
                'T': str(t),
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36',
                'Content-Type': 'application/json',
                'Referer': 'https://www.mashangpa.com/problem-detail/8/',
                'X-Requested-With': 'XMLHttpRequest',
                'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757325019,1757337823,1757566712,1757571135; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757582433; s=51b351b351b351b370b0f0b0f05110d030907151b0',
            }

            json_data = {'page': page}

            response = self.session.post(self.url, headers=headers, json=json_data)

            data = response.json()
            current_array = data.get('current_array', [])
            page_sum = sum(current_array)

            print(f"第 {page} 页的累加和为: {page_sum}")
            total_sum += page_sum

        print("\n最终20页累加和为:", total_sum)


if __name__ == '__main__':
    solver = TuLinEightJS()
    solver.run()
