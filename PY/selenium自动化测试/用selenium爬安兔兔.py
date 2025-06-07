from selenium import webdriver as wd
import time
from lxml import etree
import csv

url = 'https://www.antutu.com/ranking/rank1.htm'
driver = wd.Chrome()
driver.get(url)
time.sleep(5)

data = driver.page_source
html = etree.HTML(data)

phone_rank = html.xpath('//*[@id="rank"]/div[1]/div/div/div[1]/div/div[1]/div/ul[*]/a/div[2]/li[1]/span[1]/text()')
phone_name = html.xpath('//*[@id="rank"]/div[1]/div/div/div[1]/div/div[1]/div/ul[*]/a/div[2]/li[1]/text()')
phone_cpu = html.xpath('//*[@id="rank"]/div[1]/div/div/div[1]/div/div[1]/div/ul[*]/a/div[2]/li[2]/text()')
phone_gpu = html.xpath('//*[@id="rank"]/div[1]/div/div/div[1]/div/div[1]/div/ul[*]/a/div[2]/li[3]/text()')
phone_mem = html.xpath('//*[@id="rank"]/div[1]/div/div/div[1]/div/div[1]/div/ul[*]/a/div[2]/li[4]/text()')
phone_score = html.xpath('//*[@id="rank"]/div[1]/div/div/div[1]/div/div[1]/div/ul[*]/a/div[2]/li[6]/text()')
# 去掉phone_score中的'分'字
phone_score = [score.replace(' 分', '') for score in phone_score]

# 写入CSV文件
with open('phone_data(selenium).csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    # 写入表头
    writer.writerow(['Rank', 'Name', 'CPU', 'GPU', 'Memory', 'Score'])
    # 写入数据
    for rank, name, cpu, gpu, mem, score in zip(phone_rank, phone_name, phone_cpu, phone_gpu, phone_mem, phone_score):
        writer.writerow([rank, name, cpu, gpu, mem, score])

# 关闭WebDriver
driver.quit()
