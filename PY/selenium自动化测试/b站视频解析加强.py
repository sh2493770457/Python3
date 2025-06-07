import requests
import re

from lxml import etree

# url='https://www.bilibili.com/video/BV1nr421j7TY/?spm_id_from=333.1007.tianma.9-4-34.click'
#
# headers={
#     'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36',
#     'Referer':'https://www.bilibili.com/video/BV1nr421j7TY/?spm_id_from=333.1007.tianma.9-4-34.click'
# }
#
# response=requests.get(url,headers=headers)
# # print(response.text)
# # backupUrl
# # baseUrl
#
# #获取视频
# ad=re.findall(r'"id":32,"baseUrl":"(.*?)","base_url"',response.text)
# # print(ad)
# #获取音频
# vd=re.findall(r'"id":30216,"baseUrl":"(.*?),"base_url"',response.text)
# print(vd)
#
# #下载视频
# res1=requests.get(ad[0],headers=headers)
# res2=requests.get(vd[0],headers=headers)
#
# with open('video.mp4','wb') as f:
#     f.write(res1.content)
#
# with open('audio.mp4','wb') as f:
#     f.write(res2.content)



#视频获取
# 1.找到包含视频链接的url
# 2.解析出响应里的url
# 3.对这些url发送请求，获取单个视频页面的响应内容
# 4.解析出视频和音频的url
# 5.发送请求
# 6.保存

