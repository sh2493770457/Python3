import requests
from lxml import etree

url = 'https://www.baidu.com'
response = requests.get(url)
html = response.text
html = etree.HTML(html)

# 获取logo
logo = html.xpath("//div[@id='lg']/img/@src")[0]
# 去掉链接前的//
logo_link = logo.replace('//', '')

# 如果链接不包含 URL 方案，则添加 "https://"
if not logo_link.startswith("http"):
    logo_link = "https://" + logo_link

# 下载logo图片
with open('baidu_logo.jpg', 'wb') as f:
    f.write(requests.get(logo_link).content)
