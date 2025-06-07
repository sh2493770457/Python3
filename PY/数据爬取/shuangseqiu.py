import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# 读取CSV文件
df = pd.read_csv('shuangseqiu.csv')

# 将“开奖时间”列转换为日期格式
df['开奖时间'] = pd.to_datetime(df['开奖时间'])

# 提取后两位作为新的期号
df['期号'] = df['期号'].apply(lambda x: int(x[:-1]))

# 将“总销售额”和“奖池”列转换为数值型
df['总销售额'] = pd.to_numeric(df['总销售额'].str.rstrip('元'), errors='coerce')
df['奖池'] = pd.to_numeric(df['奖池'].str.rstrip('元'), errors='coerce')

# 设置绘图风格
sns.set(style="whitegrid")

# 设置中文字体
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

# 绘制总销售额和奖池随时间变化的折线图
plt.figure(figsize=(12, 10))
plt.subplot(2, 2, 1)

# 处理绘图中的NaN值
df_cleaned = df.dropna(subset=['总销售额', '奖池'])
sns.lineplot(x='开奖时间', y='总销售额', data=df_cleaned, marker='o', label='总销售额')
sns.lineplot(x='开奖时间', y='奖池', data=df_cleaned, marker='o', label='奖池')
plt.title('总销售额和奖池随时间变化')
plt.xlabel('开奖时间')
plt.ylabel('金额（元）')
plt.legend()

# 设置纵坐标显示刻度
min_value_sales = df_cleaned['总销售额'].min()
max_value_sales = df_cleaned['总销售额'].max()
mid_value_sales = (max_value_sales + min_value_sales) / 2

yticks_values_sales = [min_value_sales, mid_value_sales, max_value_sales]
plt.yticks(yticks_values_sales)

# 绘制中奖注数随时间变化的折线图
plt.subplot(2, 2, 2)
sns.lineplot(x='开奖时间', y='中奖注数', data=df, marker='o', label='中奖注数')
plt.title('中奖注数随时间变化')
plt.xlabel('开奖时间')
plt.ylabel('中奖注数')
plt.legend()

# 绘制总销售额和奖池的柱状图
plt.subplot(2, 2, 3)
sns.barplot(x='期号', y='总销售额', data=df_cleaned, color='blue', label='总销售额')
plt.title('总销售额随期数变化')
plt.xlabel('期号')
plt.ylabel('总销售额（元）')
plt.xticks(rotation=45)
plt.legend()

# 绘制中奖注数的饼图
plt.subplot(2, 2, 4)
df_pie = df[['中奖注数']].sum()
labels = ['中奖注数', '未中奖注数']
plt.pie(df_pie, labels=labels, autopct='%1.1f%%', startangle=90, colors=['skyblue', 'lightcoral'])
plt.title('中奖注数占比')

plt.tight_layout(pad=3.0)

# 显示图形
plt.show()
