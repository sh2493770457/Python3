import requests
import re

headers = {
    'Connection': 'keep-alive',
    'Pragma': 'no-cache',
    'Cache-Control': 'no-cache',
    'sec-ch-ua': '";Not A Brand";v="99", "Chromium";v="94"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
    'Upgrade-Insecure-Requests': '1',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36 SE 2.X MetaSr 1.0',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
    'Sec-Fetch-Site': 'same-origin',
    'Sec-Fetch-Mode': 'navigate',
    'Sec-Fetch-User': '?1',
    'Sec-Fetch-Dest': 'document',
    'Accept-Language': 'zh-CN,zh;q=0.9',
    'Cookie': 'BIDUPSID=CA74EF0653BF772D70211B74CCF25362; PSTM=1727511518; __bid_n=19232c4678706241644905; ZFY=lzmA6PETQquRaVPRC:BGjIiKoRxkmRKXw1:AWKPGUGm9o:C; BAIDUID=3008522C0F7129BA3E9B40C41D178287:FG=1; BAIDUID_BFESS=3008522C0F7129BA3E9B40C41D178287:FG=1; H_WISE_SIDS=60600_60824; H_PS_PSSID=60600; BDORZ=FFFB88E999055A3F8A630C64834BD6D0; BA_HECTOR=2ga02124252g2g058g8k818022h9d61jgc3nb1v; BDRCVFR[S_ukKV6dOkf]=mk3SLVN4HKm; BDRCVFR[dG2JNJb_ajR]=mk3SLVN4HKm; userFrom=null; BDRCVFR[-pGxjrCMryR]=mk3SLVN4HKm; ab_sr=1.0.1_ZjRkN2ZlMjZkNDM1NDA5NTQzNTAwNzQ4YzZkYjZjOTlkMDFiMGM5YjRjMWE4NzIyZjM0NDgzMTI0YjAzZjhiY2U4NWIwY2E0N2YyMDFhYjU0MTkxNDQ1NTQ2M2U4ZGI1OTE4MDgwZjJlMTA5ZmU3MGNkZGY0Nzc2Yjc0OGQ3Y2NhYmQ3NGMzZDgzMzk0OWQ4NzVkYTBjM2UxYTYzZjQzMWViMWZmYTdjNzc3MTM3ZjRhY2I2NWFmYTU4Zjc3MjgwM2NiYWQ1MmJiZjU3ZDQ1ZmVjZWI1NTcyYmRhOGJlNmM=',
}

word = input('请输入你要下载的图片关键词：')
response = requests.get(
    f'https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=&st=-1&fm=index&fr=&hs=0&xthttps=111110&sf=1&fmq=&pv=&ic=0&nc=1&z=&se=&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word={word}',
    headers=headers)
html = response.text
# print(html)
# 找到所有img的url ,"thumbURL": "https://img0.baidu.com/it/u=183576704,正则匹配
img_urls = re.findall('"thumbURL": "(.*?)"', html)
count = 0
for img_url in img_urls:
    print('正在下载:'+img_url)
    count += 1
    with open(f'{word}-{count}.jpg', 'wb') as f:
        f.write(requests.get(img_url, headers=headers).content)
print('下载完成!')