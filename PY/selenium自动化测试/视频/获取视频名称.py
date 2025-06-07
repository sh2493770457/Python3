import os
import re
import requests
from tqdm import tqdm  # 用于显示进度条
from lxml import etree
from concurrent.futures import ThreadPoolExecutor as TPE  # 多线程执行器，用于并行下载
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QLineEdit, QPushButton, QVBoxLayout, QMessageBox, QListWidget, QListWidgetItem
from PyQt5.QtCore import Qt, QThread, pyqtSignal

class DownloadThread(QThread):
    progress_update = pyqtSignal(int, int)  # 自定义信号，用于更新下载进度（当前进度，总进度）
    message_box = pyqtSignal(str)  # 自定义信号，用于显示消息框提示信息

    def __init__(self, url, headers, selected_episodes):
        super().__init__()
        self.url = url  # 下载链接
        self.headers = headers  # HTTP 请求头
        self.selected_episodes = selected_episodes  # 要下载的集数列表

    def run(self):
        try:
            video_name, _ = get_episode_numbers(self.url, self.headers)  # 获取影视名称
            download_m3u8(self.url, self.headers, self.selected_episodes, video_name, self)  # 调用下载函数下载视频
            self.message_box.emit("下载完成！")  # 发射下载完成的信号，显示消息框提示
        except Exception as e:
            self.message_box.emit(f"下载过程中发生错误：{str(e)}")  # 发射下载错误的信号，显示消息框提示错误信息

class EpisodeListThread(QThread):
    episode_list_ready = pyqtSignal(list)  # 自定义信号，用于传递获取到的视频集列表
    message_box = pyqtSignal(str)  # 自定义信号，用于显示消息框提示信息

    def __init__(self, url, headers):
        super().__init__()
        self.url = url  # 电视剧网址
        self.headers = headers  # HTTP 请求头

    def run(self):
        try:
            episode_numbers = get_episode_numbers(self.url, self.headers)  # 获取视频集列表
            self.episode_list_ready.emit(episode_numbers)  # 发射信号，传递视频集列表
        except Exception as e:
            self.message_box.emit(f"获取视频集列表时出错：{str(e)}")  # 发射错误信号，显示消息框提示错误信息

