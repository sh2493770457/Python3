"""
Burp Suite API集成示例脚本 - Turbo Intruder

功能说明：
这是一个展示如何在Turbo Intruder中使用Burp Suite API的示例脚本。
演示了如何集成Burp的各种功能，包括Collaborator、AI、扫描器等。

主要功能：
1. 使用Burp的网络栈和代理配置
2. 集成Collaborator进行外带数据检测
3. 调用Burp的AI功能
4. 使用传统的Burp扩展API
5. 触发自动化扫描和报告

API文档：
- Montoya API: https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html
- 传统API: https://portswigger.net/burp/extender/api/burp/IBurpExtenderCallbacks.html

使用场景：
- 需要与Burp Suite深度集成的测试
- 使用Collaborator进行外带检测
- 自动化漏洞扫描和报告
- 利用Burp的代理和认证配置
- 集成AI辅助分析

API示例：
1. Collaborator域名生成：
   collabDomain = api.collaborator().defaultPayloadGenerator().generatePayload()

2. AI功能调用：
   result = api.ai().prompt().execute("分析这个响应").content()

3. 站点地图添加：
   callbacks.addToSiteMap(req.getBurpRequest())

4. 触发扫描：
   callbacks.doActiveScan(host, port, useHttps, request)

5. 报告问题：
   callbacks.addScanIssue(issue)

引擎选择：
- Engine.BURP: 使用Burp的HTTP/1网络栈（包括上游代理等）
- Engine.BURP2: 使用Burp的HTTP/2网络栈

注意事项：
- 此脚本包含无限循环，仅用于演示
- 实际使用时需要添加适当的退出条件
- 某些API功能需要Burp Suite Professional版本
"""

def queueRequests(target, wordlists):
    # 创建使用Burp网络栈的请求引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,
                           engine=Engine.BURP  # 使用Burp的HTTP/1网络栈，包括上游代理等
                                              # 也可以使用Engine.BURP2支持HTTP/2
                           )

    # API文档地址：
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html

    # 以下是一些API使用示例：

    # 生成Collaborator域名 - 交互记录会出现在Collaborator标签页
    # collabDomain = api.collaborator().defaultPayloadGenerator().generatePayload()

    # 调用AI功能
    # ai_result = api.ai().prompt().execute("请分析这个HTTP响应").content()

    # 也可以通过callbacks使用Burp的传统API：
    # callbacks.addToSiteMap(req.getBurpRequest())  # 添加到站点地图

    # 还可以触发扫描、报告问题、发送到爬虫等：
    # callbacks.doActiveScan(host, port, useHttps, request)  # 主动扫描
    # callbacks.addScanIssue(issue)                          # 添加扫描问题
    # callbacks.sendToSpider(url)                            # 发送到爬虫
    # 更多API参考：https://portswigger.net/burp/extender/api/burp/IBurpExtenderCallbacks.html

    # 注意：这是一个无限循环示例，实际使用时需要添加退出条件
    while True:
        engine.queue(target.req)  # 持续发送请求


def handleResponse(req, interesting):
    # 处理响应，可以在这里集成更多Burp API功能
    table.add(req)  # 添加到结果表

    # 示例：自动添加到站点地图
    # callbacks.addToSiteMap(req.getBurpRequest())

    # 示例：基于响应内容报告问题
    # if "error" in req.response:
    #     issue = CustomScanIssue(req.url, "发现错误信息", "高", "确定")
    #     callbacks.addScanIssue(issue)

