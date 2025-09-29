# -*- encoding: utf-8 -*-
# TODO:@ModuleName: RSA加密与接口调用
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/6/5 11:02
import requests
import hashlib
import base64
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_v1_5


class RsaEncryptor:
    """ TODO: 基于RSA公钥的加密工具类 """

    def __init__(self):
        # TODO: 服务器提供的RSA公钥
        self._public_key = (
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCy9jksG0BZ7L2I4+mBfc+/DmWhfUNmgj/"
            "LtvqyJnYfNuLczOHsEg7WRqKOLi/tPDBcVobZ/hcTuUbF8Fdpeo881xRHr6IP5Wox2FrP+1LL"
            "gd4OpJ/jsDhu2JImULLfSw7/RhD9xsZ5MpPNGdKmdLHOqP8/p/WbDsR7N5fFeXwqwIDAQAB"
        )

    def _get_cipher(self):
        pem_key = f"-----BEGIN PUBLIC KEY-----\n{self._public_key}\n-----END PUBLIC KEY-----"
        rsa_key = RSA.import_key(pem_key)
        return PKCS1_v1_5.new(rsa_key)

    def encrypt(self, plaintext: str) -> str:
        """ TODO: 对文本进行RSA分段加密并Base64编码 """
        cipher = self._get_cipher()
        raw_bytes = plaintext.encode("utf-8")
        max_chunk_size = 117  # PKCS1_v1_5 最大加密块大小

        if len(raw_bytes) <= max_chunk_size:
            encrypted_bytes = cipher.encrypt(raw_bytes)
            return base64.b64encode(encrypted_bytes).decode()
        else:
            chunks = [plaintext[i:i + 37] for i in range(0, len(plaintext), 37)]
            encrypted_chunks = []
            for chunk in chunks:
                encrypted_chunk = cipher.encrypt(chunk.encode("utf-8"))
                encrypted_chunks.append(base64.b64encode(encrypted_chunk).decode())
            return ",".join(encrypted_chunks)


class ModifySysUserInfo:
    """ TODO: 用户信息修改接口调用 """

    def __init__(self, Authorization: str):
        self.Authorization = Authorization
        self._url = "https://nlkf.eda.anhuitelecom.com:31903/aop-service-workbench/api/user/modifySysUserInfo"
        self._headers = {
            "Origin": "https://nlkf.eda.anhuitelecom.com:31903",
            "Sec-Ch-Ua": "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
            "Accept": "application/json, text/plain, */*",
            "Sec-Ch-Ua-Platform": "\"Windows\"",
            "Priority": "u=1, i",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                          "(KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
            "Referer": "https://nlkf.eda.anhuitelecom.com:31903/yitihua-developer-workbench/userInfo?projectId=253&roleId=452&from=manager&PSID=74ec8c25-3d39-46e4-aad8-fccda2a532e8&authKey=9c1e7774-28e7-4da6-beca-41b3971b2bcb&usertype=undefined&clusterId=1&envFlag=1",
            "Sec-Fetch-Site": "same-origin",
            "Sec-Fetch-Dest": "empty",
            "Pragma": "no-cache",
            "Redirect": "https://nlkf.eda.anhuitelecom.com:31903/yitihua-developer-workbench/dataManager/dataSubscribe/DataRetrievalIndex?projectId=253&clusterId=1&envFlag=1&PSID=74ec8c25-3d39-46e4-aad8-fccda2a532e8",
            "Accept-Encoding": "gzip, deflate, br",
            "Sec-Fetch-Mode": "cors",
            "Authorization": self.Authorization,
            "Cache-Control": "no-cache",
            "Accept-Language": "zh-CN,zh;q=0.9",
            "Sec-Ch-Ua-Mobile": "?0",
            "Content-Type": "application/json;charset=UTF-8"
        }

    def post_encrypted_user_info(self, msg: str) -> tuple[int, str]:
        """ TODO: 使用RSA加密消息并发送POST请求 """
        rsa_encryptor = RsaEncryptor()
        encrypt_md5 = hashlib.md5(msg.encode("utf-8")).hexdigest()
        encrypt_str = rsa_encryptor.encrypt(msg)

        payload = {
            "encryptMD5": encrypt_md5,
            "encryptStr": encrypt_str,
            "encryptType": "all"
        }

        response = requests.post(self._url, headers=self._headers, json=payload)
        return response.status_code, response.text


if __name__ == "__main__":
    user_info_json = '{"user_name":"2222222222","moble_tel":"130****0717","work_tel":"0551-12345678","email":"4****@qq.com","userId":173560}'
    authorization = "TYDIC_AUTH auth_key=9c1e7774-28e7-4da6-beca-41b3971b2bcb;from=109"
    modifier = ModifySysUserInfo(authorization)
    status, resp_text = modifier.post_encrypted_user_info(user_info_json)
    print(f"[状态码]: {status}")
    print(f"[响应正文]:\n{resp_text}")
