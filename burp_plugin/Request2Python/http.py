# -*- coding: utf-8 -*-
import re
import argparse
from urllib.parse import urlparse, parse_qs

def parse_raw_request(raw_request):
    """解析Burp原始请求"""
    lines = [line.rstrip('\r') for line in raw_request.strip().split('\n')]
    
    # 提取请求方法
    method, path, _ = lines[0].split()
    
    # 构建完整URL
    host_header = next((line.split(': ', 1)[1] for line in lines if line.lower().startswith('host:')), None)
    scheme = 'https' if host_header and ':443' in host_header else 'http'
    full_url = f"{scheme}://{host_header}{path}" if host_header and not path.startswith(('http://', 'https://')) else path

    # 解析请求头
    headers = {}
    body = None
    for i, line in enumerate(lines[1:]):
        if not line.strip():
            body = '\n'.join(lines[i+2:])
            break
        if ':' in line:
            key, value = line.split(':', 1)
            headers[key.strip()] = value.strip()

    # 解析查询参数
    parsed_url = urlparse(full_url)
    query_params = parse_qs(parsed_url.query) if parsed_url.query else {}

    return {
        'method': method.upper(),
        'url': full_url,
        'headers': headers,
        'body': body,
        'query_params': query_params
    }

def generate_requests_code(parsed):
    """生成Python请求代码"""
    code_lines = [
        "import requests",
        "",
        f"url = '{parsed['url']}'",
        "headers = {"
    ]
    
    # 美化headers格式
    headers_str = ',\n    '.join([f"'{k}': '{v}'" for k, v in parsed['headers'].items()])
    code_lines[-1] += f"\n    {headers_str}\n}}"
    
    # 请求参数处理
    request_method = parsed['method'].lower()
    if parsed['method'] == 'GET' and parsed['query_params']:
        code_lines.extend([
            "",
            f"params = {parsed['query_params']}",
            f"response = requests.{request_method}(url, headers=headers, params=params)"
        ])
    elif parsed['method'] == 'POST':
        content_type = parsed['headers'].get('Content-Type', '')
        
        if 'application/json' in content_type and parsed['body']:
            code_lines.insert(1, "import json")
            code_lines.extend([
                "",
                f"payload = json.loads(r'''{parsed['body']}''')",
                f"response = requests.{request_method}(url, headers=headers, json=payload)"
            ])
        elif parsed['body']:
            try:  # 表单数据处理
                params = dict(re.findall(r'([^\s&=]+)=([^&]*)', parsed['body']))
                data_str = '{\n    ' + ',\n    '.join([f"'{k}': '{v}'" for k, v in params.items()]) + '\n}'
                code_lines.extend([
                    "",
                    f"data = {data_str}",
                    f"response = requests.{request_method}(url, headers=headers, data=data)"
                ])
            except:
                code_lines.extend([
                    "",
                    f"# 无法解析的请求体:\n# {parsed['body']}",
                    f"response = requests.{request_method}(url, headers=headers, data=data)"
                ])
    else:
        code_lines.append(f"\nresponse = requests.{request_method}(url, headers=headers)")
    
    # 新增响应处理模块
    code_lines.extend([
        "\n# 响应分析",
        "print(f'[状态码] {response.status_code}')",
        "print('[响应正文]\\n' + response.text)"
    ])
    
    return '\n'.join(code_lines)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Burp请求转换工具')
    parser.add_argument('-f', '--file', required=True, help='输入文件路径')
    parser.add_argument('-o', '--output', default='output.py', help='输出文件路径')
    args = parser.parse_args()

    try:
        with open(args.file, 'r', encoding='utf-8') as f:
            raw_request = f.read()
        
        parsed = parse_raw_request(raw_request)
        generated_code = generate_requests_code(parsed)
        
        with open(args.output, 'w', encoding='utf-8') as f:
            f.write(generated_code)
            print(f"[+] 转换完成：{args.output}")
            
    except FileNotFoundError:
        print(f"[!] 文件不存在：{args.file}")
    except Exception as e:
        print(f"[!] 处理异常：{str(e)}")