"""
Burp Suite集成攻击脚本 - Turbo Intruder

功能说明：
这是一个展示如何将Turbo Intruder与Burp Suite深度集成的示例脚本。
脚本演示了Collaborator集成、站点地图自动添加、学习模式等高级功能。

主要特性：
1. Burp Collaborator集成 - 自动生成外带检测域名
2. 站点地图自动添加 - 将有趣的响应自动添加到Burp站点地图
3. 学习模式 - 使用随机字符串建立响应基线
4. Burp网络栈 - 利用Burp的代理和认证配置

使用场景：
- 需要外带数据检测的攻击（如XXE、SSRF）
- 自动化漏洞发现和管理
- 与Burp Suite工作流深度集成
- 利用Burp的代理链和认证

技术特点：
- Collaborator域名生成：自动创建用于外带检测的域名
- 学习模式：发送随机字符串建立正常响应模式
- 自动化集成：有趣的响应自动添加到站点地图
- 扩展能力：可触发扫描、报告问题、发送到爬虫等

参数说明：
- target: 目标请求对象
- wordlists: 字典文件列表
- concurrentConnections: 并发连接数（5个）
- engine: Engine.BURP（使用Burp的HTTP/1网络栈，包括上游代理等）
         也可使用Engine.BURP2支持HTTP/2

工作流程：
1. 生成Collaborator域名用于外带检测
2. 发送随机字符串进行学习
3. 使用字典进行暴力破解
4. 自动处理有趣的响应

Collaborator集成：
- 生成的域名会出现在Collaborator标签页
- 可用于检测XXE、SSRF、DNS外带等漏洞
- 自动监控外带交互

扩展功能：
可以通过Burp扩展API实现更多功能：
- callbacks.doActiveScan() - 触发主动扫描
- callbacks.addScanIssue() - 报告安全问题
- callbacks.sendToSpider() - 发送到爬虫
- 更多API: https://portswigger.net/burp/extender/api/burp/IBurpExtenderCallbacks.html

使用方法：
1. 确保在Burp Suite环境中运行
2. 脚本会自动生成Collaborator域名
3. 监控Collaborator标签页的交互
4. 查看站点地图中自动添加的条目
"""

def queueRequests(target, wordlists):
    # 创建集成Burp网络栈的请求引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,
                           engine=Engine.BURP  # 使用Burp的HTTP/1网络栈，包括上游代理等
                                              # 也可使用Engine.BURP2支持HTTP/2
                           )


    # 生成Collaborator域名 - 交互记录会出现在Collaborator标签页
    collabDomain = api.collaborator().defaultPayloadGenerator().generatePayload()

    # 学习模式：发送随机字符串建立响应基线
    for i in range(3, 8):
        engine.queue(target.req, randstr(i), learn=1)

    # 使用字典进行暴力破解攻击
    for word in open('/usr/share/dict/words'):
        engine.queue(target.req, word.rstrip())

def handleResponse(req, interesting):
    # 只处理有趣的响应
    if interesting:
        table.add(req)  # 添加到结果表

        # 自动将有趣的响应添加到Burp站点地图
        callbacks.addToSiteMap(req.getBurpRequest())

        # 还可以触发扫描、报告问题、发送到爬虫等：
        # callbacks.doActiveScan(host, port, useHttps, request)  # 触发主动扫描
        # callbacks.addScanIssue(issue)                          # 报告安全问题
        # callbacks.sendToSpider(url)                            # 发送到爬虫
        # 更多API参考：https://portswigger.net/burp/extender/api/burp/IBurpExtenderCallbacks.html
