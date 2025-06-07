# -*- encoding: utf-8 -*-
# TODO:@ModuleName: 某宝热卖 -->sign逆向
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/4/6 17:41

import requests
import time
import hashlib

# TODO: 生成时间戳
tamp = str(int(time.time() * 1000))
token = '6f20356c0db9ecd898b7b8bddede4ded'
cookies = {
    'miid': '4705674330921056984',
    'cookie2': '19ce66a91832bb654d8709c067d06934',
    't': '4bd5c5e044e3b9031e967726cb54f8be',
    '_tb_token_': 'ee69134e34eee',
    'tk_trace': 'oTRxOWSBNwn9evfHtXQifmafmYzSU80EW6dedixskcJ%2Bc8J0R27pVpwR009bxUw3pJYMDEOjj9VTZncJdZFFzzCuQZlxVoJefOJPwjGFrv%2BuFCu8mr671KETI4pENkh6cK%2FNrANRiFQXGDcIGcPCjY1O309wWComqAa2l2NG7R5EJ1q%2Fnv53xlVJ%2BUXewllUWKRQ0Tw4W6dWz4GjuNTlFa%2F9AQAMfuFPIUi1MTz4Y2L%2Baj%2BNABKhJKY7E5a9v2WvBY0SsuJPsSP0heLOoFWXNRSrYqlfsPD%2FNsIcMILEyqw84OAoxJeSgsu%2FFrfxxbhTpljY1XcoaDtVAo%2BuZD%2FOLpP9JrFo6dzLZ%2BVoMEsjd3x7LVnMI22cWKNO6cb2JlGdOtUaoMJvTwvc7%2FFWvRJxehVHrjdtHRlxuJdrvOTv03b9KSw%3D',
    'tkSid': '1743931789714_560361059_0.0',
    'mtop_partitioned_detect': '1',
    'thw': 'xx',
    '_m_h5_tk': '6f20356c0db9ecd898b7b8bddede4ded_1743941516977',
    '_m_h5_tk_enc': 'fa4dbc125c945329cccd57306b5d6521',
    'cna': 'kjd5IPH/TxsCAY0LK+vUQpGa',
    'xlly_s': '1',
    'hng': 'SG%7Czh-CN%7CSGD%7C702',
    '_uetsid': 'ec17188012c911f0b4164f66f17940cb',
    '_uetvid': 'ec19399012c911f08bc8851d5c850139',
    'isg': 'BJKSVjbWId-ztF2jxcCJZa5W41h0o5Y9Q0AShlzvwMTPbzppUTGpTRPJ3svTUg7V',
    'tfstk': 'gQLKFFDshADhpa8dsBogqDRcXflgwcAUWpRbrTX3Vdp9idGFqYR5Nap6EXWHNvJJyOOyxMbCEgBWnLCk-UYJbzd2aecFrX7eTa7Snx0cyBRFzLD6UaLL5V1k16aQAcbsdfHwax0moBiCTaAsnejmQtBOiz_5Ag115O5uOT95VPO1Q_57d_aI6fBNw8Z5O_a1C_54Pa_WFCGOaO1CPM9W1jAmXTrCnzK2uF_bk8nQJyLO9MB7r6aRpEUc2tOCBzw3b6sLjB6TPzT9jJJww9i4sT7hLQ5pUqzFRgKBp1_-yP_pLBT5XUDioMdfNL7H5bZChHvh5etYNzOOJ9sk8ieKBtTe1E71jqGv1FJHTFduN41MnOOeRa3jgwQCdNCwrAzCehtW-M7zdvQW6_syNEYvQswcH_qI6fEz4M1ZizBYrf7rIk1Onff34uStb1Bm6fEz4M1N6tcgKurPXc5..',
}

