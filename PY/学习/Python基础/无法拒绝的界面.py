import tkinter as tk
from tkinter import messagebox
root = tk.Tk()
root.title("表白❥(^_-)")
root.resizable(False, False)
root.wm_attributes("-toolwindow", 1)
screenwidth = root.winfo_screenwidth()
screenheight = root.winfo_screenheight()
width, height = 330, 120
x = (screenwidth - width) / 2
y = (screenheight - height) / 2
root.geometry('%dx%d+%d+%d' % (width, height, x, y))  # 设置在屏幕中居中显示
tk.Label(root, text="我喜欢你，做我女朋友吧", width=50, font='黑体').pack(pady='5')
def yes():  # 同意按钮
    messagebox.showinfo("o(*￣▽￣*)ブ", "在一起啦！！！")  # 弹出信息对话框
    root.destroy()  # 同意后退出窗口
def no():  # 拒绝按钮
    messagebox.showinfo("/(ㄒoㄒ)/~~", "给次机会吧~")  # 弹出信息对话框
def close_window():  # 关闭按钮
    messagebox.showinfo("/(*^▽^*))", "逃避是没有用的哦！")  # 弹出信息对话框
tk.Button(root, text=" 好", width=10, font=('黑体', 12), height=1, command=yes).pack(pady='5')
tk.Button(root, text=" 不行", width=10, font=('黑体', 12), height=1, command=no).pack(pady='5')
root.protocol('WM_DELETE_WINDOW', close_window)  # 绑定关闭事件
root.mainloop()