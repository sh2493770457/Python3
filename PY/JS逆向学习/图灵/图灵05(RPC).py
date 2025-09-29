""""
-注入函数
window.encrypt_debug = function(data) {
    console.log("[encrypt_debug] 输入参数:", data);
    let result = encrypt(data);
    console.log("[encrypt_debug] 加密结果:", result);
    return result;
};

-注册函数
demo.regAction("get_encrypt_data", function (resolve, param) {
    let result = encrypt_debug(param);
    resolve(result);
});
"""
import requests
import json
import time


def get_remote_encrypted_data(payload_dict):
    """
    通过JSRPC远程调用网页的加密函数。
    :param payload_dict: 需要加密的原始数据字典。
    """
    # JSRPC服务端的地址
    jrpc_url = "http://127.0.0.1:12080/go"
    # 将数据字典转换为无多余空格的JSON字符串
    json_string_to_encrypt = json.dumps(payload_dict, separators=(',', ':'))
    # 构建POST请求的数据
    # action名必须与第四步中注册的 "get_encrypt_data" 一致
    post_data = {
        "group": "zzz",
        "action": "get_encrypt_data",
        "param": json_string_to_encrypt
    }
    # 发送POST请求
    response = requests.post(jrpc_url, data=post_data)
    # 解析返回的JSON并获取加密后的数据
    encrypted_data = response.json()['data']
    # 打印结果
    return encrypted_data


total_sum = 0
for page in range(1, 21):
    url = 'https://www.mashangpa.com/api/problem-detail/5/data/'
    headers = {
        'Sec-Ch-Ua': '"Chromium";v="136", "Google Chrome";v="136", "Not.A/Brand";v="99"',
        'Accept': '*/*',
        'Sec-Ch-Ua-Platform': '"Windows"',
        'Priority': 'u=1, i',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36',
        'Referer': 'https://www.mashangpa.com/problem-detail/4/',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-Dest': 'empty',
        'Pragma': 'no-cache',
        'Accept-Encoding': 'gzip, deflate, br',
        'Sec-Fetch-Mode': 'cors',
        'Cache-Control': 'no-cache',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1757566712,1757571135,1757597958,1757640983; HMACCOUNT=5ADB897338667860; s=51b351b351b351b370b0f0b090d0f09090307030f0; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1757663728',
    }

    orgin_data = {
        "page": page,
        "_ts": int(time.time() * 1000)
    }

    data = {"xl": get_remote_encrypted_data(json.dumps(orgin_data))}
    print(data)
    response = requests.post(url, headers=headers, json=data).json()
    current_array = response['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response, end='\n\n')
    total_sum += num_sum
    time.sleep(1)
print("\n最终20页累加和为:", total_sum)
