import csv
import random
import time
import threading

import pymysql
from selenium import webdriver as wd
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from lxml import etree

# 设置浏览器选项
options = wd.EdgeOptions()
options.add_argument('--headless')  # 启用无头模式

# 用户代理列表
USER_AGENTS = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.51 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.31 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.21 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.11 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.62 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.61 Safari/537.36"
]

# 最大线程数
MAX_THREADS = 10
# 信号量，用于控制线程数
thread_num = threading.Semaphore(MAX_THREADS)

# 城市字典，用于快速查找城市的代码  https://{CITY_DICT[city_input]}.ke.com/ershoufang/yuhua1/pg{page_number}
CITY_DICT = {
    '合肥': 'hf', '芜湖': 'wuhu', '马鞍山': 'mas', '安庆': 'aq', '阜阳': 'fy',
    '六安': 'la', '北京': 'bj', '重庆': 'cq', '福州': 'fz', '厦门': 'xm',
    '泉州': 'quanzhou', '龙岩': 'ly', '漳州': 'zhangzhou', '广州': 'gz',
    '深圳': 'sz', '珠海': 'zh', '佛山': 'fs', '江门': 'jiangmen',
    '湛江': 'zhanjiang', '肇庆': 'zq', '惠州': 'hui', '东莞': 'dg', '中山': 'zs',
    '清远': 'qy', '南宁': 'nn', '柳州': 'liuzhou', '桂林': 'gl', '北海': 'bh',
    '防城港': 'fcg', '贵阳': 'gy', '黔南': 'qn', '遵义': 'zunyi',
    '黔西南布依族苗族自治州': 'qxn', '兰州': 'lz', '天水': 'tianshui',
    '石家庄': 'sjz', '唐山': 'ts', '邯郸': 'hd', '邢台': 'xt',
    '保定': 'bd', '廊坊': 'lf', '承德': 'chengde', '秦皇岛': 'qhd',
    '张家口': 'zjk', '哈尔滨': 'hrb', '郑州': 'zz', '开封': 'kf',
    '洛阳': 'luoyang', '平顶山': 'pds', '安阳': 'ay',
    '新乡': 'xinxiang', '焦作': 'jiaozuo', '濮阳': 'py',
    '许昌': 'xc', '漯河': 'luohe', '南阳': 'ny', '商丘': 'shangqiu',
    '周口': 'zk', '驻马店': 'zmd', '济源': 'jiyuan', '武汉': 'wh',
    '黄石': 'huangshi', '宜昌': 'yichang', '襄阳': 'xy', '鄂州': 'ez',
    '孝感': 'xg', '荆州': 'jingzhou', '仙桃': 'xiantao', '黄冈': 'hg',
    '咸宁': 'xn', '长沙': 'cs', '株洲': 'zhuzhou', '湘潭': 'xiangtan',
    '衡阳': 'hy', '岳阳': 'yy', '常德': 'changde', '永州': 'yongzhou',
    '湘西土家族苗族自治州': 'xx', '海口': 'hk', '三亚': 'san', '五指山': 'wzs',
    '儋州': 'dz', '万宁': 'wn', '澄迈': 'cm', '乐东': 'ld', '陵水': 'ls',
    '长春': 'cc', '吉林': 'jl', '南京': 'nj', '无锡': 'wx', '徐州': 'xz',
    '常州': 'changzhou', '苏州': 'su', '常熟': 'changshu', '张家港': 'zjg',
    '昆山': 'ks', '南通': 'nt', '连云港': 'lyg', '淮安': 'ha', '盐城': 'yc',
    '扬州': 'yz', '镇江': 'zj', '丹阳': 'danyang', '海门': 'haimen',
    '句容': 'jr', '启东': 'qidong', '如皋': 'rg', '江阴': 'jy', '海安': 'haian',
    '南昌': 'nc', '景德镇': 'jdz', '萍乡': 'pingxiang', '九江': 'jiujiang',
    '赣州': 'ganzhou', '吉安': 'jian', '宜春': 'ych', '抚州': 'fuzhou',
    '上饶': 'sr', '沈阳': 'sy', '大连': 'dl', '抚顺': 'fushun', '丹东': 'dd',
    '呼和浩特': 'hhht', '包头': 'baotou', '赤峰': 'cf', '通辽': 'tongliao',
    '巴彦淖尔': 'byne', '银川': 'yinchuan', '太原': 'ty', '大同': 'dt', '运城': 'yuncheng',
    '晋中': 'jz', '上海': 'sh', '济南': 'jn', '青岛': 'qd', '淄博': 'zb', '烟台': 'yt',
    '潍坊': 'wf', '济宁': 'jining', '泰安': 'ta', '威海': 'weihai', '临沂': 'linyi',
    '德州': 'dezhou', '菏泽': 'heze', '成都': 'cd', '攀枝花': 'pzh', '泸州': 'luzhou',
    '德阳': 'dy', '绵阳': 'mianyang', '遂宁': 'sn', '内江': 'neijiang', '南充': 'nanchong',
    '眉山': 'ms', '宜宾': 'yibin', '达州': 'dazhou', '雅安': 'yaan', '巴中': 'bz',
    '资阳': 'ziyang', '凉山彝族自治州': 'liangshan', '乐山': 'leshan', '广元': 'guangyuan',
    '西安': 'xa', '宝鸡': 'baoji', '咸阳': 'xianyang', '渭南': 'weinan', '汉中': 'hanzhong',
    '天津': 'tj', '乌鲁木齐': 'wlmq', '昆明': 'km', '丽江': 'lj', '西双版纳傣族自治州': 'xsbn',
    '大理': 'dali', '杭州': 'hz', '宁波': 'nb', '温州': 'wz', '嘉兴': 'jx', '湖州': 'huzhou',
    '绍兴': 'sx', '金华': 'jh', '义乌': 'yw', '衢州': 'quzhou', '舟山': 'zhoushan', '台州': 'taizhou'
}

