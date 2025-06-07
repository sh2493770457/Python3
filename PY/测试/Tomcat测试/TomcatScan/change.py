import os

# TODO:定义空列表用于保存处理后的链接
urls_list = []

# TODO:读取文件并处理URL
with open(r'data/urls.txt', 'r') as f:
    urls = f.readlines()

    for url in urls:
        # TODO:去除url中的空格和换行符
        url = url.strip()

        # TODO:给没有http://或https://的链接添加http://
        if not url.startswith('http://') and not url.startswith('https://'):
            url = 'http://' + url

        # TODO:将处理后的链接添加到列表中
        urls_list.append(url)

# 保存处理后的所有链接，覆盖urls.txt文件
with open(r'data/urls.txt', 'w', encoding='utf-8') as f:
    for url in urls_list:
        f.write(url + '\n')

# 执行命令
os.system(r'python Tomcat_shell.py')
