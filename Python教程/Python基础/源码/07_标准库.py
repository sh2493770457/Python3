# 07_标准库.py
# Python基础语法示例：标准库的使用

# ===== 日期和时间处理（datetime模块） =====
print("===== 日期和时间处理（datetime模块） =====")

import datetime

# 获取当前日期和时间
print("\n获取当前日期和时间:")
now = datetime.datetime.now()
print(f"当前日期和时间: {now}")
print(f"当前日期: {now.date()}")
print(f"当前时间: {now.time()}")

# 创建特定的日期和时间
print("\n创建特定的日期和时间:")
specific_date = datetime.date(2023, 12, 31)
specific_time = datetime.time(12, 30, 45)
specific_datetime = datetime.datetime(2023, 12, 31, 12, 30, 45)

print(f"特定日期: {specific_date}")
print(f"特定时间: {specific_time}")
print(f"特定日期时间: {specific_datetime}")

# 日期格式化
print("\n日期格式化:")
formatted_date = now.strftime("%Y年%m月%d日 %H:%M:%S")
print(f"格式化日期: {formatted_date}")

# 日期解析
print("\n日期解析:")
date_string = "2023-10-15 14:30:00"
parsed_date = datetime.datetime.strptime(date_string, "%Y-%m-%d %H:%M:%S")
print(f"解析的日期: {parsed_date}")

# 日期计算
print("\n日期计算:")
tomorrow = now + datetime.timedelta(days=1)
yesterday = now - datetime.timedelta(days=1)
two_hours_later = now + datetime.timedelta(hours=2)

print(f"明天: {tomorrow.date()}")
print(f"昨天: {yesterday.date()}")
print(f"两小时后: {two_hours_later}")

# 计算两个日期之间的差异
print("\n计算两个日期之间的差异:")
date1 = datetime.datetime(2023, 1, 1)
date2 = datetime.datetime(2023, 12, 31)
difference = date2 - date1

print(f"日期1: {date1.date()}")
print(f"日期2: {date2.date()}")
print(f"相差天数: {difference.days}天")

# ===== 数学运算（math模块） =====
print("\n===== 数学运算（math模块） =====")

import math

# 常量
print("\n数学常量:")
print(f"π (pi): {math.pi}")
print(f"自然对数的底 e: {math.e}")
print(f"无穷大: {math.inf}")
print(f"NaN (非数字): {math.nan}")

# 基本数学函数
print("\n基本数学函数:")
print(f"绝对值 abs(-5): {abs(-5)}")
print(f"向上取整 math.ceil(4.3): {math.ceil(4.3)}")
print(f"向下取整 math.floor(4.7): {math.floor(4.7)}")
print(f"四舍五入 round(4.5): {round(4.5)}")
print(f"四舍五入到2位小数 round(3.14159, 2): {round(3.14159, 2)}")

# 幂和对数函数
print("\n幂和对数函数:")
print(f"2的3次方 math.pow(2, 3): {math.pow(2, 3)}")
print(f"平方根 math.sqrt(16): {math.sqrt(16)}")
print(f"自然对数 math.log(10): {math.log(10)}")
print(f"以10为底的对数 math.log10(100): {math.log10(100)}")

# 三角函数
print("\n三角函数:")
print(f"正弦 math.sin(math.pi/2): {math.sin(math.pi/2)}")
print(f"余弦 math.cos(math.pi): {math.cos(math.pi)}")
print(f"正切 math.tan(math.pi/4): {math.tan(math.pi/4)}")

# 角度和弧度转换
print("\n角度和弧度转换:")
print(f"45度转弧度 math.radians(45): {math.radians(45)}")
print(f"π/4弧度转角度 math.degrees(math.pi/4): {math.degrees(math.pi/4)}")

# 双曲函数
print("\n双曲函数:")
print(f"双曲正弦 math.sinh(1): {math.sinh(1)}")
print(f"双曲余弦 math.cosh(1): {math.cosh(1)}")

