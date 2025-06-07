import pickweb

# url = 'https://image.baidu.com/search/index?tn=baiduimage&ct=201326592&lm=-1&cl=2&ie=gb18030&word=%D0%A1%C3%A8%D5%D5%C6%AC&fr=ala&ala=1&alatpl=normal&pos=0&dyTabStr=MCwzLDIsMSw2LDQsNSw3LDgsOQ%3D%3D'
#
# # 请求网页
# res = pickweb.web_requests(url)
#
# # 使用Selenium获取图片链接
# link = pickweb.web_selenium(url, '//*[@id="imgid"]/div[1]/ul/li[*]/div/div[2]/a/img/@src')
#
# # 下载图片
# if link:
#     for idx, base_url in enumerate(link):
#         pickweb.down_file(base_url, f'{idx + 1}.png')

# 下载音频
# url='https://ws6.stream.qqmusic.qq.com/C400000dhW37052Yj9.m4a?guid=815237280&vkey=1CA6F7CAD9112D0116CDAAFFB651C5EC99D2ABA688F3D8D23C2FE522E9840A82A830648A8BE4186B2D518E2305321253695EAD3CA0382D68&uin=2493770457&fromtag=120032'
# pickweb.down_file(url, '花开你未来.mp3')

# 下载mv
# url='https://mv6.music.tc.qq.com/26BEC45C3C88D637D998D6234A6C618E350650BD9365E9BFB406DE13B26A1F72BE56143F55B7832CB242318909F564A5ZZqqmusic_default/qmmv_0b53aqan2aaaqaanq5xt7zsvibaa3ucabxka.f9954.ts'
# pickweb.down_file(url,'若是月亮还没来.mp4')


url = f'https://movie.douban.com/top250?start=0'
res = pickweb.web_requests(url)

# ids = '//*[@id="content"]/div/div[1]/ol/li/div/div[1]/em/text()'
# urls = '//*[@id="content"]/div/div[1]/ol/li/div/div[1]/a/@href'
# names = '//*[@id="content"]/div/div[1]/ol/li/div/div[2]/div[1]/a/span[1]/text()'
# directors_raw = "//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='info']/div[@class='bd']/p[1]/text()"
# scores = '//*[@id="content"]/div/div[1]/ol/li/div/div[2]/div[2]/div[1]/span[2]/text()'
# descriptions = "//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='info']/div[@class='bd']/p[@class='quote']/span[@class='inq']/text()"
# images = "//ol[@class='grid_view']/li[*]/div[@class='item']/div[@class='pic']/a/img/@src"
# countries_raw = "//*[@id='content']/div/div[1]/ol/li/div/div[2]/div[2]/p[1]/text()[2]"
# movie_years_raw = "//div[@class='item']/div[@class='info']/div[@class='bd']/p[1]/text[2]"

xpaths = ['//*[@id="content"]/div/div[1]/ol/li/div/div[2]/div[1]/a/span[1]/text()']
print(len(pickweb.web_selenium(url, xpaths)))
print(pickweb.web_selenium(url, xpaths))