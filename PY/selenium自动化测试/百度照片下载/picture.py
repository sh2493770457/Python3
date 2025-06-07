from lxml import etree
from selenium import webdriver as wd
import requests


keyword = input('请输入要搜索的图片：')
url = f'https://image.baidu.com/search/index?tn=baiduimage&fm=result&ie=utf-8&word={keyword}'
driver = wd.Edgee()
driver.get(url)

data = driver.page_source
html = etree.HTML(data)

# 获取图片链接
img_urls = html.xpath('//*[@id="imgid"]/div[1]/ul/li[*]/div/div[2]/a/img/@src')

count = 0
for img in img_urls:
    # 打印图片链接
    count += 1
    print(img)
    res = requests.get(img).content
    # 保存图片
    with open(f'测试版/{count}.jpg', 'wb') as f:
        f.write(res)