# 特殊函数
print("\n特殊函数:")
print(f"阶乘 math.factorial(5): {math.factorial(5)}")
print(f"最大公约数 math.gcd(12, 18): {math.gcd(12, 18)}")

# ===== 随机数生成（random模块） =====
print("\n===== 随机数生成（random模块） =====")

import random

# 设置随机种子（使结果可重现）
random.seed(42)

# 生成随机数
print("\n生成随机数:")
print(f"0到1之间的随机浮点数 random.random(): {random.random()}")
print(f"指定范围的随机整数 random.randint(1, 10): {random.randint(1, 10)}")
print(f"指定范围的随机浮点数 random.uniform(1.0, 10.0): {random.uniform(1.0, 10.0)}")

# 从序列中随机选择
print("\n从序列中随机选择:")
fruits = ["苹果", "香蕉", "橙子", "葡萄", "西瓜"]
print(f"随机选择一个元素 random.choice(fruits): {random.choice(fruits)}")
print(f"随机选择多个元素 random.sample(fruits, 3): {random.sample(fruits, 3)}")

# 打乱序列
print("\n打乱序列:")
numbers = list(range(1, 11))
print(f"原始列表: {numbers}")
random.shuffle(numbers)
print(f"打乱后: {numbers}")

# 生成随机密码示例
print("\n生成随机密码示例:")
def generate_password(length=8):
    """生成指定长度的随机密码"""
    chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+"
    return ''.join(random.choice(chars) for _ in range(length))

print(f"8位随机密码: {generate_password()}")
print(f"12位随机密码: {generate_password(12)}")

# ===== 系统和操作系统交互（os和sys模块） =====
print("\n===== 系统和操作系统交互（os和sys模块） =====")

import os
import sys
import platform

# 系统信息
print("\n系统信息:")
print(f"操作系统名称: {os.name}")
print(f"平台信息: {platform.platform()}")
print(f"Python版本: {sys.version}")
print(f"Python解释器路径: {sys.executable}")

# 环境变量
print("\n环境变量:")
print(f"当前工作目录: {os.getcwd()}")
print(f"HOME环境变量: {os.environ.get('HOME', os.environ.get('USERPROFILE', '未设置'))}")

# 命令行参数
print("\n命令行参数:")
print(f"脚本名称: {sys.argv[0]}")
print(f"所有命令行参数: {sys.argv}")

# 路径操作
print("\n路径操作:")
file_path = os.path.join("folder", "subfolder", "file.txt")
print(f"拼接路径: {file_path}")
print(f"绝对路径: {os.path.abspath(file_path)}")
print(f"目录名: {os.path.dirname(file_path)}")
print(f"文件名: {os.path.basename(file_path)}")
print(f"文件名和扩展名分离: {os.path.splitext(os.path.basename(file_path))}")

# ===== 正则表达式（re模块） =====
print("\n===== 正则表达式（re模块） =====")

import re

# 基本匹配
print("\n基本匹配:")
text = "Python编程语言非常强大和灵活。Python适合初学者和专业人士。"
matches = re.findall(r"Python", text)
print(f"文本: {text}")
print(f"找到 'Python' 的次数: {len(matches)}")

# 使用模式
print("\n使用模式:")
pattern = r"\bPython\b"  # \b表示单词边界
matches = re.findall(pattern, text)
print(f"找到完整单词 'Python' 的次数: {len(matches)}")

# 匹配多种模式
print("\n匹配多种模式:")
text = "我的电话号码是123-4567-8901和456-7890-1234"
phone_pattern = r"\d{3}-\d{4}-\d{4}"
phone_numbers = re.findall(phone_pattern, text)
print(f"文本: {text}")
print(f"找到的电话号码: {phone_numbers}")

# 替换文本
print("\n替换文本:")
text = "Python是一种编程语言，python很容易学习。"
replaced_text = re.sub(r"[Pp]ython", "Java", text)
print(f"原文本: {text}")
print(f"替换后: {replaced_text}")

