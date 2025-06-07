# -*-coding:utf-8 -*-
# 彩票各期大乐透中奖数据
# 采集网站 https://www.lottery.gov.cn/kj/kjlb.html?dlt
# 采集网址
# https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?gameNo=85&provinceId=0&pageSize=30&isVerify=1&pageNo=1
# https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?gameNo=85&provinceId=0&pageSize=30&isVerify=1&pageNo=2
# https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?gameNo=85&provinceId=0&pageSize=30&isVerify=1&pageNo=3
# https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?gameNo=85&provinceId=0&pageSize=30&isVerify=1&pageNo=83
import json
import os
import random
import time
from datetime import datetime
import pandas as pd

import requests
from lxml import etree


def getPageInfo():

    response = requests.get(url="https://m.douban.com/rexxar/api/v2/movie/recommend?refresh=0&start=0&count=20&selected_categories=%7B%7D&uncollect=false&tags=2023", headers=headers)
    response.encoding="utf-8"
    json_datas= json.loads(response.text)
    print(response)


if __name__ == '__main__':
    headers = {
        "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36"
        ,
        "accept": "application/json, text/plain, */*"
        ,
        "Referer": "https: // movie.douban.com / explore",
        "cookie": 'll="118318"; bid=Wvcv3L_hKTw; ap_v=0,6.0; __utma=30149280.1676263134.1700614956.1700614956.1700614956.1; __utmb=30149280.0.10.1700614956; __utmc=30149280; __utmz=30149280.1700614956.1.1.utmcsr=baidu|utmccn=(organic)|utmcmd=organic' }
    getPageInfo()

