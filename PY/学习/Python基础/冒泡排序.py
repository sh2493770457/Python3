#冒泡排序
a=[5,3,8,2,9,1,6,4,7]
for i in range(len(a)-1): #控制循环次数
    for j in range(len(a)-1-i): #控制比较次数
        if a[j]>a[j+1]: #比较相邻元素
            a[j],a[j+1]=a[j+1],a[j] #交换元素
print(a)

#冒泡排序法
# a=[1,9,3,8,2,7,4,6,5]
# for j in range(len(a)-1):
#     if a[j]>a[j+1]:
#         t=a[j]
#         a[j]=a[j+1]
#         a[j+1]=t
# for j in a:
#  print(j)
