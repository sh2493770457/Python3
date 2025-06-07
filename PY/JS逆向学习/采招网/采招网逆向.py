import csv
import requests
import base64
from Crypto.Cipher import AES
import re

# 采招网逆向
# https://curlconverter.com/
# url = 'https://interface.bidcenter.com.cn/search/GetSearchProHandler.ashx'

# 定义请求头
headers = {
    'accept': 'text/plain, */*; q=0.01',
    'accept-language': 'zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6',
    'cache-control': 'no-cache',
    'content-type': 'application/x-www-form-urlencoded; charset=UTF-8',
    'origin': 'https://search.bidcenter.com.cn',
    'pragma': 'no-cache',
    'priority': 'u=1, i',
    'referer': 'https://search.bidcenter.com.cn/',
    'sec-ch-ua': '"Not/A)Brand";v="8", "Chromium";v="126", "Microsoft Edge";v="126"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
    'sec-fetch-dest': 'empty',
    'sec-fetch-mode': 'cors',
    'sec-fetch-site': 'same-site',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0',
}

# 定义请求数据
data = {
    'from': '6137',
    'guid': 'a94cf1bd-bb65-482b-abfa-59607e79ed17',
    'location': '6138',
    'token': '',
    'keywords': 'python',
    'type': '1',
}

# 发送POST请求获取响应
response = requests.post('https://interface.bidcenter.com.cn/search/GetSearchProHandler.ashx', headers=headers,
                         data=data)

# 获取加密的base64数据
base64_encrypt_data = response.text

# 解码base64数据
encrypt_data = base64.b64decode(base64_encrypt_data)

# AES解密所需的密钥和初始向量
key = '3zKzyf6eEfuDjAG3'.encode()
iv = 'fyUANZ0qSNZhhNCV'.encode()

# 创建AES解密器
aes = AES.new(key, AES.MODE_CBC, iv)
data = aes.decrypt(encrypt_data)

# 定义每个字段的正则表达式
news_id = re.findall(r'"news_id":(\d+)', data.decode())
news_type = re.findall(r'"news_type":(\d+)', data.decode())
news_leibie = re.findall(r'"news_leibie":(\d+)', data.decode())
news_type_des = re.findall(r'"news_type_des":"([^"]*)"', data.decode())
news_url = re.findall(r'"news_url":"([^"]*)"', data.decode())
news_title_show = re.findall(r'"news_title_show":"([^"]*)"', data.decode())
news_tag_img = re.findall(r'"news_tag_img":"([^"]*)"', data.decode())
news_star_time_show = re.findall(r'"news_star_time_show":"([^"]*)"', data.decode())
news_end_time_show = re.findall(r'"news_end_time_show":"([^"]*)"', data.decode())
contain_kwd_fujian = re.findall(r'"contain_kwd_fujian":"([^"]*)"', data.decode())
news_diqustr = re.findall(r'"news_diqustr":"([^"]*)"', data.decode())
news_zbje_show = re.findall(r'"news_zbje_show":"([^"]*)"', data.decode())
news_zhongbiaojine_show = re.findall(r'"news_zhongbiaojine_show":"([^"]*)"', data.decode())
is_xiangmu = re.findall(r'"is_xiangmu":(true|false)', data.decode())
news_gczj_show = re.findall(r'"news_gczj_show":"([^"]*)"', data.decode())
news_jieduan_show = re.findall(r'"news_jieduan_show":"([^"]*)"', data.decode())
news_cgfs = re.findall(r'"news_cgfs":"([^"]*)"', data.decode())

# 将解析后的数据写入CSV文件
with open('招标信息.csv', mode='w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    # 写入表头
    writer.writerow(
        ['news_id', 'news_type', 'news_leibie', 'news_type_des', 'news_url', 'news_title_show', 'news_tag_img',
         'news_star_time_show', 'news_end_time_show', 'contain_kwd_fujian', 'news_diqustr', 'news_zbje_show',
         'news_zhongbiaojine_show', 'is_xiangmu', 'news_gczj_show', 'news_jieduan_show', 'news_cgfs'])

    # 写入每一条记录
    for item in zip(news_id, news_type, news_leibie, news_type_des, news_url, news_title_show, news_tag_img,
                    news_star_time_show, news_end_time_show, contain_kwd_fujian, news_diqustr, news_zbje_show,
                    news_zhongbiaojine_show, is_xiangmu, news_gczj_show, news_jieduan_show, news_cgfs):
        writer.writerow(item)
        print(item)
print("数据成功写入CSV文件。")
