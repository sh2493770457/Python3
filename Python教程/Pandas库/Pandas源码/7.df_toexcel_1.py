import pandas as pd

# 读取Excel文件
df = pd.read_excel('test.xlsx')
print(df, '\n')

# 打印工作表
sheet = pd.ExcelFile('test.xlsx')
print(sheet.sheet_names, '\n')

# 打印工作表内容
print(sheet.parse('工作表 1'), '\n')

# 读取指定表单的内容（表单名称）
df = pd.read_excel('test.xlsx', sheet_name='Sheet1')
print(df, '\n')

# 读取多个表单，返回一个字典
dfs = pd.read_excel('test.xlsx', sheet_name=['Sheet1', 'Sheet2'])
print(dfs, '\n')

# 自定义列名并跳过前两行
df = pd.read_excel('test.xlsx', header=None, names=['A', 'B', 'C'], skiprows=2)
print(df, '\n')

# 创建一个简单的 DataFrame
df = pd.DataFrame({
    'Name': ['Alice', 'Bob', 'Charlie'],
    'Age': [25, 30, 35],
    'City': ['New York', 'Los Angeles', 'Chicago']
})

# 将 DataFrame 写入 Excel 文件，写入 'Sheet1' 表单
df.to_excel('output.xlsx', sheet_name='Sheet1', index=False)

# 写入多个表单，使用 ExcelWriter
with pd.ExcelWriter('output.xlsx') as f:
    df.to_excel(f, sheet_name='Sheet1', index=False)
    df.to_excel(f, sheet_name='Sheet2', index=False)


