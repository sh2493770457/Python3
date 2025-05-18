# 03_条件语句和循环.py
# Python基础语法示例：条件语句和循环

# ===== if-elif-else 条件语句 =====
print("===== if-elif-else 条件语句 =====")

# 基本的if语句
age = 20
if age >= 18:
    print("你已经成年了")

# if-else语句
score = 75
print(f"\n分数: {score}")
if score >= 60:
    print("考试通过")
else:
    print("考试不通过")

# if-elif-else语句
grade = 85
print(f"\n成绩: {grade}")
if grade >= 90:
    print("优秀")
elif grade >= 80:
    print("良好")
elif grade >= 60:
    print("及格")
else:
    print("不及格")

# 嵌套的if语句
age = 20
has_id = True
print(f"\n年龄: {age}, 有身份证: {has_id}")
if age >= 18:
    if has_id:
        print("可以办理银行卡")
    else:
        print("需要先办理身份证")
else:
    print("未成年人，需要监护人陪同")

# 条件表达式（三元运算符）
x = 10
y = 20
max_value = x if x > y else y
print(f"\nx = {x}, y = {y}")
print(f"较大值: {max_value}")

# 逻辑运算符在条件语句中的应用
age = 25
income = 8000
print(f"\n年龄: {age}, 收入: {income}")
if age > 18 and income > 5000:
    print("符合贷款条件")
else:
    print("不符合贷款条件")

# ===== while 循环 =====
print("\n===== while 循环 =====")

# 基本的while循环
count = 1
print("\n基本的while循环:")
while count <= 5:
    print(f"计数: {count}")
    count += 1

# 使用break语句
print("\n使用break的while循环:")
num = 1
while True:  # 无限循环
    print(f"数字: {num}")
    num += 1
    if num > 5:  # 当num大于5时跳出循环
        break

# 使用continue语句
print("\n使用continue的while循环:")
num = 0
while num < 10:
    num += 1
    if num % 2 == 0:  # 如果是偶数则跳过
        continue
    print(f"奇数: {num}")

# while循环的else子句
print("\nwhile循环的else子句:")
count = 1
while count <= 5:
    print(f"计数: {count}")
    count += 1
else:
    print("计数完成")

# ===== for 循环 =====
print("\n===== for 循环 =====")

# 遍历列表
fruits = ["苹果", "香蕉", "橙子", "葡萄"]
print("\n遍历列表:")
for fruit in fruits:
    print(f"水果: {fruit}")

# 遍历字符串
name = "Python"
print("\n遍历字符串:")
for char in name:
    print(f"字符: {char}")

# 使用range()函数
print("\n使用range()函数:")
for i in range(5):  # 从0到4
    print(f"数字: {i}")

print("\nrange()指定范围:")
for i in range(2, 8):  # 从2到7
    print(f"数字: {i}")

print("\nrange()指定步长:")
for i in range(1, 10, 2):  # 从1到9，步长为2
    print(f"数字: {i}")

# 遍历字典
person = {"name": "张三", "age": 30, "city": "北京"}
print("\n遍历字典的键:")
for key in person:
    print(f"键: {key}, 值: {person[key]}")

print("\n遍历字典的键值对:")
for key, value in person.items():
    print(f"键: {key}, 值: {value}")

# 使用enumerate()函数获取索引和值
fruits = ["苹果", "香蕉", "橙子", "葡萄"]
print("\n使用enumerate()函数:")
for index, fruit in enumerate(fruits):
    print(f"索引: {index}, 水果: {fruit}")

# 使用zip()函数同时遍历多个列表
names = ["张三", "李四", "王五"]
ages = [20, 25, 22]
print("\n使用zip()函数:")
for name, age in zip(names, ages):
    print(f"姓名: {name}, 年龄: {age}")

# 使用break语句
print("\n在for循环中使用break:")
for i in range(1, 10):
    if i == 5:
        break
    print(f"数字: {i}")

# 使用continue语句
print("\n在for循环中使用continue:")
for i in range(1, 10):
    if i % 2 == 0:  # 如果是偶数则跳过
        continue
    print(f"奇数: {i}")

# for循环的else子句
print("\nfor循环的else子句:")
for i in range(1, 6):
    print(f"数字: {i}")
else:
    print("循环正常完成")

# 嵌套循环
print("\n嵌套循环:")
for i in range(1, 4):
    for j in range(1, 4):
        print(f"i = {i}, j = {j}")

# 生成九九乘法表
print("\n九九乘法表:")
for i in range(1, 10):
    for j in range(1, i + 1):
        print(f"{j} × {i} = {i*j}", end="\t")
    print()  # 换行

# 列表推导式 - 一种简洁的循环方式
numbers = [1, 2, 3, 4, 5]
squares = [x**2 for x in numbers]
print("\n列表推导式:")
print(f"原始列表: {numbers}")
print(f"平方后: {squares}")

# 带条件的列表推导式
even_squares = [x**2 for x in numbers if x % 2 == 0]
print("\n带条件的列表推导式:")
print(f"偶数的平方: {even_squares}")

# ===== 循环控制技巧 =====
print("\n===== 循环控制技巧 =====")

# 使用else子句检测循环是否正常完成
print("\n检测循环是否正常完成:")
for n in range(2, 10):
    for x in range(2, n):
        if n % x == 0:
            print(f"{n} 等于 {x} * {n//x}")
            break
    else:
        # 循环正常结束，没有触发break
        print(f"{n} 是质数")

# 使用pass语句作为占位符
print("\n使用pass语句:")
for i in range(5):
    if i == 2:
        pass  # 什么都不做，只是一个占位符
    else:
        print(f"数字: {i}")