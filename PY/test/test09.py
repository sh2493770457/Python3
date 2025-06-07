import requests
import csv
from concurrent.futures import ThreadPoolExecutor

f = open('新发地菜价.csv','w',newline='',encoding='utf-8-sig')
whd = csv.DictWriter(f,fieldnames=
['类别','名称','最低价','平均价','最高价','规格','产地','计量单位','发布日期'])
whd.writeheader()
dit = {}
def Task(i):
    url = 'http://www.xinfadi.com.cn/getPriceData.html'
    d = {
        'limit':20,
        'current':i
    }
    response = requests.post(url=url,data=d).json()
    for l in range(0,20):
        dit['类别'] = response['list'][l]['prodCat']     # 类别
        dit['名称'] = response['list'][l]['prodName']        # 名字
        dit['最低价'] = response['list'][l]['lowPrice']        # 最低价
        dit['平均价'] = response['list'][l]['avgPrice']        # 平均价
        dit['最高价'] = response['list'][l]['highPrice']        # 最高价
        dit['规格'] = response['list'][l]['specInfo']        # 规格
        dit['产地'] = response['list'][l]['place']               # 产地
        dit['计量单位'] = response['list'][l]['unitInfo']             # 计量单位
        dit['发布日期'] = response['list'][l]['pubDate']             # 发布日期
        whd.writerow(dit)

if __name__ == '__main__':
    # 创建线程池 50个
    with ThreadPoolExecutor(50) as t:
        #  获取50页的数据信息
        for i in range(1, 51):
            #  分配任务
            t.submit(Task,i)

