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

# 创建子图
fig, axes = plt.subplots(nrows=3, ncols=2, figsize=(16, 18))

# 绘制总销售额和奖池随期数变化的折线图
sns.lineplot(x='期号', y='总销售额', data=df, marker='o', label='总销售额', ax=axes[0, 0])
sns.lineplot(x='期号', y='奖池', data=df, marker='o', label='奖池', ax=axes[0, 0])
axes[0, 0].set_title('总销售额和奖池随期数变化')
axes[0, 0].set_xlabel('期号')
axes[0, 0].set_ylabel('金额（元）')
axes[0, 0].legend()

# 绘制一等奖注数和金额的趋势
sns.lineplot(x='期号', y='一等奖-注数', data=df, marker='o', label='一等奖注数', ax=axes[0, 1])
sns.lineplot(x='期号', y='一等奖-金额', data=df, marker='o', label='一等奖金额', ax=axes[0, 1])
axes[0, 1].set_title('一等奖注数和金额随期数变化')
axes[0, 1].set_xlabel('期号')
axes[0, 1].set_ylabel('数量')
axes[0, 1].legend()

# 绘制中奖注数的趋势
sns.lineplot(x='期号', y='二等奖-注数', data=df, marker='o', label='中奖注数', ax=axes[1, 0])
axes[1, 0].set_title('中奖注数随期数变化')
axes[1, 0].set_xlabel('期号')
axes[1, 0].set_ylabel('数量')
axes[1, 0].legend()

# 绘制总销售额和奖池随时间变化的折线图
sns.lineplot(x='开奖时间', y='总销售额', data=df, marker='o', label='总销售额', ax=axes[1, 1])
sns.lineplot(x='开奖时间', y='奖池', data=df, marker='o', label='奖池', ax=axes[1, 1])
axes[1, 1].set_title('总销售额和奖池随时间变化')
axes[1, 1].set_xlabel('开奖时间')
axes[1, 1].set_ylabel('金额（元）')
axes[1, 1].legend()

# 总销售额和奖池随期数变化的柱状图
sns.barplot(x='期号', y='总销售额', data=df, color='blue', ax=axes[2, 0])
sns.barplot(x='期号', y='奖池', data=df, color='orange', ax=axes[2, 0])
axes[2, 0].set_title('总销售额和奖池随期数变化')
axes[2, 0].set_xlabel('期号')
axes[2, 0].set_ylabel('金额（元）')

# 一等奖注数和金额随期数变化的柱状图
sns.barplot(x='期号', y='一等奖-注数', data=df, color='green', ax=axes[2, 1])
sns.barplot(x='期号', y='一等奖-金额', data=df, color='red', ax=axes[2, 1])
axes[2, 1].set_title('一等奖注数和金额随期数变化')
axes[2, 1].set_xlabel('期号')
axes[2, 1].set_ylabel('数量')

# 调整子图布局
plt.tight_layout()

# 显示图形
plt.show()
