import urllib.robotparser
import requests
from bs4 import BeautifulSoup
import jieba
from sklearn.feature_extraction.text import CountVectorizer
import os
import re


# 保存文件并确保目录存在
def savefile(file_dir, content, seq):
    if not os.path.exists(file_dir):
        os.makedirs(file_dir)
    file_path = os.path.join(file_dir, str(seq) + '.html')
    with open(file_path, "wb") as f:
        f.write(content.encode("utf-8"))  # 编码成字节


# 设置http头部属性
useragent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0'
http_headers = {
    'User-Agent': useragent,
    'Accept': 'text/html'
}

# 使用关键词集合方式来定义
topicwords = {"经营", "建议", "公共", "安全", "主播","游戏" }

website = 'http://roll.news.sina.com.cn/'
url = 'http://roll.news.sina.com.cn/news/gnxw/gdxw1/index.shtml'
file_dir = 'file_dir'  # 在当前文件夹中创建名为 file_dir 的文件夹

rp = urllib.robotparser.RobotFileParser()
rp.set_url(website + "robots.txt")
rp.read()

# 确保Robots中许可访问
if rp.can_fetch(useragent, url):
    page = requests.get(url, headers=http_headers)
    page.encoding = 'gb2312'
    content = page.text

    # 装载停用词列表
    with open('stopword.txt', 'r', encoding="utf-8") as f:
        stoplist = set(w.strip() for w in f.readlines())

    # 提取形如 href="http://news.sina.com.cn/o/2018-11-06/doc-ihmutuea7351575.shtml" 的字符串
    ulist = re.findall(r'href="http://[a-z0-9/.\-]+\.shtml', content)
    i = 1
    for u in ulist:
        u = u[6:]
        print(u)
        page = requests.get(u, headers=http_headers)
        page.encoding = 'utf-8'
        content = page.text

        bs = BeautifulSoup(content, 'lxml')
        ps = bs.select('div#article > p')
        doc = []
        for p in ps:
            p = p.text.strip("\n")
            if p != "":
                words = [w for w in jieba.cut(p, cut_all=True) if len(w) > 1 and w not in stoplist]
                if words:  # Ensure there are words after filtering
                    doc.append(' '.join(words))

        if not doc:
            print(f"Document {u} is empty after filtering.")
            continue

        # 使用CountVectorizer提取特征词汇
        vectorizer = CountVectorizer(max_df=1.0, min_df=2, max_features=10)
        try:
            X = vectorizer.fit_transform(doc)
            feature_names = vectorizer.get_feature_names_out()
            docwords = set(feature_names)

            # 相关度计算: topicwords和docwords集合的相似度
            commwords = topicwords.intersection(docwords)
            sim = len(commwords) / (len(topicwords) + len(docwords) - len(commwords))

            # 如果相似度满足设定的要求，则认为主题相关，可以保存到文件。
            if sim > 0.1:
                print(docwords)
                print("sim=", sim)
                savefile(file_dir, content, i)

            i += 1
        except ValueError as e:
            print(f"Error processing document {u}: {e}")
else:
    print('不允许抓取！')
