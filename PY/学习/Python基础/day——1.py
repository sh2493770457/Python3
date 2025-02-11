#打印格式化字符串
# s="你好啊，世界"
# for i in s:
#     print(i,end="   ") #  你    好    啊    ，    世    界

#把python当计算器
# tax=12.5/100
# price=100.50245435
# print(price*tax) #12.5625
# print(round(price,3)) #100.502 保留3位小数

#if 判断
# num=int(input("请输入一个数字："))
# if num>0:
#     print("这个数字是正数")
# else:
#     print("这个数字不是正数")

#用while筛选100以内的偶数
# n=0
# while n<100:
#     n=n+1
#     if n%2>0:
#         continue
#     print(n)

# 中国='china'
# print(中国)

#模拟进度条的加载
# import time
# length = 100
# for i in range(length):
#     process = i / length
#     block = '#' * int(i // (length / 20))
#     time.sleep(0.1)
#     print('\r加载条：|{0:<20}|{1:6.1%}'.format(block, process), end='')
# print("\n加载完成")


#输入平方和立方表
# n=int(input("请输入一个数字："))
# x=0
# print("数字\t\t平方\t\t\t立方")
# while x<n:
#     x+=1
#     print(str(x).rjust(2),str(x*x).rjust(3),sep='\t\t',end=' ')
#     print('',str(x*x*x).rjust(3),sep='\t\t')


#打印九九乘法表
# for i in range(1,10):
#     for j in range(1,10):
#         print("%d*%d=%2d"%(i,j,i*j),end=" ")
#     print()


#打印杨辉三角
# t=int(input("请输入一个数字："))
# if t<=0:
#     t=7
#     print("请输入正整数")
# w=5
# #打印第一行
# print('%*s'%(int((t-1)*w/2)+9-w," "),end=" ")
# print('{0:^{1}}'.format(1,w))
#
# #打印第二行
# line=[1,1]
# print('%*s'%(int((t-2)*w/2)+8-w," "),end=" ")
# for i in line:
#     print('{0:^{1}}'.format(i,w),end=" ")
# print("")
# #打印其他行
# for i in range(2,t):
#     r=[]
#     for i in range(0,len(line)-1):
#         r.append(line[i]+line[i+1])
#     line=[1]+r+[1]
#     print('%*s'%(int((t-i)*w/2)-w," "),end=" ")
#     for i in line:
#         print('{0:^{1}}'.format(i,w),end=" ")
#     print("")


#案列：快算游戏
# print("100以内快速求和运算：")
# while True:
#     num1=float(input("第一个数字："))
#     num2=float(input("第二个数字："))
#     if num1>100 or num2>100:
#         print("咱不玩大的，就100以内的数字，请重新输入")
#         continue
#     else:
#         sum=round(num1+num2,2)
#         print("%.2f+%.2f="%(num1,num2),sum)
#     print("是否退出？退出请按Q，否则，按其他键继续")
#     esc=input()
#     if esc=='q' or esc=='Q':
#         break


#检测数字是否在列表中
# list=[1,2,3,4,5,6,7,8,9,10]
# while True:
#     num=int(input("请输入一个数字："))
#     if num in list:
#         print("数字在列表中")
#     else:
#         list.append(num)
#         print("数字不在列表中，已添加到列表中")
#     print("是否退出？退出请按Q，否则，按其他键继续")
#     s=input()
#     if s=='q' or s=='Q':
#         break
#     else:
#         continue





















