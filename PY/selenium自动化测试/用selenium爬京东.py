from selenium import webdriver as wd
import time
from lxml import etree
import requests

#京东图书爬取
url='https://list.jd.com/list.html?cat=1713,3259,3333&czLogin=1'

driver = wd.Edge()
driver.get(url)
time.sleep(20)

data=driver.page_source
# print(data)


html=etree.HTML(data)
#标题
title=html.xpath('//*[@id="J_goodsList"]/ul/li[*]/div/div[3]/a/em/text()')
#价格
price=html.xpath('//*[@id="J_goodsList"]/ul/li[*]/div/div[2]/strong/i/text()')
#图片
# img=html.xpath('//*[@id="J_goodsList"]/ul/li[*]/div/div[1]/a/img/@src')
# time.sleep(10)

with open('京东图书.txt','w',encoding='utf-8') as f:
    for i in range(len(title)):
        f.write('书名：'+title[i]+'\n')
        f.write('价格：'+price[i]+'\n')
        # f.write('图片：'+img[i]+'\n')
        f.write('\n')

# https://img12.360buyimg.com/n7/jfs/t29314/24/1013367370/355495/26e1f9b/5c04b731N775a17ee.jpg
# //img12.360buyimg.com/n7/jfs/t29314/24/1013367370/355495/26e1f9b/5c04b731N775a17ee.jpg
# picture_url='https:'+img[0]
# print(picture_url)

#下载图片以{}.jpg命名
# for i in range(len(img)):
#     picture_url='https:'+img[i]
#     picture_name=str(i)+'.jpg'
#     picture_data=requests.get(picture_url).content
#     with open(picture_name,'wb') as f:
#         f.write(picture_data)

