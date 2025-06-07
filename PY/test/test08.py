import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# 1. 设置中文字体
plt.rcParams['font.sans-serif'] = ['SimHei']  # 使用中文黑体
plt.rcParams['axes.unicode_minus'] = False  # 解决负号显示问题

# 2. 加载数据集
df = pd.read_csv('酷狗TOP500.csv')

# 3. 查看数据集的基本信息
print("数据集的前几行:")
print(df.head())

print("\n描述性统计信息:")
print(df.describe())

print("\n数据集信息:")
print(df.info())

# 4. 数据清洗
df = df.dropna()
df['time_seconds'] = df['time'].apply(lambda x: int(x.split(':')[0]) * 60 + int(x.split(':')[1]))

# 5. 数据可视化
# 饼图：歌手分布
plt.figure(figsize=(10, 6))
df['singer'].value_counts().head(10).plot.pie(autopct='%1.1f%%')
plt.title('前10位歌手')
plt.show()

# 直方图：时长分布
plt.figure(figsize=(12, 6))
sns.histplot(df['time_seconds'], bins=20, kde=True)
plt.title('歌曲时长分布')
plt.xlabel('时长（秒）')
plt.ylabel('数量')
plt.show()

# 6. 数据分析
# 计算歌手的平均歌曲时长
avg_duration_by_singer = df.groupby('singer')['time_seconds'].mean().sort_values(ascending=False)

# 打印平均时长最长的前10位歌手
print("\n前10位平均时长最长的歌手:")
print(avg_duration_by_singer.head(10))

# 热力图：歌手与时长的关系
plt.figure(figsize=(12, 8))
heatmap_data = df.pivot_table(index='singer', columns='time_seconds', aggfunc='size', fill_value=0)
sns.heatmap(heatmap_data, cmap='YlGnBu', cbar_kws={'label': '数量'})
plt.title('歌手与时长的关系热力图')
plt.show()

# 获取歌手排名
top_singers = df['singer'].value_counts().index[:10]

# 绘制箱线图，按歌手排名进行排序
plt.figure(figsize=(16, 8))
sns.boxplot(x='singer', y='time_seconds', data=df[df['singer'].isin(top_singers)], order=top_singers, palette='Set3')
plt.title('歌手的歌曲时长分布')
plt.xlabel('歌手排名')
plt.ylabel('时长（秒）')
plt.xticks(rotation=45)
plt.show()
