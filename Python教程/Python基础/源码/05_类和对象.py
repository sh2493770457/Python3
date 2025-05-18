# 05_类和对象.py
# Python基础语法示例：类和对象

# ===== 类的定义和实例化 =====
print("===== 类的定义和实例化 =====")

# 定义一个简单的类
class Person:
    """这是一个表示人的简单类"""
    # 类变量（被所有实例共享）
    species = "人类"
    
    # 初始化方法（构造函数）
    def __init__(self, name, age):
        # 实例变量（每个实例独有）
        self.name = name
        self.age = age
    
    # 实例方法
    def introduce(self):
        """自我介绍方法"""
        return f"你好，我是{self.name}，今年{self.age}岁。"
    
    def have_birthday(self):
        """过生日，年龄增加1"""
        self.age += 1
        return f"{self.name}过生日了，现在{self.age}岁了。"

# 创建类的实例（对象）
print("\n创建类的实例:")
person1 = Person("张三", 25)
person2 = Person("李四", 30)

# 访问实例变量
print(f"person1的名字: {person1.name}, 年龄: {person1.age}")
print(f"person2的名字: {person2.name}, 年龄: {person2.age}")

# 访问类变量
print(f"Person类的species: {Person.species}")
print(f"person1的species: {person1.species}")

# 调用实例方法
print(person1.introduce())
print(person2.introduce())

# 修改实例变量
person1.age = 26
print(f"修改后，person1的年龄: {person1.age}")

# 调用方法修改实例变量
print(person2.have_birthday())

# ===== 类的继承 =====
print("\n===== 类的继承 =====")

# 定义一个基类（父类）
class Animal:
    """动物基类"""
    def __init__(self, name, species):
        self.name = name
        self.species = species
    
    def make_sound(self):
        """发出声音"""
        return "一些声音"
    
    def __str__(self):
        """返回对象的字符串表示"""
        return f"{self.name}是一只{self.species}"

# 定义一个派生类（子类）
class Dog(Animal):
    """狗类，继承自Animal"""
    def __init__(self, name, breed):
        # 调用父类的初始化方法
        super().__init__(name, "狗")
        self.breed = breed
    
    # 重写父类方法
    def make_sound(self):
        """重写父类的方法"""
        return "汪汪！"
    
    # 添加子类特有的方法
    def fetch(self, item):
        """捡东西"""
        return f"{self.name}捡回了{item}"

# 创建子类实例
print("\n创建子类实例:")
my_dog = Dog("旺财", "金毛")

# 访问从父类继承的属性和方法
print(f"名字: {my_dog.name}, 物种: {my_dog.species}, 品种: {my_dog.breed}")
print(str(my_dog))  # 调用__str__方法

# 调用重写的方法
print(f"{my_dog.name}叫: {my_dog.make_sound()}")

# 调用子类特有的方法
print(my_dog.fetch("球"))

# 多重继承
class FlyingAnimal:
    """会飞的动物"""
    def fly(self):
        return "我在飞！"

class SwimmingAnimal:
    """会游泳的动物"""
    def swim(self):
        return "我在游泳！"

class Duck(Animal, FlyingAnimal, SwimmingAnimal):
    """鸭子类，多重继承"""
    def __init__(self, name):
        super().__init__(name, "鸭子")
    
    def make_sound(self):
        return "嘎嘎！"

print("\n多重继承:")
my_duck = Duck("唐老鸭")
print(f"{my_duck.name}叫: {my_duck.make_sound()}")
print(f"{my_duck.name}说: {my_duck.fly()}")
print(f"{my_duck.name}说: {my_duck.swim()}")

# ===== 类的封装 =====
print("\n===== 类的封装 =====")

class BankAccount:
    """银行账户类"""
    def __init__(self, account_number, owner_name, balance=0):
        self.account_number = account_number
        self.owner_name = owner_name
        self.__balance = balance  # 私有属性，以双下划线开头
    
    # 公共方法，用于访问私有属性
    def get_balance(self):
        """获取余额"""
        return self.__balance
    
    def deposit(self, amount):
        """存款"""
        if amount > 0:
            self.__balance += amount
            return f"存款成功，当前余额: {self.__balance}元"
        else:
            return "存款金额必须大于0"
    
    def withdraw(self, amount):
        """取款"""
        if 0 < amount <= self.__balance:
            self.__balance -= amount
            return f"取款成功，当前余额: {self.__balance}元"
        else:
            return "取款金额无效或余额不足"
    
    # 私有方法，以双下划线开头
    def __update_balance(self, amount):
        """私有方法，更新余额"""
        self.__balance = amount

# 创建银行账户对象
print("\n创建银行账户:")
account = BankAccount("12345678", "王五", 1000)

# 访问公共属性
print(f"账号: {account.account_number}, 户主: {account.owner_name}")

# 使用公共方法访问私有属性
print(f"余额: {account.get_balance()}元")

