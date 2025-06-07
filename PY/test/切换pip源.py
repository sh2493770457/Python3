import re
import subprocess
import tkinter as tk

# 定义源字典，格式为 {'唯一标识符': '源URL'}
sources = {
    "阿里云": "https://mirrors.aliyun.com/pypi/simple/",
    "豆瓣": "https://pypi.douban.com/simple/",
    "清华大学": "https://pypi.tuna.tsinghua.edu.cn/simple",
    "中国科技大学": "https://mirrors.ustc.edu.cn/pypi/web/simple/",
}


def get_pip_name(url):
    for k, v in sources.items():
        if url.lower().strip("/") == v.lower().strip("/"):
            return k
    return url


def get_pip_source():
    # 运行pip config list命令获取所有pip配置信息
    output = subprocess.run(
        ["pip", "config", "list"], capture_output=True, text=True
    ).stdout

    # 使用正则表达式从输出中提取源URL
    pattern = r"index-url\s*=\s*(.*)$"
    match = re.findall(pattern, output, flags=re.M)

    if match:
        return match[0].strip().strip("'").strip('"')  # 返回提取到的源URL
    else:
        return None  # 如果没有找到源URL，则返回None


def set_pip_source(source):
    url = sources.get(source)
    if not url:
        result_label.config(text="无效的选项！")
        return

    try:
        process = subprocess.Popen(
            ["pip", "config", "set", "global.index-url", url],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
        )
        output, error = process.communicate()
        if process.returncode == 0:
            result_label.config(text="PIP 源已设置为：" + source)
        else:
            result_label.config(text=f"设置PIP源时出现错误：{error.decode()}")
    except Exception as e:
        result_label.config(text=f"设置PIP源时出现错误：{str(e)}")


def center_window(window):
    window.update_idletasks()
    width = window.winfo_width()
    height = window.winfo_height()
    x = (window.winfo_screenwidth() // 2) - (width // 2)
    y = (window.winfo_screenheight() // 2) - (height // 2) - 100
    window.geometry("{}x{}+{}+{}".format(width, height, x, y))


# 创建窗口
window = tk.Tk()
window.title("PIP 源切换")
window.withdraw()  # 使用withdraw()方法隐藏窗口

# 选项按钮
for i, key in enumerate(sources.keys()):
    row = i // 2
    col = i % 2

    button = tk.Button(
        window,
        text=key,
        width=20,
        height=2,
        command=lambda source=key: set_pip_source(source),
    )
    button.grid(row=row, column=col, padx=10, pady=5)


pip_source = get_pip_source()
if pip_source:
    current_source = get_pip_name(pip_source)
else:
    current_source = "未知pip源"
result_label = tk.Label(window, text=f"当前源：{current_source}", fg="blue")


result_label.grid(row=len(sources) // 2 + 1, columnspan=2, padx=10, pady=5)


# 将窗口定位于屏幕正中央
center_window(window)

# 使用update()方法更新窗口状态
window.update()

# 使用deiconify()方法显示窗口
window.deiconify()

# 显示窗口
window.mainloop()
