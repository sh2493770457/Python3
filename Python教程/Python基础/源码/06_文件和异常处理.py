# 06_文件和异常处理.py
# Python基础语法示例：文件操作和异常处理

import os
import json
import csv
import pickle

# ===== 文件的基本操作 =====
print("===== 文件的基本操作 =====")

# 创建一个示例文本文件
sample_text = """这是一个示例文本文件。
它包含多行文本。
Python的文件处理功能非常强大。
我们可以轻松地读取和写入文件。
"""

# 写入文本文件
print("\n写入文本文件:")
with open("sample.txt", "w", encoding="utf-8") as file:
    file.write(sample_text)
print("文件已写入: sample.txt")

# 读取整个文件内容
print("\n读取整个文件内容:")
with open("sample.txt", "r", encoding="utf-8") as file:
    content = file.read()
print(content)

# 逐行读取文件
print("\n逐行读取文件:")
with open("sample.txt", "r", encoding="utf-8") as file:
    print("文件的行:")
    for line in file:
        print(f"  {line.strip()}")

# 读取所有行到列表
print("\n读取所有行到列表:")
with open("sample.txt", "r", encoding="utf-8") as file:
    lines = file.readlines()
print(f"行数: {len(lines)}")
print(f"第一行: {lines[0].strip()}")

# 追加内容到文件
print("\n追加内容到文件:")
with open("sample.txt", "a", encoding="utf-8") as file:
    file.write("这是追加的一行。\n")
print("内容已追加到文件")

# 再次读取文件确认追加内容
with open("sample.txt", "r", encoding="utf-8") as file:
    content = file.read()
print("追加后的文件内容:")
print(content)

# ===== 文件指针操作 =====
print("\n===== 文件指针操作 =====")

with open("sample.txt", "r", encoding="utf-8") as file:
    # 读取前10个字符
    start = file.read(10)
    print(f"前10个字符: {start}")
    
    # 获取当前文件指针位置
    position = file.tell()
    print(f"当前文件指针位置: {position}")
    
    # 读取下一行
    next_line = file.readline()
    print(f"下一行: {next_line.strip()}")
    
    # 将文件指针移动到文件开头
    file.seek(0)
    print(f"文件指针重置后的位置: {file.tell()}")
    
    # 再次读取前10个字符
    start_again = file.read(10)
    print(f"再次读取前10个字符: {start_again}")

# ===== 二进制文件操作 =====
print("\n===== 二进制文件操作 =====")

# 写入二进制数据
print("\n写入二进制数据:")
binary_data = bytes([65, 66, 67, 68, 69])  # ASCII码: ABCDE
with open("binary_sample.bin", "wb") as file:
    file.write(binary_data)
print("二进制数据已写入: binary_sample.bin")

# 读取二进制数据
print("\n读取二进制数据:")
with open("binary_sample.bin", "rb") as file:
    data = file.read()
print(f"读取的二进制数据: {data}")
print(f"解码为ASCII: {data.decode('ascii')}")

# ===== 文件和目录操作 =====
print("\n===== 文件和目录操作 =====")

# 获取当前工作目录
print(f"当前工作目录: {os.getcwd()}")

# 创建目录
print("\n创建目录:")
if not os.path.exists("test_dir"):
    os.mkdir("test_dir")
    print("目录已创建: test_dir")
else:
    print("目录已存在: test_dir")

# 列出目录内容
print("\n列出当前目录内容:")
print(os.listdir("."))

# 检查路径是否为文件或目录
print("\n检查路径:")
print(f"'sample.txt'是文件吗? {os.path.isfile('sample.txt')}")
print(f"'test_dir'是目录吗? {os.path.isdir('test_dir')}")

# 文件重命名
print("\n文件重命名:")
if os.path.exists("sample.txt") and not os.path.exists("renamed_sample.txt"):
    os.rename("sample.txt", "renamed_sample.txt")
    print("文件已重命名: sample.txt -> renamed_sample.txt")

# 获取文件信息
print("\n获取文件信息:")
if os.path.exists("renamed_sample.txt"):
    file_stats = os.stat("renamed_sample.txt")
    print(f"文件大小: {file_stats.st_size} 字节")
    print(f"最后修改时间: {file_stats.st_mtime}")

# 路径操作
print("\n路径操作:")
path = os.path.join("test_dir", "test_file.txt")
print(f"拼接路径: {path}")
print(f"目录名: {os.path.dirname(path)}")
print(f"文件名: {os.path.basename(path)}")

# ===== 使用with语句（上下文管理器） =====
print("\n===== 使用with语句 =====")

