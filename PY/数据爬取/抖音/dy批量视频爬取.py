import os

import requests

headers = {
    'accept': 'application/json, text/plain, */*',
    'accept-language': 'zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6',
    'cache-control': 'no-cache',
    'cookie': 'ttwid=1%7Cu999Jt5ma6NXowqJWk6xDRSD9UQFu9oELRrK7kcxLXA%7C1720128733%7C882854b2f751d377fd0e0a643437142394902d098b65f1dc53b7b677304ce732; UIFID_TEMP=c4683e1a43ffa6bc6852097c712d14b81f04bc9b5ca6d30214b0e66b4e38528068cf3ed5006787595f8125d6e732a224cad0cc15268783cce28423ef9edeac9df95372bd2856ba97d6affae2a441ece6347c86bb5a9eb4f66920adb9b0774947fb4ab17fd69659bf873bcf7d25369aa8; douyin.com; device_web_cpu_core=16; device_web_memory_size=8; architecture=amd64; dy_swidth=1440; dy_sheight=900; csrf_session_id=cb77c6272182ea232397ebeade13b721; strategyABtestKey=%221720128731.714%22; s_v_web_id=verify_ly7s8fzo_TIFvsTpX_PgpI_4Oad_9Jfy_42IWwaafAbWg; xgplayer_user_id=563125062684; passport_csrf_token=1a73ccb87c124427b98ce26c3e3bef94; passport_csrf_token_default=1a73ccb87c124427b98ce26c3e3bef94; FORCE_LOGIN=%7B%22videoConsumedRemainSeconds%22%3A180%7D; fpk1=U2FsdGVkX1/QDYxRXOqpHp1rfH7toucMUiGZUIu8ueEKPuppxOUrcN/vlk/TcLnqsqpIxvYZRNCDliw1lzPyuA==; fpk2=5f4591689f71924dbd1e95e47aec4ed7; xg_device_score=7.90435294117647; bd_ticket_guard_client_web_domain=2; __ac_nonce=0668714f500229d92a6e; __ac_signature=_02B4Z6wo00f016DW37AAAIDDl.Ib7ArUE9eg9tsAAI6765; SEARCH_RESULT_LIST_TYPE=%22single%22; x-web-secsdk-uid=5b8693a7-98e3-496d-b1e0-c1f2609a16af; volume_info=%7B%22isUserMute%22%3Afalse%2C%22isMute%22%3Afalse%2C%22volume%22%3A0.558%7D; UIFID=c4683e1a43ffa6bc6852097c712d14b81f04bc9b5ca6d30214b0e66b4e38528068cf3ed5006787595f8125d6e732a224cad0cc15268783cce28423ef9edeac9de04e98036d9b6fd79268e8e98df65643d9b495ed2e12e1a8853604a1dde36a83abeed548865e7f1725361ec7b7d31f28503edf8e00551f973c5a8658a057ab561b75be225a6fcded6886e2f9e7255ad7e70c80ec8c6d46fbee8f2f0ee515b6a3a81f7d974da140f5816dcc4f5e20b4096dd0c19473e5922e373473aa865eeace; biz_trace_id=fe094ac0; passport_assist_user=Cj2I_qxpThHS6PgxYFgY2qcIDD0GVgvAyaAKHGnfD174NlS5ed0hFA3_AJKmS0A4oP7CMm0Z6UNNYVyCRrSvGkoKPEQPRM9jd3MSlc6fhmi2r4Y7bsukklRWL3NRG3W_CbpOWbA33NuHFtkO4J3k8kC7qv9_3JdLVNvOTx5ZtRD15dUNGImv1lQgASIBA-rmK3Y%3D; n_mh=V2T3CBlTh1bwKAP1tMhgT9g7p30qFG0hRCZIDn_iv7E; sso_uid_tt=a550b5d7d3cecb318b754e36a9a47f94; sso_uid_tt_ss=a550b5d7d3cecb318b754e36a9a47f94; toutiao_sso_user=dd3316b690cb2592ad815a6131612bb1; toutiao_sso_user_ss=dd3316b690cb2592ad815a6131612bb1; sid_ucp_sso_v1=1.0.0-KDI5M2YxNGNhODdjMWNmYzYyZmZhN2Y1OGE1YzZmMDVkNDY5ZGViNjYKHwjKv9XHgQMQ-aqctAYY7zEgDDCBgfrbBTgGQPQHSAYaAmxmIiBkZDMzMTZiNjkwY2IyNTkyYWQ4MTVhNjEzMTYxMmJiMQ; ssid_ucp_sso_v1=1.0.0-KDI5M2YxNGNhODdjMWNmYzYyZmZhN2Y1OGE1YzZmMDVkNDY5ZGViNjYKHwjKv9XHgQMQ-aqctAYY7zEgDDCBgfrbBTgGQPQHSAYaAmxmIiBkZDMzMTZiNjkwY2IyNTkyYWQ4MTVhNjEzMTYxMmJiMQ; passport_auth_status=2a1710f49397d3ca1fcd6d37d7c892ec%2C; passport_auth_status_ss=2a1710f49397d3ca1fcd6d37d7c892ec%2C; uid_tt=6597eefc27f1df42c45263ef98ac19a6; uid_tt_ss=6597eefc27f1df42c45263ef98ac19a6; sid_tt=494dc414826126160f52a9afc75e0534; sessionid=494dc414826126160f52a9afc75e0534; sessionid_ss=494dc414826126160f52a9afc75e0534; publish_badge_show_info=%220%2C0%2C0%2C1720128878819%22; _bd_ticket_crypt_doamin=2; _bd_ticket_crypt_cookie=adc87c7f9e651c503cd3c54aa75df62e; __security_server_data_status=1; sid_guard=494dc414826126160f52a9afc75e0534%7C1720128894%7C5183998%7CMon%2C+02-Sep-2024+21%3A34%3A52+GMT; sid_ucp_v1=1.0.0-KGUwMmM4ODc1Mjk2MTdmMjNjYmYwNGE2YjcxNjEwODczNDUzZmJlZmMKGQjKv9XHgQMQ_qqctAYY7zEgDDgGQPQHSAQaAmhsIiA0OTRkYzQxNDgyNjEyNjE2MGY1MmE5YWZjNzVlMDUzNA; ssid_ucp_v1=1.0.0-KGUwMmM4ODc1Mjk2MTdmMjNjYmYwNGE2YjcxNjEwODczNDUzZmJlZmMKGQjKv9XHgQMQ_qqctAYY7zEgDDgGQPQHSAQaAmhsIiA0OTRkYzQxNDgyNjEyNjE2MGY1MmE5YWZjNzVlMDUzNA; stream_player_status_params=%22%7B%5C%22is_auto_play%5C%22%3A0%2C%5C%22is_full_screen%5C%22%3A0%2C%5C%22is_full_webscreen%5C%22%3A0%2C%5C%22is_mute%5C%22%3A1%2C%5C%22is_speed%5C%22%3A1%2C%5C%22is_visible%5C%22%3A0%7D%22; pwa2=%220%7C0%7C1%7C0%22; stream_recommend_feed_params=%22%7B%5C%22cookie_enabled%5C%22%3Atrue%2C%5C%22screen_width%5C%22%3A1440%2C%5C%22screen_height%5C%22%3A900%2C%5C%22browser_online%5C%22%3Atrue%2C%5C%22cpu_core_num%5C%22%3A16%2C%5C%22device_memory%5C%22%3A8%2C%5C%22downlink%5C%22%3A10%2C%5C%22effective_type%5C%22%3A%5C%224g%5C%22%2C%5C%22round_trip_time%5C%22%3A50%7D%22; FOLLOW_NUMBER_YELLOW_POINT_INFO=%22MS4wLjABAAAAfKHYxpQA-p5sJFbCoXbS4O5O9wLS6jVjp6TILDFo6A8%2F1720195200000%2F0%2F1720129004110%2F0%22; odin_tt=9ea81be20743a9e88b703cc6025ac3ac784ec1d60c8633147ad4b0703598ca36b228127e4fb7444787c8da006afe27d8; download_guide=%223%2F20240705%2F0%22; WallpaperGuide=%7B%22showTime%22%3A1720129650224%2C%22closeTime%22%3A0%2C%22showCount%22%3A1%2C%22cursor1%22%3A12%2C%22cursor2%22%3A0%7D; IsDouyinActive=true; home_can_add_dy_2_desktop=%220%22; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtcmVlLXB1YmxpYy1rZXkiOiJCSXdaekxTT3VTTnJWdUpuTGtNc3NCRk91VzA4MUd2TFZwT2plY3NWQWdmSng1ZHFxNWpsUlNXVUxXRzNvaG1UUWNHWXlmdjFnTTY4elBMWVJJWUJjc2c9IiwiYmQtdGlja2V0LWd1YXJkLXdlYi12ZXJzaW9uIjoxfQ%3D%3D; passport_fe_beating_status=true',
    'pragma': 'no-cache',
    'priority': 'u=1, i',
    'referer': 'https://www.douyin.com/user/MS4wLjABAAAAGGlljVJpNH62x8ZZIsk3BcAag0N3VxzSMe4DF1NTjLA',
    'sec-ch-ua': '"Not/A)Brand";v="8", "Chromium";v="126", "Microsoft Edge";v="126"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
    'sec-fetch-dest': 'empty',
    'sec-fetch-mode': 'cors',
    'sec-fetch-site': 'same-origin',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0',
}