# 分割文本
print("\n分割文本:")
text = "apple,banana;orange,grape;pear"
split_text = re.split(r"[,;]", text)
print(f"原文本: {text}")
print(f"分割后: {split_text}")

# 使用组捕获
print("\n使用组捕获:")
text = "我的邮箱是example@example.com和test@test.org"
email_pattern = r"([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\.[a-zA-Z]{2,})"
emails = re.findall(email_pattern, text)
print(f"文本: {text}")
print(f"找到的邮箱: {emails}")

# 使用re.match和re.search
print("\n使用re.match和re.search:")
text = "Python是一种编程语言"
match_result = re.match(r"Python", text)
search_result = re.search(r"编程", text)

print(f"match结果: {'匹配成功' if match_result else '匹配失败'}")
if match_result:
    print(f"匹配的文本: {match_result.group()}")

print(f"search结果: {'匹配成功' if search_result else '匹配失败'}")
if search_result:
    print(f"匹配的文本: {search_result.group()}")
    print(f"匹配的位置: {search_result.start()}-{search_result.end()}")

# ===== 数据压缩（zipfile模块） =====
print("\n===== 数据压缩（zipfile模块） =====")

import zipfile
import os

# 创建一些示例文件
print("\n创建示例文件:")
if not os.path.exists("zip_example"):
    os.mkdir("zip_example")

file_paths = [
    os.path.join("zip_example", "file1.txt"),
    os.path.join("zip_example", "file2.txt"),
    os.path.join("zip_example", "file3.txt")
]

for i, file_path in enumerate(file_paths):
    with open(file_path, "w") as f:
        f.write(f"这是示例文件 {i+1} 的内容。\n")
    print(f"创建文件: {file_path}")

# 创建ZIP文件
print("\n创建ZIP文件:")
zip_file_path = "example.zip"
with zipfile.ZipFile(zip_file_path, "w") as zipf:
    for file_path in file_paths:
        zipf.write(file_path)
    print(f"添加文件到ZIP: {file_path}")

print(f"ZIP文件已创建: {zip_file_path}")

# 查看ZIP文件内容
print("\n查看ZIP文件内容:")
with zipfile.ZipFile(zip_file_path, "r") as zipf:
    print(f"ZIP文件中的文件列表:")
    for file_info in zipf.infolist():
        print(f"  {file_info.filename}, 大小: {file_info.file_size}字节")

# 从ZIP文件中提取文件
print("\n从ZIP文件中提取文件:")
extract_dir = "extracted_files"
if not os.path.exists(extract_dir):
    os.mkdir(extract_dir)

with zipfile.ZipFile(zip_file_path, "r") as zipf:
    zipf.extractall(extract_dir)
print(f"文件已提取到: {extract_dir}")

# 清理创建的文件和目录
print("\n清理创建的文件和目录:")

# 删除提取的文件
for root, dirs, files in os.walk(extract_dir, topdown=False):
    for file in files:
        os.remove(os.path.join(root, file))
        print(f"删除文件: {os.path.join(root, file)}")

# 删除提取目录
os.rmdir(extract_dir)
print(f"删除目录: {extract_dir}")

# 删除原始示例文件
for file_path in file_paths:
    os.remove(file_path)
    print(f"删除文件: {file_path}")

# 删除示例目录
os.rmdir("zip_example")
print(f"删除目录: zip_example")

# 删除ZIP文件
os.remove(zip_file_path)
print(f"删除文件: {zip_file_path}")

# ===== URL处理和网络请求（urllib模块） =====
print("\n===== URL处理和网络请求（urllib模块） =====")

from urllib import parse, request
import json

# URL解析
print("\nURL解析:")
url = "https://www.example.com/path/to/page.html?name=value&lang=zh-CN"
parsed_url = parse.urlparse(url)

print(f"原始URL: {url}")
print(f"协议: {parsed_url.scheme}")
print(f"网络位置: {parsed_url.netloc}")
print(f"路径: {parsed_url.path}")
print(f"参数: {parsed_url.query}")

