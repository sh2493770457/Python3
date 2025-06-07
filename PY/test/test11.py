
import requests
import fake_useragent
import re



url = "https://music.163.com/discover/toplist?id=3778678"
ua = fake_useragent.UserAgent()
header = {
    'user-agent':ua.random
          }
response = requests.get(url=url,headers=header)
r = response.text
# print(r)
response.close()

# 包含歌曲和歌曲链接的一段字符串
all = ''.join(re.findall('<ul class="f-hide">(.*?)</ul>',r))
# 从 all 里提取歌名
name = re.findall('<a href=".*?">(.*?)</a>',all)
# 从 all 里提取歌曲地址
song_url = re.findall('<a href="(.*?)">.*?</a>',all)
# 从页面全部源代码中提取歌手的信息
singer = re.findall('"artists":\[{"id":.*?,"name":"(.*?)",',r)

# 打印
for i in range(len(name)):
    print(name[i],'\t',singer[i],'\t',song_url[i])