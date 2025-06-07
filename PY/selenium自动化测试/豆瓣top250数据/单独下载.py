import os
import requests
from selenium import webdriver as wd
import time
from lxml import etree

# 设置浏览器选项
options = wd.EdgeOptions()
options.add_argument('--headless')  # 启用无头模式

# 创建浏览器实例
driver = wd.Edge(options=options)

# 输入要下载的页码
page = int(input("请输入要下载的页码："))

# 设置图片保存路径
save_path = "C:/Users/24937/Desktop/图片"

# 创建保存图片的目录
if not os.path.exists(save_path):
    os.makedirs(save_path)

# 计算该页的起始位置
start = (page - 1) * 25

# 生成页面的 URL 并请求页面内容
base_url = f'https://movie.douban.com/top250?start={start}'
driver.get(base_url)
time.sleep(2)  # 增加等待时间以确保页面加载完成

# 获取页面源码
tree = etree.HTML(driver.page_source)

# 获取电影名和图片URL
names = tree.xpath('//*[@id="content"]/div/div[1]/ol/li/div/div[2]/div[1]/a/span[1]/text()')
images = tree.xpath("//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='pic']/a/img/@src")

# 下载图片并保存
for index in range(len(names)):
    image_url = images[index]
    image_filename = f"{index + 1}.jpg"  # 根据每页的排名从1到25命名图片
    response = requests.get(image_url)
    with open(os.path.join(save_path, image_filename), "wb") as f:
        f.write(response.content)
        print(f"{image_filename} 下载完成")

# 关闭浏览器实例
driver.quit()