# with语句会自动关闭文件，即使发生异常
print("\n使用with语句写入文件:")
with open(path, "w", encoding="utf-8") as file:
    file.write("这是在test_dir目录中的测试文件。\n")
print(f"文件已写入: {path}")

# 读取刚才创建的文件
print("\n读取刚才创建的文件:")
with open(path, "r", encoding="utf-8") as file:
    content = file.read()
print(content)

# ===== 处理CSV文件 =====
print("\n===== 处理CSV文件 =====")

# 创建CSV文件
print("\n创建CSV文件:")
csv_data = [
    ["姓名", "年龄", "城市"],
    ["张三", "25", "北京"],
    ["李四", "30", "上海"],
    ["王五", "28", "广州"]
]

with open("data.csv", "w", newline="", encoding="utf-8") as file:
    writer = csv.writer(file)
    writer.writerows(csv_data)
print("CSV文件已创建: data.csv")

# 读取CSV文件
print("\n读取CSV文件:")
with open("data.csv", "r", encoding="utf-8") as file:
    reader = csv.reader(file)
    for row in reader:
        print(row)

# 使用DictReader和DictWriter
print("\n使用DictReader和DictWriter:")

# 创建CSV文件（使用字典）
dict_data = [
    {"姓名": "赵六", "年龄": "22", "城市": "深圳"},
    {"姓名": "钱七", "年龄": "35", "城市": "成都"},
    {"姓名": "孙八", "年龄": "27", "城市": "武汉"}
]