# 解析查询参数
print("\n解析查询参数:")
query_params = parse.parse_qs(parsed_url.query)
print(f"查询参数: {query_params}")

# URL编码和解码
print("\nURL编码和解码:")
text = "这是一段中文文本"
encoded_text = parse.quote(text)
decoded_text = parse.unquote(encoded_text)

print(f"原始文本: {text}")
print(f"URL编码后: {encoded_text}")
print(f"URL解码后: {decoded_text}")

# 构建URL
print("\n构建URL:")
base_url = "https://www.example.com/search"
params = {"q": "Python编程", "lang": "zh-CN"}
query_string = parse.urlencode(params)
full_url = f"{base_url}?{query_string}"

print(f"基础URL: {base_url}")
print(f"参数: {params}")
print(f"查询字符串: {query_string}")
print(f"完整URL: {full_url}")

# 发送HTTP请求（注释掉实际请求，避免网络依赖）
print("\n发送HTTP请求:")
print("注意: 实际的HTTP请求已被注释掉，以避免网络依赖")

'''
# 发送GET请求
response = request.urlopen("https://httpbin.org/get")
data = response.read().decode("utf-8")
print(f"响应状态码: {response.status}")
print(f"响应数据: {data[:100]}...")

# 发送POST请求
post_data = parse.urlencode({"key": "value"}).encode()
req = request.Request("https://httpbin.org/post", data=post_data)
response = request.urlopen(req)
data = response.read().decode("utf-8")
print(f"POST响应数据: {data[:100]}...")
'''

# ===== JSON处理（json模块） =====
print("\n===== JSON处理（json模块） =====")

import json

# Python对象转JSON
print("\nPython对象转JSON:")
python_obj = {
    "name": "张三",
    "age": 30,
    "skills": ["Python", "JavaScript", "SQL"],
    "is_student": False,
    "address": {
        "city": "北京",
        "street": "朝阳路"
    }
}

json_str = json.dumps(python_obj, ensure_ascii=False, indent=2)
print(f"Python对象: {python_obj}")
print(f"JSON字符串:\n{json_str}")

# JSON转Python对象
print("\nJSON转Python对象:")
json_str = '{"name":"李四","age":25,"skills":["Java","C++"],"is_student":true}'
parsed_obj = json.loads(json_str)

print(f"JSON字符串: {json_str}")
print(f"解析后的Python对象: {parsed_obj}")
print(f"姓名: {parsed_obj['name']}")
print(f"技能: {', '.join(parsed_obj['skills'])}")

# 自定义JSON编码
print("\n自定义JSON编码:")

class Person:
    def __init__(self, name, age):
        self.name = name
        self.age = age

# 自定义JSON编码器
class PersonEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, Person):
            return {"name": obj.name, "age": obj.age, "type": "Person"}
        return super().default(obj)

person = Person("王五", 35)

# 使用自定义编码器
json_str = json.dumps(person, cls=PersonEncoder, ensure_ascii=False)
print(f"自定义编码后的JSON: {json_str}")

# ===== 命令行参数解析（argparse模块） =====
print("\n===== 命令行参数解析（argparse模块） =====")

import argparse

# 创建解析器
parser = argparse.ArgumentParser(description="命令行参数解析示例")

# 添加参数
parser.add_argument("-f", "--file", help="要处理的文件路径")
parser.add_argument("-n", "--number", type=int, default=10, help="指定一个数字（默认为10）")
parser.add_argument("-v", "--verbose", action="store_true", help="启用详细输出")

# 解析参数（使用模拟参数而不是实际命令行参数）
print("\n解析命令行参数:")
print("注意: 使用模拟参数而不是实际命令行参数")

# 模拟命令行参数
simulated_args = ["-f", "example.txt", "--number", "42", "-v"]
args = parser.parse_args(simulated_args)