def data(page_number, user_agent, city_input):
    with thread_num:
        options = wd.EdgeOptions()
        options.add_argument('--headless') #隐藏浏览器，网页没问题爬取量大的时候开
        options.add_argument(f"user-agent={user_agent}")
        # 设置代理, 模拟人的行为
        driver = wd.Edge(options=options)
        url = f'https://{CITY_DICT[city_input]}.ke.com/ershoufang/pg{page_number}'
        driver.get(url)

        #页面加载异常处理
        try:
            WebDriverWait(driver, 20).until(
                EC.presence_of_element_located((By.XPATH, "//li[@class='clear']/div[@class='info clear']/div[@class='title']/a[@class='VIEWDATA CLICKDATA maidian-detail']"))
            )
        except TimeoutException:
            print(f"页面加载超时: {url}")
            driver.quit()
            return

        # 随机等待时间，防止被封IP
        time.sleep(random.uniform(4, 10))

        # 解析页面
        data = driver.page_source
        html = etree.HTML(data)

        #---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        #二手房数据
        # 描述
        titles_ershoufang = html.xpath("//li[@class='clear']/div[@class='info clear']/div[@class='title']/a[@class='VIEWDATA CLICKDATA maidian-detail']/text()")
        # 位置
        locations_ershoufang= html.xpath("//li[@class='clear']/div[@class='info clear']/div[@class='address']/div[@class='flood']/div[@class='positionInfo']/a/text()")
        # 楼层
        floors_ershoufang= html.xpath("//ul[@class='sellListContent']/li[@class='clear']/div[@class='info clear']/div[@class='address']/div[@class='houseInfo']/text()")
        # 关注人数
        hot_ershoufang = html.xpath("//ul[@class='sellListContent']/li[@class='clear']/div[@class='info clear']/div[@class='address']/div[@class='followInfo']/text()")
        # 价格
        prices_ershoufang = html.xpath("//ul[@class='sellListContent']/li[@class='clear']/div[@class='info clear']/div[@class='address']/div[@class='priceInfo']/div[@class='totalPrice totalPrice2']/span/text()")
        #单价
        unit_prices_ershoufang = html.xpath("//ul[@class='sellListContent']/li[@class='clear']/div[@class='info clear']/div[@class='address']/div[@class='priceInfo']/div[@class='unitPrice']/span/text()")

        # 数据清洗
        # 页面有的文字做了换行处理，空格也很多，处理以后，文字便于观看，
        floors_ershoufang_list = [floor.strip().replace('\n', '').replace('  ', '') for floor in floors_ershoufang]
        # 过滤空字符
        floors_ershoufang_list_filter = [item for item in floors_ershoufang_list if item]

        hot_ershoufang_list = [h.strip().replace('\n', '').replace('  ', '') for h in hot_ershoufang]
        # 过滤空字符
        hot_ershoufang_list_filter = [item for item in hot_ershoufang_list if item]

        # 页面的‘万’字与文本分开的，难得处理，直接加‘万’字
        prices_ershoufang_list = [price.strip().replace('\n', '').replace('  ', '') + '万' for price in
                                  prices_ershoufang]

        unit_prices_ershoufang_list = [unit_price.strip().replace('\n', '').replace('  ', '') for unit_price in
                                       unit_prices_ershoufang]

    print(titles_ershoufang)
    print(locations_ershoufang)
    print(floors_ershoufang_list_filter)
    print(hot_ershoufang_list_filter)
    print(prices_ershoufang_list)
    print(unit_prices_ershoufang_list)


    #----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    # 写入CSV文件

    # with open(f'{city_input}市二手房源信息.csv', mode='a', encoding='utf-8', newline='') as file:
    #     writer = csv.writer(file)
    #     for i in range(len(titles_ershoufang)):
    #          writer.writerow([titles_ershoufang[i], locations_ershoufang[i], floors_ershoufang_list_filter[i], hot_ershoufang_list_filter[i], prices_ershoufang_list[i], unit_prices_ershoufang_list[i]])

    # 连接 MySQL 数据库
    connection = pymysql.connect(
        host='localhost',
        user='root',
        password='root',
        database='二手房源信息',
        charset='utf8mb4',
        cursorclass=pymysql.cursors.DictCursor
    )

    try:
        with connection.cursor() as cursor:
            # 以输入的城市名作为表名
            table_name = f"{city_input}市二手房源信息"

            # 创建表
            create_table_sql = f"""
            CREATE TABLE IF NOT EXISTS {table_name} (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255),
                location VARCHAR(255),
                floor VARCHAR(255),
                hot VARCHAR(255),
                price VARCHAR(255),
                unit_price VARCHAR(255)
            )
            """
            cursor.execute(create_table_sql)

            # 将爬到的数据插入表中
            for i in range(len(titles_ershoufang)):
                insert_sql = f"""
                INSERT INTO {table_name} (title, location, floor, hot, price, unit_price)
                VALUES (%s, %s, %s, %s, %s, %s)
                """
                cursor.execute(insert_sql, (
                    titles_ershoufang[i],
                    locations_ershoufang[i],
                    floors_ershoufang_list_filter[i],
                    hot_ershoufang_list_filter[i],
                    prices_ershoufang_list[i],
                    unit_prices_ershoufang_list[i]
                ))
        # 提交对数据库的更改
        connection.commit()
    finally:
        # 关闭数据库连接
        connection.close()
#关闭浏览器
    driver.quit()

def main():
    print("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------")
    print("欢迎使用贝壳二手房信息爬虫程序！这边建议您一次爬取1-20页数据，分批次爬取，防止数据缺失。")
    city_input = input('请输入城市名（例如泸州，不要市字）：')

    while city_input not in CITY_DICT:
        print("当前城市没有房源信息！")
        city_input = input('请重新输入城市名（例如泸州，不要市字）：')

    start_page = int(input("请输入起始页码："))
    end_page = int(input("请输入结束页码："))
    print("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------")

    #对用户代理处理
    user_agents = USER_AGENTS * ((end_page - start_page) // len(USER_AGENTS) + 1)
    threads = []

    # 创建线程，每个线程打开世间间隔在3，10秒随机
    for page, user_agent in zip(range(start_page, end_page + 1), user_agents):
        time.sleep(random.uniform(4, 10))
        thread = threading.Thread(target=data, args=(page, user_agent, city_input))
        thread.start()
        threads.append(thread)

    for thread in threads:
        thread.join()


if __name__ == "__main__":
    main()
