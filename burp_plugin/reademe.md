# Request2Python Burp 扩展插件

![Burp Extension](https://img.shields.io/badge/Burp%20Suite-v2023.12+-orange)
![Python](https://img.shields.io/badge/Python-3.6%2B-blue)

## 📖 概述

Request2Python 是一款 Burp Suite 扩展插件，可以将选中的 HTTP 请求自动转换为可执行的 Python 脚本。特别适用于：

- **安全测试人员**：快速生成验证漏洞的 PoC 代码
- **开发人员**：快速创建 API 调试脚本
- **自动化测试**：生成基础测试用例框架

## ✨ 核心功能

### 请求转换支持
| 请求类型  | 支持特性                       |
| --------- | ------------------------------ |
| GET 请求  | ✅ URL 参数自动解析             |
| POST 请求 | ✅ 表单数据 / JSON / 二进制数据 |
| 请求头    | ✅ 自动过滤冗余头               |
| HTTPS     | ✅ 自动处理证书验证             |

### 高级特性
- **智能数据处理**
  - 自动识别 `application/json` 内容类型
  - 正确处理 `multipart/form-data` 以外的二进制数据
  - URL 参数自动编码处理

- **代码生成优化**
  - 自动生成 `requests` 库标准调用
  - 支持 Base64 编码的二进制数据传输
  - 中文注释和状态输出

## 🛠️ 安装指南

### 环境要求
- Burp Suite Professional/Community 2023.12+
- Jython 2.7.3 (嵌入在 Burp 中)
- Python 3.6+ (执行生成脚本)

### 安装步骤
1. 下载 `Request2Python.py` 文件
2. 打开 Burp Suite → Extender → Extensions
3. 点击 "Add" → 选择 "Python" 类型
4. 选择下载的 Python 文件
5. 确认控制台输出加载成功信息

## 🚀 使用方法

### 基础使用
1. 在 Proxy/Repeater 模块选中目标请求
2. 右键点击 → 选择 "Export to Python"
3. 选择保存路径 (默认: request.py)
4. 运行生成的脚本：`python request.py`

### 转换示例
#### 生成代码结构
```python
import requests

url = 'https://api.example.com/login'
headers = {
    'User-Agent': 'Mozilla/5.0',
    'Content-Type': 'application/json'
}

# JSON 请求示例
import json
payload = json.loads(r"""{"username": "admin", "password": "P@ssw0rd"}""")
response = requests.post(url, headers=headers, json=payload)

print('[状态码]:', response.status_code)
print('[响应正文]:\n', response.text)
```

*****

### http.py

- 这个为项目的初始版本,用户快速接口调试,也可快速将数据包转化为支持python格式的数据请求
- `不支持`导入burp