"""
时序攻击脚本 - Turbo Intruder

功能说明：
这是一个专门用于执行时序攻击（Timing Attack）的脚本。
通过比较两个不同载荷的响应时间差异，检测潜在的时序漏洞。

攻击原理：
1. 发送两种不同的载荷（LEFT_PAYLOAD和RIGHT_PAYLOAD）
2. 测量每种载荷的响应时间
3. 统计分析时间差异
4. 识别可能存在的时序漏洞

使用场景：
- SQL注入时序攻击
- 用户名枚举时序检测
- 密码验证时序分析
- 文件存在性时序检测
- 权限验证时序绕过

参数配置：
- LEFT_PAYLOAD: 第一种测试载荷（可使用$randomplz绕过缓存）
- RIGHT_PAYLOAD: 第二种测试载荷（可使用$randomplz绕过缓存）
- REPEATS: 重复次数（100次，更多重复可提高准确性）
- DELAY: 请求间延迟（0.2秒，避免触发服务器限流）

协议适配：
- HTTP/1.1: 使用Engine.BURP，2个并发连接
- HTTP/2: 使用Engine.BURP2，1个连接

防误报机制：
- 交替发送顺序，避免"粘性排序问题"
- 多次重复测试，提高统计可靠性
- 时间差异分析，减少网络抖动影响

统计指标：
- Confidence: 置信度百分比
- Split: 左右载荷首先响应的次数分布
- Bias: 第一/第二位置响应的偏向性
- Max-jitter: 最大时间抖动
- Ranges: 响应时间范围

使用方法：
1. 修改LEFT_PAYLOAD和RIGHT_PAYLOAD为实际测试载荷
2. 根据需要调整REPEATS和DELAY参数
3. 在目标请求中使用%s作为载荷占位符
4. 运行脚本并分析统计结果
"""

import math

def queueRequests(target, wordlists):

    LEFT_PAYLOAD = 'changeme'   # 第一种测试载荷，可使用$randomplz绕过缓存
    RIGHT_PAYLOAD = 'changeme2' # 第二种测试载荷，可使用$randomplz绕过缓存

    REPEATS = 100  # 重复次数，更多重复可降低错误结果概率
    DELAY  = 0.2   # 延迟时间，时序攻击需要避免触发服务器限流

    # 根据协议版本选择合适的引擎
    engineType = Engine.BURP
    connections = 2
    if target.req.split('\r\n', 1)[0].endswith('HTTP/2'):
        engineType = Engine.BURP2  # HTTP/2使用专用引擎
        connections = 1            # HTTP/2只需单连接


    # 创建时序攻击引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=connections,  # 连接数根据协议调整
                           requestsPerConnection=100,          # 每连接请求数
                           engine=engineType,                  # 引擎类型
                           maxQueueSize=2,                     # 队列大小限制
                           timeout=3                           # 超时设置
                           )

    attack = target.req  # 获取基础攻击请求


    # 生成两种不同载荷的攻击请求
    left_attack =  attack.replace('%s', LEFT_PAYLOAD)   # 左载荷攻击
    right_attack = attack.replace('%s', RIGHT_PAYLOAD)  # 右载荷攻击

    # 交替发送顺序以防止顺序误报 - 参见"粘性排序问题"
    for i in range(REPEATS):
        gate_id = str(i)
        if (i % 2 == 1):  # 奇数轮：左载荷先发送
            engine.queue(left_attack, gate=gate_id, label='left-first')
            engine.queue(right_attack, gate=gate_id, label='right-second')
        else:             # 偶数轮：右载荷先发送
            engine.queue(right_attack, gate=gate_id, label='right-first')
            engine.queue(left_attack, gate=gate_id, label='left-second')

        engine.openGate(gate_id)  # 同步发送当前轮次的请求
        time.sleep(DELAY*2)       # 轮次间延迟


def handleResponse(req, interesting):
    # 记录所有响应用于时序分析
    table.add(req)

def completed(reqsFromTable):
    """
    时序攻击完成后的统计分析函数

    功能：
    - 分析两种载荷的响应时间分布
    - 计算时序差异的统计指标
    - 评估时序攻击的可行性和置信度
    - 生成详细的分析报告
    """
    left_times = []    # 左载荷响应时间列表
    left_first = 0     # 左载荷首先响应的次数
    right_times = []   # 右载荷响应时间列表
    right_first = 0    # 右载荷首先响应的次数
    diffs = []         # 时间差异列表
    first_won = 0      # 第一位置响应获胜次数
    second_won = 0     # 第二位置响应获胜次数

    # 遍历所有响应，收集时序数据
    for req in reqsFromTable:
        if req.order == 0:  # 检查响应顺序
            if req.label.endswith('first'):
                first_won += 1
            else:
                second_won += 1

        # 按载荷类型分类收集时间数据
        if req.label.startswith('left'):
            left_times.append(req.time)
            if req.order == 0:
                left_first += 1
        else:
            right_times.append(req.time)
            if req.order == 0:
                right_first += 1

        # 计算配对的时间差异
        if len(right_times) == len(left_times):
            diffs.append(right_times[-1]-left_times[-1])

    # 排序时间数据用于统计分析
    left_times.sort()
    right_times.sort()
    diffs.sort()

    # 计算置信度（基于响应顺序的偏向性）
    compare = int(100 - (float(min(left_first, right_first)) / (left_first+right_first)*2)*100)

    # 计算响应时间范围（前20%的数据范围）
    ranges = '[{0}-{1}, {2}-{3}]'.format(
        (left_times[0]),
        left_times[int(math.floor(len(left_times)/5))],
        right_times[0],
        right_times[int(math.floor(len(right_times)/5))]
    )

    # 生成综合分析报告
    output = "Confidence: {0}%   Split: [{1}|{2}]   Bias: [{3}|{4}]   Max-jitter: {5}   Ranges: {6}".format(
        compare,                                                                    # 置信度
        left_first, right_first,                                                   # 左右载荷首先响应次数
        first_won, second_won,                                                     # 第一/第二位置获胜次数
        max(max(left_times)-min(left_times), max(right_times)-min(right_times)),  # 最大时间抖动
        ranges                                                                     # 时间范围
    )

    print(output)              # 打印分析结果
    handler.setMessage(output) # 设置结果消息
    time.sleep(0.5)           # 短暂延迟确保消息显示
    handler.setMessage(output) # 再次设置确保显示