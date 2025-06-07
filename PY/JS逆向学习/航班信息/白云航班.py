import requests
import execjs

# 使用到的技术：js逆向，加密算法，请求头处理（对时间戳、签名、nonce），补充环境
word = int(input('请输入想要获取的页数：'))
for page in range(1, word + 1):
    # 可根据需求修改
    json_data = {
        'type': '1',
        'terminal': '',
        'day': 0,
        'depOrArr': '1',
        'pageNum': page,
        'pageSize': 15,
    }
    # 生成逆向值
    js_code = execjs.compile(open('航班信息.js').read())
    ret = js_code.call('get_headers', json_data)

    headers = {
        'Accept': 'application/json, text/plain, */*',
        'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6',
        'Cache-Control': 'no-cache',
        'Connection': 'keep-alive',
        'Content-Type': 'application/json;charset=UTF-8',
        'Cookie': 'HMACCOUNT=026FEFC488BA9D75; Hm_lpvt_0effb2f651854e064f7d24a159e08bd5=1720368428; Hm_lpvt_38ddcda5baa19a6a899f6f3f7471381a=1720368428; Hm_lpvt_483eff6efca2ca9bff48af354895a36f=1720368428; Hm_lpvt_783519365e6df848bd882229527a15bc=1720368428; Hm_lpvt_e9e5e5ffd5a25d3f4d7e1466807ef7b7=1720368428; Hm_lvt_0effb2f651854e064f7d24a159e08bd5=1720368428; Hm_lvt_38ddcda5baa19a6a899f6f3f7471381a=1720368428; Hm_lvt_483eff6efca2ca9bff48af354895a36f=1720368428; Hm_lvt_783519365e6df848bd882229527a15bc=1720368428; Hm_lvt_e9e5e5ffd5a25d3f4d7e1466807ef7b7=1720368428; JSESSIONID=_wYLmvGMnycri3Ukdx8h_0FtAnIqJW7tuDKq_HZ3; Hm_lvt_483eff6efca2ca9bff48af354895a36f=1720368428; HMACCOUNT=026FEFC488BA9D75; Hm_lvt_0effb2f651854e064f7d24a159e08bd5=1720368428; Hm_lvt_e9e5e5ffd5a25d3f4d7e1466807ef7b7=1720368428; Hm_lvt_783519365e6df848bd882229527a15bc=1720368428; Hm_lvt_38ddcda5baa19a6a899f6f3f7471381a=1720368428; JSESSIONID=gGuhQEJree3EmonSv7iRgKJF4ogglpLeaRKExoNc; Hm_lpvt_38ddcda5baa19a6a899f6f3f7471381a=1720368450; Hm_lpvt_0effb2f651854e064f7d24a159e08bd5=1720368450; Hm_lpvt_e9e5e5ffd5a25d3f4d7e1466807ef7b7=1720368450; Hm_lpvt_783519365e6df848bd882229527a15bc=1720368450; Hm_lpvt_483eff6efca2ca9bff48af354895a36f=1720368450',

        # TODO:改为逆向后得到的值
        # 'Nonce': 'fe70e690-e053-4e4b-a93f-478ced00f9a6',
        'Nonce': ret['nonce'],

        'Origin': 'https://www.gbiac.net',
        'Pragma': 'no-cache',
        'Referer': 'https://www.gbiac.net/flight/dep',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'same-origin',

        # TODO:改为逆向后得到的值
        # 'Signature': 'bWf+dXCx7/kfy394k1LZZeSm1+RLtpkeWAtfv3clIxfR4cXeU3V8cA6fX8wLnluAYrQtqnGvVeYfDNZqe+WgnZqvZuoTBpGlLYRzKqo6UAAKMBiVTqCiTkfnipw7sFRrwP1eQqckaj5Q/aruCsIOUC6JkYd5Nb7AFr31QckBtsRurR0fNungbY/htrVyeOjffNAAt1HDzTPBwn+kC6XJG1YVYkweIm5X/uHS679W+IRqZxl+dmmpCRKdb7Mz9Tq56BbK1KamUi9owkt0Oo3/yk9rwTL2r6AiEaVYBgJ1kUNMrbSi9z4dm2JP+8kg/Y/qQtNG+Xc7tYrPTurYg3hxoA==',
        'Signature': ret['signature'],

        # TODO:改为逆向后得到的值，注意时间戳为str
        # 'Timestamp': '1720368450549',
        'Timestamp': str(ret['timestamp']),

        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0',
        'locale': 'zh_CN',
        'sec-ch-ua': '"Not/A)Brand";v="8", "Chromium";v="126", "Microsoft Edge";v="126"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
    }
    response = requests.post('https://www.gbiac.net/byairport-flight/flight/list', headers=headers, json=json_data)
    print(response.text)

