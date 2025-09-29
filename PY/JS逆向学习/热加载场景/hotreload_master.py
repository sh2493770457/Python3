import json
import base64
import typing as t
import time
import hmac
import hashlib
from fastapi import FastAPI
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
from _base_classes import *

# TODO: 配置选项
ENCODING_METHOD = "hex"  # 加解密的参数类型,可选择 "base64" 或 "hex"
KEY = b"xxxxxxxxoooooooo"  # 根据实际情况修改
IV = b"0123456789ABCDEF"  # 根据实际情况修改
JSON_KEY = "r"  # 需要加解密的参数

# TODO: 开关
ENABLE_REQUEST_ENCRYPTION = False  # 是否启用请求体加解密
ENABLE_RESPONSE_ENCRYPTION = False  # 是否启用响应体加解密
ENABLE_PARAMETER_MODIFICATION = True  # 统一控制所有动态参数修改-->用于伪造请求,请求体或者请求头亦或是请求接口参数携带签名需要单独处理

app = FastAPI()


def modify_dynamic_parameters(request: RequestModel) -> RequestModel:
    """
    用于伪造请求,请求体或者请求头亦或是请求接口参数携带签名需要单独处理
    # TODO: 更新请求路径参数
    request.query.update({"x": [x]})

    # TODO: 获取请求路径
    request.path

    # TODO:获取请求头
    request.headers.

    # TODO: 更新请求头
    request.headers.update({"T": [T]})
    统一处理所有动态参数（请求头和查询参数），以解决它们之间的依赖关系。

    # TODO: 获取请求体键的值
    page = ""  # 默认值
    # request.content 是 bytes 类型, 需要先解码
    body_json = json.loads(request.content.decode('utf-8'))
    # 使用 .get() 来安全地获取值, 避免因缺少键而报错
    page = body_json.get("page")

    # TODO: 更新请求体
    body_json.update({"T": T_b64})
    request.content = json.dumps(body_json).encode('utf-8')

    :param request:
    :return: request
    """
    timestamp = str(int(time.time() * 1000))
    body_json = json.loads(request.content.decode('utf-8'))
    body_json.update({
        'm': hmac.new('xxxooo'.encode('utf-8'), f'9527{timestamp}'.encode('utf-8'), hashlib.sha1).hexdigest(),
        'tt': base64.b64encode(timestamp.encode('utf-8')).decode('utf-8'),
    })
    request.content = json.dumps(body_json).encode('utf-8')
    return request


def decrypt(content: bytes) -> bytes:
    """解密函数"""
    cipher = AES.new(KEY, AES.MODE_CBC, IV)
    return unpad(cipher.decrypt(content), AES.block_size)


def encrypt(content: bytes) -> bytes:
    """加密函数"""
    cipher = AES.new(KEY, AES.MODE_CBC, IV)
    return cipher.encrypt(pad(content, AES.block_size))


@app.post("/hookRequestToBurp", response_model=RequestModel)
async def hook_request_to_burp(request: RequestModel):
    """HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。"""
    print(f"[+] hookRequestToBurp 已被调用. 请求: {request.model_dump_json()}")

    if not ENABLE_REQUEST_ENCRYPTION:
        print("[!] 请求体解密已禁用，跳过")
        return request

    try:
        encrypted_data: bytes = get_data(request.content)
        data: bytes = decrypt(encrypted_data)
        request.content = data
    except (KeyError, ValueError, json.JSONDecodeError) as e:
        print(f"[!] 请求数据未加密，跳过解密: {e}")

    return request


@app.post("/hookRequestToServer", response_model=RequestModel)
async def hook_request_to_server(request: RequestModel):
    """HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。"""
    print(f"[+] hookRequestToServer 已被调用. 请求: {request.model_dump_json()}")

    # 调用统一的参数修改函数
    if ENABLE_PARAMETER_MODIFICATION:
        request = modify_dynamic_parameters(request)

    if not ENABLE_REQUEST_ENCRYPTION:
        print("[!] 请求体加密已禁用，跳过")
        return request

    try:
        try:
            body_json = json.loads(request.content)
            if JSON_KEY in body_json:
                print("[!] 请求数据已加密，跳过加密")
                return request
        except (json.JSONDecodeError, TypeError):
            pass

        data: bytes = request.content
        encrypted_data: bytes = encrypt(data)
        body: bytes = to_data(encrypted_data)
        request.content = body
    except Exception as e:
        print(f"[!] 加密失败: {e}")

    return request


@app.post("/hookResponseToBurp", response_model=ResponseModel)
async def hook_response_to_burp(response: ResponseModel):
    """HTTP响应从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。"""
    print(f"[+] hookResponseToBurp 已被调用. 响应: {response.model_dump_json()}")

    if not ENABLE_RESPONSE_ENCRYPTION:
        print("[!] 响应体解密已禁用，跳过")
        return response

    try:
        encrypted_data: bytes = get_data(response.content)
        data: bytes = decrypt(encrypted_data)
        response.content = data
    except (KeyError, ValueError, json.JSONDecodeError) as e:
        print(f"[!] 响应数据未加密，跳过解密: {e}")

    return response


@app.post("/hookResponseToClient", response_model=ResponseModel)
async def hook_response_to_client(response: ResponseModel):
    """HTTP响应从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。"""
    print(f"[+] hookResponseToClient 已被调用. 响应: {response.model_dump_json()}")

    if not ENABLE_RESPONSE_ENCRYPTION:
        print("[!] 响应体加密已禁用，跳过")
        return response

    try:
        try:
            body_json = json.loads(response.content)
            if JSON_KEY in body_json:
                print("[!] 响应数据已加密，跳过加密")
                return response
        except (json.JSONDecodeError, TypeError):
            pass

        data: bytes = response.content
        encrypted_data: bytes = encrypt(data)
        body: bytes = to_data(encrypted_data)
        response.content = body
    except Exception as e:
        print(f"[!] 加密失败: {e}")

    return response


def get_data(content: bytes) -> bytes:
    """从请求/响应中提取需要解密的数据"""
    body_json: t.Dict = json.loads(content)

    if JSON_KEY not in body_json:
        raise KeyError(f"Key '{JSON_KEY}' not found in JSON data")

    if ENCODING_METHOD == "base64":
        return base64.b64decode(body_json[JSON_KEY])
    else:  # hex
        return bytes.fromhex(body_json[JSON_KEY])


def to_data(content: bytes) -> bytes:
    """将加密后的数据转换为API需要的格式"""
    if ENCODING_METHOD == "base64":
        body = {JSON_KEY: base64.b64encode(content).decode()}
    else:  # hex
        body = {JSON_KEY: content.hex()}

    return json.dumps(body).encode()


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=5000)
