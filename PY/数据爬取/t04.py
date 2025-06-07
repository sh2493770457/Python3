import requests
from bs4 import BeautifulSoup
import csv
import time

headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0"
}

def get_info(url):
    data_total = []
    # 通过请求头和链接，得到网页页面整体信息
    web_data = requests.get(url, headers=headers)
    # 对返回的结果进行解析
    soup = BeautifulSoup(web_data.text, 'lxml')
    # 找到具体的相同的数据的内容位置和内容
    ranks = soup.select('span.pc_temp_num')
    titles = soup.select('div.pc_temp_songlist > ul > li > a')
    times = soup.select('span.pc_temp_tips_r > span')
    # 提取具体的文字内容
    for rank, title, time in zip(ranks, titles, times):
        datadict = {
            'rank': rank.get_text().strip(),
            'singer': title.get_text().split('-')[0],
            'song': title.get_text().split('-')[1],
            'time': time.get_text().strip()
        }
        data_total.append(datadict)
    return data_total

def get_data_save(df):
    # 数据清洗
    data = []
    for i in range(len(df)):
        df_list = []
        rank = df[i]['rank']
        singer = df[i]['singer'].strip()
        song = df[i]['song'].strip()
        time_value = df[i]['time']
        df_list.append(rank)
        df_list.append(singer)
        df_list.append(song)
        df_list.append(time_value)
        data.append(df_list)

    # 数据保存成CSV
    with open('酷狗TOP500.csv', mode='w', encoding='utf-8', newline='') as csv_file:
        csv_writer = csv.writer(csv_file)
        # 写入表头数据
        head = ['rank', 'singer', 'song', 'time']
        csv_writer.writerow(head)

        # 写入数据
        csv_writer.writerows(data)

if __name__ == '__main__':
    urls = ['https://www.kugou.com/yy/rank/home/{}-8888.html?from=rank'.format(i) for i in range(1, 24)]
    data_total = []
    for url in urls:
        data_total.extend(get_info(url))
        time.sleep(1)

    get_data_save(data_total)
