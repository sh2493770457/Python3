# 01_变量和数据类型.py
# Python基础语法示例：变量和数据类型

# ===== 变量声明和赋值 =====
# Python中的变量不需要声明类型，直接赋值即可
# 变量名可以包含字母、数字和下划线，但不能以数字开头

name = "张三"  # 字符串类型
age = 25       # 整数类型
height = 1.75  # 浮点数类型
is_student = True  # 布尔类型

# 打印变量
print("姓名:", name)
print("年龄:", age)
print("身高:", height, "米")
print("是否是学生:", is_student)

# ===== 数据类型 =====
# Python的基本数据类型包括：整数、浮点数、字符串、布尔值、列表、元组、字典、集合等

# 1. 数字类型
int_num = 10        # 整数
float_num = 3.14    # 浮点数
complex_num = 1+2j  # 复数

print("\n数字类型示例:")
print("整数:", int_num, "类型:", type(int_num))
print("浮点数:", float_num, "类型:", type(float_num))
print("复数:", complex_num, "类型:", type(complex_num))

# 2. 字符串类型
str1 = "Hello"  # 双引号字符串
str2 = '世界'    # 单引号字符串
str3 = """这是一个
多行字符串"""  # 三引号可以创建多行字符串

print("\n字符串类型示例:")
print(str1 + " " + str2)  # 字符串拼接
print("字符串长度:", len(str1))
print("字符串重复:", str1 * 3)
print("字符串切片:", str1[1:4])  # 从索引1到3的字符
print("多行字符串:\n", str3)

# 3. 布尔类型
true_value = True
false_value = False

print("\n布尔类型示例:")
print("True:", true_value)
print("False:", false_value)
print("True + True =", true_value + true_value)  # 布尔值可以进行算术运算，True为1，False为0

# 4. 列表类型 - 可变序列
my_list = [1, 2, 3, "Python", True]

print("\n列表类型示例:")
print("列表:", my_list)
print("列表长度:", len(my_list))
print("列表索引:", my_list[3])  # 访问第4个元素
my_list.append("新元素")  # 添加元素
print("添加元素后:", my_list)
my_list.remove(2)  # 删除元素
print("删除元素后:", my_list)

# 5. 元组类型 - 不可变序列
my_tuple = (1, 2, 3, "Python", True)

print("\n元组类型示例:")
print("元组:", my_tuple)
print("元组长度:", len(my_tuple))
print("元组索引:", my_tuple[3])  # 访问第4个元素
# 元组不能修改，下面的操作会报错
# my_tuple[0] = 100  # 这行会引发错误

# 6. 字典类型 - 键值对
my_dict = {"name": "李四", "age": 30, "city": "北京"}

print("\n字典类型示例:")
print("字典:", my_dict)
print("字典长度:", len(my_dict))
print("访问键值:", my_dict["name"])
my_dict["email"] = "lisi@example.com"  # 添加新键值对
print("添加键值对后:", my_dict)
del my_dict["age"]  # 删除键值对
print("删除键值对后:", my_dict)

# 7. 集合类型 - 无序不重复元素集合
my_set = {1, 2, 3, 3, 4, 4, 5}  # 重复元素会被自动去除

print("\n集合类型示例:")
print("集合:", my_set)  # 输出不会有重复元素
print("集合长度:", len(my_set))
my_set.add(6)  # 添加元素
print("添加元素后:", my_set)
my_set.remove(3)  # 删除元素
print("删除元素后:", my_set)

# 8. None类型 - 表示空值
none_value = None

print("\nNone类型示例:")
print("None值:", none_value)
print("类型:", type(none_value))

# ===== 类型转换 =====
print("\n类型转换示例:")
print("整数转字符串:", str(100))
print("字符串转整数:", int("200"))
print("字符串转浮点数:", float("3.14"))
print("整数转浮点数:", float(42))
print("浮点数转整数:", int(7.8))  # 注意：这会截断小数部分，不会四舍五入

# ===== 检查变量类型 =====
print("\n检查变量类型:")
print("isinstance(name, str):", isinstance(name, str))  # 检查name是否为字符串类型
print("isinstance(age, float):", isinstance(age, float))  # 检查age是否为浮点数类型

# ===== 变量的内存地址 =====
print("\n变量的内存地址:")
print("name的内存地址:", id(name))
print("age的内存地址:", id(age))

# ===== 变量的命名规范 =====
# 1. 小写字母和下划线命名变量（蛇形命名法）
user_name = "王五"
user_age = 28

# 2. 大驼峰命名法（通常用于类名）
class UserInfo:
    pass

# 3. 常量通常使用全大写字母
MAX_VALUE = 1000
PI = 3.14159

print("\n变量命名规范示例:")
print("蛇形命名法:", user_name, user_age)
print("常量命名:", MAX_VALUE, PI)