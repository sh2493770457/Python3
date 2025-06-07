import pandas as pd
import matplotlib.pyplot as plt

# 设置中文字体和禁用Unicode减号
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

# 读取CSV文件到pandas DataFrame
df = pd.read_csv('AnTutu_Top.csv')

# 将得分列转换为数值类型（去掉' 分'并转换为整数）
score_columns = ['CPU', 'GPU', 'MEM', 'UX', 'Score']
for col in score_columns:
    df[col] = pd.to_numeric(df[col].str.replace(' 分', ''), errors='coerce')

# 1. 总体分布情况分析
df[score_columns].describe()

# 2. 处理器性能分析
avg_processor_scores = df.groupby('Processor')[score_columns].mean()
top_processors = avg_processor_scores.mean(axis=1).sort_values(ascending=False).head(10)
print("排名前十的处理器平均得分：\n", top_processors)

# 3. 各项得分之间的关系
correlation_matrix = df[score_columns].corr()
print("得分之间的相关系数矩阵：\n", correlation_matrix)

# 4. 分数与排名的关系
plt.plot(df['Top'], df['Score'], marker='o', linestyle='-', color='b')
plt.title('手机排名与跑分关系')
plt.xlabel('手机排名')
plt.ylabel('跑分')
plt.show()

# 5. 处理器厂商分析
processor_manufacturer_counts = df['Processor'].apply(lambda x: x.split()[0]).value_counts()
print("处理器厂商份额：\n", processor_manufacturer_counts)

# 6. 时间趋势分析
# （假设存在时间信息，此处以手机排名为时间趋势示例）
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

# 7. 各项得分的平均值分析
avg_scores = df[score_columns].mean()
print("各项得分的平均值：\n", avg_scores)

# 8. 特定处理器性能分析
specific_processor = 'S-8'
specific_processor_scores = df[df['Processor'].str.contains(specific_processor)][score_columns]
print(f"{specific_processor} 处理器的得分情况：\n", specific_processor_scores)

# 9. 地区分析（假设有地区信息）
# （此处省略，需要有地区信息列）

# 10. 用户评价与跑分的关系
# （假设有用户评价信息列）
# （此处省略，需要有用户评价信息列）

# 打印完整DataFrame以查看数据结构
print("\n完整的DataFrame：\n", df)
