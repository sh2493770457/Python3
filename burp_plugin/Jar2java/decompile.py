#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import subprocess
import argparse
import tempfile
import shutil
import atexit

# 获取资源路径的辅助函数
def resource_path(relative_path):
    """获取资源的绝对路径，适用于开发环境和PyInstaller打包后的环境"""
    try:
        # PyInstaller创建临时文件夹，将路径存储在_MEIPASS中
        base_path = sys._MEIPASS
    except Exception:
        # 不是通过PyInstaller运行的情况
        base_path = os.path.abspath(".")
    
    return os.path.join(base_path, relative_path)

# 临时文件夹，用于存放从exe中提取的资源
temp_dir = None

def cleanup_temp_files():
    """清理临时文件"""
    global temp_dir
    if temp_dir and os.path.exists(temp_dir):
        try:
            shutil.rmtree(temp_dir)
        except:
            pass

def extract_cfr_jar():
    """从打包的exe中提取CFR JAR文件到临时目录"""
    global temp_dir
    
    # 创建临时目录
    temp_dir = tempfile.mkdtemp()
    atexit.register(cleanup_temp_files)
    
    # 尝试从资源路径获取CFR JAR文件
    cfr_path = resource_path("cfr-0.152.jar")
    
    # 如果在资源路径中找到JAR文件，复制到临时目录
    if os.path.exists(cfr_path):
        temp_cfr_path = os.path.join(temp_dir, "cfr-0.152.jar")
        shutil.copy2(cfr_path, temp_cfr_path)
        return temp_cfr_path
    
    # 如果在资源路径中找不到，返回默认路径
    return "cfr-0.152.jar"

def main():
    # 创建参数解析器
    parser = argparse.ArgumentParser(description='使用CFR反编译JAR文件')
    parser.add_argument('-f', '--file', required=False,
                        help='要反编译的JAR文件路径')
    parser.add_argument('-o', '--outputdir', default='decompiled_output',
                        help='反编译输出目录 (默认: decompiled_output)')
    parser.add_argument('-c', '--cfr', default=None,
                        help='CFR反编译器JAR文件路径 (默认: 自动查找)')
    
    # 解析命令行参数
    if len(sys.argv) == 1:
        parser.print_help()
        return 0
        
    args = parser.parse_args()
    
    # 检查是否提供了JAR文件
    if not args.file:
        print("错误: 必须指定要反编译的JAR文件")
        parser.print_help()
        return 1
    
    # 获取CFR JAR文件路径
    cfr_path = args.cfr if args.cfr else extract_cfr_jar()
    
    # 检查CFR反编译器是否存在
    if not os.path.exists(cfr_path):
        print(f"错误: 找不到CFR反编译器 '{cfr_path}'")
        parser.print_help()
        return 1
    
    # 检查要反编译的JAR文件是否存在
    if not os.path.exists(args.file):
        print(f"错误: 找不到要反编译的JAR文件 '{args.file}'")
        parser.print_help()
        return 1
    
    # 确保输出目录存在
    if not os.path.exists(args.outputdir):
        try:
            os.makedirs(args.outputdir)
            print(f"已创建输出目录: {args.outputdir}")
        except OSError as e:
            print(f"错误: 无法创建输出目录 '{args.outputdir}': {e}")
            return 1
    
    # 构建反编译命令
    cmd = ['java', '-jar', cfr_path, args.file, f'--outputdir', args.outputdir]
    
    # 执行反编译命令
    print(f"正在执行: {' '.join(cmd)}")
    try:
        process = subprocess.run(cmd, check=True, text=True, capture_output=True)
        print("反编译成功完成!")
        print(f"反编译结果保存在: {os.path.abspath(args.outputdir)}")
        return 0
    except subprocess.CalledProcessError as e:
        print(f"错误: 反编译过程失败: {e}")
        print(f"错误输出: {e.stderr}")
        return 1

if __name__ == "__main__":
    sys.exit(main())