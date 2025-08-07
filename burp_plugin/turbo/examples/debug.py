"""
调试脚本 - Turbo Intruder

功能说明：
这是一个专门用于调试Turbo Intruder连接问题的脚本。
当遇到连接问题时，可以使用此脚本进行故障排除。

使用方法：
1. 当Turbo Intruder无法正常连接目标站点时使用
2. 首先运行此脚本（使用Engine.THREADED）
3. 如果失败，尝试将引擎改为Engine.BURP
4. 如果Engine.BURP可以工作，请提交bug报告

故障排除步骤：
1. 使用此脚本测试基本连接
2. 如果Engine.THREADED失败，改用Engine.BURP
3. 如果问题仍然存在，检查网络配置
4. 提交问题报告时请包含：
   - 目标请求/域名
   - 操作系统信息
   - Java版本（Help->Diagnostics）

参数说明：
- concurrentConnections: 1（单连接，避免并发问题）
- requestsPerConnection: 1（每连接单请求）
- pipeline: False（禁用管道）
- maxRetriesPerRequest: 0（不重试）
- engine: THREADED（线程引擎）

问题报告：
如果发现问题，请访问：http://github.com/PortSwigger/turbo-intruder/issues
"""

def queueRequests(target, wordlists):
    # 创建调试用的请求引擎，使用最简单的配置
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=1,         # 单个连接
                           requestsPerConnection=1,          # 每连接一个请求
                           pipeline=False,                   # 禁用管道
                           maxRetriesPerRequest=0,           # 不重试
                           engine=Engine.THREADED            # 使用线程引擎
                           )

    # 发送三个相同的测试请求
    engine.queue(target.req)
    engine.queue(target.req)
    engine.queue(target.req)


def handleResponse(req, interesting):
    # 记录所有响应用于调试分析
    table.add(req)

