# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 图灵19
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/9/28 20:37
import json
import time
from datetime import datetime
import requests
from Crypto.Cipher import DES3
from Crypto.Util.Padding import unpad
import base64


def decrypt_3des_base64(cipher_b64: str, key: str) -> str:
    """
    3DES CBC 解密，密文为 base64，密钥为 str
    :param cipher_b64: base64 编码密文
    :param key: 3DES 密钥字符串（16 或 24 字节）
    :return: 解密后的明文字符串
    """
    Iv = datetime.now().strftime('%Y%m%d').encode('utf-8')  # 8 字节

    cipher_bytes = base64.b64decode(cipher_b64)
    key_bytes = key.encode()  # 3DES 需要 bytes

    cipher = DES3.new(DES3.adjust_key_parity(key_bytes), DES3.MODE_CBC, Iv)
    decrypted = unpad(cipher.decrypt(cipher_bytes), DES3.block_size)

    return decrypted.decode('utf-8')


total_sum = 0
for page in range(1, 21):
    headers = {
        'accept': '*/*',
        'accept-language': 'zh-CN,zh;q=0.9',
        'cache-control': 'no-cache',
        'pragma': 'no-cache',
        'priority': 'u=1, i',
        'referer': 'https://www.mashangpa.com/problem-detail/19/',
        'sec-ch-ua': '"Chromium";v="140", "Not=A?Brand";v="24", "Google Chrome";v="140"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
        'sec-fetch-dest': 'empty',
        'sec-fetch-mode': 'cors',
        'sec-fetch-site': 'same-origin',
        'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',
        'cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; _nano_fp=XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj; v=QS1SVnhZbzdYOVFkMGFSRFF5eEFGbDhidGVuVGZRa0x5cUtjSmY0RWNfZHlnSXJYSm8zWWR4cXhiS2ROMTc1OTA0NzEyOTI1NQ==; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1758727507,1759024851,1759028808,1759061175; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1759062619',
    }

    params = {
        'page': page,
    }

    response = requests.get('https://www.mashangpa.com/api/problem-detail/19/data/', params=params, headers=headers)
    r = response.json()['r']
    k = response.json()['k']

    response = decrypt_3des_base64(r, k)
    response = json.loads(response)
    current_array = response['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response, end='\n\n')
    total_sum += num_sum
    time.sleep(0.1)
print("\n最终20页累加和为:", total_sum)
