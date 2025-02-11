import pandas as pd
import json

# {
#             "cms": "致远OA",
#             "method": "keyword",
#             "location": "rule: body",
#             "keyword": [
#                 "/seeyon/USER-DATA/IMAGES/LOGIN/login.gif"
#             ]
#         }
#

# TODO:提取所有字段,cms,method,location,keyword分别作为列名,将json文件转化成excel保存


# 读取 JSON 文件
with open("finger.json", "r", encoding="utf-8") as f:
    data = json.load(f)

# 展开 `fingerprint` 数组中的每个元素
df = pd.json_normalize(data['fingerprint'])

# 将 DataFrame 保存为 Excel 文件
df.to_excel("fingerprint.xlsx", index=False)

# 打印输出确认
print("转换成功，已保存为 fingerprint.xlsx")
