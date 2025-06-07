from selenium import webdriver as wd
import time
from lxml import etree

url='https://www.maoyan.com/films?showType=3'
driver=wd.Chrome()
driver.get(url)

time.sleep(20)
#//*[@id="app"]/div/div[2]/div/div[1]/div[2]/dl/dd[1]/div/a/div/div/div/div[2]
#//*[@id="app"]/div/div[2]/div/div[1]/div[2]/dl/dd[2]/div/a/div/div/div/div[2]

data=driver.page_source
html=etree.HTML(data)

# //*[@id="app"]/div/div[2]/div[2]/dl/dd[2]/div[2]/a
name=html.xpath('//*[@id="app"]/div/div[2]/div[2]/dl/dd[*]/div[2]/a/text()')
print(name)

grade=html.xpath('//*[@id="app"]/div/div[2]/div[2]/dl/dd[*]/div[3]/text()')
print(grade)
