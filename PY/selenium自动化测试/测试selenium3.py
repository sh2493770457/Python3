from selenium import webdriver as wd
import time

driver = wd.Chrome()
driver.get("http://www.baidu.com")
time.sleep(1)

driver.find_element("id", "kw").send_keys("美食大战老鼠")
time.sleep(1)

driver.find_element("id", "su").click()
time.sleep(2)

# 滚动页面到底部
# js = 'document.documentElement.scrollTop=8000'

#自定义滚动距离
js='window.scrollTo(0,700)'
driver.execute_script(js)
time.sleep(2)

driver.quit()
