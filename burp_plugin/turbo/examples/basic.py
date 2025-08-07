"""
基础字典攻击脚本 - Turbo Intruder

功能说明：
这是一个基础的字典攻击脚本，用于对目标进行暴力破解测试。
脚本会读取系统字典文件，对目标URL进行大量并发请求。

使用方法：
1. 在Burp Suite中选择一个请求
2. 发送到Turbo Intruder
3. 选择此脚本模板
4. 点击"Attack"开始攻击

参数说明：
- target: 目标请求对象，包含endpoint和req属性
- wordlists: 字典列表（此脚本中未使用）
- concurrentConnections: 并发连接数（默认5个）
- requestsPerConnection: 每个连接的请求数（默认100个）
- pipeline: 是否启用HTTP管道（默认False）

注意事项：
- 脚本会过滤掉404响应
- 只显示非404状态码的响应结果
"""

def queueRequests(target, wordlists):
    # 创建请求引擎，配置并发参数
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,        # 并发连接数
                           requestsPerConnection=100,       # 每个连接的请求数
                           pipeline=False                   # 禁用HTTP管道
                           )

    # 从系统字典文件中读取单词进行暴力破解
    for word in open('/usr/share/dict/words'):
        engine.queue(target.req, word.rstrip())  # 去除换行符并加入队列


def handleResponse(req, interesting):
    # 处理响应结果
    # 可用属性：req.status（状态码）, req.wordcount（字数）, req.length（长度）, req.response（响应内容）
    if req.status != 404:  # 过滤掉404响应
        table.add(req)     # 将结果添加到结果表中
