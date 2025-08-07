"""
Turbo Intruder 竞态条件攻击模板

文件用途：
    专门用于测试竞态条件漏洞的攻击模板。通过同时发送多个请求来触发竞态条件，
    常用于测试优惠券重复使用、账户余额竞态、文件上传竞态等场景。

使用方法：
    1. 在 Burp Suite 中选择可能存在竞态条件的请求
    2. 右键选择 "Send to turbo intruder"
    3. 选择此竞态攻击模板
    4. 点击 "Attack" 开始攻击

参数说明：
    - target: 目标对象，包含请求模板和端点信息
    - wordlists: 字典列表对象（本脚本不使用外部字典）
    - concurrentConnections: 并发连接数，设置为30以最大化竞态效果
    - requestsPerConnection: 每个连接的请求数，默认100个
    - pipeline: 是否启用HTTP管道，默认False
    - gate: 门控机制，用于同步请求发送

门控机制说明：
    - gate='race1': 将请求标记为 race1 组，阻塞最后一个字节
    - openGate('race1'): 同时释放所有 race1 组请求的最后字节
    - 这确保所有请求几乎同时到达服务器

适用场景：
    - 优惠券/折扣码重复使用
    - 账户余额/积分竞态
    - 库存数量竞态
    - 文件上传/删除竞态
    - 权限检查绕过

输出结果：
    所有响应都会被添加到结果表中，需要手动分析差异
"""

def queueRequests(target, wordlists):
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=30,
                           requestsPerConnection=100,
                           pipeline=False
                           )

    # 'gate' 参数会阻塞每个请求的最后一个字节，直到调用 openGate
    for i in range(30):
        engine.queue(target.req, target.baseInput, gate='race1')

    # 等待所有标记为 'race1' 的请求准备就绪
    # 然后发送每个请求的最后一个字节
    # (此方法是非阻塞的，就像 queue 方法一样)
    engine.openGate('race1')

    engine.complete(timeout=60)


def handleResponse(req, interesting):
    table.add(req)
