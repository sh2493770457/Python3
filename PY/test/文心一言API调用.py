import tkinter as tk
from tkinter import scrolledtext, messagebox, ttk
import requests
import json

API_KEY = "7gBXN6km2w63thvGMcF8gJUF"
SECRET_KEY = "lzPIbKVYUOqGdMNKMomSRhHYVhhgqVnf"


def get_access_token():
    """
    使用 AK，SK 生成鉴权签名（Access Token）
    :return: access_token，或是None(如果错误)
    """
    url = "https://aip.baidubce.com/oauth/2.0/token"
    params = {"grant_type": "client_credentials", "client_id": API_KEY, "client_secret": SECRET_KEY}
    return str(requests.post(url, params=params).json().get("access_token"))


def get_response(user_input):
    url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant?access_token=" + get_access_token()
    payload = json.dumps({
        "messages": [
            {
                "role": "user",
                "content": user_input
            }
        ]
    })
    headers = {
        'Content-Type': 'application/json'
    }

    response = requests.post(url, headers=headers, data=payload).json()
    return response['result']


def send_message(event=None):
    user_input = input_box.get("1.0", tk.END).strip()
    if not user_input:
        messagebox.showwarning("警告", "消息不能为空！")
        return

    if user_input == '退出':
        chat_history.insert(tk.END, "谢谢使用，再见！\n")
        root.destroy()
    else:
        chat_history.insert(tk.END, "\n你: " + user_input + "\n")
        response = get_response(user_input)
        chat_history.insert(tk.END, "\n雪儿: " + response + "\n")
        input_box.delete("1.0", tk.END)


root = tk.Tk()
root.title("文心一言聊天机器人")

# 增加 Sizegrip 小部件以实现放大缩小
sg = ttk.Sizegrip(root)
sg.grid(row=1, column=1, sticky='se')

chat_history = scrolledtext.ScrolledText(root, width=60, height=20)
chat_history.grid(row=1, column=0, padx=10, pady=10, columnspan=2, sticky="nsew")
chat_history.insert(tk.END, "欢迎使用文心一言接口！\n\n"
                            "窗口大小可以自适应调\n"
                            "Shift+Enter输入框换行。\n"
                            "输入'退出'或按'Esc'可退出程序。\n"
                            "您可以输入问题或对话按'Enter'发送信息。\n")


input_box = tk.Text(root, width=50, height=3)
input_box.grid(row=3, column=0, padx=10, pady=10, sticky="ew")


send_button = tk.Button(root, text="发送", command=send_message)
send_button.grid(row=3, column=1, padx=10, pady=10)

# 设置列和行的权重，使得对话框可以自适应窗口大小
root.columnconfigure(0, weight=1)
root.rowconfigure(1, weight=1)

# 绑定按 Enter 键发送消息
root.bind("<Return>", send_message)
# 绑定按 Esc 键退出程序
root.bind("<Escape>", lambda e: root.destroy())


root.mainloop()
