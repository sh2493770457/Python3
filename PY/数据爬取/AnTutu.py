import requests
from bs4 import BeautifulSoup
import csv

# 安兔兔排名的基本URL
base_url = 'https://www.antutu.com/ranking/rank1.htm'

# 伪装
header = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36',
}

# 打开一个CSV文件用于写入数据
f = open('AnTutu_Top.csv', mode='a', encoding='utf-8', newline='')
# 在这儿犯了个错，'Top', 'Name'，'Score'，一开始用中文结果运行不出来，检查半天
csv_write = csv.DictWriter(f, fieldnames=['Top', 'Name', 'Processor', 'CPU', 'GPU', 'MEM', 'UX', 'Score'])
csv_write.writeheader()


# 生成分页，没有分页只有一页数据，参照老师的代码，故s=0
def url_page(s=0):
    start = s * 25
    url = f"{base_url}?start={start}"
    return url


# 从列表项中提取手机信息，这是ul中其中一个list-unstyled newrank-b标签
# <span class="numrank num1 link">1</span>————对应排名
# "红魔9 Pro+"
# <span class="memory"> (S-8 Gen 3 16/512) </span>————对应手机名称
# <li>474724</li>————对应CPU
# <li>912609</li>————对应GPU
# <li>428089</li>————对应MEN
# <li>373209</li>————对应UX
# <li class="blast">2188631 分</li>————对应总分（自带一个分字）

def extract_phone_info(item):
    # 排名
    rank = item.find('span', {'class': 'numrank'}).text.strip()
    # 手机名称，对名字做处理，移除名字开头的排名。（原： 'name': '114iQOO Z7 (S-782G 8/256)'）
    phone_name = item.find('li', {'class': 'bfirst'}).text.strip().lstrip('0123456789')

    # 提取处理器信息
    processor_info = phone_name.split('(')[1].split(')')[0].strip()

    # 手机名称里处理器信息排除
    phone_name = phone_name.split('(')[0].strip()

    # 跑分信息,因为最后一项总分带有“分”字并且空格，所以给前4个li跑分标签加了' 分'
    scores = [score.text.strip() + ' 分' if index < 4 else score.text.strip() for index, score in
              enumerate(item.find_all('li')[1:6])]

    return {'Top': rank, 'Name': phone_name, 'Processor': processor_info, 'CPU': scores[0], 'GPU': scores[1],
            'MEM': scores[2], 'UX': scores[3], 'Score': scores[4]}


# 循环遍历页面，页面只有一页120个数据所以range（1）
for i in range(1):
    # 生成当前页面的url
    url = url_page(i)

    # 发送GET请求到URL
    response = requests.get(url, headers=header)

    # 使用bs4解析
    bs = BeautifulSoup(response.text, 'html.parser')

    # 查找所有ul中带有list-unstyled newrank-b标签
    # <ul class="list-unstyled newrank-b">
    # <ul class="list-unstyled newrank-b">
    # ...........
    items = bs.find_all('ul', {'class': 'list-unstyled newrank-b'})

    # 遍历每个项目并提取手机信息（info）
    for item in items:
        phone_info = extract_phone_info(item)
        print(phone_info)

        # 将手机信息写入CSV文件
        csv_write.writerow(phone_info)

# 关闭CSV文件，有开有关
f.close()
