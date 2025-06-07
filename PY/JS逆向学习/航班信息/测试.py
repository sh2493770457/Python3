import csv
import requests
import execjs
import json

word = int(input('请输入想要获取的页数：'))
field_names = [
    "航班ID", "航空公司中文", "航空公司英文", "航空公司韩文", "航空公司法文",
    "航班号", "航班日期", "出发城市代码", "出发城市英文", "出发城市中文", "出发城市韩文", "出发城市法文",
    "到达城市", "出发或到达", "国内或国际", "航班任务",
    "航班状态英文", "航班状态中文", "航班状态韩文", "航班状态法文",
    "航站楼", "登机口", "计划起飞时间", "预计起飞时间", "计划到达时间",
]

csv_file = 'flights_info.csv'

# 写入表头
with open(csv_file, 'a', newline='', encoding='utf-8') as csvfile:
    writer = csv.DictWriter(csvfile, fieldnames=field_names)
    writer.writeheader()

# 请求数据并写入CSV文件
with open(csv_file, 'a', newline='', encoding='utf-8') as csvfile:
    writer = csv.DictWriter(csvfile, fieldnames=field_names)
    for page in range(1, word + 1):
        json_data = {
            'type': '1',
            'terminal': '',
            'day': 0,
            'depOrArr': '1',
            'pageNum': page,
            'pageSize': 15,
        }

        js_code = execjs.compile(open('航班信息.js').read())
        ret = js_code.call('get_headers', json_data)

        headers = {
            'Accept': 'application/json, text/plain, */*',
            'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6',
            'Cache-Control': 'no-cache',
            'Connection': 'keep-alive',
            'Content-Type': 'application/json;charset=UTF-8',
            'Cookie': 'your_cookie_here',
            'Nonce': ret['nonce'],
            'Origin': 'https://www.gbiac.net',
            'Pragma': 'no-cache',
            'Referer': 'https://www.gbiac.net/flight/dep',
            'Sec-Fetch-Dest': 'empty',
            'Sec-Fetch-Mode': 'cors',
            'Sec-Fetch-Site': 'same-origin',
            'Signature': ret['signature'],
            'Timestamp': str(ret['timestamp']),
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0',
            'locale': 'zh_CN',
            'sec-ch-ua': '"Not/A)Brand";v="8", "Chromium";v="126", "Microsoft Edge";v="126"',
            'sec-ch-ua-mobile': '?0',
            'sec-ch-ua-platform': '"Windows"',
        }

        response = requests.post('https://www.gbiac.net/byairport-flight/flight/list', headers=headers, json=json_data)
        data = response.text
        json_data = json.loads(data)

        flights_info = []
        for flight_info in json_data['data']['list']:
            flight_data = {
                "航班ID": flight_info.get('flightId', ''),
                "航空公司中文": flight_info.get('airlineCn', ''),
                "航空公司英文": flight_info.get('airlineEn', ''),
                "航空公司韩文": flight_info.get('airlineKr', ''),
                "航空公司法文": flight_info.get('airlineFr', ''),
                "航班号": flight_info.get('flightNo', ''),
                "航班日期": flight_info.get('flightDate', ''),
                "出发城市代码": flight_info.get('orgCity', ''),
                "出发城市英文": flight_info.get('orgCityEn', ''),
                "出发城市中文": flight_info.get('orgCityCn', ''),
                "出发城市韩文": flight_info.get('orgCityKr', ''),
                "出发城市法文": flight_info.get('orgCityFr', ''),
                "到达城市": flight_info.get('dstCity', ''),
                "出发或到达": flight_info.get('depOrArr', ''),
                "国内或国际": flight_info.get('domesticOrIntl', ''),
                "航班任务": flight_info.get('flightTask', ''),
                "航班状态英文": flight_info.get('flightStatusEn', ''),
                "航班状态中文": flight_info.get('flightStatusCn', ''),
                "航班状态韩文": flight_info.get('flightStatusKr', ''),
                "航班状态法文": flight_info.get('flightStatusFr', ''),
                "航站楼": flight_info.get('terminal', ''),
                "登机口": flight_info.get('boardingGate', ''),
                "计划起飞时间": flight_info.get('setoffTimePlan', ''),
                "预计起飞时间": flight_info.get('setoffTimePred', ''),
                "计划到达时间": flight_info.get('arriTimePlan', ''),
            }
            flights_info.append(flight_data)

            # 打印航班信息
            print(f"Flight {len(flights_info)}:")
            for key, value in flight_data.items():
                print(f"  {key}: {value}")

        writer.writerows(flights_info)
