import pandas as pd

# 示例数据
name = pd.Series(['Alice', 'Bob', 'Charlie', 'David'])
age = pd.Series([25, 30, 35, 40])
salary = pd.Series([10000, 20200, 12222, 9999])

df = pd.DataFrame({
    'name': name,
    'age': age,
    'salary': salary
})
print()
print('原始数据:\n', df,'\n')

# TODO:使用to_numeric确保salary为数值
df['salary'] = pd.to_numeric(df['salary'])
# 按照salary进行降序
df_sorted = df.sort_values(by='salary', ascending=False)
df_final = df_sorted.to_string(index=False)
print('排序后的数据:\n', df_final)
