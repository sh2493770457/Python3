import pandas as pd

# 通过列表嵌套列表创建DataFrame->行
df = pd.DataFrame(
    [
        [1, 2, 3, 4],
        [5, 6, 7, 8],
        [9, 10, 11, 12]
    ],
    columns=['column1', 'column2', 'column3', 'column4']
)
print(df, '\n')


