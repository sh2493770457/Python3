# # -*- encoding: utf-8 -*-
# # TODO:@ModuleName: match
# # TODO:@Author: tomato
# # TODO:@Version: Python3.12.0
# # TODO:@Time: 2025/8/5 17:52
#
# """
# 匹配包含特定sm_text值的HTTP请求块
# :param sm_text_list: 指定要匹配的关键词列表（如 "87", "88"）
# :return: 控制台打印包含sm_text的完整HTTP请求内容
# """
#
# import re
#
# with open("1.txt", "r", encoding="utf-8") as f:
#     content = f.read()
#
# # TODO: 匹配完整HTTP请求块（两个换行分隔）
# requests = re.split(r'\r?\n\r?\n', content)
#
# # TODO: 提取包含"sm_text":""
# sm_text_list = ['87', '104', '101', '93', '102', '90', '91', '89', '88', '92', '103']
#
# for sm_text in sm_text_list:
#     matching_requests = [req for req in requests if f'"sm_text":"{sm_text}' in req]
#
#     # TODO: 输出结果
#     for req in matching_requests:
#         print(req)

# with open("1.txt", "r", encoding="utf-8") as f:
#     lines = f.read()
#
# pattern = lines.split('\n')
# matching_requests = [req for req in pattern if '"sm_text"' in req]
# sms_text_list = ['3', '5', '7', '33']
# for sms_text in sms_text_list:
#     match = [i for i in matching_requests if f'"sm_text":"{sms_text}"' in i]
#     for j in match:
#         print(j)


import re

# TODO: 匹配
match_1 = re.match(r'\d+', '7758258asd')
print(match_1.group())  # 7758258

# TODO: 搜索
match_2 = re.search(r'\d+', '7758258asd')
print(match_2.group())  # 7758258

# TODO: 提取所有
match_3 = re.findall(r'\d+', '7758258asd')
print(match_3[0])  # 7758258

# TODO: 迭代匹配
for m in re.finditer(r'\d+', '7758258asd'):
    print(m.group(), m.start(), m.end())  # 7758258 0 7
    print(m.group()[0:7])  # 7758258

# TODO: 分割
parts = re.split(r'[,;.]', '7.7,58.25;8as;d')
print(parts)  # ['7', '7', '58', '25', '8as', 'd']

# TODO: 替换
new = re.sub(r'\d', 'asd', '7758258asd')
print(new)  # asdasdasdasdasdasdasdasd

# TODO: 分组与引用
match_4 = re.search(r'(\d+)-(\d+)-(\d+)', '028-8750-666')
print(match_4.group(1))  # 028
print(match_4.group(2))  # 8750
print(match_4.group(3))  # 666
print(match_4.groups())  # ('028', '8750', '666')

# TODO: 命名分组
match_5 = re.search(r'(?P<first>\d+)-(?P<second>\d+)-(?P<third>\d+)', '028-8750-666')
print(match_5.group('first'))  # 028
print(match_5.group('second'))  # 8750
print(match_5.group('third'))  # 666

# TODO: 替换分组
replace_example = re.sub(r'(\d+)-(\d+)-(\d+)', r'\3-\2-\1', '028-8750-666')
print(replace_example)  # 666-8750-028

# TODO: 编译优化
pattern = re.compile(r'(\d+)-(\d+)-(\d+)')
pattern.findall("asdasd-666-8750-028-dsddsddddddddddddd")  # 666-8750-028
