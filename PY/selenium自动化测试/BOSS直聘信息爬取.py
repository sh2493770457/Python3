import sys
from PyQt5 import QtGui
from PyQt5.QtWidgets import QApplication, QVBoxLayout, QHBoxLayout, QLabel, QLineEdit, QPushButton, QTextEdit, QWidget, QSpinBox, QFileDialog, QMessageBox
from selenium import webdriver as wd
from selenium.webdriver.common.by import By
from selenium.webdriver.edge.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
from lxml import etree
import urllib.parse
import csv
import threading
from random import choice, uniform

# 用户代理池，用于模拟不同的浏览器访问
user_agents = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:98.0) Gecko/20100101 Firefox/98.0",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:98.0) Gecko/20100101 Firefox/98.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 Edg/98.0.1108.62",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 OPR/98.0.753.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 Vivaldi/5.1",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"
]

class JobScraperApp(QWidget):
    def __init__(self):
        super().__init__()

        # 初始化UI界面
        self.initUI()

    def initUI(self):
        self.setWindowTitle('BOSS直聘信息爬取')

        # 创建主布局
        layout = QVBoxLayout()

        # 查询输入布局
        query_layout = QHBoxLayout()
        query_label = QLabel('请输入要爬取的工作：')
        self.query_input = QLineEdit()
        query_layout.addWidget(query_label)
        query_layout.addWidget(self.query_input)

        # 页数输入布局
        page_layout = QHBoxLayout()
        page_label = QLabel('请输入要爬取的页数：')
        self.page_input = QSpinBox()
        self.page_input.setRange(1, 10)  # 设置页数范围为1到10
        page_layout.addWidget(page_label)
        page_layout.addWidget(self.page_input)

        # 爬取按钮
        self.scrape_button = QPushButton('开始爬取')
        self.scrape_button.clicked.connect(self.scrape_jobs)  # 绑定点击事件

        # 保存按钮
        self.save_button = QPushButton('保存数据')
        self.save_button.clicked.connect(self.save_data)  # 绑定点击事件
        self.save_button.setEnabled(False)  # 初始设置为不可用，等数据爬取完成后才可用

        # 输出显示区域
        self.output_display = QTextEdit()
        self.output_display.setReadOnly(True)  # 设置为只读

        # 将控件添加到布局中
        layout.addLayout(query_layout)
        layout.addLayout(page_layout)
        layout.addWidget(self.scrape_button)
        layout.addWidget(self.save_button)
        layout.addWidget(self.output_display)

        # 设置主布局
        self.setLayout(layout)
        self.all_jobs = []  # 用于存储所有页面的招聘信息

    def scrape_jobs(self):
        query = urllib.parse.quote(self.query_input.text())  # 对查询内容进行URL编码
        page = self.page_input.value()
        self.all_jobs = []

        # 创建线程列表
        threads = []
        for i in range(1, page + 1):
            thread = threading.Thread(target=self.scrape_page, args=(query, i))  # 创建线程
            time.sleep(3)  # 增加等待时间，模拟人类行为(！！！！这个很重要，否则会被BOSS直聘识别为爬虫)
            threads.append(thread)
            thread.start()  # 启动线程

        for thread in threads:
            thread.join()  # 等待所有线程完成

        # 显示结果
        self.output_display.append('\n爬取完成！所有数据如下：')
        for job in self.all_jobs:
            self.output_display.append(str(job))

        self.save_button.setEnabled(True)  # 爬取完成后启用保存按钮

    def scrape_page(self, query, page):
        options = Options()
        options.add_argument(f"user-agent={choice(user_agents)}")
        #options.add_argument("--headless") # 设置为无头模式，不显示浏览器窗口
        # 随机选择一个用户代理
        driver = wd.Edge(options=options)
        url = f'https://www.zhipin.com/web/geek/job?query={query}&city=100010000&page={page}'
        driver.get(url)
        time.sleep(uniform(10, 20))  # 随机等待时间，模拟人类行为

        jobs = self.get_job_data(driver)
        self.all_jobs.extend(jobs)  # 将获取到的工作信息添加到总列表中

        driver.quit()

    def get_job_data(self, driver):
        # 增加显示等待，确保页面加载完成
        try:
            WebDriverWait(driver, 20).until(EC.presence_of_element_located((By.XPATH, "//li[@class='job-card-wrapper'][*]")))
        except Exception as e:
            print(f"Error while waiting for page to load: {e}")

        data = driver.page_source  # 获取页面源代码
        html = etree.HTML(data)  # 解析HTML

        # 使用XPath提取所需的数据
        job_name = html.xpath(
            "//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/a[@class='job-card-left']/div[@class='job-title clearfix']/span[@class='job-name']/text()")
        job_price = html.xpath(
            "//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/a[@class='job-card-left']/div[@class='job-info clearfix']/span[@class='salary']/text()")
        job_experience = html.xpath(
            "//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/a[@class='job-card-left']/div[@class='job-info clearfix']/ul[@class='tag-list']/li[1]/text()")
        job_education = html.xpath(
            "//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/a[@class='job-card-left']/div[@class='job-info clearfix']/ul[@class='tag-list']/li[2]/text()")
        job_location = html.xpath(
            "//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/a[@class='job-card-left']/div[@class='job-title clearfix']/span[@class='job-area-wrapper']/span[@class='job-area']/text()")
        job_company = html.xpath(
            "//li[@class='job-card-wrapper'][*]/div[@class='job-card-body clearfix']/div[@class='job-card-right']/div[@class='company-info']/h3[@class='company-name']/a/text()")

        # 返回一个包含所有提取数据的列表
        return list(zip(job_name, job_price, job_experience, job_education, job_location, job_company))

    def save_data(self):
        options = QFileDialog.Options()
        file_path, _ = QFileDialog.getSaveFileName(self, "保存文件", "", "CSV Files (*.csv);;Text Files (*.txt)", options=options)
        if file_path:
            try:
                if file_path.endswith('.csv'):
                    with open(file_path, 'w', newline='', encoding='utf-8') as csvfile:
                        writer = csv.writer(csvfile)
                        writer.writerow(['职位名称', '薪资', '工作经验', '学历要求', '工作地点', '公司名称'])  # 写入表头
                        writer.writerows(self.all_jobs)  # 写入数据
                else:
                    with open(file_path, 'w', encoding='utf-8') as txtfile:
                        for job in self.all_jobs:
                            txtfile.write(str(job) + '\n')  # 写入数据
                QMessageBox.information(self, "保存成功", "数据已成功保存到文件！")
            except Exception as e:
                QMessageBox.warning(self, "保存失败", f"保存数据时出现错误: {e}")

if __name__ == '__main__':
    app = QApplication(sys.argv)
    app.setWindowIcon(QtGui.QIcon('bosszhipin123.png'))  # 设置应用程序图标
    scraper_app = JobScraperApp()
    scraper_app.show()  # 显示主窗口
    sys.exit(app.exec_())  # 运行应用程序事件循环
