import os
import concurrent.futures
import time
import base64
import re
import urllib.parse
import requests
import random
from requests import RequestException

# url = 'https://www.ddkk.tv/play/269117-1-1.html'
print("参考这个:https://www.ddkk.tv/play/269117-1-1.html")
url=input("请输入视频链接:")

# 构造随机ua请求头
headers_list = [
    {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"},
    {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:98.0) Gecko/20100101 Firefox/98.0"},
    {"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"},
    {"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15"},
    {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:98.0) Gecko/20100101 Firefox/98.0"},
    {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 Edg/98.0.1108.62"},
    {"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15"},
    {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 OPR/98.0.753.0"},
    {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 Vivaldi/5.1"},
    {"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"}
]

# 最大重试次数
max_retries = 30
final_long_url = None

for attempt in range(max_retries):
    try:
        time.sleep(3)
        headers = random.choice(headers_list)
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        js_code = response.text
        # 获取解码前的url
        url_pattern = r'"url":"([^"]+)"'
        matches = re.findall(url_pattern, js_code)

        if len(matches) >= 2:
            long_url = matches[1]
            print("提取的长URL:", long_url)
            # base64解码后再url解码
            decoded_bytes = base64.b64decode(long_url)
            decoded_str = decoded_bytes.decode('utf-8')
            final_long_url = urllib.parse.unquote(decoded_str)
            print("解码后的长URL:", final_long_url)
            break
        else:
            print("未找到足够的URL")
    except RequestException as e:
        # 如果出现这个问题建议换一个网络,网页做了反爬虫处理,一般重试几次就能解析出来
        print(f"请求失败: {e}, 服务器存在问题, 正在重试 {attempt + 1}/{max_retries}")
        time.sleep(10)

if final_long_url:
    # 请求解码后的长URL获取index.m3u8文件里的内容
    response = requests.get(final_long_url)
    # print(response.text)

    # 提取形如2000k/hls/mixed.m3u8的字符串
    pattern = r'(\d{4}k/hls/mixed\.m3u8)'
    mixed = re.findall(pattern, response.text)
    # print(mixed)

    if mixed:
        # 将解码后的长URL和提取到的字符串index.m3u8替换为mixed
        final_url = final_long_url.replace('index.m3u8', mixed[0])
        print("解析到的m3u8的url:", final_url)

        # 创建请求头
        headers = {
            'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 Edg/128.0.0.0',
        }

        # 获取M3U8文件内容
        response = requests.get(final_url, headers=headers)
        # print(response.text)

        # 正则匹配出所有的.ts文件
        pattern = re.compile(r'(.*?\.ts)')
        ts_files = pattern.findall(response.text)
        # print(ts_files)

        # https://v.cdnlz3.com/20240831/26236_386a553b/2000k/hls/8fc7b5aa757000298.ts
        for ts_file in ts_files:
            ts_url = final_url.replace('mixed.m3u8', ts_file)
            print("视频流url:", ts_url)

        # 如果目录不存在，则创建'视频'目录
        if not os.path.exists('视频'):
            os.mkdir('视频')

        # 下载函数，包含重试逻辑
        def download_ts_file(ts_file, ts_url, retry=3):
            file_path = os.path.join('视频', ts_file)

            for attempt in range(retry):
                try:
                    # 下载TS文件
                    print(f"正在下载: {ts_file}, 尝试次数: {attempt + 1}")
                    response = requests.get(ts_url, headers=headers, timeout=10)
                    response.raise_for_status()  # 如果状态码不是200则抛出异常

                    # 写入文件
                    with open(file_path, 'wb') as f:
                        f.write(response.content)
                    print(f"下载完成: {ts_file}")
                    break  # 如果成功则退出循环
                except requests.RequestException as e:
                    print(f"下载出错 {ts_file}: {e}")
                    if attempt + 1 == retry:
                        print(f"{ts_file} 下载失败，重试 {retry} 次后仍然失败。")
                    else:
                        time.sleep(2)  # 等待2秒后重试

        # 使用线程池下载多个文件
        with concurrent.futures.ThreadPoolExecutor(max_workers=16) as executor:
            futures = []
            for ts_file in ts_files:
                ts_url = final_url.replace('mixed.m3u8', ts_file)
                futures.append(executor.submit(download_ts_file, ts_file, ts_url))
            concurrent.futures.wait(futures)

        print("所有文件下载完成。")
    else:
        print("未找到m3u8文件")
else:
    print("未能获取有效的长URL")
