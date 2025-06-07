import time
import psutil  # 用于处理系统进程

# 定义需要监控的进程名称
PROCESS_NAMES = ["LeagueClient.exe", "wegame.exe", "qq.exe", "cmd.exe", "dnf.exe", "steam.exe", "msedge.exe",
                 "SunloginClient.exe","QQMusic.exe"]

def kill_process_by_name(process_name):
    """通过进程名称强制终止进程"""
    for proc in psutil.process_iter(['pid', 'name']):
        if proc.info['name'] == process_name:
            try:
                print(f"强制终止进程 {process_name} (PID: {proc.info['pid']})...")
                proc.kill()  # 强制终止进程
                proc.wait(timeout=3)  # 等待进程终止
                print(f"进程 {process_name} 已成功终止。")
            except psutil.NoSuchProcess:
                print(f"进程 {process_name} 已经结束。")
            except psutil.AccessDenied:
                print(f"无法终止进程 {process_name}，权限被拒绝。")
            except psutil.TimeoutExpired:
                print(f"强制终止进程 {process_name} 超时。")
            return

    print(f"未找到目标进程 {process_name}。")

def monitor_processes(process_names):
    """监控指定的进程列表，如果发现则终止"""
    while True:
        print(f"正在监控进程 {process_names}...")
        for process_name in process_names:
            kill_process_by_name(process_name)
        time.sleep(5)  # 每5秒检查一次

if __name__ == "__main__":
    monitor_processes(PROCESS_NAMES)  # 开始监控指定进程
