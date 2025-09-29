import time
import requests
import base64

total_sum = 0
for page in range(1, 21):
    cookies = {
        'sessionid': 'ub2p253ntgy23lyqfl2pkm50jrwkrmyi',
        '_nano_fp': 'XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj',
        'v': 'QS1SVnhZbzdYOVFkMGFSRFF5eEFGbDhidGVuVGZRa0x5cUtjSmY0RWNfZHlnSXJYSm8zWWR4cXhiS2ROMTc1OTA0NzEyOTI1NQ==',
        'Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0': '1758727507,1759024851,1759028808,1759061175',
        'HMACCOUNT': '5ADB897338667860',
        'Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0': '1759061216',
    }
    timestamp = int(time.time() * 1000)
    m = 'luoge' + str(timestamp)
    m = base64.b64encode(m.encode()).decode()
    headers = {
        'accept': 'application/json, text/javascript, */*; q=0.01',
        'accept-language': 'zh-CN,zh;q=0.9',
        'cache-control': 'no-cache',
        'client-version': '1.0.0',
        'm': m,
        'pragma': 'no-cache',
        'priority': 'u=1, i',
        'referer': 'https://www.mashangpa.com/problem-detail/18/',
        'sec-ch-ua': '"Chromium";v="140", "Not=A?Brand";v="24", "Google Chrome";v="140"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
        'sec-fetch-dest': 'empty',
        'sec-fetch-mode': 'cors',
        'sec-fetch-site': 'same-origin',
        'timestamp': '1759061460057',
        'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',
        'x-requested-with': 'XMLHttpRequest',
        # 'cookie': 'sessionid=ub2p253ntgy23lyqfl2pkm50jrwkrmyi; _nano_fp=XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj; v=QS1SVnhZbzdYOVFkMGFSRFF5eEFGbDhidGVuVGZRa0x5cUtjSmY0RWNfZHlnSXJYSm8zWWR4cXhiS2ROMTc1OTA0NzEyOTI1NQ==; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1758727507,1759024851,1759028808,1759061175; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1759061216',
    }

    params = {
        'page': page,
    }

    response = requests.get('https://www.mashangpa.com/api/problem-detail/18/data/', params=params, cookies=cookies,
                            headers=headers)
    current_array = response.json()['current_array']
    # TODO: 对列表内容求和
    num_sum = sum(current_array)
    print(f"第{page}页的累加和为:", num_sum)
    print(response.text.encode('utf-8').decode('unicode_escape'), end='\n\n')
    total_sum += num_sum
    time.sleep(0.1)
print("\n最终20页累加和为:", total_sum)
