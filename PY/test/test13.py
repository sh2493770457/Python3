import pandas as pd

# 读取CSV文件
df = pd.read_csv('3、当当网.csv')

# 1. 处理缺失值
df = df.dropna()

# 2. 处理异常值，例如价格为负数的情况
df['价格'] = df['价格'].apply(lambda x: max(0, float(x.replace('¥', '').replace(',', ''))))

# 3. 格式转换，例如日期转换为日期类型
df['出版日期'] = pd.to_datetime(df['出版日期'], errors='coerce')

# 4. 去除重复数据
df = df.drop_duplicates()

# 5. 文本数据清洗，例如去除空格
df['书名'] = df['书名'].str.strip()

# 保存处理后的数据
df.to_csv('cleaned_dangdang_data.csv', index=False, encoding='utf-8-sig')
