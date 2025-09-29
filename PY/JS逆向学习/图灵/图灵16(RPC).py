# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵16->RPC
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/9/28 16:53
"""
 _$lk.prototype.sign = function(_$lR) {
        var EO = lj;
        try {
            var _$lw = this._$sdnmd(_$lR);
            _$lw = btoa(_$lw.h5st+String(_$lw.t))
            return _$BC.resolve(_$lw);
        } catch (_$lD) {
            return this._onSign({
                'code': _$TE,
                'message': EO(0x23f)
            }),
            _$BC.resolve(_$lR);
        }
}
步骤:
1.先执行window.myLkInstance = this; -->记住先断点,执行,再注入下面的函数
2.注入函数

// 创建一个全局代理函数，你可以随意命名
// 我们叫它 invoke_lk_sign 来明确它的作用
window.invoke_lk_sign = function(params) {
  // 确保已通过断点捕获了实例
  if (!window.myLkInstance) {
    console.error("错误：请先设置断点并运行 'window.myLkInstance = this;'");
    return;
  }

  // 这里，我们调用的就是原始函数：_$lk.prototype.sign
  // .call() 方法确保了函数内部的 'this' 指向我们捕获的 myLkInstance
  return window.myLkInstance.sign.call(window.myLkInstance, params);
}

3.注册动作
// 注册一个名为 "get_signature" 的动作
demo.regAction("get_signature", function(resolve, param) {
  console.log("[JSRPC] 接收到签名请求，参数:", param);

  // 调用我们之前创建的、能处理Promise的代理函数
  invoke_lk_sign(param)
    .then(signature => {
      console.log("[JSRPC] 签名已生成:", signature);
      // 成功后，通过 resolve 将签名（必须是字符串）返回给 Python
      resolve(String(signature));
    })
    .catch(error => {
      console.error("[JSRPC] 生成签名时发生错误:", error);
      resolve("错误: " + error.toString());
    });
});

console.log("JSRPC 动作 'get_signature' 已注册，准备好接收 Python 调用。");

4.建立连接调用
"""
import requests
import json
import time


def get_signature_rpc(page_number):
    """
    通过 JSRPC 远程调用浏览器来生成签名。

    :param page_number: 需要获取签名的页码 (int)。
    :return: 浏览器返回的签名字符串 (str)。
    """
    jsrpc_url = "http://127.0.0.1:12080/go"

    # 根据之前的分析，我们知道浏览器会覆盖时间戳 t，
    # 所以这里我们传一个当前时间即可，主要是为了保持参数结构完整。
    params = {
        "page": page_number,
        "t": timestamp
    }

    payload = {
        "group": "zzz",
        "action": "get_signature",  # 必须和浏览器 regAction 的名字一致
        "param": json.dumps(params)  # 将参数对象序列化为JSON字符串
    }

    resp = requests.post(jsrpc_url, data=payload)
    resp.raise_for_status()  # 如果请求失败 (例如JSRPC服务没打开)，会抛出异常

    # 解析返回的JSON并提取签名数据
    signature = resp.json()['data']
    return signature


total_sum = 0
for page in range(1, 21):
    timestamp = int(time.time() * 1000)
    headers = {
        'X-Requested-With': 'XMLHttpRequest',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',
        'Origin': 'https://www.mashangpa.com',
        'Cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; _nano_fp=XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1758726296,1758727507,1759024851,1759028808; HMACCOUNT=5ADB897338667860; v=QS1SVnhZbzdYOVFkMGFSRFF5eEFGbDhidGVuVGZRa0x5cUtjSmY0RWNfZHlnSXJYSm8zWWR4cXhiS2ROMTc1OTA0NzEyOTI1NQ==; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1759047352',
    }
    data = f'{{"page":{page},"t":{timestamp},"h5":"{get_signature_rpc(page)}"}}'

    response = requests.post(
        'https://www.mashangpa.com/api/problem-detail/16/data/',
        headers=headers,
        data=data,
    )
    current_array = response.json()['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response.text.encode('utf-8').decode('unicode_escape'), end='\n\n')
    total_sum += num_sum
    time.sleep(0.1)
print("\n最终20页累加和为:", total_sum)
