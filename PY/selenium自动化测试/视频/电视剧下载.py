import os
import re
import requests
from tqdm import tqdm
from lxml import etree
from concurrent.futures import ThreadPoolExecutor as TPE


# 下载视频片段
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

# 下载 m3u8 视频片段
def download_m3u8_videos(m3u8_url, headers, episode_number):
    response = requests.get(m3u8_url, headers=headers)
    if response.status_code != 200:
        print(f"无法获取 m3u8 文件: {m3u8_url}")
        return

    # 查找 m3u8 文件中的所有视频片段 URL
    base_url_match = re.match(r'(.*/)(index.*)', m3u8_url)
    base_url = base_url_match.group(1) if base_url_match else ''
    matches = re.findall(r'\b[A-Za-z0-9]+\.bin\?sign=[a-f0-9]{32}&timestamp=\d+\b', response.text)

    if not matches:
        print(f"未找到视频片段: {m3u8_url}")
        return

    episode_folder = f"第{episode_number}集"
    os.makedirs(episode_folder, exist_ok=True)

    # 使用线程池并发下载视频片段
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

# 获取 m3u8 链接并下载单集
def download_episode(episode_url, headers, episode_number):
    response = requests.get(episode_url, headers=headers)
    html = response.text

    # 用正则获取 m3u8 链接
    m3u8_matches = re.findall(r'src: "(.*?)"', html)
    if m3u8_matches:
        m3u8_url = m3u8_matches[0]
        download_m3u8_videos(m3u8_url, headers, episode_number)
    else:
        print(f"未找到 m3u8 链接: {episode_url}")

# 获取所有集数的链接并下载指定的集数
def download_m3u8(url, headers, episode_numbers):
    response = requests.get(url, headers=headers)
    html = response.text
    tree = etree.HTML(html)

    # 获取所有集的 URL
    urls = tree.xpath('/html/body/div[1]/div[3]/div[2]/div[4]/div[2]/div[1]/a/@href')
    full_urls = ['https://www.keke1.app' + url for url in urls]

    # 过滤用户需要的集数
    selected_urls = [full_urls[i - 1] for i in episode_numbers if i - 1 < len(full_urls)]

    # 多线程下载多个集数的视频
    with TPE(max_workers=len(episode_numbers)) as executor:
        futures = []
        for episode_number, episode_url in zip(episode_numbers, selected_urls):
            future = executor.submit(download_episode, episode_url, headers, episode_number)
            futures.append(future)

        for future in futures:
            future.result()

# 用户输入需要下载的集数
episode_numbers_input = input("请输入你想要下载的集数(用逗号分隔，例如: 1,3): ")
# 支持中英文逗号，并去除空格
episode_numbers = [int(num) for num in re.split(r'[，,]', episode_numbers_input.replace(' ', ''))]

# url = 'https://www.keke1.app/detail/236396.html'
url=input("请输入电视剧的网址：")
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'
}

download_m3u8(url, headers, episode_numbers)
