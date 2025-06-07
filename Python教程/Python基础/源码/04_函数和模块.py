# 04_函数和模块.py
# Python基础语法示例：函数和模块

# ===== 函数定义和调用 =====
print("===== 函数定义和调用 =====")

# 定义一个简单的函数
def say_hello():
    """这是一个简单的问候函数"""
    print("你好，欢迎学习Python！")

# 调用函数
print("\n调用无参数函数:")
say_hello()

# 带参数的函数
def greet(name):
    """向指定的人问好"""
    print(f"你好，{name}！")

print("\n调用带参数函数:")
greet("张三")

# 带默认参数值的函数
def greet_with_default(name="访客"):
    """使用默认参数值向人问好"""
    print(f"你好，{name}！")

print("\n带默认参数的函数:")
greet_with_default()  # 使用默认参数
greet_with_default("李四")  # 提供自定义参数

# 返回值的函数
def add(a, b):
    """返回两个数的和"""
    return a + b

print("\n带返回值的函数:")
result = add(5, 3)
print(f"5 + 3 = {result}")

# 多个返回值
def get_person_info():
    """返回一个人的多个信息"""
    name = "王五"
    age = 30
    city = "上海"
    return name, age, city

print("\n多返回值函数:")
name, age, city = get_person_info()  # 解包返回值
print(f"姓名: {name}, 年龄: {age}, 城市: {city}")

# ===== 参数类型 =====
print("\n===== 参数类型 =====")

# 位置参数
def describe_pet(animal_type, pet_name):
    """显示宠物的信息"""
    print(f"\n我有一只{animal_type}。")
    print(f"我的{animal_type}叫{pet_name}。")

print("\n位置参数:")
describe_pet("猫", "咪咪")

# 关键字参数
print("\n关键字参数:")
describe_pet(pet_name="旺财", animal_type="狗")  # 参数顺序可以不同

# 任意数量的位置参数 *args
def make_pizza(*toppings):
    """打印顾客点的所有配料"""
    print("\n制作一个披萨，配料有:")
    for topping in toppings:
        print(f"- {topping}")

print("\n任意数量的位置参数 *args:")
make_pizza("蘑菇")
make_pizza("蘑菇", "青椒", "额外的奶酪")

# 任意数量的关键字参数 **kwargs
def build_profile(first, last, **user_info):
    """创建一个字典，包含用户的所有信息"""
    user_info['first_name'] = first
    user_info['last_name'] = last
    return user_info

print("\n任意数量的关键字参数 **kwargs:")
user_profile = build_profile('张', '三',
                            location='北京',
                            field='计算机科学',
                            age=30)
print(user_profile)

# ===== 作用域 =====
print("\n===== 变量作用域 =====")

# 局部变量
def show_local():
    """演示局部变量"""
    x = 10  # 局部变量
    print(f"函数内部 x = {x}")

print("\n局部变量:")
show_local()
# print(x)  # 这会引发错误，因为x是局部变量

# 全局变量
y = 20  # 全局变量

def show_global():
    """演示全局变量"""
    print(f"函数内部 y = {y}")

print("\n全局变量:")
print(f"函数外部 y = {y}")
show_global()

# 修改全局变量
z = 30  # 全局变量

def modify_global():
    """在函数内修改全局变量"""
    global z  # 声明z是全局变量
    z = 40
    print(f"函数内部修改后 z = {z}")

print("\n修改全局变量:")
print(f"调用函数前 z = {z}")
modify_global()
print(f"调用函数后 z = {z}")

# ===== 匿名函数（Lambda函数） =====
print("\n===== 匿名函数（Lambda函数） =====")

# 定义一个lambda函数
square = lambda x: x**2

print("\nlambda函数:")
print(f"5的平方 = {square(5)}")

# 在其他函数中使用lambda
print("\n在sorted()中使用lambda:")
students = [
    {'name': '张三', 'score': 85},
    {'name': '李四', 'score': 92},
    {'name': '王五', 'score': 78}
]

# 按分数排序
sorted_students = sorted(students, key=lambda student: student['score'], reverse=True)
print("按分数从高到低排序:")
for student in sorted_students:
    print(f"{student['name']}: {student['score']}分")

# ===== 高阶函数 =====
print("\n===== 高阶函数 =====")

