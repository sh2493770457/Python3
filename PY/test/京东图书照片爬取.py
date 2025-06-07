from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import time
import requests
import re

def clean_filename(filename, max_length=50):
    # 移除非法字符
    filename = re.add(r'[\\/*?:"<>|]', "", filename)
    # 限制文件名长度
    return filename[:max_length]

# 初始化WebDriver
driver = webdriver.Edge()
driver.get("https://www.jd.com")

# 输入搜索内容并执行搜索
search_box = driver.find_element(By.ID, 'key')
search_box.send_keys('书籍')
search_box.send_keys(Keys.ENTER)
time.sleep(20)  # 等待页面加载

# 爬取商品标题和图片URL
titles = driver.find_elements(By.CSS_SELECTOR, 'li.gl-item div.p-name a em')
images = driver.find_elements(By.CSS_SELECTOR, 'li.gl-item div.p-img a img')

title = [title.text for title in titles]
img = [image.get_attribute('src') for image in images if image.get_attribute('src')]

# 下载图片
for i in range(len(title)):
    picture_url = 'https:' + img[i] if not img[i].startswith('http') else img[i]
    picture_name = clean_filename(title[i]) + '.jpg'
    try:
        picture_data = requests.get(picture_url, headers={'User-Agent': 'Mozilla/5.0'}).content
        with open(picture_name, 'wb') as f:
            f.write(picture_data)
    except requests.RequestException as e:
        print(f"无法下载 {picture_name}: {e}")

# 关闭浏览器
driver.close()
