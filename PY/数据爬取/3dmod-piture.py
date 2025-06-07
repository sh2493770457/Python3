import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties

# 读取CSV文件
df = pd.read_csv('model.csv')

# 设置字体
font = FontProperties(fname=r"C:\Windows\Fonts\msyh.ttc", size=12) 

# 设置图形大小
plt.figure(figsize=(18, 12))

# 绘制柱状图
plt.subplot(2, 3, 1)
plt.bar(df['排名'], df['点赞量'], color='blue', alpha=0.7, label='点赞量')
plt.bar(df['排名'], df['购买量'], color='orange', alpha=0.7, label='购买量')
plt.xlabel('排名', fontproperties=font)
plt.ylabel('数量', fontproperties=font)
plt.title('模型点赞量和购买量比较', fontproperties=font)
plt.xticks(rotation=45, ha='right', fontproperties=font)


# 绘制散点图
plt.subplot(2, 3, 2)
plt.scatter(df['使用量'], df['得分'], color='green', alpha=0.7, label='散点图')
plt.xlabel('使用量', fontproperties=font)
plt.ylabel('得分', fontproperties=font)
plt.title('使用量和得分关系', fontproperties=font)


# 绘制折线图
plt.subplot(2, 3, 3)
plt.plot(df['排名'], df['收藏量'], marker='o', linestyle='-', color='red', label='收藏量')
plt.xlabel('排名', fontproperties=font)
plt.ylabel('数量', fontproperties=font)
plt.title('模型收藏量趋势', fontproperties=font)
plt.xticks(rotation=45, ha='right', fontproperties=font)


# 绘制直方图
plt.subplot(2, 3, 5)
# 直方图展示的数据有：点赞量、购买量、使用量、得分、收藏量
plt.hist([df['点赞量'], df['购买量'], df['使用量'], df['得分'], df['收藏量']], bins=10, label=['点赞量', '购买量', '使用量', '得分', '收藏量'])
# 设置x轴标签
plt.xlabel('数量', fontproperties=font)
# 设置y轴标签
plt.ylabel('频数', fontproperties=font)
# 设置标题
plt.title('模型指标分布直方图', fontproperties=font)


# 显示图形
plt.tight_layout()
plt.show()


