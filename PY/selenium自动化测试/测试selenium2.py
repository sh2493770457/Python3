from selenium import webdriver as wd
import time

#1.标签页的切换

#获取当前窗口
#cur=driver.window_handles
#根据窗口索引进行切换
#driver.switch_to.window(cur[1])

driver=wd.Chrome()
driver.get("http://www.baidu.com")
time.sleep(1)

driver.find_element("id","kw").send_keys("美食大战老鼠")
time.sleep(1)

driver.find_element("id","su").click()
time.sleep(2)

#通过执行js来新开一个标签页
js='window.open("http://www.4399.com")'
driver.execute_script(js)
time.sleep(1)

#获取所有的窗口
windows=driver.window_handles

#根据索引进行切换
driver.switch_to.window(windows[0])
time.sleep(2)
driver.switch_to.window(windows[1])
time.sleep(2)


























