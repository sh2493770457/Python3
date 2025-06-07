import os
import requests
from selenium import webdriver
from selenium.webdriver.edge.options import Options
from selenium.webdriver.common.by import By
import time

# 输入要搜索的图片关键字和下载的图片数量
query = input("请输入要搜索的图片关键字：")
num_images = int(input("请输入要下载的图片数量："))
directory = "baidu_images"

# 创建保存图片的目录
if not os.path.exists(directory):
    os.makedirs(directory)

# 设置浏览器选项
options = Options()
options.add_argument('--headless')  # 启用无头模式，隐藏浏览器
options.add_argument('--disable-gpu')
driver = webdriver.Edge(options=options)

# 访问百度图片搜索页面
search_url = f"https://image.baidu.com/search/index?tn=baiduimage&word={query}"
driver.get(search_url)
time.sleep(2)  # 等待页面加载

# 初始化图片 URL 列表和计数器
img_urls = []
count = 0

# 抓取图片 URL
while len(img_urls) < num_images:
    # 向下滚动以加载更多图片
    driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
    time.sleep(2)  # 等待新图片加载

    # 查找图片元素
    img_elements = driver.find_elements(By.XPATH, '//img[@class="main_img img-hover"]')

    for img in img_elements:
        # 获取图片 URL
        img_url = img.get_attribute('data-imgurl')

        # 检查图片 URL 是否已存在，并添加到列表中
        if img_url and img_url not in img_urls:
            img_urls.append(img_url)
            count += 1
            if count >= num_images:
                break

# 关闭浏览器
driver.quit()

# 下载并保存图片
for i, img_url in enumerate(img_urls, start=1):
    try:
        response = requests.get(img_url, stream=True)
        # 检查响应状态码
        if response.status_code == 200:
            file_path = os.path.join(directory, f'{query}-{i}.jpg')
            with open(file_path, 'wb') as file:
                for chunk in response.iter_content(1024):
                    file.write(chunk)
            print(f'成功下载: {img_url}')
        else:
            print(f'无法获取: {img_url}')
    except Exception as e:
        print(f'发生错误: {e}')
