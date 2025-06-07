import requests
from lxml import etree
from selenium import webdriver as wd
import time
#隐藏浏览器
options = wd.ChromeOptions()
options.add_argument('--headless')


#小猫图片爬取
url = 'https://image.baidu.com/search/index?tn=baiduimage&ct=201326592&lm=-1&cl=2&ie=gb18030&word=%D0%A1%C3%A8%D5%D5%C6%AC&fr=ala&ala=1&alatpl=normal&pos=0&dyTabStr=MCwzLDIsMSw2LDQsNSw3LDgsOQ%3D%3D'
driver = wd.Chrome(options=options)
driver.get(url)

time.sleep(1)  # 如果需要的话，可以额外增加一些延迟
data = driver.page_source
html = etree.HTML(data)
pictures = html.xpath('//*[@id="imgid"]/div[1]/ul/li[*]/div/div[2]/a/img/@src')

for idx, picture_url in enumerate(pictures):
    response = requests.get(picture_url)
    with open(f'{idx + 1}.png', 'wb') as f:
        f.write(response.content)

driver.quit()  # 记得关闭浏览器
