"""
默认攻击脚本 - Turbo Intruder

功能说明：
这是Turbo Intruder的默认攻击脚本模板，结合了数字序列和字典攻击。
脚本首先发送数字序列（10-19），然后使用系统字典进行暴力破解。

使用方法：
1. 在Burp Suite中拦截目标请求
2. 右键选择"Send to Turbo Intruder"
3. 选择此默认脚本
4. 根据需要修改参数
5. 点击"Attack"开始测试

参数说明：
- target: 目标请求对象
- wordlists: 字典文件列表
- concurrentConnections: 并发连接数（5个）
- requestsPerConnection: 每连接请求数（100个）
- pipeline: HTTP管道模式（关闭）
- engine: 引擎类型（THREADED线程模式）

攻击流程：
1. 发送数字10-19的请求
2. 使用系统字典文件进行暴力破解
3. 记录所有响应结果

更多示例脚本：
https://github.com/PortSwigger/turbo-intruder/blob/master/resources/examples/default.py
"""

def queueRequests(target, wordlists):
    # 创建请求引擎，使用线程模式
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,        # 并发连接数
                           requestsPerConnection=100,       # 每个连接的请求数
                           pipeline=False,                  # 禁用HTTP管道
                           engine=Engine.THREADED           # 使用线程引擎
                           )

    # 首先发送数字序列10-19
    for x in range(10, 20):
        engine.queue(target.req, x)

    # 然后使用系统字典进行暴力破解
    for word in open('/usr/share/dict/words'):
        engine.queue(target.req, word.rstrip())  # 去除换行符


def handleResponse(req, interesting):
    # 处理所有响应，不进行过滤
    table.add(req)  # 将所有结果添加到结果表
