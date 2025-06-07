import requests
from lxml import etree
from tqdm import tqdm

# 目标网页的 URL
url = 'https://tieba.baidu.com/p/8911872427'

# 发送 HTTP 请求并获取响应内容
response = requests.get(url).text

# 使用 lxml 库解析 HTML 内容
find = etree.HTML(response)

# 提取图片的 URL(//xxx[@class="xxx"/@xxx])
image_urls = find.xpath('//img[@class="BDE_Image"]/@src')

# 计数器，用于为图片命名
picture = 0

# 遍历图片 URL 列表
for image_url in image_urls:
    # 发送 HTTP 请求获取图片内容
    image_content = requests.get(image_url, stream=True)

    # 获取文件大小以便进行进度条显示
    total_size = int(image_content.headers.get('content-length', 0))

    # 将图片内容写入本地文件，并显示进度条
    with open('{}.jpg'.format(picture), 'wb') as  f, tqdm(
            total=total_size,unit='B', unit_scale=True, desc='Image {}'.format(picture), colour='blue'
    ) as pbar:
        for data in image_content.iter_content(chunk_size=1024):
            f.write(data)
            pbar.update(len(data))

    # 更新图片命名计数器
    picture += 1

print("图片下载成功")
