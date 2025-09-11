# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵10
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/9/11 16:18
import execjs
import requests


# total_sum = 0
# for page in range(1,21):
#     headers = {
#         'Sec-Ch-Ua-Platform': '"Windows"',
#         'X-Requested-With': 'XMLHttpRequest',
#         'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36',
#         'Accept': 'application/json, text/javascript, */*; q=0.01',
#         'Sec-Ch-Ua': '"Not;A=Brand";v="99", "Google Chrome";v="139", "Chromium";v="139"',
#         'Sec-Ch-Ua-Mobile': '?0',
#         'Sec-Fetch-Site': 'same-origin',
#         'Sec-Fetch-Mode': 'cors',
#         'Sec-Fetch-Dest': 'empty',
#         'Referer': 'https://www.mashangpa.com/problem-detail/10/',
#         'Accept-Language': 'zh-CN,zh;q=0.9',
#         'Priority': 'u=1, i',
#         'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757325019,1757337823,1757566712,1757571135; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757572925',
#     }
#     with open("图灵10.js", "r", encoding="utf-8") as f:
#         js_code = f.read()
#     ctx = execjs.compile(js_code)
#     t = ctx.call("OOOO", f"/api/problem-detail/10/data/?page={page}")
#     print(t)
#     params = {
#         'page': page,
#         't': t,
#     }
#     response = requests.get(
#         'https://www.mashangpa.com/api/problem-detail/10/data/',
#         params=params,
#         headers=headers,
#     ).json()
#     current_array = response['current_array']
#     # TODO: 对列表内容求和
#     num_sum = sum(current_array)
#     print(f"第{page}页的累加和为:", num_sum)
#     print(response, end='\n\n')
#     total_sum += num_sum
# print("\n最终20页累加和为:", total_sum)


class TulinTen:
    """
    调用图灵加密 JS 获取参数 t 并请求接口
    """

    def __init__(self):
        self.session = requests.Session()  # 使用 Session 保持连接和 headers
        self.session.headers.update({
            'Sec-Ch-Ua-Platform': '"Windows"',
            'X-Requested-With': 'XMLHttpRequest',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36',
            'Accept': 'application/json, text/javascript, */*; q=0.01',
            'Sec-Ch-Ua': '"Not;A=Brand";v="99", "Google Chrome";v="139", "Chromium";v="139"',
            'Sec-Ch-Ua-Mobile': '?0',
            'Sec-Fetch-Site': 'same-origin',
            'Sec-Fetch-Mode': 'cors',
            'Sec-Fetch-Dest': 'empty',
            'Referer': 'https://www.mashangpa.com/problem-detail/10/',
            'Accept-Language': 'zh-CN,zh;q=0.9',
            'Priority': 'u=1, i',
            'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757325019,1757337823,1757566712,1757571135; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757572925',
        })
        self.js_path = "图灵10.js"

        # 预编译 JS
        with open(self.js_path, "r", encoding="utf-8") as f:
            js_code = f.read()
        self.ctx = execjs.compile(js_code)

    def get_t(self, Page: int) -> str:
        """
        调用 JS 函数 OOOO 获取 t 参数
        :param Page: 页码
        :return: 加密后的 t 字符串
        """
        path = f"/api/problem-detail/10/data/?page={Page}"
        T = self.ctx.call("OOOO", path)
        return T

    def req_api(self, params: dict) -> dict:
        """
        请求目标接口并返回 JSON 数据
        :param params: 请求参数
        :return: 接口返回的 JSON 数据
        """
        correct_url = "https://www.mashangpa.com/api/problem-detail/10/data/"
        resp = self.session.get(
            correct_url,
            params=params
        )
        return resp.json()


if __name__ == '__main__':
    tulin_ten = TulinTen()
    total_sum = 0

    for page in range(1, 21):
        t = tulin_ten.get_t(page)

        query = {'page': page, 't': t}
        response = tulin_ten.req_api(query)

        # 对 current_array 列表求和
        current_array = response['current_array']

        page_sum = sum(current_array)
        print(f"第 {page} 页的累加和为: {page_sum}")
        total_sum += page_sum

    print("\n最终20页累加和为:", total_sum)
