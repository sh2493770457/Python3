"""
输出到文件脚本 - Turbo Intruder

功能说明：
这是一个将攻击结果自动保存到文件的脚本。
脚本会将有趣的响应内容提取并保存到指定文件中，便于后续分析。

主要特性：
1. 自动文件输出 - 将响应内容保存到文件
2. 学习模式 - 建立响应基线
3. 智能过滤 - 只保存有趣的响应
4. 内容分离 - 分离HTTP头部和响应体

使用场景：
- 大量数据的批量处理
- 响应内容的离线分析
- 自动化结果收集
- 长时间攻击的结果保存
- 数据挖掘和模式识别

技术特点：
- UTF-8编码处理
- HTTP头部和响应体分离
- 追加模式文件写入
- 自动文件管理

参数配置：
- concurrentConnections: 5（并发连接数）
- requestsPerConnection: 100（每连接请求数）
- pipeline: False（禁用HTTP管道）

文件输出：
- 输出文件: /tmp/output-turbo.txt
- 写入模式: 追加模式（a+）
- 内容格式: 只保存响应体
- 编码格式: UTF-8

工作流程：
1. 发送学习请求建立基线
2. 使用字典进行暴力破解
3. 识别有趣的响应
4. 提取响应体内容
5. 保存到指定文件

学习模式：
- 使用随机字符串（3-7位）
- 使用基础输入作为对照
- 建立正常响应模式
- 提高检测准确性

数据处理：
- 自动UTF-8编码转换
- 分离HTTP头部和响应体
- 只保存响应体到文件
- 每个响应占一行

注意事项：
- 确保有文件写入权限
- 长时间运行可能产生大文件
- 定期清理输出文件
- 注意磁盘空间使用

使用方法：
1. 确保/tmp目录可写
2. 运行脚本开始攻击
3. 监控/tmp/output-turbo.txt文件
4. 分析保存的响应内容

更多示例脚本：
https://github.com/PortSwigger/turbo-intruder/blob/master/resources/examples/

扩展功能：
- 可修改输出文件路径
- 可添加时间戳
- 可保存完整响应
- 可按条件分类保存
"""

def queueRequests(target, wordlists):
    # 创建文件输出攻击引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,        # 并发连接数
                           requestsPerConnection=100,       # 每连接请求数
                           pipeline=False                   # 禁用HTTP管道
                           )

    # 学习模式：发送随机字符串和基础输入建立基线
    for i in range(3, 8):
        engine.queue(target.req, randstr(i), learn=1)      # 随机字符串学习
        engine.queue(target.req, target.baseInput, learn=2) # 基础输入学习

    # 使用字典进行暴力破解
    for word in open('/usr/share/dict/words'):
        engine.queue(target.req, word.rstrip())  # 去除换行符


def handleResponse(req, interesting):
    # 只处理有趣的响应
    if interesting:
        table.add(req)  # 添加到结果表

        # 将响应编码为UTF-8
        data = req.response.encode('utf8')

        # 分离HTTP头部和响应体
        header, _, body = data.partition('\r\n\r\n')

        # 将响应体保存到文件 /tmp/output-turbo.txt
        output_file = open("/tmp/output-turbo.txt","a+")  # 追加模式打开
        output_file.write(body + "\n")                    # 写入响应体
        output_file.close()                               # 关闭文件

        # 可以添加更多处理逻辑：
        # - 保存完整响应（包含头部）
        # - 添加时间戳
        # - 按状态码分类保存
        # - 保存请求和响应对
