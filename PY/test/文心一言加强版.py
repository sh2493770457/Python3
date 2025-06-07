import tkinter as tk
from tkinter import messagebox, scrolledtext, ttk
import requests
import json
import hashlib

# 全局变量
API_KEY = "7gBXN6km2w63thvGMcF8gJUF"
SECRET_KEY = "lzPIbKVYUOqGdMNKMomSRhHYVhhgqVnf"


# 读取注册用户数据
def load_registered_users():
    try:
        with open("registered_users.json", "r") as f:
            return json.load(f)
    except FileNotFoundError:
        return {}


# 保存注册用户数据
def save_registered_users(users):
    with open("registered_users.json", "w") as f:
        json.dump(users, f)


# 登录功能模块
def check_login(event=None):
    username = username_entry.get().strip().lower()
    password = password_entry.get().strip()
    if not username:
        messagebox.showerror("错误", "账号不能为空。")
        return
    elif not password:
        messagebox.showerror("错误", "密码不能为空。")
        return

    hashed_username = hashlib.sha256(username.encode()).hexdigest()
    hashed_password = hashlib.sha256(password.encode()).hexdigest()

    registered_users = load_registered_users()

    if hashed_username in registered_users and registered_users[hashed_username] == hashed_password:
        print("登录成功！")
        root.withdraw()  # 隐藏登录窗口
        show_main_window(root)  # 显示主界面窗口
        root.unbind('<Return>')
    else:
        print("账号或密码错误！")
        password_entry.delete(0, tk.END)
        messagebox.showinfo("登录失败", "账号或密码错误！\n如果您还没有账号，请注册。")
        root.after(5000, lambda: root.geometry("200x150") and root.title("登录"))


# 注册功能模块
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

        hashed_username = hashlib.sha256(new_username.encode()).hexdigest()
        hashed_password = hashlib.sha256(new_password.encode()).hexdigest()

        registered_users = load_registered_users()

        if hashed_username in registered_users:
            messagebox.showerror("错误", "该账号已被注册，请使用其他账号试试。")
            return

        if new_password == confirm_password:
            registered_users[hashed_username] = hashed_password
            save_registered_users(registered_users)
            messagebox.showinfo("注册成功", "注册成功！请牢记账号密码否则无法找回！")
            register_window.destroy()
        else:
            messagebox.showerror("错误", "两次输入的密码不一致，请重新输入。")
            new_password_entry.delete(0, tk.END)
            confirm_password_entry.delete(0, tk.END)

    register_window = tk.Toplevel(root)
    register_window.title("账号注册")

    new_username_label = tk.Label(register_window, text="请输入新的账号:")
    new_username_label.pack()
    new_username_entry = tk.Entry(register_window)
    new_username_entry.pack()

    new_password_label = tk.Label(register_window, text="请输入密码:")
    new_password_label.pack()
    new_password_entry = tk.Entry(register_window, show="*")
    new_password_entry.pack()

    confirm_password_label = tk.Label(register_window, text="再次输入密码:")
    confirm_password_label.pack()
    confirm_password_entry = tk.Entry(register_window, show="*")
    confirm_password_entry.pack()

    register_button = tk.Button(register_window, text="注册", command=save_registration)
    register_button.pack()


# 修改密码功能模块
def change_password():
    global username_entry

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

        hashed_username = hashlib.sha256(username.encode()).hexdigest()
        hashed_old_password = hashlib.sha256(old_password.encode()).hexdigest()

        registered_users = load_registered_users()

        if hashed_username in registered_users and registered_users[hashed_username] == hashed_old_password:
            if new_password == confirm_new_password:
                registered_users[hashed_username] = hashlib.sha256(new_password.encode()).hexdigest()
                save_registered_users(registered_users)
                messagebox.showinfo("成功", "密码修改成功！")
                change_password_window.destroy()
            else:
                messagebox.showerror("错误", "两次输入的新密码不一致，请重新输入。")
                new_password_entry.delete(0, tk.END)
                confirm_new_password_entry.delete(0, tk.END)
        else:
            messagebox.showerror("错误", "原账号或密码错误，请重新输入。")
            old_password_entry.delete(0, tk.END)
            new_password_entry.delete(0, tk.END)
            confirm_new_password_entry.delete(0, tk.END)

    change_password_window = tk.Toplevel(root)
    change_password_window.title("修改密码")

    username_label = tk.Label(change_password_window, text="请输入您的账号:")
    username_label.pack()
    username_entry = tk.Entry(change_password_window)
    username_entry.pack()

    old_password_label = tk.Label(change_password_window, text="请输入原密码:")
    old_password_label.pack()
    old_password_entry = tk.Entry(change_password_window, show="*")
    old_password_entry.pack()

    new_password_label = tk.Label(change_password_window, text="请输入新密码:")
    new_password_label.pack()
    new_password_entry = tk.Entry(change_password_window, show="*")
    new_password_entry.pack()

    confirm_new_password_label = tk.Label(change_password_window, text="再次输入新密码:")
    confirm_new_password_label.pack()
    confirm_new_password_entry = tk.Entry(change_password_window, show="*")
    confirm_new_password_entry.pack()

    save_button = tk.Button(change_password_window, text="保存", command=save_new_password)
    save_button.pack()


