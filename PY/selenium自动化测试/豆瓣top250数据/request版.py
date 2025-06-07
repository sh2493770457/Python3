import requests
from bs4 import BeautifulSoup as bp
import re

headers = {
    'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
    'accept-language': 'zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6',
    'cache-control': 'no-cache',
    'cookie': 'your_cookie_here',
    'pragma': 'no-cache',
    'sec-fetch-dest': 'document',
    'sec-fetch-mode': 'navigate',
    'sec-fetch-site': 'none',
    'sec-fetch-user': '?1',
    'upgrade-insecure-requests': '1',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36 Edg/129.0.0.0',
}

# TODO:定义一个空列表,用来装东西
movies = []

# TODO:遍历每个分页,跨步为25;还能使用while循环(i=0 while<250 i+=25)
for i in range(0, 250, 25):
    url = f'https://movie.douban.com/top250?start={i}'
    response = requests.get(url, headers=headers)
    html = response.text
    soup = bp(html, 'html.parser')

    # TODO:获取排名
    ranks = soup.find_all('em', class_='')
    # TODO:获取影片名，筛选掉以`/`开头的影片名并去除前后空格
    names = [name.get_text() for name in soup.find_all('span', class_='title') if
             not name.get_text().strip().startswith('/')]
    # TODO:获取评分
    grades = soup.find_all('span', class_='rating_num')
    # TODO:获取图片URL
    images_url = soup.find_all('img', class_='')
    # TODO:获取评价人数（<span>3071954人评价</span>）
    goods_person = soup.find_all('span', text=re.compile(r'\d+人评价'))

    # TODO: 将数据放入到movies
    for i in range(len(ranks)):
        rank = ranks[i].get_text()
        name = names[i]
        rating = grades[i].get_text()
        image_url = images_url[i]['src']
        comment = goods_person[i].get_text()

        movies.append({
            "rank": rank,
            "name": name,
            "rating": rating,
            "image_url": image_url,
            "comment": comment
        })

# TODO: 打印所有电影信息
for movie in movies:
    print(f"排名: {movie['rank']}")
    print(f"影片名: {movie['name']}")
    print(f"评分: {movie['rating']}")
    print(f"图片链接: {movie['image_url']}")
    print(f"评价人数: {movie['comment']}")
    print("-" * 90)

print(f"共爬取到 {len(movies)} 部电影的数据。")
