#有道翻译
import uuid
import requests
import hashlib
import time

url = 'https://openapi.youdao.com/api'
sign = '5a7cc16ebe28508b'
salt = 'P3Su2ZHALLkedC4CZNZ4OTx7Nf4z4yts'

def encrypt(signStr):
    hash_algorithm = hashlib.sha256()
    hash_algorithm.update(signStr.encode('utf-8'))
    return hash_algorithm.hexdigest()

def truncate(q):
    if q is None:
        return None
    size = len(q)
    return q if size <= 20 else q[0:10] + str(size) + q[size - 10:size]

def do_request(data):
    headers = {'Content-Type': 'application/x-www-form-urlencoded'}
    response = requests.post(url, data=data, headers=headers)
    return response

def translate(text, from_lang='AUTO', to_lang='AUTO'):
    data = {
        'from': from_lang,
        'to': to_lang,
        'signType': 'v3',
        'curtime': str(int(time.time())),
        'salt': str(uuid.uuid1()),
        'appKey': sign,
        'q': text
    }
    signStr = sign + truncate(text) + data['salt'] + data['curtime'] + salt
    data['sign'] = encrypt(signStr)

    response = do_request(data)
    if response.status_code == 200:
        result = response.json()
        return result.get('translation', [])
    else:
        print("Error:", response.status_code, response.text)

if __name__ == '__main__':
    while True:
        input_text = input("请输入你要翻译的内容(输入T或t退出)：")
        if input_text.lower() == 't':
            break
        translated_text = translate(input_text)
        print("翻译结果:", translated_text)
