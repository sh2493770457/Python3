"""
多参数攻击脚本 - Turbo Intruder

功能说明：
这是一个专门用于同时测试多个参数的攻击脚本。
脚本会使用两个不同的字典文件，对目标请求中的多个插入点进行组合攻击。

主要特性：
1. 多参数支持 - 同时测试多个插入点
2. 组合攻击 - 使用不同字典的组合
3. 嵌套循环 - 全面的参数组合测试
4. 高效处理 - 优化的请求队列管理

使用场景：
- 多参数SQL注入测试
- 用户名密码暴力破解
- 多字段XSS测试
- 复合参数漏洞挖掘
- API多参数测试

技术特点：
- 支持数组形式的参数传递
- 自动处理多个插入点
- 组合式字典攻击
- 高并发处理能力

参数配置：
- concurrentConnections: 5（并发连接数）
- requestsPerConnection: 100（每连接请求数）
- pipeline: False（禁用HTTP管道）

字典文件：
- 第一个字典: /usr/share/dict/words（通用单词）
- 第二个字典: /usr/share/dict/american-english（美式英语）

工作原理：
1. 读取两个字典文件
2. 生成所有可能的参数组合
3. 将组合作为数组传递给请求
4. 过滤掉404响应

插入点使用：
在目标请求中使用%s标记插入点，例如：
- username=%s&password=%s
- param1=%s&param2=%s
- 第一个%s对应第一个字典
- 第二个%s对应第二个字典

性能考虑：
- 组合数量 = 字典1大小 × 字典2大小
- 可能产生大量请求
- 建议使用较小的字典进行测试
- 注意目标服务器负载

注意事项：
- 确保目标请求包含多个插入点
- 字典文件大小会影响总请求数
- 过滤404响应减少噪音
- 适合参数较少的场景

使用方法：
1. 在目标请求中标记多个插入点（%s）
2. 确保字典文件存在且可读
3. 运行脚本开始组合攻击
4. 观察非404响应的结果

优化建议：
- 使用针对性的字典文件
- 根据目标调整并发参数
- 添加更多响应过滤条件
- 考虑使用更小的测试集
"""

def queueRequests(target, wordlists):
    # 创建多参数攻击引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,        # 并发连接数
                           requestsPerConnection=100,       # 每连接请求数
                           pipeline=False                   # 禁用HTTP管道
                           )

    # 嵌套循环生成所有参数组合
    for firstWord in open('/usr/share/dict/words'):         # 第一个字典
      for secondWord in open('/usr/share/dict/american-english'):  # 第二个字典
        # 将两个参数作为数组传递给请求
        engine.queue(target.req, [firstWord.rstrip(), secondWord.rstrip()])


def handleResponse(req, interesting):
    # 处理响应，过滤掉404错误
    # 可用属性：req.status（状态码）, req.wordcount（字数）, req.length（长度）, req.response（响应内容）
    if req.status != 404:  # 只保留非404响应
        table.add(req)     # 添加到结果表

    # 可以添加更多过滤条件：
    # - 特定状态码
    # - 响应长度范围
    # - 特定响应内容
    # - 响应时间阈值
