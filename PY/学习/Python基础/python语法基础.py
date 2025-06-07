#使用方法print(self,*args,sep=' ',end='\n',file=None)
#其中，file是指默认输出到打印控制台，也可以输出到文件（文件已被打开）；sep是指字符串插入在
#多个值之间，默认为一个space；end是指在字符串末尾最后一个值后添加一个符号，默认为换行符

# print("egons")
# print("alex","erick","ergou",sep=" abc ")

#输出：egons
#     alex abc erick abc ergou


#简单的循环判断（永真循环）
# a = input("请输入第一个数：")
# b = input("请输入第二个数：")
# while True:
#     if a > b:
#         print("a > b")
#         a = float(input("请重新输入第一个数："))
#         b = float(input("请重新输入第二个数："))
#     else:
#         print("a < b")
#         a = float(input("请重新输入第一个数："))
#         b = float(input("请重新输入第二个数："))


#变量的声明和赋值
#刚开始对x赋值3，对y赋值8，然后让y的值赋给x所以x=y=8
# x=3
# print("x=",x) #x=3
# y=8
# print("y=",y) #y=8
# x=y
# print("x=",x) #x=8
# print("y=",y) #y=8
# y=x
# print("x=",x) #x=8
# print("y=",y) #y=8


#多条简单消息打印
#打印结果不受影响，各打印各的
# message="I am a student"
# print(message)
# message="I am a teacher"
# print(message)




# #使用sep来指定分隔的符号
# str5="python学习"
# print("hello",str5)
# print("hello",str5,sep='----===++++')

#用.format格式化字符串，将后面的字符依次填入{}
# str6='{}+{}={}'.format(1,2,1+2)
# print(str6)

#去掉某个字符串
# n1="python学习-5"
# n1=n1.strip('-5') #去除两侧
# print(n1)
# n1=n1.lstrip('-5') #去除左边
# print(n1)
# n1=n1.rstrip('-5') #去除右边
# print(n1)

#查找某个字符窜出现的次数
# n2='fiewkuhfnewiouqwoijdqio'
# n2=n2.count('i')
# print('a出现了：',n2,end='次\n')

# str = 'fiewkuhfnewiouqwoijdqioEWDFWFWEQJYJU'
# # 将大写字母转换为小写字母
# lower_tr=str.lower()
# print(lower_tr)


# name='python学习群'
# print(name.capitalize()) #首字母大写
# print(name.center(50,"+")) #居中两边用+号补齐

#找到目标字符串所在位置，有多个时返回第一个所在位置，没有返回—1
# name='python学习群'
# i=name.find('学')
# print('{}中{}第一次出现在第{}个位置'.format(name,'学',i+1))
# print(name.find('1')) #-1

#字符串替换
# name='python学习群'
# print(name.replace('python','java'))

#查看是否都是数字
# name1='121212211'
# name2='231243dasdaw'
# print(name1.isdigit(),name2.isdigit())

#字符串分割
# txt='人生不止，寂寞不已。寂寞人生爱无休，寂寞是爱永远的主题。我和我的影子独处。' \
#     '它说它有悄悄话想跟我说。它说它很想念。你，原来，我和我的影子都在想你。'
# print(txt.split('，'))

#定义一个kg与g的转化的函数
# def f(g):
#     weight = g / 1000
#     return str(weight)+"kg"
# g2 = f(120008654)
# print(g2)


#计算1-10的阶乘和
# j=1;sum=0;i=1
# while i<11:
#     j*=i
#     i+=1
#     sum+=j
# print(sum)








#计算1-100的偶数和
# sum=0
# for i in range(2,101,2):
#     sum+=i
# print(sum)


#计算1-100的奇数和
# sum=0
# for i in range(1,101,2):
#     sum+=i
# print(sum)




#制作万年历
# import calendar
# y=int(input("请输入年份："))
# m=int(input("请输入月份："))
# print(calendar.month(y,m))




#列表的筛选，求出最大值
# a=[12,34,56,78,90]
# maxone=0
# i=0
# j=0
# while i<=(len(a)-1):
#     if a[i]>a[maxone]:
#         maxone=i
#     i+=1
# print(a[maxone])


























































































