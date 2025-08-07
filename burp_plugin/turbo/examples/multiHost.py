"""
多主机攻击脚本 - Turbo Intruder

功能说明：
这是一个专门用于同时攻击多个主机的脚本。
脚本会为每个目标域名创建独立的请求引擎，实现并行的多目标攻击。

重要提醒：
请注意，统计面板在多主机攻击时不会反映真实情况。

主要特性：
1. 多引擎架构 - 为每个域名创建独立的请求引擎
2. 并行攻击 - 同时对多个目标进行攻击
3. 学习模式 - 为每个目标建立响应基线
4. 子域名枚举 - 支持子域名暴力破解

使用场景：
- 大规模子域名枚举
- 多目标并行测试
- 域名接管检测
- 批量安全评估
- 分布式攻击测试

技术架构：
- 每个域名使用独立的RequestEngine
- 支持不同的端点配置
- 并行处理提高效率
- 统一的响应处理机制

文件要求：
- /tmp/domains: 包含目标域名列表（每行一个域名）
- /tmp/words: 包含子域名字典（每行一个单词）

参数说明：
- endpoint: 每个域名的HTTPS端点（端口443）
- 学习模式: 使用随机字符串建立基线
- 攻击模式: 使用字典进行子域名枚举

工作流程：
1. 读取域名列表创建多个引擎
2. 为每个域名发送学习请求
3. 使用字典对所有域名进行攻击
4. 收集和分析有趣的响应

性能特点：
- 并行处理多个目标
- 独立的连接池管理
- 高效的资源利用
- 可扩展的架构设计

注意事项：
- 统计面板数据可能不准确
- 需要准备域名和字典文件
- 注意网络带宽和目标负载
- 遵守测试授权和法律要求

使用方法：
1. 准备/tmp/domains文件（目标域名列表）
2. 准备/tmp/words文件（子域名字典）
3. 运行脚本开始多主机攻击
4. 监控结果表中的有趣响应

示例文件格式：
/tmp/domains:
example.com
test.com
target.org

/tmp/words:
www
mail
ftp
admin
"""

def queueRequests(target, wordlists):
    # HTTP请求模板
    req = '''GET / HTTP/1.1
Host: %s
Connection: keep-alive

'''
    engines = {}  # 存储每个域名的引擎

    # 为每个域名创建独立的请求引擎
    for domain in open('/tmp/domains'):
        domain = domain.rstrip()  # 去除换行符
        engine = RequestEngine(endpoint='https://'+domain+':443')  # 创建HTTPS引擎
        engines[domain] = engine  # 存储引擎

    # 学习模式：为每个域名发送随机字符串请求
    for i in range(3, 8):
        for (domain, engine) in engines.items():
            engine.queue(req, randstr(i)+'.'+domain, learn=1)  # 随机子域名学习

    # 攻击模式：使用字典对所有域名进行子域名枚举
    for word in open('/tmp/words'):
        word = word.rstrip()  # 去除换行符
        for (domain, engine) in engines.items():
            engine.queue(req, word+'.'+domain)  # 子域名暴力破解


def handleResponse(req, interesting):
    # 只记录有趣的响应（减少噪音）
    if interesting:
        table.add(req)

    # 可以添加更多分析逻辑：
    # - 检测域名接管
    # - 分析响应模式
    # - 识别有效子域名