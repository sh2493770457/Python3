#打开当前目录的test.txt文件
# filename='test.txt'
# try:
#     fp=open(filename,'w+')
#     print('{}文件创建成功'.format(filename))
# except IOError:
#     print('{}文件创建失败'.format(filename))
# finally:
#     fp.close()

# 创建一个名为test.txt的文件，如果文件不存在则创建，如果文件存在则清空文件内容
# filename='test.txt'
# try:
#     fp=open(filename,'r')
#     print('{}文件创建成功'.format(filename))
# except IOError:
#     print('{}文件创建失败'.format(filename))
# finally:
#     fp.close()

# with open("job.txt", "r", encoding="utf-8") as f:
#     for line in f:
#         print(line)

# import pandas as pd
# # 加载Excel文件
# df = pd.read_excel("三位数字化爱好者协会社团成员信息登记表.xlsx")
# # 打印整个数据框架
# print(df)
# # 如果你只想逐行打印，可以这样迭代行：
# for index, row in df.iterrows():
#     print(row)

#处理excel
# import openpyxl
# def read_excel(file_name):
#     # 打开工作簿
#     workbook = openpyxl.load_workbook(file_name)
#     # 获取第一个sheet
#     sheet = workbook.active
#     # 获取表格的名称、行数、列数
#     print("Excel表的名称：{}，行数：{}，列数：{}".format(sheet.title, sheet.max_row, sheet.max_column))
#     # 打印出所有合并的单元格的初始内容
#     # 获取所有单元格内容
#     excel_list = []
#     for row in sheet.iter_rows(values_only=True):
#         excel_list.append(row)
#     # 输出所有单元格内容
#     for row in excel_list:
#         for cell in row:
#             # 判断单元格是否为空
#             if cell is not None:
#                 print(cell, end=" ")
#             else:
#                 print("", end=" ")  # 如果单元格为空，则打印一个空格
#         print()
# read_excel("三位数字化爱好者协会社团成员信息登记表.xlsx")

#创建一个顶层窗口
# from tkinter import *
# root=Tk()
# root.title("顶层窗口")
# root.mainloop() # 进入消息循环，否则消息一闪而过

#案例 设计第一个窗口
# import tkinter
# root=tkinter.Tk() # 创建一个顶层窗口
# label1=tkinter.Label(root,text="第一个界面示例") # 创建一个标签
# label1.pack() # 显示标签
# button1=tkinter.Button(root,text="按钮1") # 创建一个按钮
# button1.pack(side=tkinter.LEFT) # 显示按钮
# button2=tkinter.Button(root,text="按钮2") # 创建一个按钮
# button2.pack(side=tkinter.RIGHT) # 显示按钮
# root.mainloop() # 进入消息循环，否则消息一闪而过

# 用Label编写一个文本显示的程序，在主程序中显示“设计标签组件”
# import tkinter as tk
# root=tk.Tk()
# root.title("设计标签组件")
# #定义标签并设计样式
# label1=tk.Label(root,anchor="nw",
#                 bg="#eef",  # 背景色
#                 fg="red",    # 前景色
#                 text="这是一个标签", # 文本
#                 font=("隶书",20)  # 字体
#                 ,width=20,  # 宽度
#                 height=2)   # 高度
# label1.pack()
# root.mainloop()

#演示按钮的基本用法
# from tkinter import *
# root=Tk() # 创建一个顶层窗口
# root.title("使用按钮组件") # 设置窗口标题
# #使用state参数控制按钮是否可用
# Button(root,text='禁用',state=DISABLED).pack(side=RIGHT)
# Button(root,text='确定').pack(side=LEFT)
# Button(root,text='取消').pack(side=LEFT)
# Button(root,text='退出',command=root.quit).pack(side=RIGHT)
# root.mainloop()

#在窗口添加一个标签组件和一个按钮组件
# from tkinter import *
# import tkinter as tk
# 第一步，实例化object，建立窗口window
# window=tk.Tk()
# 第二步，给窗口命名
# window.title("设计标签组件")
# 第三步，设计窗口的长宽
# window.geometry('240x100')
# 第四步，设计标签
# var=tk.StringVar()
#将label标签的内容设定为字符类型，用var来接受hit_me
#的返回值，用以显示在标签上
# l=tk.Label(window,textvariable=var,bg='blue',
#     fg='white',font=('Arial',16),width=20,height=2)
#说明：bg为背景，fg为颜色字体，font为字体，width为长，
# height为高，这里的长和高是字符长和高，例如height=2，就是标签有两个字符高
# l.pack()
#定义一个函数功能(代码可以自由编写),供单击button按钮时被调用,
# 调用参数command=函数名
# on_hit=False
# def hit_me():
#     global on_hit
#     if on_hit==False:
#         on_hit=True
#         var.set('你单击按钮了')
#     else:
#         on_hit=False
#         var.set('')
#第五步，设计按钮
# b=tk.Button(window,text='测试按钮',
#     font=('Arial',12),width=10,height=1,command=hit_me)
# b.pack()
#第六步，运行窗口
# window.mainloop()




















































































































































