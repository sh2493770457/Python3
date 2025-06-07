import pandas as pd

data = pd.read_excel('AnTutu_Top.xlsx')
data = data.iloc[:, 1:]
data = data.to_string(index=False)
