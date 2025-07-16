# CurlConverter API 服务

这是一个基于 Express.js 的 REST API，可以将 curl 命令转换为各种编程语言的代码。

## 快速开始

### 启动服务器

```bash
# 安装依赖
npm install

# 编译代码
npm run compile

# 启动API服务器
npm run api

# 或者开发模式（自动重启）
npm run api:dev
```

服务器默认运行在 `http://localhost:3000`

## API 端点

### 1. 健康检查

**GET** `/health`

检查服务器状态。

**响应示例:**
```json
{
  "status": "ok",
  "timestamp": "2023-12-07T10:30:00.000Z"
}
```

### 2. 获取支持的语言

**GET** `/languages`

获取所有支持的编程语言列表。

**响应示例:**
```json
{
  "supported_languages": [
    "python", "javascript", "node", "java", "go", "php", "ruby", "rust",
    "csharp", "swift", "kotlin", "dart", "r", "julia", "perl", "lua",
    "clojure", "elixir", "objectivec", "ocaml", "matlab", "powershell",
    "ansible", "http", "httpie", "wget", "cfml", "c"
  ],
  "count": 25
}
```

### 3. 转换 curl 命令

**POST** `/convert`

将 curl 命令转换为指定编程语言的代码。

**请求体:**
```json
{
  "curl": "curl -X POST https://api.example.com/users -H 'Content-Type: application/json' -d '{\"name\":\"John\",\"age\":30}'",
  "language": "python"
}
```

**请求参数:**
- `curl` (必需): curl 命令字符串
- `language` (必需): 目标编程语言

**响应示例:**
```json
{
  "success": true,
  "result": "import requests\n\nheaders = {\n    'Content-Type': 'application/json',\n}\n\njson_data = {\n    'name': 'John',\n    'age': 30,\n}\n\nresponse = requests.post('https://api.example.com/users', headers=headers, json=json_data)",
  "language": "python",
  "warnings": []
}
```

**错误响应示例:**
```json
{
  "error": "Missing required field: curl"
}
```

## 支持的语言

| 语言 | 标识符 | 别名 |
|------|--------|------|
| Python | `python` | |
| JavaScript | `javascript` | `js` |
| Node.js | `node` | `nodejs` |
| Java | `java` | |
| Go | `go` | |
| PHP | `php` | |
| Ruby | `ruby` | |
| Rust | `rust` | |
| C# | `csharp` | `c#` |
| Swift | `swift` | |
| Kotlin | `kotlin` | |
| Dart | `dart` | |
| R | `r` | |
| Julia | `julia` | |
| Perl | `perl` | |
| Lua | `lua` | |
| Clojure | `clojure` | |
| Elixir | `elixir` | |
| Objective-C | `objectivec` | `objc` |
| OCaml | `ocaml` | |
| MATLAB | `matlab` | |
| PowerShell | `powershell` | `ps1` |
| Ansible | `ansible` | |
| HTTP | `http` | |
| HTTPie | `httpie` | |
| Wget | `wget` | |
| CFML | `cfml` | |
| C | `c` | |

## 使用示例

### Python 请求示例

```python
import requests

# 转换为 Python 代码
response = requests.post('http://localhost:3000/convert', json={
    'curl': 'curl -X GET https://api.github.com/user -H "Authorization: token your-token"',
    'language': 'python'
})

result = response.json()
print(result['result'])
```

### JavaScript 请求示例

```javascript
// 转换为 JavaScript 代码
fetch('http://localhost:3000/convert', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        curl: 'curl -X POST https://httpbin.org/post -d "hello=world"',
        language: 'javascript'
    })
})
.then(response => response.json())
.then(data => console.log(data.result));
```

### curl 请求示例

```bash
# 转换为 Go 代码
curl -X POST http://localhost:3000/convert \
  -H "Content-Type: application/json" \
  -d '{
    "curl": "curl -X PUT https://api.example.com/users/123 -H \"Content-Type: application/json\" -d \"{\\\"name\\\":\\\"Jane\\\"}\"",
    "language": "go"
  }'
```

## 部署

### Docker 部署

创建 `Dockerfile`:

```dockerfile
FROM node:18-alpine

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run compile

EXPOSE 3000
CMD ["npm", "run", "api"]
```

构建和运行:

```bash
docker build -t curlconverter-api .
docker run -p 3000:3000 curlconverter-api
```

### 环境变量

- `PORT`: 服务器端口（默认: 3000）

## 错误处理

API 返回适当的 HTTP 状态码和错误信息：

- `400 Bad Request`: 请求参数错误或 curl 命令无效
- `404 Not Found`: 端点不存在
- `500 Internal Server Error`: 服务器内部错误

## 限制

- 请求体大小限制: 1MB
- 支持的 curl 功能基于原始 curlconverter 库的能力
- 某些复杂的 bash 语法可能不被支持

## 许可证

MIT License - 基于原始 curlconverter 项目 