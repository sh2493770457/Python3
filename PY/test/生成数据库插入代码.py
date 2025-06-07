import random

# 生成随机手机号的函数
def generate_mobile():
    return f"{random.randint(10000000000, 19999999999)}"

# 生成随机价格的函数
def generate_price():
    return random.randint(100, 1000)

# 生成随机等级的函数
def generate_level():
    return random.randint(1, 4)

# 生成随机状态的函数
def generate_status():
    return random.randint(1, 2)

# 生成300行数据
data = []
for _ in range(300):
    mobile = generate_mobile() # 生成随机手机号
    price = generate_price()   # 生成随机价格
    level = generate_level()   # 生成随机等级
    status = generate_status() # 生成随机状态
    data.append(f"('{mobile}', {price}, {level}, {status})")

# 将所有行连接成一个单一的SQL插入语句
sql_insert = "INSERT INTO app01_prettynum (mobile, price, level, status) VALUES\n" + ",\n".join(data) + ";"

# 打印SQL插入语句
print(sql_insert)
