# -*- coding: utf-8 -*-
# TODO:@ModuleName:
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/09/12 16:49
import json
import requests
import time
import hashlib

""""
-注入函数
window.rpc_decrypt = function(data) {
    // 调用你已经逆向出来的 xxxxoooo 函数
    return xxxxoooo(data);
};

-注册函数
demo.regAction("my_decrypt", function (resolve, param) {
    let result = window.rpc_decrypt(param);
    resolve(result);
});
"""


def call_decrypt_rpc(encrypted_hex_string):
    """
    通过 jsrpc 远程调用浏览器中的解密函数
    :param encrypted_hex_string: 需要解密的十六进制字符串
    :return: 解密后的数据
    """
    # jsrpc 服务端地址
    rpc_url = "http://127.0.0.1:12080/go"

    # 构建请求数据，group 和 action 必须与浏览器端注册的保持一致
    payload = {
        "group": "zzz",
        "action": "my_decrypt",
        "param": encrypted_hex_string
    }

    # 发送 POST 请求
    response = requests.post(rpc_url, data=payload)

    # 解析返回的 JSON 数据
    response_json = response.json()

    # 返回解密结果
    return response_json.get('data')


total_sum = 0
for page in range(1, 21):
    Tt = str(int(time.time() * 1000))
    # TODO: md5(字符串+时间戳)
    S = hashlib.md5(("sssssbbbbb" + Tt).encode("utf-8")).hexdigest()
    headers = {
        'Host': 'www.mashangpa.com',
        'Pragma': 'no-cache',
        'Cache-Control': 'no-cache',
        'Sec-Ch-Ua-Platform': '"Windows"',
        'Tt': Tt,
        'S': S,
        'Sec-Ch-Ua': '"Not;A=Brand";v="99", "Google Chrome";v="139", "Chromium";v="139"',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36',
        'Sec-Ch-Ua-Mobile': '?0',
        'Accept': '*/*',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Dest': 'empty',
        'Referer': 'https://www.mashangpa.com/problem-detail/6/',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Priority': 'u=1, i',
        'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757566712,1757571135,1757597958,1757640983; HMACCOUNT=5ADB897338667860; s=51b351b351b351b370b0f0b090d0f09090307030f0; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757666879',
    }

    params = {
        'page': '2',
    }

    response = requests.get(
        'https://www.mashangpa.com/api/problem-detail/6/data/',
        params=params,
        headers=headers,
    ).json()
    encrypt_data = response['t']
    decrypt_data = call_decrypt_rpc(encrypt_data)
    decrypt_data = json.loads(decrypt_data)
    final_response = json.dumps(decrypt_data, ensure_ascii=False)
    current_array = decrypt_data['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(final_response, end='\n\n')
    total_sum += num_sum
    time.sleep(1)
print("\n最终20页累加和为:", total_sum)
