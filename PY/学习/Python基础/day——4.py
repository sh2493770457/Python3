# 使用clear清除所有元素
# a = [1, 2, 3, 4, 5]
# print(a)
# b=a.clear()
# print(b)

# 使用pop随机移除一个元素,然后把删除的元素组成新的列表对象
# set1={"a","b","c"}
# list1=[]
# for i in range(len(set1)):
#     val=set1.pop()
#     list1.append(val)
# print(set1)
# print(list1)

# 合并集合
# set1={"a","b","c","d"}
# set2={"d","e","f"}
# set1.update(set2)
# print(set1)
# print(set1|set2) #求并集
# print(set1.union(set2)) #求并集
# print(set1^set2) #求差集
# print(set1&set2) #求交集
# print(set1.difference(set2)) #求差集

#设计不重复的随机数
# import random
# s=set()
# num_range=int(input("请输入随机数的范围:"))
# count=int(input("请输入随机数个数:"))
# while len(s)<count:
#     num=random.randint(1,num_range)
#     s.add(num)
# print(s)

#设计不可变集合
# import pprint
# city_distance=dict()
# city_relationship1=frozenset(["北京","上海","广州","深圳"])
# city_relationship2=frozenset(["杭州","宁波","温州","绍兴"])
# city_distance["北京"]=frozenset(city_relationship1)
# city_distance["杭州"]=frozenset(city_relationship2)
# pprint.pprint(city_distance)

#案例 把列表作为字典的值
# import pprint
# friend_list=dict()
# while True:
#     grilfriends=list()
#     name=input("请输入名字:")
#     while True:
#         grilfriends_name=input("请输入{}的朋友名字:".format(name))
#         if grilfriends_name!="":
#             grilfriends.append(grilfriends_name)
#         else:
#             break
#     friend_list[name]=grilfriends
#     flag=input("是否继续添加朋友?y/n")
#     if flag=="y":
#         continue
#     else:
#         break

#分割字符串
# str='www.baidu.com'
# t=str.partition(".",) #以第一个.为分隔符
# print(t)
# s1=str.rpartition(".") #以最后一个.为分隔符
# print(s1)

# 使用split分割
# str="www.baidu.com"
# t=str.split(".")
# print(t)
# t1=str.split(".",1) #只分割一次
# print(t1)

#修减字符串
# str="10000 分"
# t=str.strip(" 分") #只能删除开头和结尾
# print(t)
# #删除0000
# print(str[0::6]) #删除中间的0000

#案例 打印菱形
# n=int(input("请输入菱形的层数:"))
# for i in range(1,n):
#     a='*'*i
#     print(a.center(n,' '))
# for i in range(n,0,-1):
#     a='*'*i
#     print(a.center(n,' '))

#案例 模拟上传图片文件
# filename=input("请输入文件名:")
# if filename !='':
#     if filename.find('.')==-1 or filename.find('.')==len(filename)-1 :
#         print("文件名不合法")
#     else:
#         filetype=filename.split('.')[-1]
#         if filetype in ['jpg','png','gif']:
#             print("文件上传成功")
#         else:
#             print("文件类型不合法")

#selenium自动化测试
# import re
# pattern='hello'
# pattern=re.compile(pattern)
# match=pattern.match("hello worldy")
# if match:
#     print(match.group())
# else:
#     print("匹配失败")

#行定界符
# import re
# subject="html、htm"
# pattern='^html'
# matches=re.findall(pattern,subject)
# print(matches)

#分别过滤行首H和行尾m的元素
# import re
# lines=['H','hello','m','world','Hm','Hellom','mworld']
# results=[]
# for line in lines:
#     if re.findall(r"^H",line):
#         results.append(line)
# print(results)
# results=[]
# for line in lines:
#     if re.findall(r"m$",line):
#         results.append(line)
# print(results)

#单词界定符
import re
subject="html、htm"
pattern=r'\bhtm\b'
matchs=re.findall(pattern,subject)
print(matchs)











































































































