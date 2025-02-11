from tkinter import *
from tkinter import messagebox
import json
import time

def on_close():
    root.iconify()  # 最小化窗口而不是关闭
    root.destroy()

def check_login(event=None):  # 修改为接受一个事件参数
    username = username_entry.get().strip().lower()
    password = password_entry.get().strip()

    if not username:
        messagebox.showerror("错误", "账号不能为空。")
        return
    elif not password:
        messagebox.showerror("错误", "密码不能为空。")
        return

    with open("registered_users.json", "r") as f:
        registered_users = json.load(f)

    if username in registered_users and registered_users[username] == password:
        print("登录成功！")
        root.quit()
    else:
        print("账号或密码错误！")
        # 登录失败后，清空密码
        #password_entry.delete(0, END)
        messagebox.showinfo("登录失败", "账号或密码错误！请重新输入！\n如果您还没有账号，请注册。")
        root.after(5000, lambda: root.geometry("200x150") and root.title("登录"))


def register():
    def save_registration():
        new_username = new_username_entry.get().strip().lower()
        new_password = new_password_entry.get().strip()
        confirm_password = confirm_password_entry.get().strip()

        if not new_username:
            messagebox.showerror("错误", "账号不能为空。")
            return
        elif not new_password:
            messagebox.showerror("错误", "密码不能为空。")
            return

        if len(new_username) != 10 or not new_username.isdigit():
            messagebox.showerror("错误", "账号必须为10位数字。")
            return

        if len(new_password) < 6 or new_password.isdigit():
            messagebox.showerror("错误", "密码不能是纯数字且不能少于6位。")
            return

        if new_username in registered_users:
            messagebox.showerror("错误", "该账号已被注册，请使用其他账号试试。")
            return

        if new_password == confirm_password:
            with open("registered_users.json", "r+") as f:
                registered_users[new_username] = new_password
                json.dump(registered_users, f)
            messagebox.showinfo("注册成功", "注册成功！请牢记账号密码否则无法找回！")
            register_window.destroy()
        else:
            messagebox.showerror("错误", "两次输入的密码不一致，请重新输入。")
            new_password_entry.delete(0, END)
            confirm_password_entry.delete(0, END)

    register_window = Toplevel(root)
    register_window.title("账号注册")

    with open("registered_users.json", "r") as f:
        registered_users = json.load(f)

    new_username_label = Label(register_window, text="请输入新的账号:")
    new_username_label.pack()
    new_username_entry = Entry(register_window)
    new_username_entry.pack()

    new_password_label = Label(register_window, text="请输入密码:")
    new_password_label.pack()
    new_password_entry = Entry(register_window, show="*")
    new_password_entry.pack()

    confirm_password_label = Label(register_window, text="再次输入密码:")
    confirm_password_label.pack()
    confirm_password_entry = Entry(register_window, show="*")
    confirm_password_entry.pack()

    register_button = Button(register_window, text="注册", command=save_registration)
    register_button.pack()


def change_password():
    def save_new_password():
        username = username_entry.get().strip().lower()
        old_password = old_password_entry.get().strip()
        new_password = new_password_entry.get().strip()
        confirm_new_password = confirm_new_password_entry.get().strip()

        if not new_password:
            messagebox.showerror("错误", "密码不能为空。")
            return

        if len(new_password) < 6 or new_password.isdigit():
            messagebox.showerror("错误", "新密码不能是纯数字且不能少于6位。")
            return

        if username in registered_users and registered_users[username] == old_password:
            if new_password == confirm_new_password:
                registered_users[username] = new_password
                with open("registered_users.json", "w") as f:
                    json.dump(registered_users, f)
                messagebox.showinfo("成功", "密码修改成功！")
                change_password_window.destroy()
            else:
                messagebox.showerror("错误", "两次输入的新密码不一致，请重新输入。")
                new_password_entry.delete(0, END)
                confirm_new_password_entry.delete(0, END)
        else:
            messagebox.showerror("错误", "原账号或密码错误，请重新输入。")
            old_password_entry.delete(0, END)
            new_password_entry.delete(0, END)
            confirm_new_password_entry.delete(0, END)

    change_password_window = Toplevel(root)
    change_password_window.title("修改密码")

    with open("registered_users.json", "r") as f:
        registered_users = json.load(f)

    username_label = Label(change_password_window, text="请输入您的账号:")
    username_label.pack()
    username_entry = Entry(change_password_window)
    username_entry.pack()

    old_password_label = Label(change_password_window, text="请输入原密码:")
    old_password_label.pack()
    old_password_entry = Entry(change_password_window, show="*")
    old_password_entry.pack()

    new_password_label = Label(change_password_window, text="请输入新密码:")
    new_password_label.pack()
    new_password_entry = Entry(change_password_window, show="*")
    new_password_entry.pack()

    confirm_new_password_label = Label(change_password_window, text="再次输入新密码:")
    confirm_new_password_label.pack()
    confirm_new_password_entry = Entry(change_password_window, show="*")
    confirm_new_password_entry.pack()

    save_button = Button(change_password_window, text="保存", command=save_new_password)
    save_button.pack()


def password_entry_click(event):
    if not username_entry.get().strip():
        messagebox.showerror("错误", "请先输入账号。")
        return "break"


root = Tk()
root.title("登录器")

root.geometry("200x150")
root.minsize(220, 200)
root.resizable(False, False)  # 禁止调整大小

username_label = Label(root, text="请输入账号:")
username_label.pack()
username_entry = Entry(root)
username_entry.pack()

password_label = Label(root, text="请输入密码:")
password_label.pack()
password_entry = Entry(root, show="*")
password_entry.pack()

login_button = Button(root, text="登录", command=check_login)
login_button.pack()

register_button = Button(root, text="注册", command=register)
register_button.pack()

change_password_button = Button(root, text="修改密码", command=change_password)
change_password_button.pack()

root.bind('<Return>', check_login)  # 绑定回车键到登录功能


# 定义一个退出函数
def ask_exit():
    # 如果用户选择退出，则关闭窗口
    if messagebox.askyesno("退出", "是否退出?"):
        root.destroy()


# 绑定ESC键到退出函数
root.bind('<Escape>', lambda event: ask_exit())

def on_closing():
    if messagebox.askokcancel("退出", "是否退出?")  :
        root.destroy()
root.protocol("WM_DELETE_WINDOW", on_closing)


password_entry.bind("<Button-1>", password_entry_click)  # 绑定鼠标左键单击到密码输入
root.mainloop()