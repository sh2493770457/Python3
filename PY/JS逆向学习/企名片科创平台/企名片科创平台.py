import requests
from py_mini_racer import py_mini_racer

headers = {
    'Accept': 'application/json, text/plain, */*',
    'Accept-Language': 'zh-CN,zh;q=0.9',
    'Cache-Control': 'no-cache',
    'Connection': 'keep-alive',
    'Content-Type': 'application/x-www-form-urlencoded',
    'Origin': 'https://wx.qmpsee.com',
    'Platform': 'web',
    'Pragma': 'no-cache',
    'Sec-Fetch-Dest': 'empty',
    'Sec-Fetch-Mode': 'cors',
    'Sec-Fetch-Site': 'same-site',
    'Source': 'see',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36',
    'sec-ch-ua': '"Chromium";v="134", "Not:A-Brand";v="24", "Google Chrome";v="134"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
}

data = {
    'page': '1',
    'num': '20',
    'ca_uuid': 'feef62bfdac45a94b9cd89aed5c235be',
}

response = requests.post('https://wyiosapi.qmpsee.com/Web/getCaDetail', headers=headers, data=data)
# TODO:截取encrypt_data
encrypt_data = response.json()['encrypt_data']
# TODO:创建py_mini_racer实例并执行JS代码
decrypt_data = py_mini_racer.MiniRacer()

with open('企名片科创平台.js', encoding='utf-8') as f:
    decrypt_data.eval(f.read())

# TODO:然后用 ctx.call 来调用函数
decrypt_data = decrypt_data.call('Fc', encrypt_data)

print(decrypt_data)
