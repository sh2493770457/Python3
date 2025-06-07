import pandas as pd

'''
data = pd.read_excel('AnTutu_Top_clean.xlsx')
# print(data)

# 去掉CPU列里的' 分'
data['CPU'] = data['CPU'].str.replace(' 分', '', regex=False)
# 将CPU列转换为数值类型
data['CPU'] = pd.to_numeric(data['CPU'], errors='coerce')

# 去掉GPU列里的' 分'
data['GPU'] = data['GPU'].str.replace(' 分', '', regex=False)
# 将GPU列转换为数值类型
data['GPU'] = pd.to_numeric(data['GPU'], errors='coerce')

# 去掉MEM列里的' 分'
data['MEM'] = data['MEM'].str.replace(' 分', '', regex=False)
# 将MEM列转换为数值类型
data['MEM'] = pd.to_numeric(data['MEM'], errors='coerce')

# 去掉UX列里的' 分'
data['UX'] = data['UX'].str.replace(' 分', '', regex=False)
# 将UX列转换为数值类型
data['UX'] = pd.to_numeric(data['UX'], errors='coerce')

# 去掉Score列里的' 分'
data['Score'] = data['Score'].str.replace(' 分', '', regex=False)
# 将Score列转换为数值类型
data['Score'] = pd.to_numeric(data['Score'], errors='coerce')

print(data)
'''

# TODO:通过观察我们发现,这样写,功能其实都是去掉' 分',但是代码冗余,所以使用函数来操作

"""
data = pd.read_excel('AnTutu_Top_clean.xlsx')


# 统一清理数据的函数
def clean_data(df, column):
    df[column] = df[column].str.replace(' 分', '', regex=False)
    df[column] = pd.to_numeric(df[column], errors='coerce')
    return df  # 返回修改后的 DataFrame


# 定义需要清洗的列
require_columns = ['CPU', 'GPU', 'MEM', 'UX', 'Score']
for require_column in require_columns:
    data = clean_data(data, require_column)

print(data)
"""


# TODO:上面函数写好了,有强迫症的就想把所有代码全部写入一个函数中,最终直接返回一个data,这个也是可以的
def process_data(file_path, columns):
    """读取数据，清理指定列，并返回处理后的 DataFrame"""
    data = pd.read_excel(file_path)

    def clean_data(df, column):
        """对指定的列去掉' 分'并转换为数值类型"""
        df[column] = df[column].str.replace(' 分', '', regex=False)
        df[column] = pd.to_numeric(df[column], errors='coerce')
        return df

    for require_columns in columns:
        data = clean_data(data, require_columns)
    return data


def analysis_data(data):
    """数据分析,求平均值,最大值,最小值,标准差"""
    analysis_result = {}

    # 只对数值类型的列进行分析
    numeric_columns = data.select_dtypes(include='number').columns

    for column in numeric_columns:
        analysis_result[column] = {
            'mean': data[column].mean(),
            'max': data[column].max(),
            'min': data[column].min(),
            'std': data[column].std()
        }

    return analysis_result


if __name__ == '__main__':
    # 数据处理
    program = process_data('AnTutu_Top_clean.xlsx', ['CPU', 'GPU', 'MEM', 'UX', 'Score'])
    print(program)
    program.to_excel('AnTutu_Top.xlsx', index=False)

    # 数据分析
    analysis = analysis_data(program)
    df_analysis = pd.DataFrame(analysis, index=['mean', 'max', 'min', 'std'])
    print(df_analysis)

