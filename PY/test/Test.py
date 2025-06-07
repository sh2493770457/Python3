import requests
import csv

f = open('中国气象局天气.csv',mode='w',newline='',encoding='utf-8-sig')
w_header = csv.DictWriter(f,fieldnames=
 ['date','city','country','weather','max_temperature','min_temperature','Wind','Wind_level'])
w_header.writeheader()


url = 'https://weather.cma.cn/api/map/weather/1?t=1675244104697'
res = requests.get(url=url)
text = res.json()

weather = {}

weather['date'] = text['data']['date']
city = text['data']['city']
# print(city)
for i in city:
    # 城市
    weather['city'] = i[1]
    # 国家
    weather['country'] = i[2]
    # 天气
    weather['weather'] = i[7]
    # 最高温
    weather['max_temperature'] = i[6]
    # 最低温
    weather['min_temperature'] = i[11]
    # 风向
    weather['Wind'] = i[9]
    # 风级
    weather['Wind_level'] = i[10]
    # print(weather)
    w_header.writerow(weather)
    print(f'天气-{weather["city"]}-已保存!')