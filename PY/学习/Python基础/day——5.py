# 案例 过滤敏感词
# import re
# def filter_words(keywords, text):
#     escaped_keywords = [re.escape(keyword) for keyword in keywords]  # 转义正则表达式中的特殊字符
#     pattern = '|'.join(escaped_keywords)
#     return re.sub(pattern, '***', text)
# keywords = ('小学生', '操你妈', '傻逼', '我操', '你妈逼', '我日', '我草', '我靠', '狗日的')
# text = input('请输入一段话：')
# print(filter_words(keywords, text))

#可变位置参数
# def sum(*nums):
#     i=0
#     for n in nums:
#         if(isinstance(n,(int,float))):
#             i+=n
#     return i
# print(sum(1,2,3,4,5))
# print(sum(1,2,3,4,5,6,7,8,'b','a'))
# print(sum(1,2,3,4,5,6,7,8,9,10,11,12,13.55,14.2,15.1,'a','b'))

#可变关键字参数
# def sum(**nums):
#     i=0
#     for n in nums.values():
#         if(isinstance(n,(int,float))):
#             i+=n
#     return i
# print(sum(a=1,b=2,c=3,d=4,e=5))

#定义一个函数，包含位置参数和默认参数，在调用函数时，使用位置参数和关键字
# def f(name,age,sex=1):
#     print('name=',name,'age=',age,'sex=',sex)
# f('张三',20,0)
# f(age=20,name='张三',sex=0)
# f(name='张三',age=20)

#可变参数和可变关键字参数参数混用：可变位置参数在前，可变关键字参数在后
# def f(*nums,**nums2):
#     print('nums=',nums,'nums2=',nums2)
# f(1,2,3,a=1,b=2)

#位置参数、默认参数、可变位置参数、可变关键字参数混用
# def f(x,*args,a=4,**kwargs):
#     print('x=',x,'args=',args,'a=',a,'kwargs=',kwargs)
# f(1,2,3,a=1,b=2)
# f(1,2,3,a=1,b=2,c=3,d=4,e=5)

# 函数的返回值
# def f():
#     pass
# def g():
#     return
# def f1():
#     return '孙华'
# print(f())
# print(g())
# print(f1())

#求和匿名函数
# sum=lambda a,b:a+b
# print(sum(1,2))
# print(sum(10,20))

#求解递归函数
# def f(x):
#     if (x<2):return 1
#     else: return x*f(x-1)
# print(f(10))

#解析递归型数据结构

#用迭代算法设计斐波那契数列
# def fib(n):
#     a=[0,1]
#     for i in range(2,n+1):
#         a.append(a[i-1]+a[i-2])
#     return a[n]
# print(fib(19))

#初始化类
# class Student:
#     def __init__(self,name,age):
#         self.name=name
#         self.age=age
#     def saying(self):
#         return "我的名字是{}，今年{}岁了。".format(self.name,self.age)
# s1=Student('孙华',18)
# print(s1.saying())

#案例 定义学生类
# class Student:
#     def __init__(self,name,age,sex):
#         self.name=name
#         self.age=age
#         self.sex=sex
#     def introduce(self):
#         return "我的名字是{}，今年{}岁了，性别是{}。".format(self.name,self.age,self.sex)
# s1=Student('孙华',18,'男')
# s2=Student('李四',20,'女')
# s3=Student('张三',19,'男')
# print(s1.introduce())
# print(s2.introduce())
# print(s3.introduce())

#定义province类
# class Province:
#     country='中国'
#     def __init__(self,name):
#         self.name=name
# object=Province('四川')
# print(object.name)
# #访问静态字段
# print(Province.country)
# #通过实例访问静态字段
# print(object.country)

# class Goods:
#     def __init__(self, price, discount=1):
#         self._orig_price = price
#         self._discount = discount
#
#     @property
#     def price(self):
#         return self._orig_price * self._discount
#
#     @price.setter
#     def price(self, value):
#         raise NotImplementedError("Cannot set price directly")
#     @property
#     def orig_price(self):
#         return self._orig_price
#     @orig_price.setter
#     def orig_price(self, value):
#         self._orig_price = value
#     @property
#     def discount(self):
#         return self._discount
#     @discount.setter
#     def discount(self, value):
#         self._discount = value
#     @price.deleter
#     def price(self):
#         del self._orig_price
#         del self._discount
# obj = Goods(100, 0.8)
# print(obj.price)
# obj.orig_price = 200
# print(obj.price)
# obj.discount = 0.5
# print(obj.price)
# del obj.price



























































