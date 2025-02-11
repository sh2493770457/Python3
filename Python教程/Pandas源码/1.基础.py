import pandas as pd

# TODO:数据分别对应第一列第二列
# data = {
#         'name': ['sunhua', 'lihua'],
#         'age': ['18', '20']
#         }
# df = pd.DataFrame(data)
# print(df)
# df.to_excel('1.xlsx', index=False)

# TODO:创建两个series对象-->和上面等效
# serice_name = pd.Series(['黄诗扶', '蓝心羽'])
# serice_age = pd.Series(['17', '18'])
# df = pd.DataFrame({'姓名': serice_name, '年龄': serice_age})
# print(df)

# TODO:这里使用name可以在创建DataFrame对象的时候自动为列名赋值
# serice = pd.Series([1, 2, 3, 4], name="A")
# df = pd.DataFrame(serice)
# """
# # 默认索引是从0开始,可以自定义
# print(df)
# df.to_excel("1.xlsx")
# """
# my_index=[4,5,6,7]
# my_index_data = pd.Series([1, 2, 3, 4], index=my_index, name='B')  # TODO:这时候索引就变成了my_index指定的了
# my_index_data_df = pd.DataFrame(my_index_data)
# print(my_index_data_df)

a = ["Google", "Runoob", "Wiki"]
myvar = pd.Series(a)
print(myvar[2])  # 这儿通过index获取值
print(myvar.iloc[-1])  # 这个ilco是通过位置获取值,等同列表的切片方法
