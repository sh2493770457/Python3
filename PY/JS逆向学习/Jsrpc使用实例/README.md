### 第一步:断点定义函数

```js
// 给 Kc 包装一个调试版本
window.kc_debug = function(data) {
    console.log("[kc_debug] 输入参数:", data);
    let result = Kc(data);
    console.log("[kc_debug] 解密结果:", result);
    return result;
};
```

![image-20250613145138000](./assets/image-20250613145138000.png)

******

### 第二部:放掉断点注入环境

```js
var rpc_client_id, Hlclient = function (wsURL) {
    this.wsURL = wsURL;
    this.handlers = {
        _execjs: function (resolve, param) {
            var res = eval(param)
            if (!res) {
                resolve("没有返回值")
            } else {
                resolve(res)
            }
        }
    };
    this.socket = undefined;
    if (!wsURL) {
        throw new Error('wsURL can not be empty!!')
    }
    this.connect()
}
Hlclient.prototype.connect = function () {
    if (this.wsURL.indexOf("clientId=") === -1 && rpc_client_id) {
        this.wsURL += "&clientId=" + rpc_client_id
    }
    console.log('begin of connect to wsURL: ' + this.wsURL);
    var _this = this;
    try {
        this.socket = new WebSocket(this.wsURL);
        this.socket.onmessage = function (e) {
            _this.handlerRequest(e.data)
        }
    } catch (e) {
        console.log("connection failed,reconnect after 10s");
        setTimeout(function () {
            _this.connect()
        }, 10000)
    }
    this.socket.onclose = function () {
        console.log('rpc已关闭');
        setTimeout(function () {
            _this.connect()
        }, 10000)
    }
    this.socket.addEventListener('open', (event) => {
        console.log("rpc连接成功");
    });
    this.socket.addEventListener('error', (event) => {
        console.error('rpc连接出错,请检查是否打开服务端:', event.error);
    })
};
Hlclient.prototype.send = function (msg) {
    this.socket.send(msg)
}
Hlclient.prototype.regAction = function (func_name, func) {
    if (typeof func_name !== 'string') {
        throw new Error("an func_name must be string");
    }
    if (typeof func !== 'function') {
        throw new Error("must be function");
    }
    console.log("register func_name: " + func_name);
    this.handlers[func_name] = func;
    return true
}
Hlclient.prototype.handlerRequest = function (requestJson) {
    var _this = this;
    try {
        var result = JSON.parse(requestJson)
    } catch (error) {
        console.log("请求信息解析错误", requestJson);
        return
    }
    if (result["registerId"]) {
        rpc_client_id = result['registerId']
        return
    }
    if (!result['action'] || !result["message_id"]) {
        console.warn('没有方法或者消息id,不处理');
        return
    }
    var action = result["action"], message_id = result["message_id"]
    var theHandler = this.handlers[action];
    if (!theHandler) {
        this.sendResult(action, message_id, 'action没找到');
        return
    }
    try {
        if (!result["param"]) {
            theHandler(function (response) {
                _this.sendResult(action, message_id, response);
            })
            return
        }
        var param = result["param"]
        try {
            param = JSON.parse(param)
        } catch (e) {
        }
        theHandler(function (response) {
            _this.sendResult(action, message_id, response);
        }, param)
    } catch (e) {
        console.log("error: " + e);
        _this.sendResult(action, message_id, e);
    }
}
Hlclient.prototype.sendResult = function (action, message_id, e) {
    if (typeof e === 'object' && e !== null) {
        try {
            e = JSON.stringify(e)
        } catch (v) {
            console.log(v)//不是json无需操作
        }
    }
    this.send(JSON.stringify({"action": action, "message_id": message_id, "response_data": e}));
}
```

![image-20250613145239023](./assets/image-20250613145239023.png)

******

### 第三步:建立连接

- 本地监听

![image-20250613145323328](./assets/image-20250613145323328.png)

- 建立连接

```js
var demo = new Hlclient("ws://127.0.0.1:12080/ws?group=zzz");
```

![image-20250613145407910](./assets/image-20250613145407910.png)

******

### 第四步:注册动作

```js
// 注意这个注册动作的名字
demo.regAction("kc_debug", function (resolve, param) {
    try {
        let result = kc_debug(param);  // 或者直接用 Kc(param)
        resolve(result);
    } catch (e) {
        resolve("解密失败：" + e.toString());
    }
});
```

![image-20250613145537911](./assets/image-20250613145537911.png)

******

### 第五步:调用

- `get`请求,动作需要和注册的动作一致

```py
from urllib.parse import quote

safe_param = quote(encrypt_data, safe='')
url = f'http://127.0.0.1:12080/go?group=zzz&action=kc_debug&param={safe_param}'
decrypt_data = requests.get(url).json()['data']
print(decrypt_data)
```

```py
# -*- encoding: utf-8 -*-
# TODO:@ModuleName: test
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/6/13 10:29
import requests
from urllib.parse import quote

headers = {
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
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36',
    'appflag': 'see-h5-1.0.0',
    'sec-ch-ua': '"Google Chrome";v="137", "Chromium";v="137", "Not/A)Brand";v="24"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
}
data = {
    'page': '1',
    'num': '20',
    'ca_uuid': 'feef62bfdac45a94b9cd89aed5c235be',
    'appflag': 'see-h5-1.0.0',
}
response = requests.post('https://wyiosapi.qmpsee.com/Web/getCaDetail', headers=headers, data=data).json()
encrypt_data = response['encrypt_data']

safe_param = quote(encrypt_data, safe='')
url = f'http://127.0.0.1:12080/go?group=zzz&action=kc_debug&param={safe_param}'
decrypt_data = requests.get(url).json()['data']
print(decrypt_data)
```

![image-20250613145741595](./assets/image-20250613145741595.png)

- 效果如图

![image-20250613145817069](./assets/image-20250613145817069.png)

******