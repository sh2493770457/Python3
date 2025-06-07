from selenium import webdriver as wd
import time
from lxml import etree  #
import urllib.parse
import csv
import pymysql
# from colorama import Fore

# # 设置浏览器选项
# options = wd.EdgeOptions()
# options.add_argument('--headless')  # 启用无头模式,试了发现没有数据写入数据库


def get_job_data(driver):
    data = driver.page_source  # 获取当前页面的HTML源代码
    html = etree.HTML(data)  # 解析HTML源代码

    # 使用XPath提取职位名称
    job_name = html.xpath("//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/a[@class='job-card-left']/div[@class='job-title clearfix']/span[@class='job-name']/text()")
    # 使用XPath提取薪资信息
    job_price = html.xpath("//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/a[@class='job-card-left']/div[@class='job-info clearfix']/span[@class='salary']/text()")
    # 使用XPath提取工作经验要求
    job_experience = html.xpath("//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/a[@class='job-card-left']/div[@class='job-info clearfix']/ul[@class='tag-list']/li[1]/text()")
    # 使用XPath提取学历要求
    job_education = html.xpath("//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/a[@class='job-card-left']/div[@class='job-info clearfix']/ul[@class='tag-list']/li[2]/text()")
    # 使用XPath提取工作地点
    job_location = html.xpath("//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/a[@class='job-card-left']/div[@class='job-title clearfix']/span[@class='job-area-wrapper']/span[@class='job-area']/text()")
    # 使用XPath提取公司名称
    job_company = html.xpath("//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/div[@class='job-card-right']/div[@class='company-info']/h3[@class='company-name']/a/text()")
    # 将提取的数据组合成一个列表，返回给调用者
    return list(zip(job_name, job_price, job_experience, job_education, job_location, job_company))

# 输入要爬取的工作
query = urllib.parse.quote(input('请输入要爬取的工作：'))  # URL编码

# 输入要爬取的页数
page = int(input('请输入要爬取的页数（网页限制最多10页）：'))
all_jobs = []  # 用于存储所有页面的招聘信息

# driver = wd.Chrome()  # 启动Chrome浏览器
driver=wd.Edge()    # 启动Edge浏览器,谷歌加载慢就换

# 成都 https://www.zhipin.com/web/geek/job?query={query}&city=101270100&page={i}
# 全国 https://www.zhipin.com/web/geek/job?query={query}&city=100010000&page={i}
for i in range(1, page + 1):  # 循环遍历每一页
    url = f'https://www.zhipin.com/web/geek/job?query={query}&city=101270100&page={i}'  # 构建当前页的URL
    driver.get(url)  # 加载当前页
    time.sleep(10)  # 等待页面完全加载

    # 提取当前页的招聘信息并追加到总列表中
    all_jobs.extend(get_job_data(driver))

# 将提取的招聘信息写入到文本文件中
with open('job_data.txt', 'w', encoding='utf-8') as f:
    for job in all_jobs:  # 遍历每条招聘信息
        f.write(str(job) + '\n')  # 将招聘信息写入文件

# 将提取的招聘信息写入到CSV文件中
with open('job_data.csv', 'w', encoding='utf-8', newline='') as f:
    writer = csv.writer(f) # 创建一个csv写入器
    writer.writerow(['职位名称', '薪资', '工作经验', '学历要求', '工作地点', '公司名称'])  # 写入表头
    for job in all_jobs:  # 遍历每条招聘信息
        writer.writerow(job)  # 将招聘信息写入文件

# 将提取的招聘信息写入到数据库中,没有数据库则创建数据库
# def create_data(query, all_jobs):
#     try:
#         # 连接到MySQL服务器上的默认数据库
#         conn = pymysql.connect(host='localhost', user='root', password='root', charset='utf8')
#         cursor = conn.cursor()
#
#         # 如果不存在，则创建名为'BOSS直聘'的数据库
#         cursor.execute('CREATE DATABASE IF NOT EXISTS BOSS直聘')
#         conn.commit()
#
#         # 选择'BOSS直聘'数据库
#         conn.select_db('BOSS直聘')
#
#         # 根据'query'变量创建表，如果表不存在
#         table_name = f'{query}'
#         cursor.execute(f'''CREATE TABLE IF NOT EXISTS {table_name} (
#                             job_name VARCHAR(255),
#                             job_price VARCHAR(255),
#                             job_experience VARCHAR(255),
#                             job_education VARCHAR(255),
#                             job_location VARCHAR(255),
#                             job_company VARCHAR(255))''')
#         conn.commit()
#
#         # 插入数据，假设'all_jobs'已定义并填充了数据
#         insert_sql = f'INSERT INTO {table_name} (job_name, job_price, job_experience, job_education, job_location, job_company) VALUES (%s, %s, %s, %s, %s, %s)'
#         for job in all_jobs:
#             cursor.execute(insert_sql, job)
#         conn.commit()
#         print("数据已成功写入数据库")
#
#     except pymysql.Error as e:
#         print("数据库操作出错：", e)
#         conn.rollback()  # 出错时回滚
#     finally:
#         cursor.close()
#         conn.close()
#
# # 调用函数将数据写入数据库
# create_data(query, all_jobs)



print("操作完成!")
driver.quit()  # 关闭浏览器
