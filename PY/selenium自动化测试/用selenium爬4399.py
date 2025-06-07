from selenium import webdriver as wd
import time
from lxml import etree

url = 'https://www.4399.com/flash/gamehw.htm'
driver = wd.Edge()
driver.get(url)

# 等待页面加载完成
time.sleep(10)

# 获取页面源代码
data = driver.page_source
html = etree.HTML(data)

# 获取游戏名称
game_name = html.xpath("//div[@class='w_box cf'][1]/ul[@class='tm_list']/li[*]/a/b/text()")
print("Game Names:", game_name)

# 获取游戏分类
game_type = html.xpath("//div[@class='w_box cf'][1]/ul[@class='tm_list']/li[*]/em[1]/a/text()")
print("Game Types:", game_type)

# 获取游戏图片
game_jpg = html.xpath("//div[@class='w_box cf'][1]/ul[@class='tm_list']/li[*]/a/img/@src")
print("Game Images:", game_jpg)

# 获取发行时间
game_time = html.xpath("//div[@class='w_box cf'][1]/ul[@class='tm_list']/li[*]/em[2]/text()")
print("Game Times:", game_time)

# 获取游戏链接
game_link = html.xpath('//*[@id="skinbody"]/div[6]/ul/li[*]/a/@href')
game_finallink = ['https://www.4399.com' + i for i in game_link]
print("Game Links:", game_finallink)

# 找到所有列表的最小长度
min_length = min(len(game_name), len(game_type), len(game_jpg), len(game_time), len(game_finallink))
print(f"Minimum length of data lists: {min_length}")


# 将数据写入文件
with open('4399.txt', 'w', encoding='utf-8') as f:
    for i in range(min_length):
        f.write(f"游戏名称: {game_name[i]}\n")
        f.write(f"游戏分类: {game_type[i]}\n")
        f.write(f"游戏图片: {game_jpg[i]}\n")
        f.write(f"发行时间: {game_time[i]}\n")
        f.write(f"游戏链接: {game_finallink[i]}\n")
        f.write('\n')

# 关闭浏览器
driver.quit()