headers = {
    'accept': '*/*',
    'accept-language': 'zh-CN,zh;q=0.9',
    'cache-control': 'no-cache',
    'pragma': 'no-cache',
    'referer': 'https://uland.taobao.com/sem/tbsearch?localImgKey=&page=2&q=deepseek&tab=all',
    'sec-ch-ua': '"Chromium";v="134", "Not:A-Brand";v="24", "Google Chrome";v="134"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
    'sec-fetch-dest': 'script',
    'sec-fetch-mode': 'no-cors',
    'sec-fetch-site': 'same-site',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36',
    # 'cookie': 'miid=4705674330921056984; cookie2=19ce66a91832bb654d8709c067d06934; t=4bd5c5e044e3b9031e967726cb54f8be; _tb_token_=ee69134e34eee; tk_trace=oTRxOWSBNwn9evfHtXQifmafmYzSU80EW6dedixskcJ%2Bc8J0R27pVpwR009bxUw3pJYMDEOjj9VTZncJdZFFzzCuQZlxVoJefOJPwjGFrv%2BuFCu8mr671KETI4pENkh6cK%2FNrANRiFQXGDcIGcPCjY1O309wWComqAa2l2NG7R5EJ1q%2Fnv53xlVJ%2BUXewllUWKRQ0Tw4W6dWz4GjuNTlFa%2F9AQAMfuFPIUi1MTz4Y2L%2Baj%2BNABKhJKY7E5a9v2WvBY0SsuJPsSP0heLOoFWXNRSrYqlfsPD%2FNsIcMILEyqw84OAoxJeSgsu%2FFrfxxbhTpljY1XcoaDtVAo%2BuZD%2FOLpP9JrFo6dzLZ%2BVoMEsjd3x7LVnMI22cWKNO6cb2JlGdOtUaoMJvTwvc7%2FFWvRJxehVHrjdtHRlxuJdrvOTv03b9KSw%3D; tkSid=1743931789714_560361059_0.0; mtop_partitioned_detect=1; thw=xx; _m_h5_tk=6f20356c0db9ecd898b7b8bddede4ded_1743941516977; _m_h5_tk_enc=fa4dbc125c945329cccd57306b5d6521; cna=kjd5IPH/TxsCAY0LK+vUQpGa; xlly_s=1; hng=SG%7Czh-CN%7CSGD%7C702; _uetsid=ec17188012c911f0b4164f66f17940cb; _uetvid=ec19399012c911f08bc8851d5c850139; isg=BJKSVjbWId-ztF2jxcCJZa5W41h0o5Y9Q0AShlzvwMTPbzppUTGpTRPJ3svTUg7V; tfstk=gQLKFFDshADhpa8dsBogqDRcXflgwcAUWpRbrTX3Vdp9idGFqYR5Nap6EXWHNvJJyOOyxMbCEgBWnLCk-UYJbzd2aecFrX7eTa7Snx0cyBRFzLD6UaLL5V1k16aQAcbsdfHwax0moBiCTaAsnejmQtBOiz_5Ag115O5uOT95VPO1Q_57d_aI6fBNw8Z5O_a1C_54Pa_WFCGOaO1CPM9W1jAmXTrCnzK2uF_bk8nQJyLO9MB7r6aRpEUc2tOCBzw3b6sLjB6TPzT9jJJww9i4sT7hLQ5pUqzFRgKBp1_-yP_pLBT5XUDioMdfNL7H5bZChHvh5etYNzOOJ9sk8ieKBtTe1E71jqGv1FJHTFduN41MnOOeRa3jgwQCdNCwrAzCehtW-M7zdvQW6_syNEYvQswcH_qI6fEz4M1ZizBYrf7rIk1Onff34uStb1Bm6fEz4M1N6tcgKurPXc5..',
}

