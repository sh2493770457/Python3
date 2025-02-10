import pandas as pd
import numpy as np

# data = [
#     ['Google', 10],
#     ['Runoob', 12],
#     ['Wiki', 13]
# ]
# df = pd.DataFrame(data, columns=['Website', 'Age'])
#
# # TODO:使用astype设置每列的数据类型
# df['Website'] = df['Website'].astype(str)
# df['Age'] = df['Age'].astype(float)
# print(df)

"""
# TODO:使用字典创建,与上面等效->输出结果一致
dict_data = {
    'Website': ['Google', 'Runoob', 'Wiki'],
    'Age': [10, 12, 13]
}
df_data = pd.DataFrame(dict_data)
df_data['Website'] = df_data['Website'].astype(str)
df_data['Age'] = df_data['Age'].astype(float)
print(df_data)
"""

# np_data = np.array([
#     ['Google', 10],
#     ['Runoob', 12],
#     ['Wiki', 13]
# ])
#
# # TODO:使用DataFrame构造函数创建数据帧
# df_np = pd.DataFrame(np_data, columns=['Website', 'Age'])
# df_np['Website'] = df_np['Website'].astype(str)
# df_np['Age'] = df_np['Age'].astype(float)
# # print(df_np)
#
# print(df_np.iloc[0:2], '\n')  # TODO:使用iloc选择数据
# print(df_np.loc[0:1])  # TODO:使用loc选择数据

# # TODO:创建DataFrame
# data = {
#     '姓名': ['张三', '李四', '王五'],
#     '年龄': [23, 21, 32],
#     '性别': ['男', '女', '男'],
#     '城市': ['北京', '上海', '广州']
# }

# df = pd.DataFrame(data)
# print(df)

# # TODO:查看前两行数据
# print(df.head(2), '\n')
# # TODO:查看DataFrame的基本信息
# print(df.info(), '\n')
# # TODO:获取描述统计信息
# print(df.describe(), '\n')
# # TODO:按照年龄排序,ascending=True升序,ascending=False降序,默认升序->True
# print(df.sort_values(by='年龄', ascending=False), '\n')
# # TODO:选择姓名和城市列->使用[[value1,value2]]
# print(df[['姓名', '城市']], '\n')
# # TODO:按照索引选择2-3行
# print(df.iloc[1:3], '\n')
# # TODO:按照标签选择行
# print(df.loc[[1, 2]], '\n')
# # TODO:计算分组统计,按照性别分组,计算年龄的平均值
# print(df.groupby('性别')['年龄'].mean(), '\n')
# # TODO:处理缺失值,进行填充
# df['城市'] = df['城市'].fillna('未知')
# # TODO:导出为csv文件
# df.to_csv('data.csv', index=False)