# map函数
numbers = [1, 2, 3, 4, 5]
squared = list(map(lambda x: x**2, numbers))

print("\nmap函数:")
print(f"原始列表: {numbers}")
print(f"平方后: {squared}")

# filter函数
numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
even_numbers = list(filter(lambda x: x % 2 == 0, numbers))

print("\nfilter函数:")
print(f"原始列表: {numbers}")
print(f"偶数列表: {even_numbers}")

# reduce函数
from functools import reduce
numbers = [1, 2, 3, 4, 5]
sum_result = reduce(lambda x, y: x + y, numbers)

print("\nreduce函数:")
print(f"列表: {numbers}")
print(f"总和: {sum_result}")

# ===== 装饰器 =====
print("\n===== 装饰器 =====")

# 定义一个简单的装饰器
def my_decorator(func):
    def wrapper():
        print("函数执行前的操作")
        func()
        print("函数执行后的操作")
    return wrapper

# 使用装饰器
@my_decorator
def say_hello():
    print("Hello, World!")

print("\n使用装饰器:")
say_hello()

# 带参数的装饰器
def repeat(n):
    def decorator(func):
        def wrapper(*args, **kwargs):
            for _ in range(n):
                result = func(*args, **kwargs)
            return result
        return wrapper
    return decorator

@repeat(3)
def greet(name):
    print(f"你好，{name}！")
    return "问候完成"

print("\n带参数的装饰器:")
greet("赵六")

# ===== 递归函数 =====
print("\n===== 递归函数 =====")

# 计算阶乘的递归函数
def factorial(n):
    """计算n的阶乘"""
    if n == 0 or n == 1:
        return 1
    else:
        return n * factorial(n-1)

print("\n递归计算阶乘:")
print(f"5的阶乘 = {factorial(5)}")

# 斐波那契数列的递归函数
def fibonacci(n):
    """返回斐波那契数列的第n个数"""
    if n <= 0:
        return "输入必须是正整数"
    elif n == 1:
        return 0
    elif n == 2:
        return 1
    else:
        return fibonacci(n-1) + fibonacci(n-2)

print("\n递归计算斐波那契数列:")
print("斐波那契数列的前10个数:")
for i in range(1, 11):
    print(f"第{i}个数: {fibonacci(i)}")

# ===== 模块导入 =====
print("\n===== 模块导入 =====")

# 导入标准库模块
import math
import random
from datetime import datetime

print("\n使用math模块:")
print(f"π的值: {math.pi}")
print(f"2的平方根: {math.sqrt(2)}")

print("\n使用random模块:")
print(f"随机整数(1-10): {random.randint(1, 10)}")
print(f"随机选择: {random.choice(['苹果', '香蕉', '橙子'])}")

print("\n使用datetime模块:")
now = datetime.now()
print(f"当前日期和时间: {now}")
print(f"格式化日期: {now.strftime('%Y-%m-%d %H:%M:%S')}")

# ===== 创建自定义模块 =====
# 注意：通常自定义模块会保存在单独的.py文件中
# 这里为了演示，我们假设已经有了一个名为my_module.py的文件

print("\n===== 自定义模块 =====")
print("在实际应用中，你可以创建自己的模块，例如:")
print("""# my_module.py
def greet(name):
    return f"你好，{name}！"

def add(a, b):
    return a + b

PI = 3.14159
""")

print("\n然后在其他文件中导入和使用:")
print("""# 导入整个模块
import my_module
print(my_module.greet("张三"))
print(my_module.add(5, 3))
print(my_module.PI)

# 导入特定函数或变量
from my_module import greet, PI
print(greet("李四"))
print(PI)

# 使用别名
import my_module as mm
print(mm.greet("王五"))
""")

# ===== 包 =====
print("\n===== 包 =====")
print("包是一种组织模块的方式，它是一个包含__init__.py文件的目录")
print("目录结构示例:")
print("""
my_package/
    __init__.py
    module1.py
    module2.py
    subpackage/
        __init__.py
        module3.py
""")

print("\n导入包中的模块:")
print("""# 导入包中的模块
import my_package.module1
from my_package import module2
from my_package.subpackage import module3

# 使用包中的函数
my_package.module1.function1()
module2.function2()
module3.function3()
""")