import csv
import os

# TODO: 读取2.csv文件中的所有链接
with open(r'C:/Users/24937/Desktop/3.csv', 'r', encoding='utf-8') as f:
    reader = csv.reader(f)
    urls = []
    # TODO: 跳过第一行（标题行）
    next(reader)
    # TODO: 获取每一行的第三列值
    for row in reader:
        if len(row) > 2:  # TODO: 确保第三列存在
            urls.append(row[2].strip())  # TODO: 去除前后空格

# TODO: 定义空列表
urls_list = []

# TODO: 遍历每个链接
for url in urls:
    # TODO: 给没有http://的链接添加http://,有https的则不动
    if not url.startswith('http://') and not url.startswith('https://'):
        url = 'http://' + url
    # TODO: 去除url中的空格
    url = url.replace(' ', '')
    # TODO: 打印处理后的链接
    print(url)
    # TODO: 将处理后的链接添加到列表中
    urls_list.append(url)

# TODO: 保存处理后的所有链接，覆盖urls.txt文件
with open(r'data/urls.txt', 'w', encoding='utf-8') as f:
    for url in urls_list:
        f.write(url + '\n')

# TODO: 执行命令
os.system(r'python TomcatScanPro.py')
