import csv
import random
import time
import re
from selenium import webdriver as wd
from selenium.webdriver.edge.options import Options
from lxml import etree

# 设置浏览器选项
options = Options()
options.add_argument('--headless')  # 启用无头模式

# 创建浏览器实例
driver = wd.Edge(options=options)

# 输入要爬取的页码
page = int(input("请输入要爬取的页数："))

# 初始化信息列表
ids = []
urls = []
names = []
directors = []
scores = []
descriptions = []
images = []
countries = []
years = []
actors = []

# 生成页面的 URL 并请求页面内容，循环爬取每一页
for i in range(0, page * 25, 25):
    base_url = f'https://movie.douban.com/top250?start={i}'
    driver.get(base_url)
    time.sleep(2)  # 增加等待时间以确保页面加载完成

    # 获取页面源码
    tree = etree.HTML(driver.page_source)

    # 获取各项信息
    ids.extend(tree.xpath('//*[@id="content"]/div/div[1]/ol/li/div/div[1]/em/text()'))
    urls.extend(tree.xpath('//*[@id="content"]/div/div[1]/ol/li/div/div[1]/a/@href'))
    names.extend(tree.xpath('//*[@id="content"]/div/div[1]/ol/li/div/div[2]/div[1]/a/span[1]/text()'))
    directors_raw = tree.xpath("//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='info']/div[@class='bd']/p[1]/text()")
    scores.extend(tree.xpath('//*[@id="content"]/div/div[1]/ol/li/div/div[2]/div[2]/div[1]/span[2]/text()'))
    descriptions.extend(tree.xpath("//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='info']/div[@class='bd']/p[@class='quote']/span[@class='inq']/text()"))
    images.extend(tree.xpath("//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='pic']/a/img/@src"))
    countries_raw = tree.xpath("//*[@id='content']/div/div[1]/ol/li/div/div[2]/div[2]/p[1]/text()[2]")
    movie_years_raw = tree.xpath("//div[@class='item']/div[@class='info']/div[@class='bd']/p[1]/text()[2]")
    actors_raw = tree.xpath("//*[@id='content']/div/div[1]/ol/li/div/div[2]/div[2]/p[1]/text()")

    # 提取导演信息
    directors.extend([re.findall(r'导演:(.*?)\s\s', director.strip())[0] for director in directors_raw if re.findall(r'导演:(.*?)\s\s', director.strip())])

    # 提取年份信息
    years.extend([re.findall(r'\d{4}', year.strip())[0] + '年' for year in movie_years_raw if re.findall(r'\d{4}', year.strip())])

    # 提取国家信息
    countries.extend([re.findall(r'([\u4e00-\u9fa5]+)', country.strip())[0] for country in countries_raw if re.findall(r'([\u4e00-\u9fa5]+)', country.strip())])

    # 提取主演信息
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

# 保存数据为 CSV 文件
with open('top250.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['电影名称', '导演', '主演', '年份', '国家', '语言', '时长', '类型', '评分', '简介'])
    for i in range(len(names)):
        if i < len(directors) and i < len(actors) and i < len(years) and i < len(countries) and i < len(languages) and i < len(lengths) and i < len(movie_types) and i < len(scores) and i < len(descriptions):
            writer.writerow([names[i], directors[i], actors[i], years[i], countries[i], languages[i], lengths[i], movie_types[i], scores[i], descriptions[i]])

# 关闭浏览器
driver.quit()
