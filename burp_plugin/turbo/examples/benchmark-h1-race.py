"""
HTTP/1.1竞态条件基准测试脚本 - Turbo Intruder

功能说明：
这是一个专门用于测试HTTP/1.1协议下竞态条件性能的基准测试脚本。
通过批量发送同步请求，测量服务器响应时间的差异，评估竞态攻击的可行性。

测试原理：
1. 分批发送同步请求（每批20个）
2. 测量每批请求的响应时间差异
3. 计算统计数据（最小值、平均值、标准差等）
4. 评估竞态条件攻击的时间窗口

使用场景：
- 评估目标服务器的竞态条件漏洞风险
- 测试HTTP/1.1协议下的并发处理能力
- 基准测试服务器响应时间一致性
- 为竞态攻击提供时序参考数据

参数说明：
- BATCH_SIZE: 每批请求数量（20个）
- 批次数量: 10批
- concurrentConnections: 并发连接数（等于批次大小）
- engine: Engine.THREADED（线程引擎）
- 间隔时间: 每批间隔0.5秒

统计指标：
- Best: 最小时间差
- Mean: 平均时间差
- Stddev: 标准差
- Median: 中位数
- Range: 时间差范围

注意事项：
- 需要修改endpoint为实际目标
- 响应需要包含时间戳信息
- 适用于HTTP/1.1协议测试
"""

def queueRequests(target, wordlists):

    global BATCH_SIZE
    BATCH_SIZE = 20  # 每批请求数量


    # 创建HTTP/1.1基准测试引擎
    engine = RequestEngine(endpoint='https://x.psres.net:443/',  # 目标服务器
                           concurrentConnections=BATCH_SIZE,      # 并发连接数
                           requestsPerConnection=100,             # 每连接请求数
                           engine=Engine.THREADED,                # 线程引擎
                           pipeline=False,                        # 禁用管道
                           maxQueueSize=BATCH_SIZE                 # 队列大小限制
                           )

    # 请求模板，包含时间参数
    req = '''GET /wtf/?nottime=%s HTTP/1.1
Host: x.psres.net
Accept-Encoding: gzip, deflate
Accept: */*
Accept-Language: en-US;q=0.9,en;q=0.8
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.5481.78 Safari/537.36
Cache-Control: max-age=0

'''

    # 执行10批基准测试
    for i in range(10):
        gate_id = str(i)  # 每批使用不同的gate标识

        # 每批发送BATCH_SIZE个请求
        for x in range(BATCH_SIZE):
            engine.queue(req, '0.000', gate=gate_id)

        # 同步发送当前批次的所有请求
        engine.openGate(gate_id)
        time.sleep(0.5)  # 批次间间隔


def handleResponse(req, interesting):
    # 从响应中提取时间戳信息
    xtime= req.response.split('\r\n\r\n')[1]  # 获取响应体中的时间戳
    req.label = xtime                         # 将时间戳设置为请求标签
    table.add(req)                           # 添加到结果表


def completed(reqsFromTable):
    """
    测试完成后的统计分析函数

    功能：
    - 计算每批请求的时间差异
    - 生成统计报告
    - 评估竞态条件攻击的可行性
    """
    diffs = []
    time.sleep(1)  # 等待所有响应处理完成
    print len(reqsFromTable)  # 打印总请求数

    # 遍历所有请求，按批次分组分析
    for i in range(len(reqsFromTable)):
        if i % BATCH_SIZE != 0:  # 只处理每批的第一个请求
            continue

        entries = []
        # 收集当前批次所有请求的时间戳
        for x in range(BATCH_SIZE):
            entries.append(float(reqsFromTable[i+x].label))

        entries.sort()  # 排序时间戳
        diffs.append(entries[-1] - entries[0])  # 计算最大最小时间差

    diffs.sort()  # 排序时间差数组

    # 输出统计结果
    print('Best: '+str(min(diffs)))                    # 最小时间差
    print('Mean: '+str(mean(diffs)))                   # 平均时间差
    print('Stddev: '+str(stddev(diffs)))               # 标准差
    print('Median: '+str(diffs[len(diffs)/2]))         # 中位数
    print('Range: '+str(max(diffs)-min(diffs)))        # 时间差范围
    handler.setMessage(str(sum(diffs)/len(diffs)))     # 设置平均值消息