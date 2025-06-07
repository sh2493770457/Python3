import asyncio

import aiohttp
import requests
import re
import os
import time
from urllib.parse import urljoin
import urllib.parse
import aiofiles

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36'
}


def get_movie_url(movie_url):
    if not movie_url.endswith('/1-example_1.html'):
        movie_url = movie_url.replace('.html', '/1-example_1.html')
        # print(movie_url)
    return movie_url


def get_first_m3u8_url(movie_url):
    """
    从指定的电影URL中提取第一个m3u8链接和电影名称。

    参数:
    movie_url (str): 电影信息页面的URL。

    返回:
    tuple: 包含第一个m3u8链接和电影名称的元组。
    """

    # 发起请求获取电影页面内容
    resp = requests.get(movie_url, headers=headers)

    # 正则表达式匹配m3u8链接
    pattern = re.compile(r'url":"(.*?)"')
    # 正则表达式匹配电影名称
    pattern_name = re.compile(r'class="hl-infos-title" href="(.*?)" title="(.*?)">')

    # 搜索并获取m3u8链接
    result = pattern.search(resp.text)
    # 搜索并获取电影名称
    result_name = pattern_name.search(resp.text)

    # 提取电影名称
    movie_name = result_name.group(2)
    # print(movie_name)

    # 解码获取的m3u8链接
    encoded_url = result.group(1)
    decoded_url = urllib.parse.unquote(encoded_url)
    # print(decoded_url

    # 从解码后的URL中提取第一个m3u8链接
    first_m3u8_url = decoded_url.split('url=')[-1]

    # print(first_m3u8_url)

    return first_m3u8_url, movie_name

    # print(first_m3u8_url)


def deal_first_m3u8(first_m3u8_url, movie_name):
    """
    处理第一个M3U8链接，下载并解析以获取第二个M3U8链接。

    :param first_m3u8_url: 第一个M3U8链接的URL。
    :param movie_name: 用于存储相关文件的文件夹名称。
    """
    if not os.path.exists(f'{movie_name}/2.m3u8'):
        # 检查并创建存储文件的目录
        if not os.path.exists(movie_name):
            os.makedirs(movie_name)
        if not os.path.exists(f'{movie_name}/1.m3u8'):
            # 定义第一个M3U8文件的存储路径
            file_name = '1.m3u8'
            file_path = f'{movie_name}/{file_name}'

            # 从第一个M3U8链接下载内容并保存到文件
            resp = requests.get(first_m3u8_url, headers=headers)
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(resp.text)

            # 初始化第二个M3U8链接的URL
            second_m3u8_url = ''
            # 读取第一个M3U8文件，查找并提取第二个M3U8链接
            with open(file_path, 'r', encoding='utf-8') as f:
                for line in f:
                    if line.startswith('#') or line == '\n':
                        # 跳过注释行和空行
                        continue
                    second_m3u8 = line.strip()
        # 组合得到第二个M3U8链接的完整URL
        second_m3u8_url = urljoin(first_m3u8_url, second_m3u8)

        # 下载第二个M3U8文件并保存
        file_name = '2.m3u8'
        file_path = f'{movie_name}/{file_name}'
        resp = requests.get(second_m3u8_url, headers=headers)
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(resp.text)
        # 注释掉了返回第二个M3U8链接的代码行


def deal_second_m3u8(movie_name):
    """
    处理第二个m3u8文件，从中提取出电影片段的URL列表。

    参数:
    - movie_name: 电影名称，用于构造文件路径。

    返回值:
    - lst_movies: 包含电影片段URL的列表。
    """
    # 构造文件路径
    file_path = f'{movie_name}/2.m3u8'

    # 初始化电影片段URL列表
    lst_movies = []
    with open(file_path, 'r', encoding='utf-8') as f:
        # 遍历文件每一行，过滤注释和空行
        for line in f:
            if line.startswith('#') or line == '\n':
                continue
            # 提取并加入电影片段URL列表
            lst_movies.append(line.strip())
    return lst_movies