with open("dict_data.csv", "w", newline="", encoding="utf-8") as file:
    fieldnames = ["姓名", "年龄", "城市"]
    writer = csv.DictWriter(file, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(dict_data)
print("CSV文件已创建: dict_data.csv")

# 读取CSV文件（使用字典）
print("\n读取CSV文件（使用字典）:")
with open("dict_data.csv", "r", encoding="utf-8") as file:
    reader = csv.DictReader(file)
    for row in reader:
        print(row)

# ===== 处理JSON文件 =====
print("\n===== 处理JSON文件 =====")

# 创建JSON数据
print("\n创建JSON数据:")
json_data = {
    "姓名": "张三",
    "年龄": 25,
    "城市": "北京",
    "爱好": ["编程", "阅读", "旅行"],
    "工作": {
        "公司": "ABC科技",
        "职位": "软件工程师"
    }
}

# 将Python对象转换为JSON字符串
json_str = json.dumps(json_data, ensure_ascii=False, indent=4)
print("JSON字符串:")
print(json_str)

# 将JSON字符串转换为Python对象
python_obj = json.loads(json_str)
print("\n转换回Python对象:")
print(f"姓名: {python_obj['姓名']}")
print(f"爱好: {', '.join(python_obj['爱好'])}")
print(f"公司: {python_obj['工作']['公司']}")

# 写入JSON文件
print("\n写入JSON文件:")
with open("data.json", "w", encoding="utf-8") as file:
    json.dump(json_data, file, ensure_ascii=False, indent=4)
print("JSON文件已创建: data.json")

# 读取JSON文件
print("\n读取JSON文件:")
with open("data.json", "r", encoding="utf-8") as file:
    loaded_data = json.load(file)
print(f"从文件加载的数据: {loaded_data['姓名']}的爱好是{', '.join(loaded_data['爱好'])}")

# ===== 使用pickle序列化Python对象 =====
print("\n===== 使用pickle序列化Python对象 =====")

# 创建一个复杂的Python对象
class Person:
    def __init__(self, name, age):
        self.name = name
        self.age = age
    
    def greet(self):
        return f"你好，我是{self.name}，今年{self.age}岁。"

person = Person("李四", 30)

# 序列化对象到文件
print("\n序列化对象到文件:")
with open("person.pkl", "wb") as file:
    pickle.dump(person, file)
print("对象已序列化到文件: person.pkl")

# 从文件反序列化对象
print("\n从文件反序列化对象:")
with open("person.pkl", "rb") as file:
    loaded_person = pickle.load(file)
print(f"加载的对象: {loaded_person.name}, {loaded_person.age}")
print(f"调用方法: {loaded_person.greet()}")

# ===== 异常处理 =====
print("\n===== 异常处理 =====")

# 基本的try-except结构
print("\n基本的try-except结构:")
try:
    # 尝试执行可能引发异常的代码
    result = 10 / 0  # 除以零会引发ZeroDivisionError
    print(f"结果: {result}")  # 这行不会执行
except ZeroDivisionError:
    # 捕获特定类型的异常
    print("错误：除以零！")

# 捕获多种异常
print("\n捕获多种异常:")
try:
    number = int("abc")  # 这会引发ValueError
    result = 10 / number  # 如果上面没有异常，这可能引发ZeroDivisionError
except ValueError:
    print("错误：无法将字符串转换为整数！")
except ZeroDivisionError:
    print("错误：除以零！")

# 使用一个except块捕获多种异常
print("\n使用一个except块捕获多种异常:")
try:
    # 尝试打开不存在的文件
    with open("nonexistent_file.txt", "r") as file:
        content = file.read()
except (FileNotFoundError, PermissionError) as e:
    print(f"文件错误: {e}")

# 捕获所有异常
print("\n捕获所有异常:")
try:
    # 尝试执行一些可能引发异常的代码
    result = 10 / 0
except Exception as e:
    # 捕获所有异常（不推荐这样做，最好捕获特定类型的异常）
    print(f"发生了一个异常: {type(e).__name__}: {e}")

# try-except-else结构
print("\n使用try-except-else结构:")
try:
    # 尝试执行可能引发异常的代码
    number = int("10")
except ValueError:
    # 如果发生ValueError异常，执行这里的代码
    print("无法将字符串转换为整数！")
else:
    # 如果没有异常发生，执行这里的代码
    print(f"成功转换为整数: {number}")

# try-except-finally结构
print("\n使用try-except-finally结构:")
try:
    # 尝试执行可能引发异常的代码
    file = open("sample_file.txt", "w")
    file.write("这是一个测试文件。")
except Exception as e:
    # 如果发生异常，执行这里的代码
    print(f"发生了一个异常: {e}")
finally:
    # 无论是否发生异常，都会执行这里的代码
    if 'file' in locals() and not file.closed:
        file.close()
    print("文件已关闭")

# 使用with语句（上下文管理器）自动处理资源清理
print("\n使用with语句自动处理资源清理:")
try:
    with open("sample_file.txt", "r") as file:
        content = file.read()
        print(f"文件内容: {content}")
        # 即使这里发生异常，文件也会被正确关闭
except FileNotFoundError:
    print("文件不存在！")

# 自定义异常
print("\n自定义异常:")

class MyCustomError(Exception):
    """自定义异常类"""
    def __init__(self, message, code):
        self.message = message
        self.code = code
        super().__init__(self.message)
    
    def __str__(self):
        return f"{self.message} (错误代码: {self.code})"

# 引发自定义异常
try:
    raise MyCustomError("这是一个自定义错误", 1001)
except MyCustomError as e:
    print(f"捕获到自定义异常: {e}")

# 异常的传播
print("\n异常的传播:")

def function_a():
    # 这个函数会引发一个异常
    return 10 / 0

def function_b():
    # 这个函数调用function_a，但不处理异常
    return function_a()

def function_c():
    # 这个函数调用function_b，并处理异常
    try:
        return function_b()
    except ZeroDivisionError:
        print("在function_c中捕获了除以零的错误")
        return None

# 调用function_c
result = function_c()
print(f"function_c的结果: {result}")

# 使用assert语句
print("\n使用assert语句:")

def calculate_average(numbers):
    """计算平均值，要求列表不为空"""
    assert len(numbers) > 0, "列表不能为空"
    return sum(numbers) / len(numbers)

try:
    # 尝试计算空列表的平均值
    avg = calculate_average([])
except AssertionError as e:
    print(f"断言错误: {e}")

# 正确使用
try:
    avg = calculate_average([1, 2, 3, 4, 5])
    print(f"平均值: {avg}")
except AssertionError as e:
    print(f"断言错误: {e}")

# ===== 清理临时文件 =====
print("\n===== 清理临时文件 =====")

# 删除之前创建的文件
files_to_delete = [
    "renamed_sample.txt", "binary_sample.bin", "data.csv", 
    "dict_data.csv", "data.json", "person.pkl", "sample_file.txt"
]

for file_name in files_to_delete:
    if os.path.exists(file_name):
        os.remove(file_name)
        print(f"已删除: {file_name}")

# 删除之前创建的目录
if os.path.exists("test_dir"):
    # 先删除目录中的文件
    test_file = os.path.join("test_dir", "test_file.txt")
    if os.path.exists(test_file):
        os.remove(test_file)
        print(f"已删除: {test_file}")
    
    # 然后删除目录
    os.rmdir("test_dir")
    print("已删除目录: test_dir")

print("\n临时文件和目录清理完成")