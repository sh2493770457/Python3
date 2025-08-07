"""
杂项功能演示脚本 - Turbo Intruder

功能说明：
这是一个展示Turbo Intruder各种杂项功能和高级配置的示例脚本。
演示了手动引擎控制、自定义请求、高级参数配置等功能。

主要特性：
1. 手动引擎启动控制
2. 自定义任意请求
3. 高级引擎参数配置
4. 队列大小控制
5. 重试机制配置
6. 超时控制

使用场景：
- 需要精确控制引擎启动时机
- 发送与插入点无关的自定义请求
- 高级参数调优和性能优化
- 复杂的攻击流程控制
- 多种请求类型混合测试

参数详解：
- endpoint: 目标端点（协议:域名:端口格式，如https://example.com:443）
- concurrentConnections: 并发连接数（5个）
- requestsPerConnection: 每连接请求数（100个）
- pipeline: HTTP管道（禁用）
- maxQueueSize: 最大队列大小（10个）
- timeout: 请求超时（5秒）
- maxRetriesPerRequest: 最大重试次数（3次）
- autoStart: 自动启动（禁用，需手动启动）

手动启动：
由于禁用了autoStart，需要手动调用engine.start()来启动引擎。
这允许在启动前进行额外的配置或准备工作。

自定义请求：
可以排队任意的HTTP请求，不必使用插入点。
这对于发送特定的探测请求或辅助请求很有用。

响应过滤：
脚本会过滤掉包含"404 Not Found"的响应，只保留有效结果。

使用方法：
1. 根据需要调整引擎参数
2. 修改自定义请求内容
3. 调整响应过滤条件
4. 运行脚本进行测试

注意事项：
- 手动启动模式需要显式调用start()
- 队列大小限制可能影响性能
- 重试机制会增加总请求数
- 超时设置需要根据目标调整
"""

def queueRequests(target, wordlists):
    # 创建具有高级配置的请求引擎
    engine = RequestEngine(endpoint=target.endpoint,  # 目标端点（协议:域名:端口格式）
                           concurrentConnections=5,        # 并发连接数
                           requestsPerConnection=100,       # 每连接请求数
                           pipeline=False,                  # 禁用HTTP管道
                           maxQueueSize=10,                 # 最大队列大小
                           timeout=5,                       # 请求超时（秒）
                           maxRetriesPerRequest=3,          # 最大重试次数
                           autoStart=False                  # 禁用自动启动
                           )

    # 由于禁用了autoStart，需要手动启动引擎
    engine.start(timeout=5)

    # 可以排队任意请求 - 不必使用插入点
    oddRequest = """GET /static/style.css HTTP/1.1
Host: hackxor.net

"""
    engine.queue(oddRequest)  # 排队自定义请求

    # 使用字典进行常规暴力破解
    for word in open('/usr/share/dict/words'):
        engine.queue(target.req, word.rstrip())  # 排队字典攻击请求


def handleResponse(req, interesting):
    # 过滤响应，只保留非404的结果
    if '404 Not Found' not in req.response:
        table.add(req)  # 添加有效响应到结果表

    # 可以添加更多过滤条件：
    # - 特定状态码
    # - 响应长度范围
    # - 特定响应头部
    # - 响应时间阈值
