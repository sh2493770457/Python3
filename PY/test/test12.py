# 导入所需的库
import requests
from fake_useragent import UserAgent
import csv
from bs4 import BeautifulSoup

# 打开一个CSV文件以写入抓取到的数据
f = open('3、当当网.csv', mode='w', newline='', encoding='utf-8-sig')
w_headers = csv.DictWriter(f,
                           fieldnames=['name', 'comments', 'writer', 'Date of publication', 'publishing house', 'price',
                                       'E-books_price'])
w_headers.writeheader()

# 设置headers中的用户代理
headers = {
    'User-Agent': UserAgent().random
}

# 设置要抓取的页面数量
page = 2
a = 0

# 遍历每一页
for p in range(1, page + 1):
    # 构建当前页的URL
    url = f'http://bang.dangdang.com/books/bestsellers/01.00.00.00.00.00-recent7-0-0-1-{p}'

    # 发送GET请求获取页面内容
    response = requests.get(url=url, headers=headers)
    res = response.text

    # 使用BeautifulSoup解析HTML内容
    soup = BeautifulSoup(res, 'html.parser')

    # 初始化一个字典来存储书籍信息
    dit = {}
    j = 0

    # 遍历当前页面上的每本书籍
    for i in range(0, 20):
        # 提取每本书的信息
        dit['name'] = soup.find_all('div', class_='name')[i].text
        dit['comments'] = soup.find_all('div', class_='star')[i].text
        dit['writer'] = soup.find_all('div', class_="publisher_info")[i * 2].text
        dit['Date of publication'] = soup.find_all('div', class_="publisher_info")[i * 2 + 1].find_next('span').text
        dit['publishing house'] = soup.find_all('div', class_="publisher_info")[i * 2 + 1].find_next('a').text
        dit['price'] = soup.find_all('div', class_='price')[i].find_next('p').find_next('span', class_='price_n').text
        dit['E-books_price'] = soup.find_all('p', class_="price_e")[i].find_next('span').text

        # 将信息写入CSV文件
        w_headers.writerow(dit)

        # 更新计数器并打印进度
        a += 1
        print(f'已保存完成{a}本书的数据信息')

        # 增加索引以导航HTML结构
        j += 2

# 关闭CSV文件
f.close()