params = {
    'jsv': '2.7.2',
    'appKey': '12574478',
    't': tamp,
    'sign': 'ef56123699c2738e001b215ce5719104',
    'api': 'mtop.relationrecommend.wirelessrecommend.recommend',
    'v': '2.0',
    'type': 'jsonp',
    'dataType': 'jsonp',
    'callback': 'mtopjsonp13',
    'data': '{"appId":"43356","params":"{\\"device\\":\\"HMA-AL00\\",\\"isBeta\\":\\"false\\",\\"grayHair\\":\\"false\\",\\"from\\":\\"nt_history\\",\\"brand\\":\\"HUAWEI\\",\\"info\\":\\"wifi\\",\\"index\\":\\"4\\",\\"rainbow\\":\\"\\",\\"schemaType\\":\\"auction\\",\\"elderHome\\":\\"false\\",\\"isEnterSrpSearch\\":\\"true\\",\\"newSearch\\":\\"false\\",\\"network\\":\\"wifi\\",\\"subtype\\":\\"\\",\\"hasPreposeFilter\\":\\"false\\",\\"prepositionVersion\\":\\"v2\\",\\"client_os\\":\\"Android\\",\\"gpsEnabled\\":\\"false\\",\\"searchDoorFrom\\":\\"srp\\",\\"debug_rerankNewOpenCard\\":\\"false\\",\\"homePageVersion\\":\\"v7\\",\\"searchElderHomeOpen\\":\\"false\\",\\"search_action\\":\\"initiative\\",\\"sugg\\":\\"_4_1\\",\\"sversion\\":\\"13.6\\",\\"style\\":\\"list\\",\\"ttid\\":\\"600000@taobao_pc_10.7.0\\",\\"needTabs\\":\\"true\\",\\"areaCode\\":\\"SG\\",\\"vm\\":\\"nw\\",\\"countryNum\\":\\"702\\",\\"m\\":\\"pc_sem\\",\\"page\\":4,\\"n\\":48,\\"q\\":\\"deepseek\\",\\"qSource\\":\\"manual\\",\\"pageSource\\":\\"\\",\\"tab\\":\\"all\\",\\"pageSize\\":\\"48\\",\\"totalPage\\":\\"100\\",\\"totalResults\\":\\"5000\\",\\"sourceS\\":\\"48\\",\\"sort\\":\\"_coefp\\",\\"filterTag\\":\\"\\",\\"service\\":\\"\\",\\"prop\\":\\"\\",\\"loc\\":\\"\\",\\"start_price\\":null,\\"end_price\\":null,\\"startPrice\\":null,\\"endPrice\\":null,\\"p4pIds\\":\\"897064354283,864286849298,703472354910,903321520906,848805772951,845781587885,890870831165,657932504386,906239321548,795053877812,771181296558,887717607415,859061092057,799144123508,896224053421,893595724275,891531845667,891878551558,894131599474,889563836435,892188587695,899150419473,900308664917,896224997182,896597160165,892507558023,890149863179,837899848716,899516963242,891738390008,894035080835,889972167882,898463149398,892221839421,895233939118,840771689807,895161649384,896924804279,898945265555,894768526242,618758646400,900563554669,701908943900,782856713317,893218406933,765779182962,708039190429,904008118314,902655801891,892165906121,872415733475,836477630526,906660432211,903594444548,690812402524,850963530571,897562460647,896230135969,891612588216,897669113915,896528917152,892632076540,898530740358,888478884100,901610953665,886148460505,897921510647,770715166966,899068717554\\",\\"categoryp\\":\\"\\",\\"myCNA\\":\\"kjd5IPH/TxsCAY0LK+vUQpGa\\"}"}',
}
# TODO: 拼接->r.token + "&" + u + "&" + s + "&" + n.data
content = token + "&" + tamp + "&" + params['appKey'] + "&" + params['data']

# TODO: 创建md5对象
md5_encrypt = hashlib.md5()
md5_encrypt.update(content.encode('utf-8'))
sign = md5_encrypt.hexdigest()
params['sign'] = sign

response = requests.get(
    'https://h5api.m.taobao.com/h5/mtop.relationrecommend.wirelessrecommend.recommend/2.0/',
    params=params,
    cookies=cookies,
    headers=headers,
)
print(response.text)
