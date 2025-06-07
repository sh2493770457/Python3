import time
import subprocess  # 用于执行系统命令

# 定义目标计算机信息
TARGET_IP = "fftq1314s"
USERNAME = "老六"
PASSWORD = "fftq1314s"
PROCESS_NAMES = ["LeagueClient.exe", "wegame.exe", "qq.exe", "cmd.exe", "dnf.exe", "steam.exe", "msedge.exe",
                 "SunloginClient.exe"]


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


def monitor_processes(process_names):
    while True:
        try:
            print(f"正在监控进程 {process_names}...")
            for process_name in process_names:
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


if __name__ == "__main__":
    print_remote_processes()
    monitor_processes(PROCESS_NAMES)
