import requests
import csv
import fake_useragent
from lxml import etree

f = open('home.csv', mode='w', newline='', encoding='utf-8-sig')
writer = csv.DictWriter(f, fieldnames=['小区', '室厅', '面积', '朝向', '装修情况', '总价', '单价', '建造年份'])
writer.writeheader()


def extract_number(text):
    try:
        # 去除逗号和其他非数字字符
        return float(''.join(filter(str.isdigit, text)))
    except ValueError:
        return None


def extract_info(info_text):
    parts = info_text.split('|')
    return {
        '室厅': parts[0].strip(),
        '面积': extract_number(parts[1]),
        '朝向': parts[2].strip(),
        '装修情况': parts[3].strip(),
        '建造年份': extract_number(parts[5])
    }


for i in range(1, 3):
    ua = fake_useragent.UserAgent()
    url = "https://bj.lianjia.com/ershoufang/pg{}/".format(i)
    header = {"User-Agent": ua.random}
    r = requests.get(url=url, headers=header).text
    html = etree.HTML(r)
    data = html.xpath('//ul[@class="sellListContent"]/li[@class="clear LOGVIEWDATA LOGCLICKDATA"]')

    for item in data:
        d = {}
        try:
            d['小区'] = item.xpath('.//a[@data-el="region"]/text()')[0].strip()

            # 提取并分割 '简介' 信息
            intro_text = item.xpath('.//div[@class="houseInfo"]/text()')[0].strip()
            intro_info = extract_info(intro_text)

            d.update(intro_info)

            # 修正总价和单价的提取逻辑
            d['总价'] = extract_number(item.xpath('.//div[@class="totalPrice totalPrice2"]/span/text()')[0]) * 10000
            d['单价'] = extract_number(item.xpath('.//div[@class="unitPrice"]/span/text()')[0])

            writer.writerow(d)
        except Exception as e:
            print(f"提取数据时出现错误: {e}")

f.close()
