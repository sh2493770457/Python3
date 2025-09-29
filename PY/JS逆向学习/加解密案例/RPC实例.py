# -*- encoding: utf-8 -*-
# TODO:@ModuleName: http_encrypt_call.py
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/06/25 14:00
import json
import urllib.parse
import requests


def call_encrypt_via_http(url, groups, Action, param_list):
    """
    通过调用encrypt返回加密后的数据
    :param url:
    :param groups:
    :param Action:
    :param param_list:
    :return:resp.json()
    """
    param_json = json.dumps(param_list)
    # TODO: URL编码param参数
    param_encoded = urllib.parse.quote(param_json)
    url = f"{url}?group={groups}&action={Action}&param={param_encoded}"
    resp = requests.get(url)
    resp.raise_for_status()
    return resp.json()


if __name__ == "__main__":
    base_url = "http://127.0.0.1:12080/go"
    group = "zzz"
    action = "encrypt"
    phone = "17718147716"
    verify_code = '1234'
    totel_param = [phone, "dxsq", "emss", "123"]
    toTel = call_encrypt_via_http(base_url, group, action, totel_param)

    check_code_param = [verify_code + phone, "dxsq", "emss", "123"]
    check_code = call_encrypt_via_http(base_url, group, action, check_code_param)
    # print(f"{toTel['data']}")
    # print(f"{check_code['data']}")

    final_data = {"toTel": toTel["data"], "fromTel": "10001", "beginDate": "",
                  "id": 999, "params": "", "sentType": "SUB", "endDate": "", "flag": 1, "token": "",
                  "inputCode": "1234", "checkCode":
                      check_code["data"]}
    print(json.dumps(final_data))

