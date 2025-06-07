import socket
import threading


def PortScan(target_ip, start_port=0, end_port=65535, num_threads=16):
    open_ports = []
    thread_list = []

    def scan_ports(ip, start, end):
        local_open_ports = []
        for port in range(start, end + 1):
            try:
                with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                    s.settimeout(0.001)
                    result = s.connect_ex((ip, port))
                    if result == 0:
                        local_open_ports.append(port)
            except KeyboardInterrupt:
                break
            except Exception as e:
                pass
        open_ports.extend(local_open_ports)

    ports_per_thread = (end_port - start_port + 1) // num_threads
    for i in range(num_threads):
        thread_start_port = start_port + i * ports_per_thread
        thread_end_port = thread_start_port + ports_per_thread - 1

        thread = threading.Thread(target=scan_ports, args=(target_ip, thread_start_port, thread_end_port))
        thread.start()
        thread_list.append(thread)
    for thread in thread_list:
        thread.join()

    return open_ports


if __name__ == "__main__":
    target_ip = "192.168.1.52"

    print(f"开始多线程扫描 {target_ip} 上的端口...")
    open_ports = PortScan(target_ip)

    if open_ports:
        print(f"开放的端口：{open_ports}")
    else:
        print("没有开放的端口。")