params = {
    'device_platform': 'webapp',
    'aid': '6383',
    'channel': 'channel_pc_web',
    'sec_user_id': 'MS4wLjABAAAAGGlljVJpNH62x8ZZIsk3BcAag0N3VxzSMe4DF1NTjLA',
    'max_cursor': '0',
    'locate_query': 'false',
    'show_live_replay_strategy': '1',
    'need_time_list': '1',
    'time_list_query': '0',
    'whale_cut_token': '',
    'cut_version': '1',
    'count': '18',
    'publish_video_strategy_type': '2',
    'update_version_code': '170400',
    'pc_client_type': '1',
    'version_code': '290100',
    'version_name': '29.1.0',
    'cookie_enabled': 'true',
    'screen_width': '1440',
    'screen_height': '900',
    'browser_language': 'zh-CN',
    'browser_platform': 'Win32',
    'browser_name': 'Edge',
    'browser_version': '126.0.0.0',
    'browser_online': 'true',
    'engine_name': 'Blink',
    'engine_version': '126.0.0.0',
    'os_name': 'Windows',
    'os_version': '10',
    'cpu_core_num': '16',
    'device_memory': '8',
    'platform': 'PC',
    'downlink': '10',
    'effective_type': '4g',
    'round_trip_time': '50',
    'webid': '7387896494597064203',
    'msToken': '-998nC2-mhA4TkJApWBTxgFs1CSRjvE5wgj6ZLyFR6crNRboQdaAEi4aeSuNzdxndjkXgwl1vTqDysCib1vUoCo1rLGtRjv6lVg04JGmmVXuxBJcsTnQu618BrPsYw==',
    'a_bogus': 'Y7m0/DgXDk2pkf6k5lKLfY3q6WF3YDNg0trEMD2fhnVi4y39HMO59exLc/JvPJujNs/DIb6jy4hSYNNMic2bA3vX98DKl2Kh-g00t-P2so0j5ZkHejuDnUmF-vT-SaBp5vV3xcXmy7dtzuRplnAJ5k1cthMeavD=',
    'verifyFp': 'verify_ly7s8fzo_TIFvsTpX_PgpI_4Oad_9Jfy_42IWwaafAbWg',
    'fp': 'verify_ly7s8fzo_TIFvsTpX_PgpI_4Oad_9Jfy_42IWwaafAbWg',
}


response = requests.get('https://www.douyin.com/aweme/v1/web/aweme/post/', params=params, headers=headers)
print(response.text)
data_list = response.json().get('aweme_list')
for i in data_list:
    print(i.get('desc'), i.get('video').get('play_addr').get('url_list')[0])
os.makedirs('./蓝心羽lxy', exist_ok=True)
# 保存视频
for i in data_list:
    video_url = i.get('video').get('play_addr').get('url_list')[0]
    video_name = i.get('desc')
    response = requests.get(video_url, headers=headers,params=params)
    with open(f'./蓝心羽lxy/{video_name}.mp4', 'wb') as f:
        f.write(response.content)

    print(video_name, '下载完成!')
