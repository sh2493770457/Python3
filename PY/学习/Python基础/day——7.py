#在主窗口中显示创建密文形式和明文形式的单行文本框
# import tkinter as tk
# #第一步：创建主窗口
# window=tk.Tk()
# #第二步：设置主窗口的标题
# window.title('设计单行文本框')
# #第三步：设置主窗口的大小
# window.geometry('280x100')
# #第四步：设定输入框控件entry并放置
# e1=tk.Entry(window,show='*',font=('Arial',14))
# e2=tk.Entry(window,show=None,font=('Arial',14))
# e1.pack()
# e2.pack()
# window.mainloop()
import tkinter
#为文本框,设计默认值,禁止用户输入
# import tkinter as tk
# #第一步：创建主窗口
# window=tk.Tk()
# #第二步：设置主窗口的标题
# window.title('设计文本框状态属性')
# #第三步：设置主窗口的大小
# window.geometry('280x100')
# #第四步：定义一个Stringvar对象
# value1=tk.StringVar()
# value2=tk.StringVar()
# #第五步：设定输入框控件entry并放置
# e1=tk.Entry(window,state="disabled",textvariable=value1,font=('Arial',14)) #禁用文本
# e2=tk.Entry(window,state="readonly",textvariable=value2,font=('Arial',14)) #只读文本
# value1.set('丑八怪,禁止用户输入')
# value2.set('丑八怪,你就眼巴巴看着吧')
# e1.pack()
# e2.pack()
# window.mainloop()

#在窗口放置两个文本框,单行文本框e和多行文本框t,再放置两个按钮,绑定回调函数,
# 实现单击按钮时,读取单行文本框的值,然后分别插入到多行文本框的焦点位置和尾部位置
# import tkinter as tk
# window = tk.Tk()
# window.title('读取文本框的值')
# window.geometry('360x160')
# # 创建单行文本框
# e = tk.Entry(window)
# e.pack()
# # 定义插入光标位置和末尾位置的函数
# def insert_point():
#     var = e.get()  # 获取单行文本框中的值
#     t.insert('insert', var)  # 在光标位置插入值
# def insert_end():
#     var = e.get()  # 获取单行文本框中的值
#     t.insert('end', var)  # 在末尾位置插入值
# # 创建按钮，并绑定回调函数
# b1 = tk.Button(window, text='在光标位置插入', width=20, height=2, command=insert_point)
# b1.pack()
# b2 = tk.Button(window, text='在文本框尾部插入', width=20, height=2, command=insert_end)
# b2.pack()
# # 创建多行文本框
# t = tk.Text(window, height=3)
# t.pack()
# window.mainloop()

#在窗口插入3个按钮,将他们绑定成一组,当用户点选某个选项时,则在顶部的标签中动态显示被选中的项的提示信息
# import tkinter as tk
# window = tk.Tk()
# window.title('设计单选按钮')
# window.geometry('240x140')
# var = tk.StringVar()
# label_text = '等待你的选择'  # 初始化标签文本
# # 创建标签并放置
# l = tk.Label(window, bg='yellow', width=20, text=label_text)
# l.pack()
# # 定义更新标签文本的函数
# def print_selection():
#     l.config(text='你的选项是: ' + var.get())
# # 创建单选按钮，并绑定回调函数
# r1 = tk.Radiobutton(window, text='A', variable=var, value='a', command=print_selection)
# r1.pack()
# r2 = tk.Radiobutton(window, text='B', variable=var, value='b', command=print_selection)
# r2.pack()
# r3 = tk.Radiobutton(window, text='C', variable=var, value='c', command=print_selection)
# r3.pack()
# window.mainloop()

#设计复选按钮
# import tkinter as tk
# window=tk.Tk()
# window.title('设计复选按钮')
# window.geometry('300x100')
# l=tk.Label(window,bg='yellow',width=20,text='')
# l.pack()
# def print_selection():
#     if (var1.get()==1)&(var2.get()==0):
#         l.config(text='你选择了A')
#     elif (var1.get()==0)&(var2.get()==1):
#         l.config(text='你选择了B')
#     elif (var1.get()==1)&(var2.get()==1):
#         l.config(text='你选择了A和B')
#     else:
#         l.config(text='你没有选择A和B')
# var1=tk.IntVar()
# var2=tk.IntVar()
# c1=tk.Checkbutton(window,text='A',variable=var1,onvalue=1,offvalue=0,command=print_selection)
# c1.pack()
# c2=tk.Checkbutton(window,text='B',variable=var2,onvalue=1,offvalue=0,command=print_selection)
# c2.pack()
# window.mainloop()


# 设计一个下拉菜单
# import tkinter as tk
# def onselect(event):
#     # 获取选择的项的索引
#     index = lb.curselection()
#     # 获取选择的项的文本
#     value = lb.get(index)
#     print("你选择的是:", value)
# window = tk.Tk()
# window.title('设计下拉菜单')
# window.geometry('200x100')
# # 创建下拉菜单
# lb = tk.Listbox(window, height=2, width=10)
# lb.pack()
# lb.insert(tk.END, '选项1')
# lb.insert(tk.END, '选项2')
# lb.insert(tk.END, '选项3')
# # 绑定事件，当选择列表框中的选项时触发
# lb.bind('<<ListboxSelect>>', onselect)
# window.mainloop()

#创建一个顶级菜单
# import tkinter as tk
# window = tk.Tk()
# m=tk.Menu(window)
# m.add_command(label='Hello')
# n=tk.Menu(window)
# m.add_cascade(label='World',menu=n)
# window.config(menu=m)
# window.mainloop()


#创建下拉菜单或子菜单
# import tkinter as tk
# window=tk.Tk()
# window.title('创建下拉菜单或子菜单')
# window.geometry('250x150')
# m=tk.Menu(window)
# f=tk.Menu(m,tearoff=1)
# m.add_cascade(label='文件',menu=f)
# f.add_command(label='新建')
# f.add_command(label='打开')
# f.add_command(label='保存')
# window.config(menu=m)
# window.mainloop()

#创建菜单弹出
import tkinter as tk
# window=tk.Tk()
# window.title('创建菜单弹出')
# window.geometry('300x20')
# l=tk.Label(window,text='操作次数：0',bg='yellow')
# l.pack()
# counter=1
# def callback():
#     global counter
#     l.config(text='操作次数：'+str(counter))
#     counter+=1
# m=tk.Menu(window,tearoff=0)
# m.add_command(label='撤销',command=callback)
# m.add_command(label='重做',command=callback)
# def popup(event):
#     m.post(event.x_root,event.y_root)
# window.bind('<Button-3>',popup)
# window.mainloop()

#使用mseeage组件设计一个消息提示
# from tkinter import *
# from tkinter import messagebox  # 从 tkinter 导入 messagebox 模块
# window = Tk()
# window.title('消息提示')
# window.geometry('200x100')
# l = Label(window, text='点击按钮显示消息提示', bg='yellow')
# l.pack()
# def callback():
#     messagebox.showinfo('消息提示', '你点击了按钮')
# b = Button(window, text='点击', command=callback)
# b.pack()
# window.mainloop()









