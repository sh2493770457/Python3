# Turbo Intruder Python 模板详细分析

## 模板架构

所有Turbo Intruder Python脚本都遵循标准的两函数架构：

```python
def queueRequests(target, wordlists):
    # 1. 配置RequestEngine
    # 2. 队列化所有要发送的请求
    
def handleResponse(req, interesting):
    # 3. 处理每个响应
    # 4. 决定是否添加到结果表
```

## 核心组件分析

### RequestEngine 配置选项

```python
engine = RequestEngine(
    endpoint=target.endpoint,           # 目标端点
    concurrentConnections=5,            # 并发连接数 (1-100)
    requestsPerConnection=100,          # 每连接请求数
    pipeline=False,                     # HTTP管道 (True/False)
    engine=Engine.THREADED,             # 引擎类型
    maxRetriesPerRequest=0,             # 最大重试次数
    readCallback=handleRead,            # 读取回调函数
    readSize=256                        # TCP缓冲区大小
)
```

### 引擎类型
- **Engine.THREADED**: 独立多线程引擎，性能最高
- **Engine.BURP**: 使用Burp的网络栈，支持上游代理

### 队列方法
```python
# 基础队列
engine.queue(target.req, payload)

# 带标签队列 (用于分类)
engine.queue(target.req, payload, label='benchmark')

# 学习模式队列 (用于基准测试)
engine.queue(target.req, payload, learn=1)

# 多参数队列
engine.queue(target.req, [param1, param2])

# 竞态条件队列 (gate机制)
engine.queue(target.req, payload, gate='race1')
```

## 攻击模式分类

### 1. 基础字典攻击
**文件**: `basic.py`, `default.py`
**特点**: 
- 简单的单词列表遍历
- 基于状态码过滤结果
- 适合目录/文件发现

### 2. 竞态条件攻击
**文件**: `race.py`
**核心技术**:
```python
# 队列30个请求，但不立即发送最后一个字节
for i in range(30):
    engine.queue(target.req, target.baseInput, gate='race1')

# 同时释放所有请求的最后一个字节
engine.openGate('race1')
```
**应用场景**: 
- 优惠券重复使用
- 账户余额竞态
- 文件上传竞态

### 3. 时序攻击
**文件**: `timingAttackWithState.py`
**核心技术**:
```python
# 建立基准时间
engine.userState['base_times'] = []
for i in range(20):
    engine.queue(target.req, randstr(i), label='benchmark')

# 检测异常响应时间
if req.time > max(req.engine.userState['base_times'])+10:
    table.add(req)
```
**应用场景**:
- 用户名枚举
- SQL盲注
- 密码重置时序攻击

### 4. 多参数攻击
**文件**: `multipleParameters.py`
**核心技术**:
```python
for firstWord in open('/usr/share/dict/words'):
    for secondWord in open('/usr/share/dict/american-english'):
        engine.queue(target.req, [firstWord.rstrip(), secondWord.rstrip()])
```
**应用场景**:
- 多字段注入
- 组合参数测试

### 5. 递归扫描
**文件**: `recursive.py`
**核心技术**:
```python
def handleResponse(req, interesting):
    if '404 Not Found' not in req.response:
        table.add(req)
        # 发现目录后，递归扫描子目录
        for word in open('/usr/share/dict/words'):
            req.engine.queue(req.template, req.words[0]+'/'+word.rstrip())
```
**应用场景**:
- 深度目录扫描
- 动态路径发现

### 6. 速率控制
**文件**: `ratelimit.py`
**核心技术**:
```python
for word in open('words.txt'):
    time.sleep(throttleMillisecs/1000)  # 每请求延迟
    engine.queue(target.req, word.rstrip())
    n += 1
    if n == triedWords:
        time.sleep(secs)  # 批量暂停
        n = 0
```
**应用场景**:
- 避免WAF检测
- 绕过速率限制

### 7. 凭据喷洒
**文件**: `pinwheel.py`
**核心技术**:
```python
# 为每个用户维护独立的密码列表
users = loadFile('users.txt')
lists = []
for i in range(1, len(users)+1):
    words = loadFile('words'+str(i)+'.txt')
    lists.append(words)

# 轮询所有用户，每次尝试一个密码
while lists:
    for i, list in enumerate(lists):
        if list:
            engine.queue(target.req, [users[i], list.pop(0)])
```
**应用场景**:
- 避免账户锁定
- 分布式密码攻击

### 8. 实时响应处理
**文件**: `partialReadCallback.py`
**核心技术**:
```python
engine = RequestEngine(
    readCallback=handleRead,
    readSize=256
)

def handleRead(data):
    if 'token' in data:
        # 在响应完成前就可以提取token并发送新请求
        engine.queue('something-using-the-token')
```
**应用场景**:
- 实时token提取
- 流式数据处理

## 高级特性

### 状态管理
```python
# 在引擎中维护全局状态
engine.userState['key'] = value

# 在响应处理中访问状态
req.engine.userState['key']
```

### 学习模式
```python
# 发送学习请求，建立基准
engine.queue(target.req, randstr(i), learn=1)
engine.queue(target.req, target.baseInput, learn=2)
```

### Burp集成
```python
# 使用Burp网络栈
engine = RequestEngine(engine=Engine.BURP)

# 自动添加到站点地图
callbacks.addToSiteMap(req.getBurpRequest())

# 触发扫描
callbacks.doActiveScan(host, port, https, request)
```

### 特殊字典源
```python
# 剪贴板内容
for word in wordlists.clipboard:
    engine.queue(target.req, word)

# 被动扫描观察到的词汇
for word in wordlists.observedWords:
    engine.queue(target.req, word)

# 暴力破解生成器
seed = wordlists.bruteforce.generate(seed, 5000, batch)
```

## 性能优化建议

1. **并发设置**: 根据目标服务器调整`concurrentConnections`
2. **连接复用**: 设置合适的`requestsPerConnection`
3. **管道技术**: 对支持的服务器启用`pipeline=True`
4. **引擎选择**: 优先使用`Engine.THREADED`获得最佳性能
5. **内存管理**: 大量请求时考虑分批处理

## 调试技巧

1. **使用debug.py**: 测试连接问题
2. **添加日志**: 在关键位置添加`print()`语句
3. **状态检查**: 利用`engine.userState`跟踪状态
4. **响应分析**: 检查`req.status`, `req.length`, `req.response`

这些模板展示了Turbo Intruder的强大灵活性，可以适应各种复杂的Web安全测试场景。
