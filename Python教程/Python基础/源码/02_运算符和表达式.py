# 02_运算符和表达式.py
# Python基础语法示例：运算符和表达式

# ===== 算术运算符 =====
print("===== 算术运算符 =====")
a = 10
b = 3

print("a =", a, "b =", b)
print("加法 a + b =", a + b)       # 加法
print("减法 a - b =", a - b)       # 减法
print("乘法 a * b =", a * b)       # 乘法
print("除法 a / b =", a / b)       # 除法（返回浮点数）
print("整除 a // b =", a // b)     # 整除（返回整数）
print("取余 a % b =", a % b)       # 取余
print("幂运算 a ** b =", a ** b)   # 幂运算（a的b次方）

# ===== 比较运算符 =====
print("\n===== 比较运算符 =====")
print("等于 a == b:", a == b)       # 等于
print("不等于 a != b:", a != b)     # 不等于
print("大于 a > b:", a > b)         # 大于
print("小于 a < b:", a < b)         # 小于
print("大于等于 a >= b:", a >= b)   # 大于等于
print("小于等于 a <= b:", a <= b)   # 小于等于

# ===== 赋值运算符 =====
print("\n===== 赋值运算符 =====")
x = 5
print("初始值 x =", x)

x += 3      # 等同于 x = x + 3
print("x += 3 后，x =", x)

x -= 2      # 等同于 x = x - 2
print("x -= 2 后，x =", x)

x *= 4      # 等同于 x = x * 4
print("x *= 4 后，x =", x)

x /= 2      # 等同于 x = x / 2
print("x /= 2 后，x =", x)

x //= 2     # 等同于 x = x // 2
print("x //= 2 后，x =", x)

x %= 3      # 等同于 x = x % 3
print("x %= 3 后，x =", x)

x **= 2     # 等同于 x = x ** 2
print("x **= 2 后，x =", x)

# ===== 逻辑运算符 =====
print("\n===== 逻辑运算符 =====")
p = True
q = False

print("p =", p, "q =", q)
print("与运算 p and q:", p and q)   # 逻辑与：两者都为True时结果为True
print("或运算 p or q:", p or q)     # 逻辑或：至少一个为True时结果为True
print("非运算 not p:", not p)       # 逻辑非：取反
print("非运算 not q:", not q)

# 逻辑运算符的短路特性
print("\n逻辑运算符的短路特性:")
print("True or 表达式:")
# 由于or前面已经是True，后面的表达式不会被执行
result1 = True or print("这不会被打印")
print("结果:", result1)

print("\nFalse and 表达式:")
# 由于and前面已经是False，后面的表达式不会被执行
result2 = False and print("这不会被打印")
print("结果:", result2)

# ===== 位运算符 =====
print("\n===== 位运算符 =====")
c = 60      # 二进制：0011 1100
d = 13      # 二进制：0000 1101

print("c =", c, "(", bin(c), ")")
print("d =", d, "(", bin(d), ")")
print("按位与 c & d =", c & d, "(", bin(c & d), ")")       # 按位与
print("按位或 c | d =", c | d, "(", bin(c | d), ")")       # 按位或
print("按位异或 c ^ d =", c ^ d, "(", bin(c ^ d), ")")     # 按位异或
print("按位取反 ~c =", ~c, "(", bin(~c & 0xFF), ")")       # 按位取反
print("左移 c << 2 =", c << 2, "(", bin(c << 2), ")")      # 左移2位
print("右移 c >> 2 =", c >> 2, "(", bin(c >> 2), ")")      # 右移2位

# ===== 成员运算符 =====
print("\n===== 成员运算符 =====")
my_list = [1, 3, 5, 7, 9]

print("列表:", my_list)
print("3 in my_list:", 3 in my_list)           # 检查元素是否在列表中
print("4 in my_list:", 4 in my_list)
print("4 not in my_list:", 4 not in my_list)   # 检查元素是否不在列表中

# ===== 身份运算符 =====
print("\n===== 身份运算符 =====")
a = [1, 2, 3]
b = [1, 2, 3]
c = a

print("a =", a, "id(a) =", id(a))
print("b =", b, "id(b) =", id(b))
print("c =", c, "id(c) =", id(c))

print("a is b:", a is b)       # 检查两个对象是否是同一个对象（引用相同）
print("a is c:", a is c)       # a和c引用同一个对象
print("a is not b:", a is not b)   # 检查两个对象是否不是同一个对象

# ===== 运算符优先级 =====
print("\n===== 运算符优先级示例 =====")
result = 5 + 3 * 2
print("5 + 3 * 2 =", result)  # 乘法优先级高于加法

result = (5 + 3) * 2
print("(5 + 3) * 2 =", result)  # 括号内的运算优先执行

result = 10 / 2 + 3
print("10 / 2 + 3 =", result)  # 除法优先级高于加法

result = 10 / (2 + 3)
print("10 / (2 + 3) =", result)  # 括号内的运算优先执行

result = 4 ** 2 ** 3
print("4 ** 2 ** 3 =", result)  # 幂运算是从右向左结合的，等价于4 ** (2 ** 3)

# ===== 表达式 =====
print("\n===== 表达式示例 =====")

# 算术表达式
arithmetic_expr = (10 + 5) * 2 / 5
print("算术表达式 (10 + 5) * 2 / 5 =", arithmetic_expr)

# 比较表达式
comparison_expr = 10 > 5 and 7 < 12
print("比较表达式 10 > 5 and 7 < 12 =", comparison_expr)

# 三元表达式（条件表达式）
x = 10
y = 20
max_value = x if x > y else y
print(f"x = {x}, y = {y}")
print("三元表达式 x if x > y else y =", max_value)

# 列表推导式
numbers = [1, 2, 3, 4, 5]
squares = [x**2 for x in numbers]
print("原始列表:", numbers)
print("列表推导式 [x**2 for x in numbers] =", squares)

# 带条件的列表推导式
even_squares = [x**2 for x in numbers if x % 2 == 0]
print("带条件的列表推导式 [x**2 for x in numbers if x % 2 == 0] =", even_squares)

# 字典推导式
dict_comp = {x: x**2 for x in range(1, 6)}
print("字典推导式 {x: x**2 for x in range(1, 6)} =", dict_comp)

# 集合推导式
set_comp = {x**2 for x in range(1, 6)}
print("集合推导式 {x**2 for x in range(1, 6)} =", set_comp)

# 生成器表达式（返回生成器对象）
gen_expr = (x**2 for x in range(1, 6))
print("生成器表达式 (x**2 for x in range(1, 6)) =", gen_expr)
print("生成器表达式转列表:", list(gen_expr))