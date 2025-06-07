import pandas as pd
import matplotlib.pyplot as plt

# 设置中文字体
plt.rcParams['font.sans-serif'] = ['SimHei']

# 读取CSV文件到pandas DataFrame
df = pd.read_csv('AnTutu_Top.csv')

# # 显示DataFrame的前几行以了解数据结构
# print("数据前5行:")
# print(df.head())

# 将得分列转换为数值类型（去掉' 分'并转换为整数）
score_columns = ['CPU', 'GPU', 'MEM', 'UX', 'Score']
for col in score_columns:
    df[col] = pd.to_numeric(df[col].str.replace(' 分', ''), errors='coerce')


# # 处理器分析
# processor_counts = df['Processor'].apply(lambda x: x.split()[0]).value_counts()
# print("处理器统计：\n", processor_counts)
#
# # 各项得分的平均值分析
# avg_scores = df[score_columns].mean()
# print("各项得分的平均值：\n", avg_scores)
#
# # 特定处理器性能分析
# specific_processor = 'S-8'
# specific_processor_scores = df[df['Processor'].str.contains(specific_processor)][score_columns]
# print(f"{specific_processor} 处理器的得分情况：\n", specific_processor_scores)
#
# # 处理器性能分析
# avg_processor_scores = df.groupby('Processor')[score_columns].mean()
# top_processors = avg_processor_scores.mean(axis=1).sort_values(ascending=False).head(10)
# print("排名前十的处理器平均得分：\n", top_processors)
#
# # 各项得分之间的关系
# correlation_matrix = df[score_columns].corr()
# print("得分之间的相关系数矩阵：\n", correlation_matrix)

# 可视化排名前处理器的条形图
top_processors = df['Processor'].value_counts().head(10)
top_processors.plot(kind='barh', color='skyblue')
plt.title('AnTuTu排名前十的处理器')
plt.xlabel('手机数量')
plt.ylabel('处理器')
plt.show()
plt.close()

# 绘制平均分数条形图
# 计算每个处理器类型的平均分数
avg_scores = df.groupby('Processor')[score_columns].mean()
avg_scores.plot(kind='bar', figsize=(12, 6), colormap='viridis')
plt.title('各处理器类型平均得分')
plt.xlabel('处理器类型')
plt.ylabel('平均得分')
plt.show()
plt.close()

# 分数与排名的关系
plt.plot(df['Top'], df['Score'], marker='o', linestyle='-', color='b')
plt.xticks(range(1, 121, 10))
plt.title('手机排名与跑分关系')
plt.xlabel('手机排名')
plt.ylabel('跑分')
plt.show()
plt.close()

# 手机排名与跑分分析
plt.plot(df['Top'], df['CPU'], marker='o', linestyle='-', color='b', label='CPU')
plt.plot(df['Top'], df['GPU'], marker='o', linestyle='-', color='g', label='GPU')
plt.plot(df['Top'], df['MEM'], marker='o', linestyle='-', color='r', label='MEM')
plt.plot(df['Top'], df['UX'], marker='o', linestyle='-', color='c', label='UX')
plt.title('手机排名与得分关系')
plt.xlabel('手机排名')
plt.ylabel('得分')
plt.legend()
plt.xticks(range(1, 121, 10))  # 设置横坐标刻度，每隔10个显示一个数字
plt.show()
plt.close()
