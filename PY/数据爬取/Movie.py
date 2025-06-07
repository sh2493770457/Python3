
import requests
import re
from concurrent.futures import ThreadPoolExecutor





class Download():
    def __init__(self,url):
        self.url = url
        self.headers = {
            'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.78'
        }
    def M3u8(self):
        m3u8 = requests.get(url=self.url,headers=self.headers).text
        # print(m3u8)
        self.ts_url = re.findall('http.*?.ts',m3u8)


    def Get_ts(self,i,a):

        content = requests.get(url=i,headers=self.headers,timeout=30).content
        with open(f'40088/{a}.mp4',mode='wb') as f:
            f.write(content)
            print(f'正在保存视频:{a}...')


    def Again(self,j,b):
        try:
            with open(f'40088/{b}.mp4', mode='rb') as f:
                print(f'检测文件-{b}')
        except (FileNotFoundError):
            content = requests.get(url=j, headers=self.headers, timeout=30).content
            with open(f'40088/{b}.mp4', mode='wb') as f1:
                f1.write(content)
                print(f'已补-{b}')



    def merge_ts(self):
        print('正在合并视频...')
        b = 1

        for i in self.ts_url:
            with open(f'40088/{b}.mp4', mode='rb') as f:
                content = f.read()
                with open(f'40088/out.mp4', mode='ab') as f1:
                    f1.write(content)
                    print(f'已合并视频:{b}')
            b += 1
        print('已全部合并完毕！')



if __name__ == '__main__':
    D1 = Download(url='https://hot.qqaku.com/20230122/SARRFgKf/1000kb/hls/index.m3u8')
    D1.M3u8()
    a = 0
    with ThreadPoolExecutor(200) as t:
        for i in D1.ts_url:
            a += 1
            t.submit(D1.Get_ts,i,a)
    b = 0
    with ThreadPoolExecutor(30) as t1:
        for j in D1.ts_url:
            b += 1
            t1.submit(D1.Again,j,b)



    print('片段视频已全部保存完成！')
    D1.merge_ts()