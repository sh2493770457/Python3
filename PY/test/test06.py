import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from matplotlib.font_manager import FontProperties

# 设置字体
font_path = r"C:\Windows\Fonts\msyh.ttc"
font = FontProperties(fname=font_path, size=12)

# 读取CSV文件
df = pd.read_csv('音乐热歌榜单.csv')

# 使用排名替代歌手名字
df['歌手'] = df['排名'].astype(str)

# 歌手分布分析
singer_distribution = df['歌手'].value_counts()
print("歌手分布情况：")
print(singer_distribution)

# 歌曲时长分析
df['时长_seconds'] = pd.to_datetime(df['时长'], format='%M:%S').dt.minute * 60 + pd.to_datetime(df['时长'], format='%M:%S').dt.second
average_duration = df['时长_seconds'].mean()
min_duration = df['时长_seconds'].min()
max_duration = df['时长_seconds'].max()
print("\n歌曲时长分析：")
print(f"平均歌曲时长: {average_duration:.2f}秒")
print(f"最短歌曲时长: {min_duration}秒")
print(f"最长歌曲时长: {max_duration}秒")

# 热门歌曲分析
top_songs = df[['歌名', '歌手', '排名']].sort_values(by='排名').head(5)
print("\n热门歌曲：")
print(top_songs)

# 排名与时长关系分析
plt.figure(figsize=(10, 6))
sns.scatterplot(x='排名', y='时长_seconds', data=df)
plt.title('排名与歌曲时长关系', fontproperties=font)
plt.xlabel('排名', fontproperties=font)
plt.ylabel('歌曲时长（秒）', fontproperties=font)
plt.show()

# 不同歌手平均时长分析
average_duration_by_singer = df.groupby('歌手')['时长_seconds'].mean().sort_values()

# 歌曲时长分布直方图
plt.figure(figsize=(10, 6))
sns.histplot(df['时长_seconds'], bins=20, kde=True)
plt.title('歌曲时长分布', fontproperties=font)
plt.xlabel('歌曲时长（秒）', fontproperties=font)
plt.ylabel('数量', fontproperties=font)
plt.show()

# 热门歌曲时长分析
top_songs_duration = df[df['歌名'].isin(top_songs['歌名'])]['时长_seconds']
print("\n热门歌曲时长分析：")
print(top_songs_duration.describe())

# 歌手平均时长分析
plt.figure(figsize=(12, 6))
sns.barplot(x='歌手', y='时长_seconds', data=df, ci=None)
plt.title('不同歌手平均歌曲时长', fontproperties=font)
plt.xlabel('歌手', fontproperties=font)
plt.ylabel('平均歌曲时长（秒）', fontproperties=font)
plt.xticks(rotation=45, ha='right')
plt.show()

# 歌曲时长分布箱线图
plt.figure(figsize=(10, 6))
sns.boxplot(x='时长_seconds', data=df)
plt.title('歌曲时长分布箱线图', fontproperties=font)
plt.xlabel('歌曲时长（秒）', fontproperties=font)
plt.show()

# 歌曲时长与排名关系热力图
plt.figure(figsize=(12, 8))
heatmap_data = df.pivot_table(values='时长_seconds', index='歌手', columns='排名', aggfunc='mean')
sns.heatmap(heatmap_data, cmap="YlGnBu", annot=True, fmt=".0f", linewidths=.5)
plt.title('歌曲时长与排名关系热力图', fontproperties=font)
plt.xlabel('排名', fontproperties=font)
plt.ylabel('歌手', fontproperties=font)
plt.show()
