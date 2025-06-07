import pandas as pd
import matplotlib.pyplot as plt

# 设置字体为 SimHei
plt.rcParams['font.sans-serif'] = ['SimHei']

# 读取数据
df = pd.read_csv('新发地菜价.csv')

# 计算每种水果的平均价格
average_prices = df.groupby('名称')['平均价'].mean()

# 找出价格最高和最低的水果
max_price_fruit = df.loc[df['最高价'].idxmax()]['名称']
min_price_fruit = df.loc[df['最低价'].idxmin()]['名称']

# 创建一个柱状图来比较不同水果的平均价格
plt.figure(figsize=(10, 6))
average_prices.plot(kind='bar', color='skyblue', edgecolor='black')
plt.title('水果的平均价格', fontsize=16)
plt.xlabel('水果', fontsize=14)
plt.ylabel('平均价', fontsize=14)
plt.xticks(rotation=90)
plt.grid(True)
plt.show()

# 创建一个箱线图来展示价格的分布情况
plt.figure(figsize=(10, 6))
df[['最低价', '平均价', '最高价']].plot(kind='box', patch_artist=True)
plt.title('价格分布', fontsize=16)
plt.ylabel('价格', fontsize=14)
plt.grid(True)
plt.show()

# 创建一个折线图来比较不同水果的平均价格
plt.figure(figsize=(10, 6))
average_prices.plot(kind='line', color='skyblue')
plt.title('水果的平均价格', fontsize=16)
plt.xlabel('水果', fontsize=14)
plt.ylabel('平均价', fontsize=14)
plt.xticks(rotation=90)
plt.grid(True)
plt.show()

# 创建一个散点图来展示最高价和最低价的关系
plt.figure(figsize=(10, 6))
plt.scatter(df['最低价'], df['最高价'], alpha=0.5)
plt.title('最高价和最低价的关系', fontsize=16)
plt.xlabel('最低价', fontsize=14)
plt.ylabel('最高价', fontsize=14)
plt.grid(True)
plt.show()