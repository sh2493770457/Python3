import os
import re
import requests
from tqdm import tqdm
from lxml import etree
from concurrent.futures import ThreadPoolExecutor as TPE
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QLineEdit, QPushButton, QVBoxLayout, QMessageBox, QListWidget, QListWidgetItem
from PyQt5.QtCore import Qt, QThread, pyqtSignal

class DownloadThread(QThread):
    progress_update = pyqtSignal(int, int)
    message_box = pyqtSignal(str)

    def __init__(self, url, headers, selected_episodes):
        super().__init__()
        self.url = url
        self.headers = headers
        self.selected_episodes = selected_episodes

    def run(self):
        try:
            download_m3u8(self.url, self.headers, self.selected_episodes)
            self.message_box.emit("下载完成！")
        except Exception as e:
            self.message_box.emit(f"下载过程中发生错误：{str(e)}")

class EpisodeListThread(QThread):
    episode_list_ready = pyqtSignal(list)
    message_box = pyqtSignal(str)

    def __init__(self, url, headers):
        super().__init__()
        self.url = url
        self.headers = headers

    def run(self):
        try:
            episode_numbers = get_episode_numbers(self.url, self.headers)
            self.episode_list_ready.emit(episode_numbers)
        except Exception as e:
            self.message_box.emit(f"获取视频集列表时出错：{str(e)}")

class MainWindow(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        self.setWindowTitle('视频下载器')
        self.setGeometry(200, 200, 600, 400)

        self.url_label = QLabel('电视剧网址:')
        self.url_input = QLineEdit()
        self.get_episodes_button = QPushButton('获取可下载集数')
        self.episode_list_label = QLabel('可下载集数:')
        self.episode_list_widget = QListWidget()
        self.episode_input_label = QLabel('选择下载集数:')
        self.download_button = QPushButton('开始下载')

        vbox = QVBoxLayout()
        vbox.addWidget(self.url_label)
        vbox.addWidget(self.url_input)
        vbox.addWidget(self.get_episodes_button)
        vbox.addWidget(self.episode_list_label)
        vbox.addWidget(self.episode_list_widget)
        vbox.addWidget(self.episode_input_label)
        vbox.addWidget(self.download_button)

        self.setLayout(vbox)

        self.get_episodes_button.clicked.connect(self.get_episode_list)
        self.download_button.clicked.connect(self.start_download)

        self.show()

    def get_episode_list(self):
        url = self.url_input.text().strip()

        if not url:
            QMessageBox.critical(self, '错误', '请输入电视剧网址！')
            return

        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'
        }

        self.thread = EpisodeListThread(url, headers)
        self.thread.episode_list_ready.connect(self.populate_episode_list)
        self.thread.message_box.connect(self.show_message)
        self.thread.start()

    def populate_episode_list(self, episode_numbers):
        self.episode_list_widget.clear()
        for i, episode_number in enumerate(episode_numbers, start=1):
            item = QListWidgetItem(f'第{i}集')
            item.setData(Qt.UserRole, episode_number)
            item.setFlags(item.flags() | Qt.ItemIsUserCheckable)
            item.setCheckState(Qt.Unchecked)
            self.episode_list_widget.addItem(item)

    def start_download(self):
        url = self.url_input.text().strip()

        selected_episodes = []
        for index in range(self.episode_list_widget.count()):
            item = self.episode_list_widget.item(index)
            if item.checkState() == Qt.Checked:
                selected_episodes.append(item.data(Qt.UserRole))

        if not url or not selected_episodes:
            QMessageBox.critical(self, '错误', '请填写完整信息或选择至少一个集数！')
            return

        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'
        }

        self.thread = DownloadThread(url, headers, selected_episodes)
        self.thread.progress_update.connect(self.update_progress)
        self.thread.message_box.connect(self.show_message)
        self.thread.start()

        self.download_button.setEnabled(False)

    def update_progress(self, current, total):
        pass  # 可以在这里添加进度条更新的逻辑

    def show_message(self, message):
        QMessageBox.information(self, '提示', message)
        self.download_button.setEnabled(True)

def get_episode_numbers(url, headers):
    response = requests.get(url, headers=headers)
    html = response.text
    tree = etree.HTML(html)

    urls = tree.xpath('/html/body/div[1]/div[3]/div[2]/div[4]/div[2]/div[1]/a/@href')
    episode_numbers = list(range(1, len(urls) + 1))

    return episode_numbers

def download_video_segment(segment_url, headers, segment_path, max_retries=30):
    for attempt in range(max_retries):
        try:
            res = requests.get(segment_url, headers=headers, timeout=120)
            if res.status_code == 200:
                with open(segment_path, "wb") as f:
                    f.write(res.content)
                return True
            else:
                print(f"\n下载失败，HTTP 状态码: {res.status_code}，重试次数：{attempt + 1}")
        except requests.exceptions.RequestException as e:
            print(f"\n下载失败正在重新下载，重试次数：{attempt + 1}")
    return False

def download_m3u8_videos(m3u8_url, headers, episode_number):
    response = requests.get(m3u8_url, headers=headers)
    if response.status_code != 200:
        print(f"无法获取 m3u8 文件: {m3u8_url}")
        return

    base_url_match = re.match(r'(.*/)(index.*)', m3u8_url)
    base_url = base_url_match.group(1) if base_url_match else ''
    matches = re.findall(r'\b[A-Za-z0-9]+\.bin\?sign=[a-f0-9]{32}&timestamp=\d+\b', response.text)

    if not matches:
        print(f"未找到视频片段: {m3u8_url}")
        return

    episode_folder = f"第{episode_number}集"
    os.makedirs(episode_folder, exist_ok=True)

    with TPE(max_workers=16) as executor:
        futures = []
        progress = tqdm(total=len(matches), desc=f"下载第{episode_number}集进度：", unit="file")
        for i, match in enumerate(matches):
            segment_url = base_url + match
            segment_path = os.path.join(episode_folder, f"video_{i}.mp4")
            future = executor.submit(download_video_segment, segment_url, headers, segment_path)
            futures.append(future)

        for future in futures:
            future.result()
            progress.update(1)

        progress.close()

def download_episode(episode_url, headers, episode_number):
    response = requests.get(episode_url, headers=headers)
    html = response.text

    m3u8_matches = re.findall(r'src: "(.*?)"', html)
    if m3u8_matches:
        m3u8_url = m3u8_matches[0]
        download_m3u8_videos(m3u8_url, headers, episode_number)
    else:
        print(f"未找到 m3u8 链接: {episode_url}")

def download_m3u8(url, headers, episode_numbers):
    response = requests.get(url, headers=headers)
    html = response.text
    tree = etree.HTML(html)

    urls = tree.xpath('/html/body/div[1]/div[3]/div[2]/div[4]/div[2]/div[1]/a/@href')
    full_urls = ['https://www.keke1.app' + url for url in urls]

    selected_urls = [full_urls[i - 1] for i in episode_numbers if i - 1 < len(full_urls)]

    with TPE(max_workers=len(episode_numbers)) as executor:
        futures = []
        for episode_number, episode_url in zip(episode_numbers, selected_urls):
            future = executor.submit(download_episode, episode_url, headers, episode_number)
            futures.append(future)

        for future in futures:
            future.result()

if __name__ == '__main__':
    app = QApplication([])
    window = MainWindow()
    app.exec_()
