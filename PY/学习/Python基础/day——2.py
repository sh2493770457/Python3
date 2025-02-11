# 案例： 拿鸡蛋
# for i in range(1,1000):
#     if i%2==1 and i%3==0 and i%4==1 and i%5==1 and i%6==3 and i%7==0 and i%8==1 and i%9==0:
#         print(i)


# 案例： 字母数字个数问题
# content=input("请输入一串字符：")
# num=0
# str=0
# for i in content:
#     if i.isdecimal() == True:
#         num+=1
#     elif i.isalpha() == True:
#         str+=1
#     else:
#         pass
# print("数字个数为：",num)
# print("字母个数为：",str)
# print("其他字符个数为：",len(content)-num-str)
# print("总个数为：",len(content))


# 案列： 字符串大小字母转换
# str=input("请输入一串字符：")
# str1=""
# for i in str:
#     if "a"<=i<="z":
#         str2=ord(i)-32
#     elif "A"<=i<="Z":
#         str2=ord(i)+32
#     else:
#         str2=ord(i)
#     str1+=chr(str2)
# print(str1)


# 随机生成10个数字判断是奇数还是偶数
# import random
# for i in range(10):
#     num=random.randint(1,10000)
#     print(num)
#     if num%2==0:
#         print("偶数",num)
#     else:
#         print("奇数",num)

# for循环遍历
# for i in range(1,10000):
#     if i%2==1 and i%3==2 and i%4==3 and i%5==4 and i%6==5 and i%7==6 and i%8==7 and i%9==8:
#         print(i)

#三重嵌套循环结构
# cnt=0
# for i in range(1,6):
#     for j in range(1,6):
#         for k in range(1,6):
#             if i!=j and i!=k and j!=k :
#                 print(i*100+j*10+k,end=" ")
#                 cnt+=1
# print()
# print(cnt,"个")

#兔生崽问题
# first=second=1
# for month in range(1,50):
#     if month>2:
#         third=first+second
#         first=second
#         second=third
#         print("第{}个月有{}对兔子".format(month,third))
#     else:
#         print("第{}个月有{}对兔子".format(month,first))


#使用list函数
# list1=list((1,2,3,4,5,6,7,8,9,10))
# list2=list([1,2,3,4,5,6,7,8,9,10])
# list3=list({1,2,3,4,5,6,7,8,9,10})
# list4=list(range(1,11))
# list5=list('Python')
# list6=list({'a':1,'b':2,'c':3})
# list7=list()
# print(list1,list2,list3,list4,list5,list6,list7)
# print(list1[0],list2[1],list3[2],list4[0],list5[4],list6[1])


#用while语句遍历列表元素，把每个字母转换成大写形式
# list1=list('python')
# i=0
# while i<len(list1):
#     list1[i]=list1[i].upper()
#     i+=1
# print(list1)

#切片操作
# list1=list('python')
# print(list1[::]) #返回包含所有元素的新列表
# print(list1[::-1]) #倒序读取所有元素
# print(list1[::2]) #偶数位置隔一个取一个
# print(list1[1::2]) #奇数位置隔一个取一个
# print(list1[3::]) #从第4个元素开始到最后一个元素
# print(list1[3:6]) #从第4个元素到第6个元素
# print(list1[0:100:1]) #从第一个元素开始到最后一个元素，步长为1
# print(list1[100:]) #从第101个元素到最后一个元素


#使用切片原地修改列表元素
# list1=list('python')
# list1[len(list1):]=['java'] #在末尾添加元素
# print(list1)
# list1[:3]=['c','c++','c#'] #修改前3个元素
# print(list1)
# list1[3:]=[] #删除后面的元素
# print(list1)


#列表的打包
# list1=list('python')
# list2=list('java')
# list3=list('c++')
# list4=list('c#')
# list5=list('php')
# list6=list('ruby')
# list7=list('go')
# list8=list('basic')
# list9=list('perl')
# rar=zip(list1,list2,list3,list4,list5,list6,list7,list8,list9)
# print(list(rar))

