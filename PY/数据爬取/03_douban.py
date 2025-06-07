import requests
from bs4 import BeautifulSoup
import csv
import time

base_url = "https://movie.douban.com/top250?start="

f = open('douban_top250.csv', mode='a', encoding='gbk', newline='')
csv_write = csv.DictWriter(f, fieldnames=['排序', '名称', '得分', '推荐语', '详情的网址'])
csv_write.writeheader()

# 根据分页，生成每一页的地址
def url_page(s=0):
    start = s * 25
    # 注意：在拼接字符串的时候。分页信息是整数，是数字，所以没法直接拼接，需要转为字符串类型 str（），才可以适用拼接
    url = base_url + str(start) + '&filter='
    return url

# 为robots访问创建一个header以欺骗服务器
header = {
    'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36',
    'Cookie' : 'bid=0jmryqSbOz4; ll="118318"; _pk_id.100001.4cf6=fd751a6c6e246ef3.1699927790.; _vwo_uuid_v2=DC65A4FB258F82A8C363B94F02560B430|6f744ff95048ba243ff09435be2e9149; __yadk_uid=uq9vSGuwn2u4Hs4Zb21SJSZECfL0OZrc; Hm_lvt_16a14f3002af32bf3a75dfe352478639=1699947054; __utmc=30149280; __utmz=30149280.1700012503.3.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmc=223695111; __utmz=223695111.1700012503.3.3.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; _pk_ref.100001.4cf6=%5B%22%22%2C%22%22%2C1700026654%2C%22https%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3DZF4nSYeFKwoWmRJCy__ayohdYmIrMm9mxDbShAw5GwJjpKGHjM80q2xGDhkx-2cX%26wd%3D%26eqid%3D94f5529600068acc00000006655421d7%22%5D; _pk_ses.100001.4cf6=1; ap_v=0,6.0; __utma=30149280.1222651244.1699927788.1700017144.1700026655.5; __utma=223695111.995184361.1699927790.1700017144.1700026658.5; __utmb=223695111.0.10.1700026658; __utmb=30149280.13.8.1700026751175; RT=s=1700028475182&r=https%3A%2F%2Fmovie.douban.com%2Ftop250',
    'Host': 'movie.douban.com',
}

# 根据有多少页，去执行访问
for i in range(10):
    print(i)
    # 组成对应的请求的地址
    url = url_page(i)
    # 发送请求，获得响应结果
    response = requests.get(url, headers=header)
    print(response)
    # 获取其中的内容
    bs = BeautifulSoup(response.text, 'html.parser')
    # print(type(bs))

    # 通过这个bs对象，我们可以使用一些bs自己的属性和方法来解析和提取数据
    #   find()，提取满足需求的首个数据，
    #       对象.find(标签，属性)  ==>  对象.find('div', {'class':'items'})
    #   find_all()，提取满足需求的所有数据，
    #       对象.find(标签，属性)  ==>  对象.find('div', {'class':'items'})
    items = bs.find_all('div', {'class':'item'})

    for item in items:
        # 排序编号
        index = item.find('em').text
        # 电影名称
        title = item.find('span', {'class':'title'}).text
        # 电影评分
        score = item.find('span', {'class': 'rating_num'}).text
        # 推荐语
        if item.find('span', {'class':'inq'}) :
            inq =  item.find('span', {'class': 'inq'}).text
        else:
            inq = ''

        # 详情的网址
        detail_url = item.find('a')['href']
        # 组合数据
        series = {'排序': index, '名称':title, '得分':score, '推荐语':inq, '详情的网址':detail_url}
        csv_write.writerow(series)
    time.sleep(1)
# 有开就有关！！！！！！
f.close()