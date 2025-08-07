"""
HTTP/2协议攻击脚本 - Turbo Intruder

功能说明：
这是一个专门针对HTTP/2协议的攻击脚本，利用HTTP/2的特性进行高效的暴力破解。
相比HTTP/1.1，HTTP/2提供了更好的性能和更精确的时序控制。

HTTP/2优势：
1. 多路复用：单连接并发多个请求
2. 二进制协议：更高效的数据传输
3. 服务器推送：可能暴露额外信息
4. 头部压缩：减少网络开销
5. 流优先级：更好的资源控制

引擎选择：
- Engine.HTTP2: 使用Turbo Intruder原生HTTP/2引擎
- Engine.BURP2: 使用Burp Suite的HTTP/2网络栈

字符重写规则：
当使用任一HTTP/2引擎时，会执行以下字符重写：
- ^ -> \r (回车符)
- ~ -> \n (换行符)
- ` -> :  (冒号)

伪头部处理：
可以通过指定普通头部的方式覆盖伪头部，例如：
':path: /robots.txt'
':method: POST'
':scheme: https'
':authority: example.com'

使用场景：
- HTTP/2服务器暴力破解
- 利用HTTP/2特性的攻击
- 高性能并发测试
- HTTP/2协议漏洞挖掘
- 服务器推送滥用测试

参数说明：
- concurrentConnections: 5（HTTP/2可以用更少连接）
- requestsPerConnection: 100（HTTP/2支持高复用）
- engine: Engine.HTTP2（原生HTTP/2引擎）

注意事项：
- 确保目标服务器支持HTTP/2
- 注意字符重写规则的影响
- 可以利用HTTP/2的流控制特性
- 观察服务器推送的响应
"""

def queueRequests(target, wordlists):
    # 创建HTTP/2请求引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,        # HTTP/2可用更少连接
                           requestsPerConnection=100,       # HTTP/2支持高复用率
                           engine=Engine.HTTP2              # 使用原生HTTP/2引擎
                                                           # 也可使用Engine.BURP2（Burp的HTTP/2栈）
                           )

    # 使用任一HTTP/2引擎时，会执行以下字符重写：
    # ^ -> \r (回车符)
    # ~ -> \n (换行符)
    # ` -> :  (冒号)
    # 可以通过指定普通头部的方式覆盖伪头部
    # 例如：':path: /robots.txt'

    # 使用系统字典进行HTTP/2暴力破解
    for word in open('/usr/share/dict/words'):
        engine.queue(target.req, word.rstrip())  # 去除换行符并排队


def handleResponse(req, interesting):
    # 处理HTTP/2响应
    # 可用属性：req.status（状态码）, req.wordcount（字数）, req.length（长度）, req.response（响应内容）
    if req.status != 404:  # 过滤404响应
        table.add(req)     # 添加有效响应到结果表

    # HTTP/2特有的分析可以在这里添加：
    # - 检查服务器推送的响应
    # - 分析HTTP/2特有的头部
    # - 监控流的优先级变化
