import random
import pymysql
import requests
from selenium import webdriver as wd
import time
from lxml import etree
import re
import os

# 设置浏览器选项
options = wd.EdgeOptions()
options.add_argument('--headless')  # 启用无头模式

# 创建浏览器实例
driver = wd.Edge(options=options)

# 输入要爬取的页码
page = int(input("请输入要爬取的页码："))

# 设置图片保存路径
save_path = "D:/desired_folder/downloaded_images"

# 创建保存图片的目录
if not os.path.exists(save_path):
    os.makedirs(save_path)

# 连接数据库
conn = pymysql.connect(host='localhost', user='root', password='root', charset='utf8')
cursor = conn.cursor()

# 选择数据库
conn.select_db('moviesdata')

# 创建数据表
cursor.execute("""
    CREATE TABLE IF NOT EXISTS allmovies (
        id INT PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(255),
        score VARCHAR(255),
        director VARCHAR(255),
        scriptwriter VARCHAR(255),
        actor VARCHAR(255),
        years VARCHAR(255),
        country VARCHAR(255),
        languages VARCHAR(255),
        length VARCHAR(255),
        image VARCHAR(255),
        des VARCHAR(255),
        url VARCHAR(255),
        type VARCHAR(255)
    )
""")
conn.commit()

# 计算该页的起始位置
start = (page - 1) * 25

# 生成页面的 URL 并请求页面内容
base_url = f'https://movie.douban.com/top250?start={start}'
driver.get(base_url)
time.sleep(2)  # 增加等待时间以确保页面加载完成

# 获取页面源码
tree = etree.HTML(driver.page_source)

# 获取各项信息
ids = tree.xpath('//*[@id="content"]/div/div[1]/ol/li/div/div[1]/em/text()')
urls = tree.xpath('//*[@id="content"]/div/div[1]/ol/li/div/div[1]/a/@href')
names = tree.xpath('//*[@id="content"]/div/div[1]/ol/li/div/div[2]/div[1]/a/span[1]/text()')
directors_raw = tree.xpath("//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='info']/div[@class='bd']/p[1]/text()")
scores = tree.xpath('//*[@id="content"]/div/div[1]/ol/li/div/div[2]/div[2]/div[1]/span[2]/text()')
descriptions = tree.xpath("//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='info']/div[@class='bd']/p[@class='quote']/span[@class='inq']/text()")
images = tree.xpath("//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='pic']/a/img/@src")
countries_raw = tree.xpath("//*[@id='content']/div/div[1]/ol/li/div/div[2]/div[2]/p[1]/text()[2]")
movie_years_raw = tree.xpath("//div[@class='item']/div[@class='info']/div[@class='bd']/p[1]/text()[2]")


# 提取导演信息
directors = [re.findall(r'导演:(.*?)\s\s', director.strip())[0] for director in directors_raw if re.findall(r'导演:(.*?)\s\s', director.strip())]

# 提取年份信息
years = [re.findall(r'\d{4}', year.strip())[0] + '年' for year in movie_years_raw if re.findall(r'\d{4}', year.strip())]

# 提取国家信息
countries = [re.findall(r'([\u4e00-\u9fa5]+)', country.strip())[0] for country in countries_raw if re.findall(r'([\u4e00-\u9fa5]+)', country.strip())]

# 提取主演信息
actors_raw = tree.xpath("//*[@id='content']/div/div[1]/ol/li/div/div[2]/div[2]/p[1]/text()")
actors = []
for m in actors_raw:
    match = re.search(r'主演:\s*([\u4e00-\u9fa5·/\s]+)\s*', m.strip())
    if match:
        actors.append(match.group(1))

# 生成语言、时长、类型信息
language = ['中文', '英语', '俄语', '墨西哥语', '日语', '韩语', '法语', '德语', '意大利语', '西班牙语']
languages = random.choices(language, k=len(names))
lengths = [str(random.randint(120, 180)) + '分钟' for _ in range(len(names))]
types = ['动作', '冒险', '喜剧', '爱情', '科幻', '恐怖', '悬疑', '动画', '犯罪', '纪录片', '家庭', '历史', '奇幻', '音乐', '剧情', '惊悚', '战争', '西部']
movie_types = [','.join(random.sample(types, 1)) for _ in range(len(names))]


# 检查所有列表长度是否一致
# min_length = min(len(names), len(scores), len(directors), len(actors), len(years), len(countries), len(languages), len(lengths), len(images), len(descriptions), len(urls), len(movie_types))

# 将数据插入到数据库
# insert_sql = """
#     INSERT INTO allmovies
#     (name, score, director, scriptwriter, actor, years, country, languages, length, image, des, url, type)
#     VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
# """
# for i in range(min_length):
#     # 先插入数据以生成自增ID
#     cursor.execute(insert_sql, (names[i], scores[i], directors[i], directors[i], actors[i], years[i], countries[i], languages[i], lengths[i], "", descriptions[i], urls[i], movie_types[i]))
#     conn.commit()
#
#     # 获取自增ID
#     cursor.execute("SELECT LAST_INSERT_ID()")
#     last_id = cursor.fetchone()[0]
#
#     # 下载图片并保存为{id}.jpg
#     image_filename = os.path.join(save_path, f"{last_id}.jpg")
#     image_url = images[i]
#     response = requests.get(image_url)
#     with open(image_filename, "wb") as f:
#         f.write(response.content)
#         print(f"{image_filename} 下载完成")
#
#     # 更新数据库中的image字段
#     cursor.execute("UPDATE allmovies SET image = %s WHERE id = %s", (image_filename, last_id))
#     conn.commit()
#
#     print(f"{names[i]} 插入完成")

# 关闭数据库连接
# cursor.close()
# conn.close()
# driver.quit()


