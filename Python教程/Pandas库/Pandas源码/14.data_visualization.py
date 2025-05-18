import pandas as pd
import matplotlib.pyplot as plt

# 设置中文字体
plt.rcParams['font.sans-serif'] = ['SimHei']

# 数据读取
df = pd.read_excel('AnTutu_Top.xlsx')
print(df.to_string(index=False))

# 绘制排名TOP和跑分Score折线图
plt.plot(df['Top'], df['CPU'], marker='o', linestyle='-', color='b', label='CPU')
plt.plot(df['Top'], df['GPU'], marker='o', linestyle='-', color='g', label='GPU')
plt.plot(df['Top'], df['MEM'], marker='o', linestyle='-', color='r', label='MEM')
plt.plot(df['Top'], df['UX'], marker='o', linestyle='-', color='c', label='UX')
plt.plot(df['Top'], df['Score'], marker='o', linestyle='-', color='m', label='Score')
plt.title('手机排名与得分关系')
plt.xlabel('手机排名')
plt.ylabel('得分')
# 显示网格
plt.grid(True)
plt.legend()
plt.xticks(range(1, 121, 10))  # 设置横坐标刻度，每隔10个显示一个数字
# 保存图像
plt.savefig('手机排名与得分关系.png')

# 对前10名手机各方面进行分析可视化
top_10_df = df.head(10)

# 绘制前10名手机的各方面得分柱状图
top_10_df.set_index('Top')[['CPU', 'GPU', 'MEM', 'UX', 'Score']].plot(kind='bar', figsize=(12, 8))

# 修改x轴标签为手机名称
plt.xticks(ticks=range(10), labels=top_10_df['Name'], rotation=45)  # 旋转x轴标签以便显示

plt.title('前10名手机各方面得分分析')
plt.xlabel('手机名称')
plt.ylabel('得分')
plt.grid(True)
plt.legend(title="得分项")
plt.tight_layout()  # 调整布局
plt.savefig('前10名手机各方面得分分析.png')
