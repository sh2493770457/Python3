# -*- coding: utf-8 -*-
# TODO:@ModuleName: 图灵09-->扣js
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/09/11 20:30
import base64
import requests
import time
import execjs


#
# total_sum = 0
# for page in range(1, 21):
#     headers = {
#         'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36',
#         'Sec-Ch-Ua': '"Not;A=Brand";v="99", "Google Chrome";v="139", "Chromium";v="139"',
#         'Content-Type': 'application/json',
#         'Origin': 'https://www.mashangpa.com',
#         'Referer': 'https://www.mashangpa.com/problem-detail/9/',
#         'Accept-Language': 'zh-CN,zh;q=0.9',
#         'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757325019,1757337823,1757566712,1757571135; HMACCOUNT=5ADB897338667860; s=51b351b351b351b370b0f0b0f051f051d0509050b0; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757593813',
#     }
#     with open("图灵09.js", 'r', encoding='utf-8') as f:
#         js_code = f.read()
#     ctx = execjs.compile(js_code)
#     timestamp = str(int(time.time() * 1000))
#     m = ctx.call("get_m", "9527" + timestamp)
#     tt = base64.b64encode(timestamp.encode()).decode()
#     json_data = {
#         'page': page,
#         'm': m,
#         'tt': tt,
#     }
#
#     response = requests.post(
#         'https://www.mashangpa.com/api/problem-detail/9/data/',
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

# -*- coding: utf-8 -*-


class TuringNineSolver:
    """
    一个用于解决图灵09问题的面向对象爬虫。
    它封装了JS编译、参数生成和API请求的逻辑。
    """

    def __init__(self):
        """
        初始化爬虫，设置会话、URL，并预编译JS文件以提高效率。
        """
        self.api_url = 'https://www.mashangpa.com/api/problem-detail/9/data/'
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/5.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36',
            'Sec-Ch-Ua': '"Not;A=Brand";v="99", "Google Chrome";v="139", "Chromium";v="139"',
            'Content-Type': 'application/json',
            'Origin': 'https://www.mashangpa.com',
            'Referer': 'https://www.mashangpa.com/problem-detail/9/',
            'Accept-Language': 'zh-CN,zh;q=0.9',
            'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757325019,1757337823,1757566712,1757571135; HMACCOUNT=5ADB897338667860; s=51b351b351b351b370b0f0b0f051f051d0509050b0; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757593813',
        })

        # 在初始化时只编译一次JS，避免在循环中重复读写文件和编译
        with open("图灵09.js", 'r', encoding='utf-8') as f:
            js_code = f.read()
        self.js_ctx = execjs.compile(js_code)

    def _generate_params(self):
        """
        生成单次请求所需的动态参数 m 和 tt。
        这是一个内部方法。
        """
        timestamp = str(int(time.time() * 1000))
        message_to_encrypt = "9527" + timestamp

        m = self.js_ctx.call("get_m", message_to_encrypt)
        tt = base64.b64encode(timestamp.encode()).decode()

        return m, tt

    def fetch_page_data(self, page):
        """
        获取指定页码的数据。
        """
        m, tt = self._generate_params()

        json_data = {
            'page': page,
            'm': m,
            'tt': tt,
        }

        response = self.session.post(self.api_url, json=json_data)
        response.raise_for_status()  # 如果请求失败（非2xx状态码），则抛出异常
        return response.json()

    def run(self):
        """
        执行主逻辑，遍历所有页面并计算总和。
        """
        total_sum = 0
        for page in range(1, 21):
            try:
                data = self.fetch_page_data(page)
                current_array = data.get('current_array', [])

                # 对列表内容求和
                num_sum = sum(current_array)
                print(f"第{page}页的累加和为: {num_sum}")
                print(data, end='\n\n')

                total_sum += num_sum
                # time.sleep(0.5) # 可以在请求之间添加短暂延时，模仿人类行为
            except requests.exceptions.RequestException as e:
                print(f"请求第{page}页时发生错误: {e}")
            except Exception as e:
                print(f"处理第{page}页数据时发生未知错误: {e}")

        print(f"\n最终20页累加和为: {total_sum}")


if __name__ == '__main__':
    solver = TuringNineSolver()
    solver.run()