# 使用公共方法操作私有属性
print(account.deposit(500))
print(account.withdraw(200))

# 尝试直接访问私有属性会失败
# print(account.__balance)  # 这会引发错误

# Python的名称改写机制
print(f"通过名称改写访问私有属性: {account._BankAccount__balance}元")  # 不推荐这样做

# ===== 类的多态 =====
print("\n===== 类的多态 =====")

# 定义一个基类
class Shape:
    """形状基类"""
    def area(self):
        """计算面积"""
        pass
    
    def perimeter(self):
        """计算周长"""
        pass

# 定义子类
class Circle(Shape):
    """圆形类"""
    def __init__(self, radius):
        self.radius = radius
    
    def area(self):
        """计算圆的面积"""
        import math
        return math.pi * self.radius ** 2
    
    def perimeter(self):
        """计算圆的周长"""
        import math
        return 2 * math.pi * self.radius

class Rectangle(Shape):
    """矩形类"""
    def __init__(self, width, height):
        self.width = width
        self.height = height
    
    def area(self):
        """计算矩形的面积"""
        return self.width * self.height
    
    def perimeter(self):
        """计算矩形的周长"""
        return 2 * (self.width + self.height)

# 多态函数
def print_area(shape):
    """打印形状的面积"""
    print(f"面积: {shape.area()}")

def print_perimeter(shape):
    """打印形状的周长"""
    print(f"周长: {shape.perimeter()}")

# 创建不同形状的对象
print("\n创建不同形状:")
circle = Circle(5)
rectangle = Rectangle(4, 6)

# 使用多态
print("圆形:")
print_area(circle)
print_perimeter(circle)

print("\n矩形:")
print_area(rectangle)
print_perimeter(rectangle)

# ===== 类的特殊方法 =====
print("\n===== 类的特殊方法 =====")

class Vector:
    """二维向量类"""
    def __init__(self, x, y):
        self.x = x
        self.y = y
    
    # 字符串表示
    def __str__(self):
        """非正式字符串表示"""
        return f"Vector({self.x}, {self.y})"
    
    def __repr__(self):
        """正式字符串表示"""
        return f"Vector({self.x}, {self.y})"
    
    # 运算符重载
    def __add__(self, other):
        """向量加法，使用+运算符"""
        return Vector(self.x + other.x, self.y + other.y)
    
    def __sub__(self, other):
        """向量减法，使用-运算符"""
        return Vector(self.x - other.x, self.y - other.y)
    
    def __mul__(self, scalar):
        """向量乘以标量，使用*运算符"""
        return Vector(self.x * scalar, self.y * scalar)
    
    # 比较运算符
    def __eq__(self, other):
        """相等比较，使用==运算符"""
        return self.x == other.x and self.y == other.y
    
    # 长度
    def __len__(self):
        """向量的长度，使用len()函数"""
        import math
        return int(math.sqrt(self.x**2 + self.y**2))
    
    # 布尔值
    def __bool__(self):
        """向量的布尔值，使用bool()函数"""
        return bool(self.x or self.y)

# 创建向量对象
print("\n创建向量:")
v1 = Vector(3, 4)
v2 = Vector(1, 2)

# 使用特殊方法
print(f"v1: {v1}")
print(f"v1 + v2: {v1 + v2}")
print(f"v1 - v2: {v1 - v2}")
print(f"v1 * 2: {v1 * 2}")
print(f"v1 == v2: {v1 == v2}")
print(f"v1 == Vector(3, 4): {v1 == Vector(3, 4)}")
print(f"len(v1): {len(v1)}")
print(f"bool(v1): {bool(v1)}")
print(f"bool(Vector(0, 0)): {bool(Vector(0, 0))}")

# ===== 类方法和静态方法 =====
print("\n===== 类方法和静态方法 =====")

