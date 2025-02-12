import pandas as pd

# TODO:读取csv文件
df = pd.read_csv('property-data.csv')
print(df.to_string())

# TODO:读取PID列
PID = df['PID']
# TODO:将PID列中的空值填充为100005000.0
PID = PID.fillna(100005000.0)
# print(PID.to_string())

# TODO:读取ST_NUM列
ST_NUM = df['ST_NUM']
# TODO:将ST_NUM列中的空值填充为平均值
ST_NUM = ST_NUM.fillna(ST_NUM.mean().round(1))
# print(ST_NUM.to_string())

# TODO:读取OWN_OCCUPIED列
OWN_OCCUPIED = df['OWN_OCCUPIED']
# TODO:将OWN_OCCUPIED列中的值不是Y或N的值填充为-
OWN_OCCUPIED = OWN_OCCUPIED.mask(~OWN_OCCUPIED.isin(['Y', 'N']), '-')
# print(OWN_OCCUPIED.to_string())

"""
# TODO:也可以用lambda函数
OWN_OCCUPIED = df['OWN_OCCUPIED']
replace_lambda = lambda x: '-' if x not in ['Y', 'N'] else x
OWN_OCCUPIED = OWN_OCCUPIED.apply(replace_lambda)
print(OWN_OCCUPIED.to_string())
"""

# TODO:读取NUM_BEDROOMS列
NUM_BEDROOMS = df['NUM_BEDROOMS']
# TODO:计算NUM_BEDROOMS列的平均值
NUM_BEDROOMS_value = pd.to_numeric(NUM_BEDROOMS, errors='coerce').mean().round(1)
# TODO:将NUM_BEDROOMS列中非数字值替换成平均值
NUM_BEDROOMS = pd.to_numeric(NUM_BEDROOMS, errors='coerce').fillna(NUM_BEDROOMS_value)
# print(NUM_BEDROOMS.to_string())

# TODO:读取NUM_BATH列
NUM_BATH = df['NUM_BATH']
# TODO:计算NUM_BATH列的平均值
NUM_BATH_value = pd.to_numeric(NUM_BATH, errors='coerce').mean().round(1)
# TODO:将NUM_BATH列中非数字值替换成平均值
NUM_BATH = pd.to_numeric(NUM_BATH, errors='coerce').fillna(NUM_BATH_value)
# print(NUM_BATH.to_string())

# TODO:读取SQ_FT列
SQ_FT = df['SQ_FT']
# TODO:计算SQ_FT列的平均值,保留一位小数
SQ_FT_mean = pd.to_numeric(SQ_FT, errors='coerce').mean().round(1)
# TODO:将SQ_FT列中非数字值替换成平均值
SQ_FT = pd.to_numeric(SQ_FT, errors='coerce').fillna(SQ_FT_mean)
# print(SQ_FT.to_string())

# TODO:将处理后的数据保存到新的csv文件中
df = pd.DataFrame({'PID': PID, 'ST_NUM': ST_NUM, 'OWN_OCCUPIED': OWN_OCCUPIED,
                   'NUM_BEDROOMS': NUM_BEDROOMS, 'NUM_BATH': NUM_BATH, 'SQ_FT': SQ_FT})
df.to_csv('property-data-clean.csv', index=False)