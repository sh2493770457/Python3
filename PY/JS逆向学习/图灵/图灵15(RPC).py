# -*- coding: utf-8 -*-
# TODO:@ModuleName: 图灵15 RPC远程调用(无参数传递类型)
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/09/28 16:03

"""
-注入函数
window.D_debug = function() {
  console.log("[D_debug] 正在调用 D() 函数...");
  let result = D(); // 调用原始的D()函数
  console.log("[D_debug] D() 函数返回结果:", result);
  return result;
};

-注册动作
demo.regAction("get_v_cookie", function (resolve) {
  try {
    let result = D_debug();
    // 关键：务必将返回结果转为字符串，防止JSRPC服务端处理非字符串类型时出错
    resolve(String(result));
  } catch (e) {
    resolve("调用失败:" + e.toString());
  }
});
"""
import requests
import time


def get_v_cookie():
    """
    通过 JSRPC 远程调用浏览器中的 D() 函数，获取 v 值。
    此函数不需要传递参数。
    """
    # 这里的 'action' 参数必须与浏览器中 regAction 注册的名称 'get_v_cookie' 一致
    url = "http://127.0.0.1:12080/go?group=zzz&action=get_v_cookie"

    resp = requests.get(url)
    v_value = resp.json()['data']
    return v_value


total_sum = 0
for page in range(1, 21):
    v = get_v_cookie()
    print(f"成功获取到 v 值: {v}")
    cookies = {
        'sessionid': 'ub2p253ntgy23lyqfl2pkm50jrwkrmyi',
        '_nano_fp': 'XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj',
        'Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0': '1758726296,1758727507,1759024851,1759028808',
        'HMACCOUNT': '5ADB897338667860',
        'Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0': '1759043649',
        'v': v,
    }

    headers = {
        'Host': 'www.mashangpa.com',
        'Sec-Ch-Ua-Platform': '"Windows"',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',
        'Sec-Ch-Ua': '"Chromium";v="140", "Not=A?Brand";v="24", "Google Chrome";v="140"',
        'Hexin-V': v,
        'Sec-Ch-Ua-Mobile': '?0',
        'Accept': '*/*',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Dest': 'empty',
        'Referer': 'https://www.mashangpa.com/problem-detail/15/',
        # 'Accept-Encoding': 'gzip, deflate, br',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Priority': 'u=1, i',
        # 'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; _nano_fp=XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1758726296,1758727507,1759024851,1759028808; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1759043649; v=QTZMeDhRUXBNWDd2SnlMNV9QYWVqSTJ0OHlNQjg2UnFXUFdhTXV3N3pZWEM4a3lkMUlQMkhTaUg2a2VfMTc1OTA0MzY3Nzg5NA==',
    }

    params = {
        'page': page,
    }

    response = requests.get(
        'https://www.mashangpa.com/api/problem-detail/15/data/',
        params=params,
        cookies=cookies,
        headers=headers,
    )
    current_array = response.json()['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response.text.encode('utf-8').decode('unicode_escape'), end='\n\n')
    total_sum += num_sum
    time.sleep(0.1)
print("\n最终20页累加和为:", total_sum)
