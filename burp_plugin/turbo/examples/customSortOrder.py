"""
自定义排序顺序脚本 - Turbo Intruder

功能说明：
这是一个演示如何自定义结果表排序顺序的示例脚本。
脚本会持续发送请求，并按照指定的列和顺序对结果进行排序。

主要特性：
1. 自定义排序设置 - 可指定按哪一列排序
2. 排序方向控制 - 支持升序和降序排列
3. 持续请求模式 - 无限循环发送请求
4. 实时结果显示 - 结果按设定顺序实时更新

使用场景：
- 需要按特定顺序查看结果
- 监控响应时间变化趋势
- 按状态码或响应长度排序
- 实时监控目标响应

排序参数：
- 列索引：0=第一列，1=第二列，以此类推
- 排序方向：True=升序，False=降序
- 常见列：状态码、响应时间、响应长度、载荷等

技术细节：
- table.setSortOrder(列索引, 是否升序)
- 列索引从0开始计数
- False表示降序排列
- 排序设置会立即生效

使用方法：
1. 根据需要修改排序列和方向
2. 调整请求间隔时间
3. 运行脚本观察排序效果
4. 可随时停止脚本

注意事项：
- 脚本包含无限循环，需要手动停止
- 请求间隔可根据目标调整
- 排序设置影响所有结果显示
- 适合长时间监控场景

自定义示例：
- table.setSortOrder(0, False) # 按第一列降序
- table.setSortOrder(1, True)  # 按第二列升序
- table.setSortOrder(2, False) # 按第三列降序
"""

def queueRequests(target, wordlists):
    # 创建基础请求引擎
    engine = RequestEngine(endpoint=target.endpoint)

    # 设置按第一列降序排序
    table.setSortOrder(0, False)  # 0=第一列，False=降序

    # 持续发送请求进行监控
    while True:
        engine.queue(target.req)  # 发送目标请求
        time.sleep(0.1)          # 请求间隔100毫秒

def handleResponse(req, interesting):
    # 将所有响应添加到结果表（按设定顺序排序）
    table.add(req)
