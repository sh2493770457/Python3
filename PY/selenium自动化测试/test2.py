import requests
import re
from pprint import pprint
from lxml import etree

# #requests使用
# r=requests.get("http://www.baidu.com")
# print(r.status_code) #状态码
# print(r.encoding) #从http header 中猜测相应的编码方式
# print(r.apparent_encoding) #从内容中分析出相应内容彼岸吗方式
# print(r.text) #html文本
# print(r.headers) #获取头部信息

# #requests的异常
# try:
#     r=requests.get("www.baidu.com",timeout=30) #请求超时时间为30s
#     r.raise_for_status() #如果状态码不是200，引发异常
#     r.encoding=r.apparent_encoding #配置编码
#     print(r.text)
# except:
#     print("产生异常")

#x向url post一个字典：
# payload = {"key1": "value1", "key2": "value2"}
# r = requests.post("http://httpbin.org/post", data=payload)
# print(r.text)
# # 将响应内容解析为 JSON 格式
# response_json = r.json()
# headers = response_json["headers"]
# print("Headers=")
# pprint(headers)

#html补全（etree）
# text='''       <div class="header-navbar navbar-collapse collapse">
#             <ul class="nav navbar-nav header-a">
#                 <div class="hidden-sm hidden-md hidden-lg header-remove">
#                     <p class="iconfont">&#xe711;</p>
#                 </div>'''
# html=etree.HTML(text)
# result=etree.tostring(html)
# #print(result.decode("UTF-8"))
#
# #读取html文件
# html_1=etree.parse('./Untitled-example_1.html',etree.HTMLParser())
# result_1=etree.tostring(html_1)
# #print(result_1.decode('UTF-8'))
#
# #用xpath查标签
# result_2=html_1.xpath('//ul//a/text()')
# print(result_2) #输出：['首页', '蛋糕分类', '热销蛋糕', '新品蛋糕', '登录', '注册', '\r\n
#                 #                         ', '\r\n                        ']

