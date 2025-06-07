import os
import sys
import subprocess
from rich.table import Table
from rich.console import Console
from rich.panel import Panel

console = Console()

def get_scripts_path():
    """ 通过 `where pip` 获取 `Scripts` 目录路径 """
    try:
        result = subprocess.run(["where", "pip"], capture_output=True, text=True, shell=True)
        paths = result.stdout.strip().split("\n")  # 获取所有 pip.exe 路径

        # 过滤出包含 AppData\Roaming 的路径
        for path in paths:
            path = path.strip()
            if "AppData\\Roaming" in path and path.endswith("pip.exe"):
                return os.path.dirname(path)  # 返回用户目录中的 Scripts 目录

        # 如果没有 AppData\Roaming，默认返回第一个 pip.exe 所在目录
        if paths:
            return os.path.dirname(paths[0].strip())
    except Exception:
        pass

    # 备用方案：回退到 sys.executable 解析 Python 安装目录
    python_dir = os.path.dirname(sys.executable)
    return os.path.join(python_dir, "Scripts")

# 自动获取 Scripts 目录
scripts_path = get_scripts_path()

# 工具列表（编号, 脚本文件, 描述）
TOOLS = [
    (1, "systemsearch.py", "验收系统模糊匹配"),
    (2, "vulsuggest.py", "漏洞类型模糊匹配"),
    (3, "translator.py", "翻译器"),
    (4, "ip封堵.py", "内网IP封堵"),
    (5, "search.py", "GitHub仓库搜索"),
    (6, "ip去重.py", "IP去重"),
    (7, "capcha-killer.py", "BP验证码识别"),
    (8, "count.py", "报告分析求和"),
]

def show_menu():
    """ 显示工具列表 """
    console.print(Panel.fit(f"[bold #00FF87]🛠️ 自定义工具箱 🛠️[/]\n[italic #005F87]Scripts 目录: {scripts_path}[/]",
                     border_style="#00FF87",
                     padding=(1, 4),
                     subtitle="[italic #005F87]v1.2 by tomato[/]"))
    
    table = Table(
        title="[bold #FF7043]可用工具列表[/]",
        title_justify="center",
        header_style="bold #0087AF on #333333",
        border_style="#0087AF",
        style="bold #EEEEEE",
        row_styles=["on #1F1F1F", "on #2A2A2A"],
        expand=True,
        pad_edge=True,
        highlight=True
    )
    
    table.add_column("编号", justify="center", style="bold #FFA726", width=8)
    table.add_column("工具名称", style="#00C853", width=25)
    table.add_column("功能描述", style="italic #29B6F6", width=40)

    for tool in TOOLS:
        table.add_row(str(tool[0]), tool[1], tool[2])

    console.print(table, justify="center")
    console.print(Panel.fit("[#0087AF]输入工具编号选择功能[/] [blink][bold #FF7043]»[/][/blink] [italic #00C853]输入 'exit' 退出[/]",
                        border_style="#0087AF",
                        padding=(1, 2)))

def run_tool(choice):
    """ 运行指定工具 """
    for tool in TOOLS:
        if choice == tool[0]:
            script_path = os.path.join(scripts_path, tool[1])
            if os.path.exists(script_path):
                console.print(
                    Panel.fit(
                        f"[b #00C853]🚀 正在启动: [bold #0087AF]{tool[1]}[/][/]",
                        border_style="#00C853",
                        padding=(1, 2)
                    )
                )
                subprocess.run([sys.executable, script_path])
            else:
                console.print(
                    Panel.fit(
                        f"[bold white on #D32F2F]❗ 文件不存在: {tool[1]}[/]  \n[italic]请检查 {scripts_path} 目录是否正确[/]",
                        border_style="#D32F2F"
                    )
                )
            return
    
    console.print(
        Panel.fit(
            "[bold #FF7043]⚠ 无效选项! 请输入1-8的数字[/]",
            border_style="#FF7043"
        )
    )

def show_exit_message():
    """ 显示退出信息 """
    console.print("\n")
    console.print(
        Panel.fit(
            "[blink][bold #00C853]🎉 感谢使用![/][/blink] [italic #0087AF]期待下次见面 👋[/]",
            border_style="#00C853",
            padding=(1, 4),
            title="[bold #0087AF]退出程序[/]",
            title_align="left"
        )
    )
    console.print("\n")
    sys.exit(0)  # 确保完全退出

if __name__ == "__main__":
    try:
        while True:
            show_menu()
            try:
                choice = input("请输入选择 ➜   ").strip()
                if choice.lower() in ('exit', 'quit', 'q'):
                    show_exit_message()

                choice = int(choice)
                run_tool(choice)
            except ValueError:
                console.print(
                    Panel.fit(
                        "[bold #D32F2F]❌ 需要输入数字编号![/]",
                        border_style="#D32F2F",
                        padding=(0, 2)
                ))
    except KeyboardInterrupt:
        console.print("\n[bold #FF7043]检测到中断操作，正在退出...[/]")
        show_exit_message()  # 调用退出函数，避免出现 "输入无效"
