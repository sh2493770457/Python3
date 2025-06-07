import requests
from fake_useragent import UserAgent
import re
import csv
import time

# Function to get song duration from song page
def get_song_duration(song_url):
    response = requests.get(url=song_url, headers=headers)
    html = response.text
    duration_match = re.search(r'"interval":(\d+),', html)
    if duration_match:
        seconds = int(duration_match.group(1))
        minutes, seconds = divmod(seconds, 60)
        return f"{minutes:02}:{seconds:02}"
    return "N/A"

# 打开csv文件
f = open('../测试/音乐热歌榜单.csv', mode='w', newline='', encoding='utf-8-sig')
w_headers = csv.DictWriter(f, fieldnames=['排名', '歌名', '歌手', '歌曲地址', '时长'])
w_headers.writeheader()

# 目标地址
url = 'https://y.qq.com/n/ryqq/toplist/26'

# 请求头
headers = {
    'user-agent': UserAgent().random
}

time.sleep(2)

# 发送请求
response = requests.get(url=url, headers=headers)
html = response.text

# re正则表达式解析提取数据
Dict = {}
title = re.finditer(r'<a title=".*?" href="(?P<src>.*?)">(?P<name>.*?)<.*?<a class="playlist__author" title="(?P<singer>.*?)" href', html)
j = 0
for i in title:
    j += 1
    Dict['排名'] = j
    Dict['歌名'] = i.group('name')
    Dict['歌手'] = i.group('singer')
    song_url = 'https://y.qq.com' + i.group('src')
    Dict['歌曲地址'] = song_url
    Dict['时长'] = get_song_duration(song_url)
    w_headers.writerow(Dict)
    print(f'保存完成-{j}')

f.close()
