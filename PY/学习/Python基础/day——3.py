#列表倒序
# a=[1,2,3,4]
# a.reverse()
# print(a)
import sortedcontainers

#升序排序
# a=[5,7,8,1,2,3,4]
# a.sort()
# print(a)

#列表推导式
# a=range(10)
# b=[i**2 for i in a ]
# print(b)
# c=[i**2 for i in a if i%2==0]
# print(c)

#有列表nums，请找到任意相加等于9的元素集合
# nums=[1,2,3,4,5,6,7,8,9,0]
# new_nums=[]
# for i in  range(len(nums)-1):
#     for j in range(i+1,len(nums)):
#         if nums[i]+nums[j]==9:
#             new_nums.append((nums[i],nums[j]))
# print(new_nums)


#元组
# a=(1,2,3)
# b=(4,5,6)
# c=a+b
# print(c)
# d=4,
# print(d)

#模拟出栈，入栈
# def d2b (num):
#     a=[]
#     b=''
#     while (num>0):
#         r=num%2
#         a.append(r)
#         num=num//2
#     while (len(a)):
#         b+=str(a.pop())
#     return 'ob'+b
# print(d2b(10))
# print(d2b(100))
# print(d2b(6))


#用中括号定义二维列表，设计一个课程表
# kech=[["数学","英语","物理","化学"],["数学","英语","物理","化学"],["数学","英语","物理","化学"],["数学","英语","物理","化学"]]
# grade=[["一年级","一年级","一年级","一年级"],["二年级","二年级","二年级","二年级"],["三年级","三年级","三年级","三年级"],["四年级","四年级","四年级","四年级"]]
# print("课程表")
# for i in range(4):
#     print(grade[i])
#     for j in range(4):
#         print(kech[i][j])

#用嵌套的for循环定义二维列表,用for遍历换行
# row=[]
# for i in range(1,6):
#     row.append([])
#     for j in range(1,6):
#         row[i-1].append(10*i+j)
# for k in row:
#     print(k)

#使用dict将常用的可迭代的数据结构转换为字典
# a=(1,2,3,4,5,6,7,8,9)
# b=('a','b','c','d','e','f','g','h','i')
# c=dict(zip(a,b))
# print(c)

#使用fromkeys()
# t=range(10)
# dict1=dict.fromkeys(t,"你好")
# print(dict1)
# for i in dict1:
#     print(i,dict1[i])

#使用popitem()方法
# dict1={"a":1,"b":2,"c":3,"d":4,"e":5}
# while len(dict1)>0:
#     print(dict1.popitem())
# print(dict1)


#合并字典
# dict1={"a":1,"b":2,"c":3}
# dict2={"d":4,"e":5,"f":6}
# dict1.update(dict2)
# print(dict1)


#用setdefault方法添加元素
# dict1={"a":1,"b":2,"c":3}
# dict1.setdefault("d",4)
# print(dict1)


# 用set函数把常用的可迭代元素转化为集合
# a=(1,2,3,4,5,6,7,8,9)
# b=('a','b','c','d','e','f','g','h','i')
# c=set(a)
# d=set(b)
# print(c)
# print(d)
# for i in b: #访问集合
#     print(i)

#使用add添加元素
# dict1={"a","b","c"}
# dict1.add("d")
# print(dict1)

#使用update添加多个元素
# dict1={"a":1,"b":2,"c":3}
# dict1.update({"d":4,"e":5,"f":6})
# print(dict1)

#当使用update添加字符串时，要注意格式
# a=set()
# b=set()
# a.update("python","java")
# b.update({"python","java"})
# print(a)
# print(b)

# 使用remove删除元素
# dict1={"a","b","c"}
# dict1.remove("b")
# print(dict1)

# 使用discard移除指定元素集合.可删除一个不存在的元素
# dict1={"a","b","c"}
# dict1.discard("d")
# print(dict1)