import execjs
import time
import requests

# TODO:生成时间戳
temp = round(time.time() * 1000)

cookies = {
    'qq_domain_video_guid_verify': '9a93a915cdd5f09f',
    '_qimei_uuid42': '18408122826100dd5f38f6744a81019c72aa7e4f64',
    '_qimei_fingerprint': '9114f62c86c2518d13892e1ed49b83df',
    '_qimei_q36': '',
    '_qimei_h38': '2db8c1775f38f6744a81019c0200000cf18408',
    'RK': 'Mw/Z2+fi17',
    'ptcz': '93e1ceb64ae737477667352083426c87ba05cd8c73c9724eba3f89dc1a7f1249',
    'eas_sid': '41N7R1k5f8k7M5j9N3A4u7l6P3',
    'LW_uid': 'k1P7Y1r5U8a7b6T2A821E6g3y9',
    'LW_sid': 'Y1b7S1Z8w7U8H7P3F5v4e3y1C9',
    'pgv_pvid': '7052159198',
    'fqm_pvqid': 'c73f16a9-94d2-4f9a-99c1-22692c6fddca',
    'ts_refer': 'www.baidu.com/link',
    'ts_uid': '427273887',
    'fqm_sessionid': '5711fb4b-4411-44a3-b594-7342621238ec',
    'pgv_info': 'ssid=s3589111690',
    '_qpsvr_localtk': '0.6864062856093633',
    'ptui_loginuin': '403411124',
    'login_type': '1',
    'psrf_qqaccess_token': '4F7EF764FA92D178357FB82E2AFE3177',
    'psrf_access_token_expiresAt': '1734605605',
    'psrf_musickey_createtime': '1726829605',
    'wxopenid': '',
    'psrf_qqopenid': 'FDBCD66A37B20B608EF1F6048C22702D',
    'qm_keyst': 'Q_H_L_63k3N_ESg9c-cTJyjzyR14h3IP0RsOFBvjPVkUfTtw_xs5R-dQRgfuqyTtWGF1t5dEwzN0IXRQBD-o-RoDlY6Xg',
    'tmeLoginType': '2',
    'wxunionid': '',
    'psrf_qqrefresh_token': '00446E9574D2C3E2B675581C2BCA0F59',
    'wxrefresh_token': '',
    'euin': '7eni7e65oK-P',
    'music_ignore_pskey': '202306271436Hn@vBj',
    'qqmusic_key': 'Q_H_L_63k3N_ESg9c-cTJyjzyR14h3IP0RsOFBvjPVkUfTtw_xs5R-dQRgfuqyTtWGF1t5dEwzN0IXRQBD-o-RoDlY6Xg',
    'psrf_qqunionid': 'F0BEC60816CFE96B66BC61F36AA1659A',
    'uin': '403411124',
    'ts_last': 'y.qq.com/n/ryqq/search',
}
# TODO:请求头部
headers = {
    'accept': 'application/json',
    'accept-language': 'zh-CN,zh;q=0.9',
    'cache-control': 'no-cache',
    'content-type': 'application/x-www-form-urlencoded',
    'origin': 'https://y.qq.com',
    'pragma': 'no-cache',
    'priority': 'u=1, i',
    'referer': 'https://y.qq.com/',
    'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
    'sec-fetch-dest': 'empty',
    'sec-fetch-mode': 'cors',
    'sec-fetch-site': 'same-site',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
}
# TODO:请求参数
data = '{"comm":{"cv":4747474,"ct":24,"format":"json","inCharset":"utf-8","outCharset":"utf-8","notice":0,"platform":"yqq.json","needNewCode":1,"uin":2493770457,"g_tk_new_20200303":1111088749,"g_tk":1111088749},"req_1":{"method":"DoSearchForQQMusicDesktop","module":"music.search.SearchCgiService","param":{"remoteplace":"txt.yqq.top","searchid":"63839698154544288","search_type":0,"query":"蓝心羽","page_num":1,"num_per_page":10}}}'

# TODO:使用UTF-8读取js文件
with open('demo2.js', 'r', encoding='utf-8') as f:
    js = execjs.compile(f.read())

params = {
    '_': temp,
    'sign': js.call('get_sign', data),
}
print(params)
response = requests.post('https://u6.y.qq.com/cgi-bin/musics.fcg', params=params, cookies=cookies, headers=headers, data=data.encode())
print(response.content.decode())
