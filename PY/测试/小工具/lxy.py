import os

import requests
import re
from lxml import etree


headers = {
    'authority': 'y.qq.com',
    'pragma': 'no-cache',
    'cache-control': 'no-cache',
    'sec-ch-ua': '";Not A Brand";v="99", "Chromium";v="94"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
    'upgrade-insecure-requests': '1',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36 SE 2.X MetaSr 1.0',
    'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
    'sec-fetch-site': 'same-origin',
    'sec-fetch-mode': 'navigate',
    'sec-fetch-user': '?1',
    'sec-fetch-dest': 'document',
    'referer': 'https://y.qq.com/n/ryqq/mvList',
    'accept-language': 'zh-CN,zh;q=0.9',
    'q-ua2': 'PR=SE&CO=WBK&QV=3&PL=WIN&PB=GE&PPVN=12.4.0.6073&COVC=049400&CHID=2100110000&RL=2880*1800&MO=SE&VE=GA&BIT=64&OS=10.0.22631',
    'q-guid': '9bd9b6ed481e7c682a2fdd54377988cb',
    'cookie': 'RK=IZO1TjzjnU; ptcz=71b5301fdbf20d5cbaaf3d8a5606b5a9fbacec3902fab2c10eb329d84102f6b8; _t_qbtool_uid=9bd9b6ed481e7c682a2fdd54377988cb; _ga=GA1.1.1747267275.1728291687; _ga_TPFW0KPXC1=GS1.1.1728291687.1.1.1728292480.0.0.0; fqm_pvqid=8413a77b-e52c-4e46-993e-a2a02c5f9db1; pgv_pvid=3566078884; ts_refer=www.sogou.com/link; ts_uid=1576801413; euin=owvqoiSloevk7z**; tmeLoginType=2; music_ignore_pskey=202306271436Hn@vBj; fqm_sessionid=8de233db-856f-4fb8-8d6c-fdc061aac907; pgv_info=ssid=s8013758530; _qpsvr_localtk=0.501472904405772; login_type=1; psrf_qqopenid=CB6455850097DD5AF41D9706DE4A0EF9; qqmusic_key=Q_H_L_63k3NAoOiT0kxebEeH8XwjFB64x1sUUr3Z1VLFWH82GSO3maUqv5p53BgDEXI9BQNh_udOmpmian5-NH0O60dWnOnpEU; qm_keyst=Q_H_L_63k3NAoOiT0kxebEeH8XwjFB64x1sUUr3Z1VLFWH82GSO3maUqv5p53BgDEXI9BQNh_udOmpmian5-NH0O60dWnOnpEU; uin=2493770457; psrf_qqunionid=564CF352CA5A9EDC35C013B06C600342; wxrefresh_token=; psrf_qqrefresh_token=C30E653BC8E716816FF381585879CAAC; psrf_access_token_expiresAt=1736348664; wxopenid=; psrf_musickey_createtime=1728572664; psrf_qqaccess_token=2058E1B01AE53654688A8C7ADB619408; wxunionid=; ts_last=y.qq.com/n/ryqq/mv/0117Zrih4YigIP',
}

# 获取页面内容
res = requests.get('https://y.qq.com/n/ryqq/singer/003Rc7tU01TXKL/mv', headers=headers)
html = res.text

# 使用正则表达式查找 vid
vid_list = re.findall(r'"vid":"([a-zA-Z0-9]*)"', html)
vid_list = [i for i in vid_list if i != '']  # 删除空字符串

# 使用 lxml 解析标题
data = etree.HTML(html)
title_list = data.xpath('//*[@id="app"]/div/div[2]/ul/li[*]/div/h3/a/text()')
# 遍历 vid 和 title，下载视频
for vid, title in zip(vid_list, title_list):
    end_url = f'https://y.qq.com/n/ryqq/singer/003Rc7tU01TXKL/mv/{vid}'
    print(end_url)
    print(f"正在处理: {title}")
    # 保存视频
    with open(f'{title}.mp4', 'wb') as f:
        f.write(requests.get(end_url, headers=headers).content)
