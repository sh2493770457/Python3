from sklearn.linear_model import LinearRegression
import numpy as np
import pandas as pd
# 假设你的数据是这样的
X = np.array([168, 210, 375, 584]) # 输入横坐标
Y = np.array([321, 487, 620, 768]) # 输入纵坐标

# 将数据转换为合适的形状
X = X.reshape(-1, 1)
Y = Y.reshape(-1, 1)

# 创建并训练模型
model = LinearRegression()
model.fit(X, Y)

# 打印模型参数
print("斜率（权重）：", model.coef_)
print("截距：", model.intercept_)

# 预测
X_test = np.array([168, 210, 375, 584]).reshape(-1, 1) # 预测的横坐标
Y_pred = model.predict(X_test)

print("预测的纵坐标：", Y_pred) # 打印预测的纵坐标