print(f"解析结果:")
print(f"  文件路径: {args.file}")
print(f"  数字: {args.number}")
print(f"  详细模式: {args.verbose}")

# 根据参数执行操作
if args.verbose:
    print("详细模式已启用，将显示更多信息")

if args.file:
    print(f"将处理文件: {args.file}")

print(f"将使用数字: {args.number}")

# ===== 单元测试（unittest模块） =====
print("\n===== 单元测试（unittest模块） =====")

import unittest

# 要测试的函数
def add(a, b):
    return a + b

def multiply(a, b):
    return a * b

# 测试用例类
class TestMathFunctions(unittest.TestCase):
    def test_add(self):
        self.assertEqual(add(2, 3), 5)
        self.assertEqual(add(-1, 1), 0)
        self.assertEqual(add(-1, -1), -2)
    
    def test_multiply(self):
        self.assertEqual(multiply(2, 3), 6)
        self.assertEqual(multiply(-1, 1), -1)
        self.assertEqual(multiply(-1, -1), 1)
    
    def test_add_float(self):
        self.assertAlmostEqual(add(0.1, 0.2), 0.3, places=1)

# 运行测试
print("\n运行单元测试:")
test_suite = unittest.TestLoader().loadTestsFromTestCase(TestMathFunctions)
test_result = unittest.TextTestRunner(verbosity=2).run(test_suite)

print(f"\n测试结果摘要:")
print(f"  运行测试数: {test_result.testsRun}")
print(f"  成功: {test_result.testsRun - len(test_result.errors) - len(test_result.failures)}")
print(f"  失败: {len(test_result.failures)}")
print(f"  错误: {len(test_result.errors)}")

# ===== 日志记录（logging模块） =====
print("\n===== 日志记录（logging模块） =====")

import logging

# 配置日志记录
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler()  # 输出到控制台
    ]
)

# 创建日志记录器
logger = logging.getLogger("example_logger")

# 记录不同级别的日志
print("\n记录不同级别的日志:")
logger.debug("这是一条调试信息")
logger.info("这是一条信息消息")
logger.warning("这是一条警告消息")
logger.error("这是一条错误消息")
logger.critical("这是一条严重错误消息")

# 使用日志记录异常
print("\n使用日志记录异常:")
try:
    result = 10 / 0
except Exception as e:
    logger.exception("发生了一个异常")

# ===== 虚拟环境和包管理 =====
print("\n===== 虚拟环境和包管理 =====")

print("\n虚拟环境是Python的一个重要特性，它允许你创建独立的Python环境。")
print("以下是创建和使用虚拟环境的基本命令:")

print("\n1. 创建虚拟环境:")
print("   python -m venv myenv")

print("\n2. 激活虚拟环境:")
print("   Windows: myenv\\Scripts\\activate")
print("   Unix/MacOS: source myenv/bin/activate")

print("\n3. 安装包:")
print("   pip install package_name")

print("\n4. 查看已安装的包:")
print("   pip list")

print("\n5. 导出依赖:")
print("   pip freeze > requirements.txt")

print("\n6. 从依赖文件安装:")
print("   pip install -r requirements.txt")

print("\n7. 退出虚拟环境:")
print("   deactivate")

# ===== 总结 =====
print("\n===== 总结 =====")

print("\nPython标准库非常丰富，本示例只展示了一小部分常用模块。")
print("其他有用的标准库模块包括:")
print("  - collections: 提供额外的数据结构（如defaultdict, Counter等）")
print("  - itertools: 提供高效的迭代器工具")
print("  - functools: 提供高阶函数和操作可调用对象的工具")
print("  - threading和multiprocessing: 提供并发执行的功能")
print("  - sqlite3: 提供SQLite数据库接口")
print("  - email: 处理电子邮件消息")
print("  - http: HTTP协议客户端和服务器")
print("  - tkinter: 标准GUI库")
print("  - asyncio: 异步I/O, 事件循环, 协程和任务")
print("\n学习和使用这些标准库可以大大提高你的Python编程效率。")