"""
HTTP/2竞态条件基准测试脚本 - Turbo Intruder

功能说明：
这是一个专门用于测试HTTP/2协议下竞态条件性能的基准测试脚本。
利用HTTP/2的多路复用特性和单包攻击能力，实现更精确的时序测试。

测试原理：
1. 使用HTTP/2协议的单连接多路复用
2. 通过单包攻击实现精确的请求同步
3. 测量批量请求的响应时间差异
4. 评估HTTP/2环境下的竞态攻击效果

HTTP/2优势：
- 单连接多路复用，减少网络延迟
- 单包攻击，实现更精确的时序控制
- 更高的并发性能
- 更小的时间窗口差异

使用场景：
- HTTP/2服务器竞态条件测试
- 单包攻击效果评估
- 高精度时序攻击基准测试
- 与HTTP/1.1性能对比分析

参数说明：
- BATCH_SIZE: 每批请求数量（20个）
- concurrentConnections: 1（HTTP/2单连接）
- requestsPerConnection: 1000（高复用率）
- engine: Engine.BURP2（HTTP/2引擎）
- 协议版本: HTTP/2

性能对比：
相比HTTP/1.1版本，此脚本通常能获得：
- 更小的时间差异
- 更稳定的时序表现
- 更高的攻击成功率

注意事项：
- 需要目标支持HTTP/2协议
- 单连接高复用，减少网络影响
- 适合精确时序要求的测试场景
"""

def queueRequests(target, wordlists):

    global BATCH_SIZE
    BATCH_SIZE = 20  # 每批请求数量


    # 创建HTTP/2基准测试引擎
    engine = RequestEngine(endpoint='https://x.psres.net:443',  # 目标服务器
                           concurrentConnections=1,             # HTTP/2单连接
                           requestsPerConnection=1000,          # 高复用率
                           engine=Engine.BURP2,                 # HTTP/2引擎
                           pipeline=False,                      # 禁用管道
                           maxQueueSize=BATCH_SIZE              # 队列大小限制
                           )


    # HTTP/2请求模板
    req = '''GET /wtf/?nottime=%s HTTP/2
Host: x.psres.net
Accept-Encoding: gzip, deflate
Accept: */*
Accept-Language: en-US;q=0.9,en;q=0.8
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.5481.78 Safari/537.36
Cache-Control: max-age=0

'''

    # 执行10批HTTP/2基准测试
    for i in range(10):
        gate_id = str(i)  # 每批使用不同的gate标识

        # 每批发送BATCH_SIZE个请求
        for x in range(BATCH_SIZE):
            engine.queue(req, '0.000', gate=gate_id)

        # 使用HTTP/2单包攻击同步发送
        engine.openGate(gate_id)
        time.sleep(0.5)  # 批次间间隔


def handleResponse(req, interesting):
    # 从HTTP/2响应中提取时间戳信息
    xtime= req.response.split('\r\n\r\n')[1]  # 获取响应体中的时间戳
    req.label = xtime                         # 将时间戳设置为请求标签
    table.add(req)                           # 添加到结果表


def completed(reqsFromTable):
    """
    HTTP/2测试完成后的统计分析函数

    功能：
    - 分析HTTP/2单包攻击的时序效果
    - 计算精确的时间差异统计
    - 评估HTTP/2竞态攻击的优势
    - 与HTTP/1.1结果进行对比
    """
    diffs = []
    time.sleep(1)  # 等待所有HTTP/2响应处理完成
    print len(reqsFromTable)  # 打印总请求数

    # 按批次分析HTTP/2请求的时序表现
    for i in range(len(reqsFromTable)):
        if i % BATCH_SIZE != 0:  # 只处理每批的第一个请求
            continue

        entries = []
        # 收集当前批次所有HTTP/2请求的时间戳
        for x in range(BATCH_SIZE):
            entries.append(float(reqsFromTable[i+x].label))

        entries.sort()  # 排序时间戳
        diffs.append(entries[-1] - entries[0])  # 计算最大最小时间差

    diffs.sort()  # 排序时间差数组

    # 输出HTTP/2基准测试统计结果
    print('Best: '+str(min(diffs)))                    # 最小时间差（通常比HTTP/1.1更小）
    print('Mean: '+str(mean(diffs)))                   # 平均时间差
    print('Stddev: '+str(stddev(diffs)))               # 标准差（通常比HTTP/1.1更小）
    print('Median: '+str(diffs[len(diffs)/2]))         # 中位数
    print('Range: '+str(max(diffs)-min(diffs)))        # 时间差范围
    handler.setMessage(str(sum(diffs)/len(diffs)))     # 设置平均值消息