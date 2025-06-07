from selenium import webdriver as wd
import time
#创建option对象
option = wd.ChromeOptions()
#开启无界模式（隐藏浏览器窗口）
option.add_argument('--headless')


# # 初始化WebDriver
# driver = wd.Chrome(options=option)
# # 向url发送一个请求
# driver.get('https://my.4399.com/yxmsdzls/')
# time.sleep(3)
#
#
# # 打印页面标题
# print(driver.title)
#
# #把网页保存为图片
# driver.save_screenshot('美食大战耗儿.png')
#
# # 关闭浏览器
# driver.quit()


#获取源代码
# driver=wd.Chrome(options=option)
# driver.get('https://my.4399.com/yxmsdzls/')
#
# # 打印渲染后的源代码
# # print(driver.page_source)
#
# #当前标签页的url
# # print(driver.current_url)
#
# #网页截图
# driver.save_screenshot('美食大战耗儿.png')
# print(driver.title)


#定位元素
driver=wd.Chrome()
driver.get('http://www.baidu.com')

time.sleep(1)
# 通过id获取元素，找到搜索框
element = driver.find_element('id', 'kw')
#输入关键字
element.send_keys('美食大战老鼠')
time.sleep(1)

#找到id为su的节点，模拟点击
driver.find_element('id', 'su').click()
time.sleep(2)
print(driver.title)