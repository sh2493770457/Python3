import json
import os
import pyquery
import re

url = 'https://www.zhipin.com/salaryxc/'
url1 = 'https://www.zhipin.com/salaryxc/c101290100_p100202.html'

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36',
    'Cookie': ''
}

html = pyquery.PyQuery(url=url1, headers=headers)

os.mkdir('json') if not os.path.exists('json') else None


def save(urls, name):
    with open(f"json\ {name}.json", 'a', encoding='utf-8') as f:
        json.dump(urls, f, ensure_ascii=False, indent=4)


city_codes = html('.legend-wrap li')
patten = r'{code:(\d+),name:\"([^\"]+)\",.*?}'
professions = re.findall(patten, html.html())

for city in city_codes.items():
    href = city('a').attr('href')
    city_code = href.split('/')[2].split('_')[0]
    city_name = city.text()
    l = []
    for p in professions:
        if len(p[0]) > 4:
            profession_code = 'p' + p[0]
            profession_name = p[1].replace("\\u002F", "/")
            urls = {
                "city": city_name,
                "profession": profession_name,
                "url": f"https://www.zhipin.com/salaryxc/{city_code}_{profession_code}.html"
            }
            l.append(urls)

    save(l, city_name)
