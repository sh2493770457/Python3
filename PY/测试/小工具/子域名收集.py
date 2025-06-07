import time
from selenium import webdriver as wd
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from bs4 import BeautifulSoup
import re

domain = input("请输入想收集域名：")
num = int(input("请输入想收集的页数："))

# TODO:设置驱动
driver = wd.Chrome(service=Service(ChromeDriverManager().install()))

# TODO:使用集合来存储去重后的子域名
collected_subdomains = set()

for page in range(1, num + 1):
    print(f"正在爬取第 {page} 页...")

    url = f'https://www.google.com/search?q=site:{domain}&sca_esv=8914734fd05b3950&sxsrf=ADLYWIJJxSEB3PbwCq7XWFtmnght1PbsqA:1728631039643&ei=_9AIZ531JsuXvr0P98mZyQc&start={(page - 1) * 10}&sa=N&sstk=AagrsujJ4Dmib6JBcxZkd_We9NSVXt1JA6JYtCuSocsJcs3LORX5Uq7DjB0CECsePc9k9en5o3v9tvQVVD5BOYzwZjC_TaWYjzRvcXC3U0t9MzPZffrR71TdflMmVKeux9T6qZBqJ8wKVQvA-6JZW62bgUcsiAddpBVwdzFu5JPyDFByvLgcmTimdVGNDXbAg3_9C1TR6ObD47tQQc-cU_2rqygXO7xEbv5rN5gEgcPXRqCANMS1jkPq0HBgAUI&ved=2ahUKEwjd8MK45IWJAxXLi68BHfdkJnk40gEQ8NMDegQIBxAX&biw=1392&bih=774&dpr=2'
    driver.get(url)
    time.sleep(2)

    # TODO:检查是否出现人机验证
    if "人机验证" in driver.page_source or "captcha" in driver.page_source:
        print("检测到人机验证，请完成验证后按回车继续...")
        input()  # TODO:暂停程序，等待验证后继续

    # TODO:检查是否有“找不到和您查询的内容”提示
    if "找不到和您查询的" in driver.page_source:
        print("找不到更多内容，挖掘结束。")
        break  # TODO:结束循环，停止挖掘

    data = driver.page_source
    soup = BeautifulSoup(data, 'html.parser')

    # TODO:提取所有<a href=''>标签
    a_tags = soup.find_all('a', href=True)

    # TODO:使用正则表达式提取以 'https://' 开头，并且包含目标域名的子域名链接
    for a_tag in a_tags:
        href = a_tag['href']
        # TODO:只提取子域名，不包括 Google 的 "/url?q=" 跳转链接
        clean_link = re.search(r'(https?://[a-zA-Z0-9.-]+\.' + re.escape(domain) + r'[^\s]*)', href)
        if clean_link:
            real_link = clean_link.group(1)
            # TODO:提取子域名部分去除路径，只保留协议+子域名
            subdomain = re.match(r'https?://([a-zA-Z0-9.-]+\.' + re.escape(domain) + r')', real_link).group(1)
            # TODO:比对子域名去重
            if subdomain not in collected_subdomains:
                collected_subdomains.add(subdomain)
                print(f"提取到的子域名链接: {real_link.split('/')[2]}")  # TODO:输出去掉路径的域名
                with open('domain.txt', 'a') as f:
                    f.write(real_link.split('/')[2] + '\n')

