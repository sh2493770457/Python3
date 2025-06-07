#b站单个视频分析

import requests

url_sing='https://upos-sz-mirror08c.bilivideo.com/upgcxcode/93/90/1545989093/1545989093-1-30216.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1715848569&gen=playurlv2&os=08cbv&oi=0&trid=8bf7d723228141a5a389783f24ff8a62u&mid=0&platform=pc&upsig=7a48885ec158639a71d6fa97d861d2f9&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=0,3&buvid=458462E1-B9FC-263E-03D7-F6F9DE42158F89043infoc&build=0&f=u_0_0&agrr=1&bw=6100&logo=80000000'
url_ved='https://upos-sz-mirrorcos.bilivideo.com/upgcxcode/93/90/1545989093/1545989093-1-100022.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1715848569&gen=playurlv2&os=cosbv&oi=0&trid=8bf7d723228141a5a389783f24ff8a62u&mid=0&platform=pc&upsig=3bcd119e2cf7a71d66499c94978c141f&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=0,3&buvid=458462E1-B9FC-263E-03D7-F6F9DE42158F89043infoc&build=0&f=u_0_0&agrr=1&bw=27643&logo=80000000'
headers={
    'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36',
    'Referer':'https://www.bilibili.com/video/BV1nr421j7TY/?spm_id_from=333.1007.tianma.9-4-34.click'
}

res=requests.get(url_sing,headers=headers)
# print(res.content)
# with open('音频.mp4','wb') as f:
#     f.write(res.content)

# res2=requests.get(url_ved,headers=headers)
# with open('视频.mp4','wb') as f:
    # f.write(res2.content)

# 合并视频和音频
# from moviepy.editor import *
# vd=VideoFileClip('视频.mp4')
# ad=AudioFileClip('音频.mp4')
# vd2=vd.set_audio(ad)
# vd2.write_videofile('赵露思新片预告.mp4')

