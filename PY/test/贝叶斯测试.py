import pandas as pd
from sklearn.linear_model import LinearRegression
import warnings


#读取数据
df = pd.read_csv("统计建模问题.csv")

df['年份'] = df['时间'].str.extract(r'(\d+)').astype(int)
for col in df.columns[1:]:
    df[col] = df[col].astype(float)

#创建线性回归模型
model = LinearRegression()

# 数据拟合
X = df[['年份']]
y_columns = df.columns[1:-2]
predictions = {}

for y_col in y_columns:
    y = df[y_col]
    model.fit(X, y)
    predictions[y_col] = model.predict([[2024]])[0]

# 输出预测结果
for key, value in predictions.items():
    print(f"预测的 {key} 为: {value:.2f}")
