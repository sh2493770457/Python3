import json
import base64
import typing as t
import re  # TODO: 导入re模块用于解析multipart/form-data
import uuid  # TODO: 导入uuid模块用于生成新的boundary
from fastapi import FastAPI
from urllib.parse import parse_qs, urlencode  # TODO: 导入urllib.parse用于处理和构建urlencoded数据
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
from _base_classes import *

# TODO: 根据用户脚本，替换为实际的KEY
KEY = b"32byteslongsecretkeyforaes256!aa"
# TODO: 根据用户脚本，替换为实际的IV
IV = b"16byteslongiv456"
# TODO: 根据用户脚本分析，响应体中加密数据所在的JSON字段为 "data"
JSON_KEY = "data"
app = FastAPI()


@app.post("/hookRequestToBurp", response_model=RequestModel)
async def hook_request_to_burp(request: RequestModel):
    """HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。"""
    print(f"[+] hookRequestToBurp be called. request: {request.model_dump_json()}")
    # TODO: 使用正则表达式从 multipart/form-data 中提取加密的 username 字段
    # 注意: 这个正则表达式是基于您提供的数据包格式编写的，如果form-data结构复杂，可能需要调整
    match = re.search(b'name="username"\\r\\n\\r\\n(.*)\\r\\n', request.content)
    if match:
        encrypted_b64 = match.group(1).decode()
        encrypted_data = base64.b64decode(encrypted_b64)
        # 调用函数解密
        data: bytes = decrypt(encrypted_data)
        # TODO: 为了方便在Burp中修改，将请求体转换为 application/x-www-form-urlencoded 格式
        request.content = urlencode({"username": data.decode()}).encode()
        # TODO: 相应地更新 Content-Type 请求头，以便Burp能正确解析
        request.headers['Content-Type'] = ['application/x-www-form-urlencoded']
        # 移除可能存在的老的 content-type key (处理大小写不一致的情况)
        old_content_type_key = next((k for k in request.headers if k.lower() == 'content-type'), None)
        if old_content_type_key and old_content_type_key != 'Content-Type':
            del request.headers[old_content_type_key]
    return request


@app.post("/hookRequestToServer", response_model=RequestModel)
async def hook_request_to_server(request: RequestModel):
    """HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。"""
    print(f"[+] hookRequestToServer be called. request: {request.model_dump_json()}")
    # TODO: 从Burp修改后的 application/x-www-form-urlencoded 格式的请求体中获取明文数据
    data: bytes = parse_qs(request.content.decode())["username"][0].encode()
    # 调用函数加密回去
    encryptedData: bytes = encrypt(data)
    encrypted_b64_str = base64.b64encode(encryptedData).decode()

    # TODO: 重新构建服务器期望的 multipart/form-data 请求体
    boundary = uuid.uuid4().hex
    new_body = []
    new_body.append(f'--{boundary}')
    new_body.append('Content-Disposition: form-data; name="username"')
    new_body.append('')
    new_body.append(encrypted_b64_str)
    new_body.append(f'--{boundary}--')
    request.content = '\r\n'.join(new_body).encode('utf-8')

    # TODO: 更新 Content-Type 请求头以匹配新的 multipart/form-data 请求体
    request.headers['Content-Type'] = [f'multipart/form-data; boundary={boundary}']
    # 移除可能存在的老的 content-type key (处理大小写不一致的情况)
    old_content_type_key = next((k for k in request.headers if k.lower() == 'content-type'), None)
    if old_content_type_key and old_content_type_key != 'Content-Type':
        del request.headers[old_content_type_key]

    return request


@app.post("/hookResponseToBurp", response_model=ResponseModel)
async def hook_response_to_burp(response: ResponseModel):
    """HTTP响应从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。"""
    print(f"[+] hookResponseToBurp be called. response: {response.model_dump_json()}")
    # 获取需要解密的数据
    encryptedData: bytes = get_data(response.content)
    # 调用函数解密
    data: bytes = decrypt(encryptedData)
    # 更新body
    response.content = data
    return response


@app.post("/hookResponseToClient", response_model=ResponseModel)
async def hook_response_to_client(response: ResponseModel):
    """HTTP响应从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。"""
    print(f"[+] hookResponseToClient be called. response: {response.model_dump_json()}")
    # 获取被解密的数据
    data: bytes = response.content
    # 调用函数加密回去
    encryptedData: bytes = encrypt(data)
    # 将已加密的数据转换为Server可识别的格式
    body: bytes = to_data(encryptedData)
    # 更新body
    response.content = body
    return response


def decrypt(content: bytes) -> bytes:
    """解密函数，直接使用用户脚本的核心逻辑"""
    cipher = AES.new(KEY, AES.MODE_CBC, IV)
    return unpad(cipher.decrypt(content), AES.block_size)


def encrypt(content: bytes) -> bytes:
    """加密函数，直接使用用户脚本的核心逻辑"""
    cipher = AES.new(KEY, AES.MODE_CBC, IV)
    return cipher.encrypt(pad(content, AES.block_size))


def get_data(content: bytes) -> bytes:
    """从响应体中提取加密数据"""
    body_json: t.Dict = json.loads(content)
    return base64.b64decode(body_json[JSON_KEY])


def to_data(content: bytes) -> bytes:
    """将加密后的数据封装成响应体格式"""
    body_json = {}
    body_json[JSON_KEY] = base64.b64encode(content).decode()
    return json.dumps(body_json).encode()


if __name__ == "__main__":
    # 多进程启动
    # uvicorn your_script_name:app --host 0.0.0.0 --port 5000 --workers 4
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=5000)