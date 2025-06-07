import time
import subprocess

# 定义目标计算机信息
TARGET_IP = input("请输入目标计算机的IP地址(192.168.1.86): ")
USERNAME = input("请输入目标计算机的用户名(Administrator): ")  # 用户名和密码这里暂时还没做校验,不过输错了不会打印进程列表
PASSWORD = input("请输入密码(可为空):")  # 密码可以为空


def print_remote_processes():
    try:
        print(f"连接到 {TARGET_IP} 并获取进程列表...")
        command = f'tasklist /s {TARGET_IP} /u {USERNAME} /p "{PASSWORD}"'
        result = subprocess.check_output(command, shell=True, text=True)
        print(result)
    except subprocess.CalledProcessError as e:
        print(f"获取进程列表失败: {e}")


def kill_process_by_name(process_name):
    try:
        print(f"终止进程 {process_name}...")
        command = f'taskkill /s {TARGET_IP} /u {USERNAME} /p "{PASSWORD}" /im {process_name} /f'
        result = subprocess.check_output(command, shell=True, text=True)
        print(result)
    except subprocess.CalledProcessError as e:
        print(f"终止进程失败: {e}")


def kill_process_by_pid(pid):
    try:
        print(f"终止PID为 {pid} 的进程...")
        command = f'taskkill /s {TARGET_IP} /u {USERNAME} /p "{PASSWORD}" /pid {pid} /f'
        result = subprocess.check_output(command, shell=True, text=True)
        print(result)
    except subprocess.CalledProcessError as e:
        print(f"终止进程失败: {e}")


def single_termination():
    choice = input("请输入 'name' 通过进程名称终止进程，或 'pid' 通过PID终止进程: ").strip().lower()
    if choice == 'name':
        process_name = input("请输入要终止的进程名称(LeagueClient.exe): ").strip()
        kill_process_by_name(process_name)
    elif choice == 'pid':
        pid = input("请输入要终止的进程PID: ").strip()
        if pid.isdigit():
            kill_process_by_pid(pid)
        else:
            print("PID 必须是一个数字。")
    else:
        print("无效的选项，请重新输入。")


def monitor_process(process_name):
    while True:
        try:
            print(f"正在监控进程 {process_name}...")
            command = f'tasklist /s {TARGET_IP} /u {USERNAME} /p "{PASSWORD}" /fi "IMAGENAME eq {process_name}"'
            result = subprocess.check_output(command, shell=True, text=True)
            if process_name in result:
                print(f"发现目标进程 {process_name}，准备终止。")
                kill_process_by_name(process_name)
            else:
                print(f"未发现目标进程 {process_name}，继续监控。")
        except subprocess.CalledProcessError as e:
            print(f"监控进程失败: {e}")
        time.sleep(5)


def user_interaction():
    while True:
        choice = input("请输入 '1' 进行单独结束进程，或 '2' 持续监控进程: ").strip().lower()
        if choice == '1':
            single_termination()
        elif choice == '2':
            process_name = input("请输入要监控的进程名称: ").strip()
            monitor_process(process_name)
        else:
            print("无效的选项，请重新输入。")


if __name__ == "__main__":
    print_remote_processes()
    user_interaction()
