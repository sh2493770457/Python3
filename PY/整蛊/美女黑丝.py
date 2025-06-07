import tkinter as tk
import random

# 定义提示词列表
prompt_messages = [
    "想看黑丝吗？",
    "今天天气不错哦！",
    "出去走走吧！",
    "作业写完了吗?",
    "满脑子都是脏东西!",
    "明天会更好！",
    "一看你就不是好东西！",
    "把眼睛给你戳瞎！",
    "还不破防，盾真厚！",
    "让我给你一塔耳!",
    "家里条件又不好！",
    "人长得也不行？",
    "打游戏也菜？",
    "学习也差？",
    "还没有女朋友？",
    "找工作没人要？",
    "做梦呢！",
    "不努力学习靠谁？",
    "希望这次你能长教训！"
]


def show_prompt():
    # 创建 Toplevel 窗口
    popup = tk.Toplevel(root)
    popup.title("随机提示")
    # 从提示词列表中随机选择一个提示词
    prompt_message = random.choice(prompt_messages)
    label = tk.Label(popup, text=prompt_message, font=("Arial", 14))
    label.pack(pady=20)
    # 生成随机坐标
    x = random.randint(0, root.winfo_screenwidth() - 200)  # 200 是窗口的宽度
    y = random.randint(0, root.winfo_screenheight() - 100)  # 100 是窗口的高度
    # 设置窗口在屏幕上的随机位置
    popup.geometry("+{}+{}".format(x, y))
    # 设置窗口关闭的事件，当窗口关闭时重新显示提示词
    popup.protocol("WM_DELETE_WINDOW", show_prompt)


# 创建主窗口
root = tk.Tk()
root.withdraw()  # 隐藏主窗口

# 创建单独的窗口显示提示词
for _ in range(600):
    show_prompt()

root.mainloop()
times = 0
