import time

import requests
import pandas as pd

base_url = 'https://www.cwl.gov.cn/cwl_admin/front/cwlkj/search/kjxx/findDrawNotice?name=ssq&issueCount=&issueStart=&issueEnd=&dayStart=&dayEnd='
header = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36',
    'Referer': 'https://www.cwl.gov.cn/ygkj/wqkjgg/ssq/'
}
data=[]
def url_page(p=1):
    url = f'{base_url}&pageNo={p}&pageSize=30&week=&systemType=PC'
    return url

for i in range(5):
    p = i + 1
    url = url_page(p)
    response = requests.get(url, headers=header)
    #print(response.json())['result']
    items=response.json()['result']
    for item in items:
        code=item['code']
        date=item['date']
        num =item['red']+','+item['blue']
        data.append([code,date,num])

    time.sleep(1)
    df = pd.DataFrame(data)
    df.to_csv('shuangseqiu_data.csv', encoding='gbk', index=False, header=False)