# 文心一言聊天机器人界面
def show_main_window(parent):
    def get_access_token():
        url = "https://aip.baidubce.com/oauth/2.0/token"
        params = {"grant_type": "client_credentials", "client_id": API_KEY, "client_secret": SECRET_KEY}
        try:
            response = requests.post(url, params=params)
            response.raise_for_status()
            return str(response.json().get("access_token"))
        except requests.RequestException as e:
            messagebox.showerror("错误", f"获取Access Token失败: {e}")
            return None

    def get_response(user_input):
        access_token = get_access_token()
        if not access_token:
            return "无法获取Access Token。"
        url = f"https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant?access_token={access_token}"
        payload = json.dumps({
            "messages": [
                {
                    "role": "user",
                    "content": user_input
                }
            ]
        })
        headers = {'Content-Type': 'application/json'}

        try:
            response = requests.post(url, headers=headers, data=payload)
            response.raise_for_status()
            return response.json().get('result', '无法获取回复。')
        except requests.RequestException as e:
            return f"请求失败: {e}"

    def send_message(event=None):
        user_input = input_box.get("1.0", tk.END).strip()
        if not user_input:
            messagebox.showwarning("警告", "消息不能为空！")
            return

        if user_input == '退出':
            if messagebox.askyesno("退出", "是否退出文心一言聊天窗口?"):
                chat_history.insert(tk.END, "谢谢使用，再见！\n")
                main_window.destroy()
        else:
            chat_history.insert(tk.END, "\n你: " + user_input + "\n")
            response = get_response(user_input)
            chat_history.insert(tk.END, "\n雪儿: " + response + "\n")
            input_box.delete("1.0", tk.END)

    main_window = tk.Toplevel(parent)
    main_window.title("文心一言聊天机器人")
    main_window.geometry("600x400")

    sg = ttk.Sizegrip(main_window)
    sg.grid(row=1, column=1, sticky='se')

    chat_history = scrolledtext.ScrolledText(main_window, width=60, height=20)
    chat_history.grid(row=1, column=0, padx=10, pady=10, columnspan=2, sticky="nsew")
    chat_history.insert(tk.END, "欢迎使用文心一言接口！\n\n"
                                "窗口大小可以自适应调\n"
                                "Shift+Enter输入框换行。\n"
                                "输入'退出'或按'Esc'可退出程序。\n"
                                "您可以输入问题或对话按'Enter'发送信息。\n")

    input_box = tk.Text(main_window, width=50, height=3)
    input_box.grid(row=3, column=0, padx=10, pady=10, sticky="ew")

    send_button = tk.Button(main_window, text="发送", command=send_message)
    send_button.grid(row=3, column=1, padx=10, pady=10)

    main_window.columnconfigure(0, weight=1)
    main_window.rowconfigure(1, weight=1)

    main_window.bind("<Return>", send_message)
    main_window.bind("<Escape>", lambda e: ask_exit())

    def ask_exit():
        if messagebox.askyesno("退出", "是否退出文心一言聊天窗口?"):
            main_window.destroy()

    main_window.protocol("WM_DELETE_WINDOW", ask_exit)


# 主程序
root = tk.Tk()
root.title("登录器")
root.geometry("200x150")
root.minsize(220, 200)
root.resizable(False, False)

username_label = tk.Label(root, text="请输入账号:")
username_label.pack()
username_entry = tk.Entry(root)
username_entry.pack()

password_label = tk.Label(root, text="请输入密码:")
password_label.pack()
password_entry = tk.Entry(root, show="*")
password_entry.pack()

login_button = tk.Button(root, text="登录", command=check_login)
login_button.pack()

register_button = tk.Button(root, text="注册", command=register)
register_button.pack()

change_password_button = tk.Button(root, text="修改密码", command=change_password)
change_password_button.pack()

root.bind('<Return>', check_login)
root.bind('<Escape>', lambda event: root.quit())


def ask_exit():
    if messagebox.askokcancel("退出", "是否退出?"):
        root.destroy()


root.protocol("WM_DELETE_WINDOW", ask_exit)
password_entry.bind("<Button-1>",
                    lambda event: username_entry.get().strip() or messagebox.showerror("错误", "请先输入账号。"))
root.mainloop()
