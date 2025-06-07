# -*- coding: utf-8 -*-
'''
pip install  -i https://mirrors.aliyun.com/pypi/simple/ jieba # 安装jieba分词包
pip install  -i https://mirrors.aliyun.com/pypi/simple/ requests # 安装requests包
pip install  -i https://mirrors.aliyun.com/pypi/simple/ lxml # 安装lxml包
pip install  -i https://mirrors.aliyun.com/pypi/simple/ beautifulsoup4 # 安装beautifulsoup4
pip install  -i https://mirrors.aliyun.com/pypi/simple/  gensim      
pip install  -i https://mirrors.aliyun.com/pypi/simple/  scipy==1.10.1 最新版不行！！！
'''
import urllib.robotparser
import requests
from bs4 import BeautifulSoup
import jieba
from gensim.corpora.dictionary import Dictionary
import os
import re


# 函数功能：保存文件
# 参数1：文件路径
# 参数2：文件内容
# 参数3：文件序号
def savefile(file_dir, content, seq):
    file_path = file_dir + os.sep + str(seq) + '.html'
    f = open(file_path, "wb")
    f.write(content.encode("utf-8"))  # 编码成字节
    f.close()


# 设置http头部属性
useragent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0'
http_headers = {
    'User-Agent': useragent,
    'Accept': 'text/html'
}

# 使用关键词集合方式来定义主题词
# topicwords={"网络","安全","法案","预警","设施","互联网"}
topicwords = {"经济", "政治", "中国", "美国", "贸易", "商业"}
# 网站根路径
website = 'http://roll.news.sina.com.cn/'
# 需要爬取的新闻网站
url = 'http://roll.news.sina.com.cn/news/gnxw/gdxw1/index.shtml'
# 保存的路径
file_dir = './topic'

rp = urllib.robotparser.RobotFileParser()
rp.set_url(website + "robots.txt")
rp.read()

# 确保Robots中许可访问
if rp.can_fetch(useragent, url):
    page = requests.get(url, headers=http_headers)
    page.encoding = 'gb2312'
    content = page.text

    # 装载停用词列表
    stoplist = open('stopword.txt', 'r', encoding="utf-8").readlines()
    stoplist = set(w.strip() for w in stoplist)

    # 提取形如 href="http://news.sina.com.cn/o/2018-11-06/doc-ihmutuea7351575.shtml" 的字符串，也就是所有的链接的信息
    ulist = re.findall('href="http://[a-z0-9/.\-]+\.shtml', content)
    i = 1
    for u in ulist:
        u = u[6:]
        print(u)  # 打印链接地址
        page = requests.get(u, headers=http_headers)
        page.encoding = 'utf-8'
        content = page.text

        bs = BeautifulSoup(content, 'lxml')
        ps = bs.select('div#article > p')  # 提取正文内容的p标签
        ptext = ''  # 存储正文内容的字符串
        doc = []  # 存储正文内容的词汇列表
        # ----------------------------------------------------
        # 步骤1：提取正文内容
        for p in ps:
            p = p.text.strip("\n")  # 去掉换行符
            if p != "":
                d = []
                # 词汇切分、过滤
                for w in list(jieba.cut(p, cut_all=True)):
                    # 不要停用词
                    if len(w) > 1 and w not in stoplist:
                        d.append(w)
                doc.append(d)
        print(doc)

        # 步骤2：特征选择，假设依据是：词汇至少出现2次，而且词汇所在的段落数/总的段落数<=1.0
        # 选择符合这两个条件的前10个词汇作为页面内容的代表
        dictionary = Dictionary(doc)
        dictionary.filter_extremes(no_below=2, no_above=1.0, keep_n=10)
        d = dict(dictionary.items())
        docwords = set(d.values())

        # 步骤3：相关度计算: topicwords和docwords集合的相似度
        commwords = topicwords.intersection(docwords)
        sim = len(commwords) / (len(topicwords) + len(docwords) - len(commwords))

        # 如果相似度满足设定的要求，则认为主题相关，可以保存到文件。
        if sim > 0.1:
            print(docwords)  # 页面关键词
            print("sim=", sim)  # 相似度
            savefile(file_dir, content, i)
        # ----------------------------------------------------------
        i = i + 1
else:
    print('不允许抓取！')
