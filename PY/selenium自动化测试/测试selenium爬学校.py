import requests
from lxml import etree
from selenium import webdriver as wd
import time
from tqdm import tqdm
from colorama import Fore

url = 'https://www.scit.cn/zszx_wskx.htm'
driver = wd.Chrome()
driver.get(url)

time.sleep(3)  # 等加载

data = driver.page_source
html = etree.HTML(data)

#图片名
names = html.xpath('//*[@id="wskxcontent"]/div[*]/div[2]/text()')
#图片地址
pictures = html.xpath('//*[@id="wskxcontent"]/div[*]/div[1]/a/img/@src')

# 使用tqdm，创建进度条
for name, picture in tqdm(zip(names, pictures), desc=Fore.CYAN + "下载进度", unit="张"):
    picture_url = 'https://www.scit.cn' + picture
    response = requests.get(picture_url)
    with open(f'{name}.jpg', 'wb') as f:
        f.write(response.content)

print('下载完成!')
driver.quit()