class Date:
    """日期类"""
    def __init__(self, year, month, day):
        self.year = year
        self.month = month
        self.day = day
    
    def __str__(self):
        return f"{self.year}年{self.month}月{self.day}日"
    
    # 实例方法（需要实例调用）
    def is_leap_year(self):
        """判断是否为闰年"""
        if self.year % 400 == 0 or (self.year % 4 == 0 and self.year % 100 != 0):
            return True
        return False
    
    # 类方法（可以通过类或实例调用）
    @classmethod
    def from_string(cls, date_string):
        """从字符串创建日期对象"""
        year, month, day = map(int, date_string.split('-'))
        return cls(year, month, day)
    
    # 静态方法（不需要类或实例作为第一个参数）
    @staticmethod
    def is_valid_date(year, month, day):
        """检查日期是否有效"""
        if year < 1 or month < 1 or month > 12 or day < 1:
            return False
        
        days_in_month = [0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
        
        # 处理闰年的2月
        if month == 2 and (year % 400 == 0 or (year % 4 == 0 and year % 100 != 0)):
            return day <= 29
        
        return day <= days_in_month[month]

# 创建日期对象
print("\n创建日期对象:")
date1 = Date(2023, 5, 15)
print(f"日期: {date1}")
print(f"是闰年吗? {date1.is_leap_year()}")

# 使用类方法创建对象
date2 = Date.from_string('2024-2-29')
print(f"从字符串创建的日期: {date2}")
print(f"是闰年吗? {date2.is_leap_year()}")

# 使用静态方法
print(f"2024-2-29是有效日期吗? {Date.is_valid_date(2024, 2, 29)}")
print(f"2023-2-29是有效日期吗? {Date.is_valid_date(2023, 2, 29)}")

# ===== 属性装饰器 =====
print("\n===== 属性装饰器 =====")

class Temperature:
    """温度类，展示属性装饰器的使用"""
    def __init__(self, celsius=0):
        self._celsius = celsius  # 使用下划线表示受保护的属性
    
    # 使用@property将方法转换为属性
    @property
    def celsius(self):
        """获取摄氏温度"""
        return self._celsius
    
    # 设置器
    @celsius.setter
    def celsius(self, value):
        """设置摄氏温度"""
        if value < -273.15:
            raise ValueError("温度不能低于绝对零度")
        self._celsius = value
    
    # 另一个属性，华氏温度
    @property
    def fahrenheit(self):
        """获取华氏温度"""
        return self._celsius * 9/5 + 32
    
    @fahrenheit.setter
    def fahrenheit(self, value):
        """设置华氏温度"""
        self.celsius = (value - 32) * 5/9

# 创建温度对象
print("\n创建温度对象:")
temp = Temperature(25)

# 使用属性
print(f"摄氏温度: {temp.celsius}°C")
print(f"华氏温度: {temp.fahrenheit}°F")

# 修改属性
temp.celsius = 30
print(f"修改后的摄氏温度: {temp.celsius}°C")
print(f"修改后的华氏温度: {temp.fahrenheit}°F")

temp.fahrenheit = 68
print(f"通过华氏温度修改: {temp.celsius}°C")

# 尝试设置无效温度
print("\n尝试设置无效温度:")
try:
    temp.celsius = -300
except ValueError as e:
    print(f"错误: {e}")

# ===== 抽象基类 =====
print("\n===== 抽象基类 =====")

from abc import ABC, abstractmethod

class AbstractVehicle(ABC):
    """抽象车辆类"""
    def __init__(self, brand, model):
        self.brand = brand
        self.model = model
    
    @abstractmethod
    def start(self):
        """启动车辆（抽象方法，必须由子类实现）"""
        pass
    
    @abstractmethod
    def stop(self):
        """停止车辆（抽象方法，必须由子类实现）"""
        pass
    
    def honk(self):
        """鸣喇叭（普通方法，可以被子类继承）"""
        return "嘟嘟！"

class Car(AbstractVehicle):
    """汽车类"""
    def start(self):
        return f"{self.brand} {self.model}汽车启动了，引擎轰鸣！"
    
    def stop(self):
        return f"{self.brand} {self.model}汽车停止了。"

class Motorcycle(AbstractVehicle):
    """摩托车类"""
    def start(self):
        return f"{self.brand} {self.model}摩托车启动了，引擎嗡嗡响！"
    
    def stop(self):
        return f"{self.brand} {self.model}摩托车停止了。"

# 创建具体类的实例
print("\n创建车辆:")
# vehicle = AbstractVehicle("通用", "抽象车")  # 这会引发错误，不能实例化抽象类
car = Car("奔驰", "C级")
motorcycle = Motorcycle("本田", "CBR")

# 调用方法
print(car.start())
print(car.stop())
print(f"汽车鸣笛: {car.honk()}")

print(motorcycle.start())
print(motorcycle.stop())
print(f"摩托车鸣笛: {motorcycle.honk()}")

# ===== 数据类 =====
print("\n===== 数据类 =====")

from dataclasses import dataclass

@dataclass
class Point:
    """点类（数据类）"""
    x: float
    y: float
    z: float = 0.0  # 默认值

@dataclass
class Student:
    """学生类（数据类）"""
    name: str
    age: int
    grades: list = None
    
    def __post_init__(self):
        """初始化后执行"""
        if self.grades is None:
            self.grades = []
    
    def add_grade(self, grade):
        """添加成绩"""
        self.grades.append(grade)
    
    def average_grade(self):
        """计算平均成绩"""
        if not self.grades:
            return 0
        return sum(self.grades) / len(self.grades)

# 创建数据类实例
print("\n创建数据类实例:")
point1 = Point(1.0, 2.0)
point2 = Point(3.0, 4.0, 5.0)

print(f"点1: {point1}")
print(f"点2: {point2}")
print(f"点1 == 点2: {point1 == point2}")

student = Student("赵六", 18)
student.add_grade(85)
student.add_grade(92)
student.add_grade(78)

print(f"学生: {student}")
print(f"平均成绩: {student.average_grade()}")