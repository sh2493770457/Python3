import requests
from bs4 import BeautifulSoup

url = ('https://www.baidu.com/')
header={
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'
}
res = requests.get(url,headers=header)
soup = BeautifulSoup(res.text, 'html.parser')
# print(res.text)
# 获取第一条热搜链接及文本#hotsearch-content-wrapper > li:nth-child(1) > a
hot_search_link = soup.select_one('#hotsearch-content-wrapper > li:nth-child(1) > a')
print('链接：', hot_search_link['href'])
print('文本：', hot_search_link.text)
