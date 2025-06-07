import csv
import requests
from bs4 import BeautifulSoup


#提交关键字到url进行搜索
keyword=input("请输入要搜索的书籍名字：")
url='https://search.dangdang.com/?key={}&act=input'.format(keyword)

# url='https://search.dangdang.com/?key=呐喊&act=input'
# 设置请求头，模拟浏览器访问
headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36'
    }
res = requests.get(url, headers=headers)
# print(res.text)

#用bs4获取a标签里的titel元素
soup = BeautifulSoup(res.text, 'html.parser')
# print(soup.prettify())

# 获取书名 <a title=" 呐喊（少年儿童读本，收录鲁迅小说集《呐喊》+《彷徨》） "
a_tags = soup.find_all('a', class_='pic')
for a_tag in a_tags:
    title = a_tag.get('title')
    # print(title)

#获取价格 <span class="search_now_price">&yen;7.00</span>
price_tags = soup.find_all('span', class_='search_now_price')
for price_tag in price_tags:
    price = price_tag.text
    # print(price)

#提取简介 <p class="detail">中国现代白话小说的开山之作和扛鼎之作，鲁迅写的非常好的小说集。欲读鲁迅小说，只需读一本《呐喊》。 今天我们中国社会遇见的问题，鲁迅都曾在《呐喊》里提到过；鲁迅是超越时代的，今天，我们读鲁迅，更有其深刻意义。 中国国民的劣根性，在这本小说里鲁迅都提到过，比《丑陋的中国人》更深刻。 在鲁迅之前，中国小说史上还没有真正塑造农民形象的作品。鲁迅的真情始终倾注在农民身上，但他更重于挖掘旧中国农民的精神残疾和国民性格中的奴性。这在《彷徨》中也有深刻的体现。</p>
intro_tags = soup.find_all('p', class_='detail')
for intro_tag in intro_tags:
    intro = intro_tag.text
    # print(intro)

#提取作者、出版时间、出版社
publish_tags = soup.find_all('p', class_='search_book_author')
for publish_tag in publish_tags:
    publish = publish_tag.text.replace('加入购物车购买电子书收藏', '').replace('加入购物车收藏', '').strip()
    # print(publish)

# 写入csv
with open('book.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['书名', '价格', '简介', '出版信息'])
    for i in range(len(a_tags)):
        writer.writerow([a_tags[i].get('title'), price_tags[i].text, intro_tags[i].text, publish_tags[i].text.replace('加入购物车购买电子书收藏', '').replace('加入购物车收藏', '').strip()])
