#创建一个列表框，添加三个列表项目
# from tkinter import *
# window=Tk()
# lb=Listbox(window)
# for i in ['Python','Java','C++','Basic']:
#     lb.insert(END,i)
# lb.pack()
# window.mainloop()

#使用selectmode=MULTIPLE，允许选择多个项目
# from tkinter import *
# window=Tk()
# lb=Listbox(window,selectmode=MULTIPLE)
# for i in ['Python','Java','C++','Basic']:
#     lb.insert(END,i)
# lb.pack()
# window.mainloop()

# 为列表项目绑定双击事件
# from tkinter import *
# window=Tk()
# l=Label(window,bg='yellow',width=20,text='')
# l.pack()
# def printList(event):
#     l.config(text='被选项为：'+lb.get(lb.curselection()))
# lb=Listbox(window)
# lb.bind('<Double-Button-1>',printList)
# for i in range(10):
#     lb.insert(END,str(i*100))
# lb.pack()
# window.mainloop()

#创建一个滚动条，并把他绑定到列表框组件上
# from tkinter import *
# window=Tk()
# lb=Listbox(window) #创建列表框
# sb=Scrollbar(window) #创建滚动条
# sb.pack(side=RIGHT,fill=Y) #将滚动条放置在右侧，并填充整个高度
# lb['yscrollcommand']=sb.set #将列表框的滚动条与滚动条绑定
# for i in range(100):
#     lb.insert(END,str(i))
# lb.pack(side=LEFT) #将列表框放置在左侧
# sb['command']=lb.yview #将滚动条的滚动与列表框的滚动绑定
# window.mainloop()

#设计一个简单的框架，在框架中绑定两个标签
# from tkinter import *
# window=Tk()
# window.title('简单的框架')
# window.geometry('600x500')
# #创建frame
# fm=Frame(height=200,width=200,bg='red',border=2)
# #将frame放置在主窗口的指定位置
# fm.pack_propagate(0)
# fm.pack()
# #在frame中添加组件
# Label(fm,text='左侧标签').pack(side=LEFT)
# Label(fm,text='右侧标签').pack(side=RIGHT)
# window.mainloop()

#设计一个按钮点击计数
# import tkinter as tk
# window=tk.Tk()
# name=tk.StringVar() #定义一个字符串变量
# name.set('点我')  #设置字符串变量的初始值
# n=0
# def click(e):
#     global n
#     n+=1
#     name.set('我被点击{}了次'.format(n))
# btn=tk.Button(window,textvariable=name)
# btn.bind('<Button-1>',click)
# btn.place(relx=0.5,rely=0.5,anchor=tk.CENTER)
# window.mainloop()

#绑定事件：鼠标经过和离开
# import tkinter as tk
# window=tk.Tk()
# entry=tk.Entry(window)
# def f(event):
#     event.widget['bg']='red'
# def f2(event):
#     event.widget['bg']='white'
# #绑定事件
# entry.bind('<Enter>',f)
# entry.bind('<Leave>',f2)
# entry.pack()
# window.mainloop()






