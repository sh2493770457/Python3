import pandas as pd
from datetime import datetime, date

# 创建dataframe
df = pd.DataFrame({
    '姓名': ['张三', '李四', '王五', '赵六'],
    '年龄': [20, 25, 30, 35],
    '性别': ['男', '女', '男', '女'],
})

# 写入多个工作表
with pd.ExcelWriter('df_toexcel.xlsx') as writer:
    df.to_excel(writer, sheet_name='工作人员1', index=False)
    df.to_excel(writer, sheet_name='工作人员2', index=False)
# 使用ExcelFile加载文件
excel_file = pd.ExcelFile('df_toexcel.xlsx')
# 查看所有表单名称
print(excel_file.sheet_names)
# 读取`工作人员1`表单
print(excel_file.parse('工作人员1'))

