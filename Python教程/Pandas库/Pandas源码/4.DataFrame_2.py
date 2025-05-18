import pandas as pd
import numpy as np

# 通过列表嵌套列表创建DataFrame->行
df_list = pd.DataFrame(
    [
        [1, 2, 3, 4],
        [5, 6, 7, 8],
        [9, 10, 11, 12]
    ],
    columns=['column1', 'column2', 'column3', 'column4']
)
print(df_list, '\n')

# 通过字典创建DataFrame->列
df_dict = pd.DataFrame(
    {
        'column1': [1, 2, 3, 4],
        'column2': [5, 6, 7, 8],
        'column3': [9, 10, 11, 12]
    }
)
print(df_dict, '\n')

# 通过numpy数组创建
df_np = pd.DataFrame(np.array(
    [
        [1, 2, 3],
        [4, 5, 6]
    ]
),
    columns=['column1', 'column2', 'column3'], index=[1, 2]
)
print(df_np, '\n')

# 通过Serice创建DataFrame
df_serice1 = pd.Series([1, 2, 3, 4])
df_serice2 = pd.Series([5, 6, 7, 8])
df_serice3 = pd.Series([9, 10, 11, 12])
df_serice = pd.DataFrame({'column1': df_serice1, 'column2': df_serice2, 'column3': df_serice3})
print(df_serice, '\n')

# TODO:DataFrame的属性和方法
print('形状:', df_serice.shape, '\n')
print('列名:', df_serice.columns, '\n')
print('索引:', df_serice.index, '\n')
print('前5行数据:', df_serice.head(), '\n')
print('后5行数据:', df_serice.tail(), '\n')
print('数据信息:', df_serice.info(), '\n')
print('数据统计:', df_serice.describe(), '\n')

# 通过列名访问元素
print(df_serice['column1'], '\n')
# 通过属性访问元素
print(df_serice.column1, '\n')
# 通过.iloc[]访问
print(df_serice.iloc[:, 0], '\n')
# 通过loc[]访问
print(df_serice.loc[:, 'column1'], '\n')
# 访问单个元素
print(df_serice['column1'][0], '\n')
# 通过行标签访问
print(df_serice.loc[:, 'column1'], '\n')

# 修改数据
df_serice['column1'] = [6, 6, 6, 6]
# 添加新列,并赋值
df_serice['column5'] = [13, 14, 15, 16]
print(df_serice, '\n')

# 使用loc为特定索引添加新行
df_serice.loc[4] = [17, 18, 19, 20]
print(df_serice, '\n')

# 在末尾添加多行数据
new_row = {
    'column8': [21, 22, 23, 24],
    'column9': [25, 26, 27, 28]
}
df_new = pd.DataFrame(new_row)

# 使用 pd.concat()
df = pd.concat([df_serice, df_new], ignore_index=True)
print(df, '\n')