#实现从控制行输入行数和列数，打印星号构成矩形
n=int(input("请输入行数："))
m=int(input("请输入列数："))
for i in range(n):
    for j in range(m):
        print('*',end=' ')
    print()

#用星号打印直角三角形
# n=int(input("请输入行数："))
# for i in range(1,n+1):
#     for j in range(1,i+1):
#         print('*',end=' ')
#     print()

#用星号打印等腰三角形
# n=int(input("请输入行数："))
# for i in range(1,n+1): #控制行数
#     for j in range(1,n-i+1): #控制左边空格
#         print(' ',end=' ')
#     for j in range(1,2*i):
#         print('*',end=' ')
#     print()