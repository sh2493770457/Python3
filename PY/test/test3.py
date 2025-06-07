import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# 读取CSV文件到DataFrame
file_path = "中国气象局天气.csv"
weather_data = pd.read_csv(file_path)

# 设置中文字体
plt.rcParams['font.sans-serif'] = ['SimHei']

# 可视化最高温度和最低温度的分布
plt.figure(figsize=(12, 6))

# 最高温度直方图
plt.subplot(1, 2, 1)
sns.histplot(weather_data["max_temperature"], bins=20, kde=True, color='blue')
plt.title('最高温度分布')

# 最低温度直方图
plt.subplot(1, 2, 2)
sns.histplot(weather_data["min_temperature"], bins=20, kde=True, color='orange')
plt.title('最低温度分布')

plt.tight_layout()
plt.show()

# 天气条件对应的最高温度和最低温度条形图
plt.figure(figsize=(12, 6))
sns.barplot(x='max_temperature', y='weather', data=weather_data, palette='viridis')
plt.title('不同天气条件下的平均最高温度')
plt.show()

plt.figure(figsize=(12, 6))
sns.barplot(x='min_temperature', y='weather', data=weather_data, palette='plasma')
plt.title('不同天气条件下的平均最低温度')
plt.show()
