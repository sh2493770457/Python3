# -*- coding: utf-8 -*-
# TODO:@ModuleName: 图灵11-->RPC
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/09/12 18:44
import time
import requests
import json

"""
-注入函数
window.callEncryptFunction_debug = function(c, d) {
    console.log("[callEncryptFunction_debug] 输入参数 c:", c);
    console.log("[callEncryptFunction_debug] 输入参数 d:", d);
    let result = callEncryptFunction(c, d);
    console.log("[callEncryptFunction_debug] 返回结果:", result);
    return result;
};

-注册动作
demo.regAction("callEncryptFunction_debug", function (resolve, param) {
    // param 默认会是一个JSON字符串，JSRPC环境已自动解析为对象
    // 我们将从 param 对象中获取 c 和 d
    let result = callEncryptFunction_debug(param["c"], param["d"]);
    // 关键步骤：必须将结果转为字符串再返回，否则可能导致Python端接收不到数据
    resolve(String(result));
});
"""


def invoke_encrypt_function(c_param, d_param):
    """
    通过 JSRPC 远程调用浏览器中的 callEncryptFunction_debug 函数。
    :param c_param: 对应浏览器中的参数 c
    :param d_param: 对应浏览器中的参数 d
    :return: 经过加密函数处理后的结果字符串
    """
    # JSRPC 服务的地址
    jrpc_url = "http://127.0.0.1:12080/go"
    # 构造请求数据，使用 POST 方式
    # 将多个参数打包成一个JSON对象字符串
    payload = {
        "group": "zzz",
        "action": "callEncryptFunction_debug",
        "param": json.dumps({"c": c_param, "d": d_param})
    }

    # 发送POST请求
    resp = requests.post(jrpc_url, data=payload)

    # 解析返回的JSON并直接返回 "data" 字段
    return resp.json()['data']


total_sum = 0
for page in range(1, 21):
    cookies = {
        'sessionid': 'ub2p253ntgy23lyqfl2pkm50jrwkrmyi',
        'Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0': '1757566712,1757571135,1757597958,1757640983',
        'HMACCOUNT': '5ADB897338667860',
        's': '51b351b351b351b370b0f0b090d0f09090307030f0',
        'Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0': '1757670909',
    }

    headers = {
        'Host': 'www.mashangpa.com',
        'Pragma': 'no-cache',
        'Cache-Control': 'no-cache',
        'Sec-Ch-Ua-Platform': '"Windows"',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36',
        'Sec-Ch-Ua': '"Not;A=Brand";v="99", "Google Chrome";v="139", "Chromium";v="139"',
        'Sec-Ch-Ua-Mobile': '?0',
        'Accept': '*/*',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Dest': 'empty',
        'Referer': 'https://www.mashangpa.com/problem-detail/11/',
        # 'Accept-Encoding': 'gzip, deflate, br',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Priority': 'u=1, i',
        # 'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757566712,1757571135,1757597958,1757640983; HMACCOUNT=5ADB897338667860; s=51b351b351b351b370b0f0b090d0f09090307030f0; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757670909',
    }
    timestamp = str(int(time.time()))
    params = {
        'page': page,
        'm': invoke_encrypt_function(page, timestamp),
        '_ts': timestamp,
    }
    response = requests.get(
        'https://www.mashangpa.com/api/problem-detail/11/data/',
        params=params,
        cookies=cookies,
        headers=headers,
    ).json()
    current_array = response['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response, end='\n\n')
    total_sum += num_sum
print("\n最终20页累加和为:", total_sum)
