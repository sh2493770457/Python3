"""
Turbo Intruder 竞态条件基准测试模板

文件用途：
    专门用于测试和评估竞态条件攻击效果的基准测试模板。
    通过多轮测试来衡量请求同步性能，分析时间窗口分布。

使用方法：
    1. 确保目标服务器支持时间戳响应
    2. 修改请求模板中的目标URL
    3. 在 Burp Suite 中选择任意请求（仅用于触发）
    4. 右键选择 "Send to turbo intruder"
    5. 选择此基准测试模板
    6. 点击 "Attack" 开始测试

测试原理：
    通过分析多个并发请求的时间戳差异来评估竞态条件的可行性。
    时间窗口越小，竞态攻击成功的可能性越大。

参数说明：
    - samples: 测试轮数，默认30轮
    - concurrentConnections: 并发连接数，默认5个
    - requestsPerConnection: 每连接请求数，设置为1
    - pipeline: 是否启用HTTP管道，默认False

测试流程：
    1. 进行30轮独立测试
    2. 每轮发送5个并发请求
    3. 使用gate机制确保请求同步
    4. 收集每轮的时间戳数据
    5. 计算时间窗口分布

数据结构：
    - window[]: 存储所有轮次的时间窗口
    - results[]: 存储单轮测试的时间戳
    - userState: 引擎状态管理

时间分析：
    - 提取响应中的时间戳（最后一行）
    - 使用Decimal精确计算时间差
    - 排序后取第二小值减去最小值作为时间窗口

统计输出：
    - max(window): 最大时间窗口
    - min(window): 最小时间窗口
    - window[median]: 中位数时间窗口

目标服务器要求：
    - 响应最后一行包含精确时间戳
    - 支持高精度时间测量
    - 示例：portswigger-labs.net/time.php

适用场景：
    - 竞态攻击可行性评估
    - 网络延迟分析
    - 服务器性能测试
    - 攻击效果验证

结果解读：
    - 时间窗口越小，竞态攻击越容易成功
    - 一般小于10ms的窗口较为理想
    - 网络环境会显著影响结果

注意事项：
    - 需要稳定的网络环境
    - 目标服务器必须支持时间戳
    - 测试结果仅供参考
"""

from decimal import *

def queueRequests(target, wordlists):
    req = '''GET /time.php HTTP/1.1
Host: portswigger-labs.net
Accept-Encoding: gzip, deflate
Accept: */*
Accept-Language: en
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:67.0) Gecko/20100101 Firefox/67.0
Connection: keep-alive

'''

    window = []
    samples = 30
    for i in range(samples):

        engine = RequestEngine(endpoint='https://portswigger-labs.net:443',
                               concurrentConnections=5,
                               requestsPerConnection=1,
                               pipeline=False
                               )
        engine.userState['results'] = []
        engine.userState['window'] = window

        for k in range(5):
            engine.queue(req, gate='race1')

        engine.openGate('race1')

        engine.complete(timeout=60)

    window.sort()
    print max(window)
    print min(window)
    print window[(samples/2)-1]


def handleResponse(req, interesting):
    table.add(req)
    timestamp = req.response.splitlines()[-1].rstrip('\x00')
    req.engine.userState['results'].append(Decimal(timestamp))
    if len(req.engine.userState['results']) == 5:
        sorted = req.engine.userState['results']
        sorted.sort()
        req.engine.userState['window'].append(sorted[1] - sorted[0])
