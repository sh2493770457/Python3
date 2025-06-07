"""自定爬虫包，包含requests、selenium和
下载方法，并封装常用方法
"""

import requests
from selenium import webdriver
import time
from lxml import etree


def web_requests(base_url, headers=None):
    """放入url，返回网页内容，可根据需要使用(封装好的requests方法)
       web_requests('https://www.example.com').text
       web_requests('https://www.example.com').content
    """
    try:
        if headers is None:
            headers = {}  # 如果没有传headers，默认使用空字典
        response = requests.get(base_url, headers=headers)

        if response.status_code == 200:
            response.encoding = response.apparent_encoding
            return response
        else:
            print("请求失败，状态码：", response.status_code)
            print("请添加headers参数，尝试再次请求！")
            return None
    except requests.RequestException as e:
        print("请求过程中出现错误：", e)
        return None


def web_selenium(base_url, xpaths=None, headless=True):
    """放入url和xpath，返回xpath匹配的内容，可根据需要使用(封装好的Selenium方法)
       web_selenium('https://www.example.com', '//h1/text()')
       web_selenium('https://www.example.com', '//a/@href')
       web_selenium('https://www.example.com', headless=False)————>默认不启动浏览器，改为false可启用
    """
    try:
        options = webdriver.EdgeOptions()
        if headless:
            options.add_argument('--headless')
        driver = webdriver.Edge(options=options)
        driver.get(base_url)
        time.sleep(0.1)  # 等待页面加载

        # 定义列表放xpath（多条xpath的情况）
        results = []
        if xpaths:
            for xpath in xpaths:
                data = driver.page_source
                html = etree.HTML(data)
                result = html.xpath(xpath)
                results.extend(result)  # 使用 extend 方法将单个XPath查询结果扩展到 results 中
        else:
            results = "URL成功打开，没有执行XPath"

        driver.quit()  # 确保浏览器正确关闭
        return results
    except Exception as e:
        print("使用Selenium过程中出现错误：", e)
        return None


def down_file(base_url, form):
    """放入url和文件格式，下载文件，可根据需要使用(封装好的下载方法)
       down_file('https://www.example.com/file.txt', 'file.txt')
       down_file('https://www.example.com/file.jpg', 'file.jpg')
       down_file('https://www.example.com/file.pdf', 'file.mp3')
        """
    try:
        response = requests.get(base_url)
        if response.status_code == 200:
            with open(form, 'wb') as f:
                f.write(response.content)
            print(f"文件已成功下载并保存：{form}")
        else:
            print("请求失败，状态码：", response.status_code)
    except requests.RequestException as e:
        print("下载过程中出现错误：", e)


if __name__ == "__main__":
    url = 'https://movie.douban.com/top250?start=0'
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'
    }
    res = web_requests(url, headers)

    ids = '//*[@id="content"]/div/div[1]/ol/li/div/div[1]/em/text()'
    urls = '//*[@id="content"]/div/div[1]/ol/li/div/div[1]/a/@href'
    names = '//*[@id="content"]/div/div[1]/ol/li/div/div[2]/div[1]/a/span[1]/text()'
    directors_raw = "//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='info']/div[@class='bd']/p[1]/text()"
    scores = '//*[@id="content"]/div/div[1]/ol/li/div/div[2]/div[2]/div[1]/span[2]/text()'
    descriptions = "//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='info']/div[@class='bd']/p[@class='quote']/span[@class='inq']/text()"
    images = "//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='pic']/a/img/@src"
    countries_raw = "//*[@id='content']/div/div[1]/ol/li/div/div[2]/div[2]/p[1]/text()[2]"
    movie_years_raw = "//div[@class='item']/div[@class='info']/div[@class='bd']/p[1]/text()[2]"

    xpaths = [ids, urls, names, directors_raw, scores, descriptions, images, countries_raw, movie_years_raw]

    for xpath in xpaths:
        print(web_selenium(url, xpaths=[xpath], headless=False))