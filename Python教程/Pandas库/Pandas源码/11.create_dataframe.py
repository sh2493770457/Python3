import pandas as pd
import numpy as np

# TODO:使用3种方式创建dataframe对象
# 方法一:列表嵌套列表
data_list = [
    ['sunhua', 'boy', '24'],
    ['lxy', 'gril', '22'],
    ['lm', 'gril', '23']
]
df_list = pd.DataFrame(data_list, columns=['姓名', '性别', '年龄'])
print(df_list,'\n')

# 方法二:字典
data_dict = {
    '姓名': ['sunhua', 'lxy', 'lm'],
    '性别': ['boy', 'gril', 'gril'],
    '年龄': ['24', '22', '23']
}
df_dict = pd.DataFrame(data_dict)
print(df_dict, '\n')

# 方法三:serice创建
serice_name = pd.Series(['sunhua', 'lxy', 'lxy'], name='姓名')
serice_gender = pd.Series(['boy', 'gril', 'gril'], name='性别')
serice_age = pd.Series(['24', '22', '23'], name='年龄')
df_serice = pd.DataFrame({'姓名':serice_name, '性别':serice_gender, '年龄':serice_age})
print(df_serice,'\n')

# 方法四:使用numpy数组创建
data_np = np.array([['sunhua', 'boy', 24], ['lxy', 'gril', 22], ['lm', 'gril', 23]])
df_numpy = pd.DataFrame(data_np, columns=['姓名', '性别', '年龄'])
print(df_numpy,'\n')

# 方法五:使用zip函数创建
data = zip(['sunhua', 'lxy', 'lm'], ['boy', 'gril', 'gril'], [24, 22, 23])
df_zip = pd.DataFrame(data, columns=['姓名', '性别', '年龄'])
print(df_zip)
