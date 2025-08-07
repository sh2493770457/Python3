"""
引擎测试脚本 - Turbo Intruder

功能说明：
这是一个专门用于开发期间测试引擎功能的脚本。
脚本演示了不同类型的载荷使用方法，包括无载荷、单载荷和多载荷请求。

重要提醒：
这只是为了确保引擎在开发过程中正常工作的测试脚本。

启动方式：
java -jar build/libs/turbo-intruder-all.jar resources/examples/test.py /dev/null z z

主要特性：
1. 引擎功能验证 - 测试基本的引擎功能
2. 载荷类型演示 - 展示不同载荷使用方法
3. 开发调试 - 用于开发期间的功能验证
4. 简单测试 - 基础的功能测试用例

测试用例：
1. 无载荷请求：直接发送固定请求
2. 单载荷请求：使用一个载荷替换占位符
3. 多载荷请求：使用多个载荷替换多个占位符

使用场景：
- 引擎功能开发测试
- 载荷机制验证
- 基础功能调试
- 开发环境验证
- 回归测试

技术特点：
- 简单的测试用例
- 不同载荷类型的演示
- 基础的响应处理
- 完成回调函数

参数配置：
- endpoint: https://hackxor.net:443（测试目标）
- concurrentConnections: 1（单连接测试）
- requestsPerConnection: 10（每连接10个请求）
- pipeline: False（禁用管道）

测试请求类型：

1. 无载荷请求：
   - 直接发送固定的HTTP请求
   - 不使用任何载荷替换
   - 测试基础的请求发送功能

2. 单载荷请求：
   - 使用%s占位符
   - 传递单个字符串载荷
   - 测试单载荷替换机制

3. 多载荷请求：
   - 使用多个%s占位符
   - 传递载荷数组
   - 测试多载荷替换机制

响应处理：
- 记录所有响应到结果表
- 在完成时打印状态码
- 简单的调试输出

注意事项：
- 这是开发测试脚本，不用于实际攻击
- 目标是固定的测试服务器
- 主要用于功能验证
- 适合开发和调试环境

使用方法：
1. 在开发环境中运行
2. 验证引擎基础功能
3. 检查载荷替换机制
4. 观察响应处理结果

扩展用途：
- 添加更多测试用例
- 验证新功能
- 性能基准测试
- 回归测试套件
"""

def queueRequests(target, wordlists):
    # 创建测试引擎
    engine = RequestEngine(endpoint='https://hackxor.net:443',  # 测试目标
                           concurrentConnections=1,             # 单连接测试
                           requestsPerConnection=10,            # 每连接10个请求
                           pipeline=False                       # 禁用管道
                           )

    # 测试用例1：无载荷请求
    noPayload = '''GET /static/404 HTTP/1.1
Host: hackxor.net
Connection: close

'''
    engine.queue(noPayload)  # 直接发送固定请求

    # 测试用例2：单载荷请求
    onePayload = '''GET /static/404?q=%s HTTP/1.1
Host: hackxor.net
Connection: close

'''
    engine.queue(onePayload, 'one payload')  # 使用单个载荷

    # 测试用例3：多载荷请求
    twoPayloads = '''GET /static/404?q=%s HTTP/1.1
Host: hackxor.net
Connection: close

'''
    # 使用载荷数组（注意：这个例子中只有一个%s，但演示了数组用法）
    engine.queue(twoPayloads, ['first payload', 'second payload'])



def handleResponse(req, interesting):
    # 记录所有响应用于测试验证
    table.add(req)


def completed(requests):
    """
    完成回调函数 - 测试完成后的处理

    功能：
    - 打印所有请求的状态码
    - 用于验证测试结果
    - 简单的调试输出
    """
    for req in requests:
        print(req.code)  # 打印状态码进行验证