class MainWindow(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        self.setWindowTitle('视频下载器')  # 窗口标题
        self.setGeometry(200, 200, 600, 400)  # 窗口初始位置和大小

        self.url_label = QLabel('电视剧网址:')  # 标签，显示文字“电视剧网址:”
        self.url_input = QLineEdit()  # 单行文本输入框，用于输入电视剧网址
        self.get_episodes_button = QPushButton('获取可下载集数')  # 按钮，用于获取可下载集数的操作
        self.episode_list_label = QLabel('可下载集数:')  # 标签，显示文字“可下载集数:”
        self.episode_list_widget = QListWidget()  # 列表控件，用于显示可下载的视频集列表
        self.episode_input_label = QLabel('选择下载集数:')  # 标签，显示文字“选择下载集数:”
        self.download_button = QPushButton('开始下载')  # 按钮，用于开始下载选定的视频集
        self.website_link = QLabel('<a href="https://www.keke1.app">点击进入网页</a>')  # 标签，显示超链接文字
        self.website_link.setOpenExternalLinks(True)  # 设置可以在默认浏览器中打开超链接

        vbox = QVBoxLayout()  # 垂直布局管理器
        vbox.addWidget(self.url_label)  # 将电视剧网址标签添加到布局中
        vbox.addWidget(self.url_input)  # 将电视剧网址输入框添加到布局中
        vbox.addWidget(self.get_episodes_button)  # 将获取可下载集数按钮添加到布局中
        vbox.addWidget(self.episode_list_label)  # 将可下载集数标签添加到布局中
        vbox.addWidget(self.episode_list_widget)  # 将可下载集数列表控件添加到布局中
        vbox.addWidget(self.episode_input_label)  # 将选择下载集数标签添加到布局中
        vbox.addWidget(self.download_button)  # 将开始下载按钮添加到布局中
        vbox.addWidget(self.website_link)  # 将超链接标签添加到布局中

        self.setLayout(vbox)  # 设置窗口布局为垂直布局

        self.get_episodes_button.clicked.connect(self.get_episode_list)  # 连接获取可下载集数按钮的点击事件到方法
        self.download_button.clicked.connect(self.start_download)  # 连接开始下载按钮的点击事件到方法

        self.show()  # 显示窗口

    def get_episode_list(self):
        url = self.url_input.text().strip()  # 获取输入的电视剧网址

        if not url:
            QMessageBox.critical(self, '错误', '请输入电视剧网址！')  # 如果没有输入网址，显示错误消息框并返回
            return

        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'
        }  # 设置请求头，模拟浏览器请求

        self.thread = EpisodeListThread(url, headers)  # 创建获取视频集列表的线程
        self.thread.episode_list_ready.connect(self.populate_episode_list)  # 连接信号到更新界面显示视频集列表的方法
        self.thread.message_box.connect(self.show_message)  # 连接信号到显示消息框的方法
        self.thread.start()  # 启动线程

    def populate_episode_list(self, episode_numbers):
        self.episode_list_widget.clear()  # 清空视频集列表控件

        for i, episode_number in enumerate(episode_numbers, start=1):
            item = QListWidgetItem(f'第{i}集')  # 创建列表项，显示第几集
            item.setData(Qt.UserRole, episode_number)  # 设置列表项的数据为视频集数
            item.setFlags(item.flags() | Qt.ItemIsUserCheckable)  # 设置列表项可用户选择
            item.setCheckState(Qt.Unchecked)  # 设置列表项默认为未选中状态
            self.episode_list_widget.addItem(item)  # 将列表项添加到列表控件中

    def start_download(self):
        url = self.url_input.text().strip()  # 获取输入的电视剧网址

        selected_episodes = []
        for index in range(self.episode_list_widget.count()):
            item = self.episode_list_widget.item(index)
            if item.checkState() == Qt.Checked:
                selected_episodes.append(item.data(Qt.UserRole))  # 将选中的视频集数添加到列表中

        if not url or not selected_episodes:
            QMessageBox.critical(self, '错误', '请填写完整信息或选择至少一个集数！')  # 如果没有输入网址或选择集数，显示错误消息框并返回
            return

        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'
        }  # 设置请求头，模拟浏览器请求

        def download_m3u8_videos(m3u8_url, headers, episode_numbers, video_name, thread):
            response = requests.get(m3u8_url, headers=headers)  # 发送 HTTP GET 请求获取 m3u8 文件内容
            if response.status_code != 200:
                thread.message_box.emit(f"无法获取 m3u8 文件: {m3u8_url}")  # 发射消息框提示信息
                return

            base_url_match = re.match(r'(.*/)(index.*)', m3u8_url)  # 使用正则表达式匹配 m3u8 文件的基础 URL
            base_url = base_url_match.group(1) if base_url_match else ''  # 获取基础 URL
            matches = re.findall(r'\b[A-Za-z0-9]+\.bin\?sign=[a-f0-9]{32}&timestamp=\d+\b',
                                 response.text)  # 使用正则表达式匹配所有视频片段的 URL

            if not matches:
                thread.message_box.emit(f"未找到视频片段: {m3u8_url}")  # 发射消息框提示信息
                return

            episode_folder = f"第{episode_numbers[0]}集"  # 设置视频集的文件夹名称
            os.makedirs(episode_folder, exist_ok=True)  # 创建视频集的文件夹，如果已存在则不创建

            with TPE(max_workers=32) as executor:  # 使用线程池执行器，并发下载视频片段
                futures = []  # 用于存储每个下载任务的 future 对象
                progress = tqdm(total=len(matches), desc=f"下载第{episode_numbers[0]}集进度：",
                                unit="file")  # 使用 tqdm 显示下载进度条
                for i, match in enumerate(matches):
                    segment_url = base_url + match  # 拼接完整的视频片段 URL
                    segment_path = os.path.join(episode_folder,
                                                f"{video_name}_第{episode_numbers[0]}集_{i}.mp4")  # 设置视频片段的保存路径
                    future = executor.submit(download_video_segment, segment_url, headers, segment_path)  # 提交下载任务到线程池
                    futures.append(future)  # 将 future 对象添加到列表中，用于获取下载结果

                for future in futures:
                    future.result()  # 等待并获取每个下载任务的结果
                    progress.update(1)  # 更新进度条

                progress.close()  # 关闭进度条

        def download_episode(episode_url, headers, episode_number, video_name):
            response = requests.get(episode_url, headers=headers)  # 发送 HTTP GET 请求获取剧集页面内容
            html = response.text  # 获取页面文本内容

            m3u8_matches = re.findall(r'src: "(.*?)"', html)  # 使用正则表达式匹配 m3u8 文件的 URL
            if m3u8_matches:
                m3u8_url = m3u8_matches[0]  # 获取匹配到的第一个 m3u8 文件的 URL
                download_m3u8_videos(m3u8_url, headers, [episode_number], video_name)  # 调用下载 m3u8 视频的函数
            else:
                print(f"未找到 m3u8 链接: {episode_url}")  # 打印未找到 m3u8 链接的错误信息

        def download_m3u8(url, headers, episode_numbers):
            response = requests.get(url, headers=headers)  # 发送 HTTP GET 请求获取页面内容
            html = response.text  # 获取页面文本内容
            tree = etree.HTML(html)  # 构建 XPath 解析树

            # 提取影视名称
            video_name = tree.xpath('/html/body/div[1]/div[3]/div[2]/div[2]/div[2]/div[1]/strong/text()')[0]

            urls = tree.xpath('/html/body/div[1]/div[3]/div[2]/div[4]/div[2]/div[1]/a/@href')  # 使用 XPath 获取所有剧集链接
            full_urls = ['https://www.keke1.app' + url for url in urls]  # 将相对链接转换为完整链接

            selected_urls = [full_urls[i - 1] for i in episode_numbers if i - 1 < len(full_urls)]  # 根据选择的集数获取对应的链接列表

            with TPE(max_workers=len(episode_numbers)) as executor:  # 使用线程池执行器，并发下载剧集
                futures = []  # 用于存储每个下载任务的 future 对象
                for episode_number, episode_url in zip(episode_numbers, selected_urls):
                    future = executor.submit(download_episode, episode_url, headers, episode_number,
                                             video_name)  # 提交下载任务到线程池
                    futures.append(future)  # 将 future 对象添加到列表中，用于获取下载结果

                for future in futures:
                    future.result()  # 等待并获取每个下载任务的结果