def merge_movie(lst_movies, movie_name):
    """
    合并电影文件。

    参数:
    lst_movies: 包含电影文件名的列表。
    movie_name: 合并后电影的名称。

    说明:
    此函数将给定列表中的电影文件合并成一个单一的MP4文件。
    它首先将列表中的电影文件分批合成TS文件，然后将所有TS文件合并成一个MP4文件。
    """
    temp = []  # 临时存储合成批次的文件名
    n = 1  # 初始化批次号
    now_path = os.getcwd()  # 获取当前工作目录
    path = f'{movie_name}/before_synthesis'  # 设置合成前文件存放路径
    os.chdir(path)  # 切换到合成前文件存放路径
    # 循环处理每个电影文件，直到处理完所有文件
    for i in range(len(lst_movies)):
        file_name = lst_movies[i][-13:]  # 提取文件名（不含路径）
        temp.append(file_name)  # 添加文件名到临时列表
        # 当临时列表达到20个文件名时，进行一次合成
        if len(temp) == 20:
            cmd = f'copy /b {"+".join(temp)} {n}.ts'  # 构造合成命令
            r = os.popen(cmd)  # 执行合成命令
            print(r.read())  # 打印命令执行结果
            n += 1  # 更新批次号
            temp = []  # 清空临时列表

    # 处理剩余的文件名，进行最后一次合成
    cmd = f'copy /b {"+".join(temp)} {n}.ts'
    r = os.popen(cmd)
    print(r.read())
    last_temp = []  # 存储所有合成批次的文件名
    for i in range(1, n + 1):
        last_temp.append(f'{i}.ts')
    cmd = f'copy /b {"+".join(last_temp)} {movie_name}.mp4'  # 构造最终合成命令
    r = os.popen(cmd)  # 执行最终合成命令
    print(r.read())  # 打印最终合成命令的执行结果
    os.chdir(now_path)  # 返回初始工作目录

    # pass


# /**
#  * 异步下载文件。
#  *
#  * @param down_url 文件下载的URL。
#  * @param file_path 保存文件的本地路径。
#  * @return 不返回任何内容。
#  */
async def download(down_url, file_path, sem):
    # 根据URL后缀生成文件名，并确定保存路径 千万别split（‘/’）文件名太长了。。。
    file_name = file_path + '/' + down_url[-13:]
    if not os.path.exists(file_name):
        for i in range(5):
            async with sem:
                try:
                    print(f'{file_name}开始下载。')
                    # 使用aiohttp异步获取文件内容
                    async with aiohttp.ClientSession() as session:
                        async with session.get(down_url) as resp:
                            content = await resp.content.read()
                            # 使用aiofiles异步写入文件
                            async with aiofiles.open(file_name, 'wb') as f:
                                await f.write(content)
                    print(f'{file_name}下载完成。')
                    break  # 文件下载成功后跳出循环
                except Exception as e:
                    print(f'{file_name}下载失败,已重新下载，错误信息为：{e}')
                    continue  # 下载失败则尝试重新下载


async def main(lst_movie, movie_name):
    file_path = f'{movie_name}/before_synthesis'
    if not os.path.exists(file_path):
        os.makedirs(file_path)
    # 创建信号量，限制同时下载的线程数
    sem = asyncio.Semaphore(100)
    tasks = []
    # print(len(lst_movie))
    for item in lst_movie[:]:
        tasks.append(asyncio.create_task(download(item, file_path, sem)))
    await asyncio.gather(*tasks)


if __name__ == '__main__':
    # 初始化电影URL 这个网站的电影应该都可以
    movie_url = 'https://www.7qhb.com/vod/qthl2024.html'
    # 获取电影的真实URL
    movie_url = get_movie_url(movie_url)
    # 从电影URL中提取第一个m3u8链接和电影名称
    first_m3u8_url, movie_name = get_first_m3u8_url(movie_url)
    # 处理第一个m3u8链接，获取第二个级别的m3u8链接
    second_m3u8_url = deal_first_m3u8(first_m3u8_url, movie_name)
    # 解析并获取电影片段列表
    lst_movies = deal_second_m3u8(movie_name)
    # 使用异步方式执行主要的电影下载逻辑
    asyncio.run(main(lst_movies, movie_name))
    # 合并所有电影片段为一个完整的电影文件
    merge_movie(lst_movies[:], movie_name)
    print(len(lst_movies))
    print('电影下载完成！')