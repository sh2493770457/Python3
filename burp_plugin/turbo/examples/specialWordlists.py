"""
特殊字典列表脚本 - Turbo Intruder

功能说明：
这是一个展示如何使用各种特殊字典源的综合示例脚本。
脚本演示了文件字典、剪贴板内容、观察到的单词和无限暴力破解等多种字典来源。

主要特性：
1. 多源字典 - 支持多种不同的字典来源
2. 剪贴板集成 - 直接使用剪贴板内容作为字典
3. 智能观察 - 利用被动扫描观察到的单词
4. 无限暴力破解 - 自动生成字符组合进行暴力破解

字典来源：
1. 文件字典：从磁盘文件读取单词列表
2. 剪贴板字典：使用剪贴板内容（按行分割）
3. 观察字典：被动扫描中发现的所有单词
4. 暴力破解：自动生成的字符组合（a, b ... aaa, aab等）

使用场景：
- 综合性的字典攻击
- 利用目标特定的单词
- 基于观察的智能攻击
- 无限制的暴力破解
- 多源数据融合攻击

技术特点：
- 多种字典源的统一处理
- 批量暴力破解生成
- 实时字典扩展
- 高效的内存管理

参数配置：
- concurrentConnections: 5（并发连接数）
- requestsPerConnection: 100（每连接请求数）
- pipeline: False（禁用HTTP管道）

字典详解：

1. 常规字典文件：
   - 文件：disc_words.txt
   - 格式：每行一个单词
   - 用途：标准字典攻击

2. 剪贴板字典：
   - 来源：系统剪贴板内容
   - 处理：按行自动分割
   - 用途：临时字典或目标特定单词

3. 观察字典：
   - 来源：被动扫描发现的单词
   - 特点：目标相关性高
   - 用途：智能化的定向攻击

4. 暴力破解字典：
   - 生成：自动字符组合
   - 模式：a, b, c ... aa, ab, ac ... aaa, aab等
   - 特点：无限制，覆盖所有可能

工作流程：
1. 加载文件字典进行初始攻击
2. 使用剪贴板内容进行定向测试
3. 利用观察到的单词进行智能攻击
4. 启动无限暴力破解覆盖所有可能

性能考虑：
- 暴力破解会产生无限请求
- 建议设置合理的停止条件
- 监控系统资源使用
- 注意目标服务器负载

注意事项：
- 确保disc_words.txt文件存在
- 暴力破解部分会无限运行
- 需要手动停止脚本
- 注意内存和网络使用

使用方法：
1. 准备disc_words.txt字典文件
2. 复制目标相关单词到剪贴板
3. 运行脚本开始综合攻击
4. 监控各种字典源的效果

优化建议：
- 根据目标调整字典优先级
- 添加暴力破解停止条件
- 实现字典去重机制
- 监控攻击效果和资源使用
"""

def queueRequests(target, wordlists):
    # 创建特殊字典攻击引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,        # 并发连接数
                           requestsPerConnection=100,       # 每连接请求数
                           pipeline=False                   # 禁用HTTP管道
                           )

    # 1. 常规字典文件
    for line in open('disc_words.txt'):
        engine.queue(target.req, line.rstrip())  # 去除换行符

    # 2. 剪贴板字典，按行分割
    for word in wordlists.clipboard:
        engine.queue(target.req, word)

    # 3. 被动扫描中观察到的所有单词列表
    for word in wordlists.observedWords:
        engine.queue(target.req, word)

    # 4. 无限运行的暴力破解（a, b ... aaa, aab等）
    seed = 0  # 暴力破解种子
    while True:  # 无限循环
        batch = []  # 批次容器
        # 生成下一批暴力破解单词（每批5000个）
        seed = wordlists.bruteforce.generate(seed, 5000, batch)
        for word in batch:
            engine.queue(target.req, word)  # 排队暴力破解单词


def handleResponse(req, interesting):
    # 记录所有响应用于分析
    table.add(req)

    # 可以添加更多分析逻辑：
    # - 按字典来源分类结果
    # - 识别最有效的字典类型
    # - 动态调整字典优先级
    # - 实现智能停止机制
