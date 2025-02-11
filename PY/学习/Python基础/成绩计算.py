#输入学生人数,将成绩顺序先后打印出来,计算平均分,最大最小值,方差,标准差
n=int(input("请输入学生人数："))
score=[]
for i in range(n):
    score.append(int(input("请输入第{}个学生的成绩：".format(i+1))))
print("成绩顺序先后打印出来：",score)
print("平均分：",sum(score)/n)
print("最大值：",max(score))
print("最小值：",min(score))
print("方差：",sum([(i-sum(score)/n)**2 for i in score])/n)
print("标准差：",(sum([(i-sum(score)/n)**2 for i in score])/n)**0.5)


# 输入学生人数,输入成绩，将及格和不及格的打印出来
# b=int(input("请输入学生人数："))
# a=[]
# c=[]
# d=[]
# i=0
# j=0
# while(i<=(b-1)):
#     a.append(input("请输入第%s个学生成绩："%(i+1)))
#     i+=1
# while(j<=(b-1)):
#     if(float(a[j])>=60):
#         c.append(a[j:j+1:1])
#     else:
#         d.append(a[j:j+1:1])
#     j+=1
# print("及格的学生成绩为：",c)
# print("不及格的学生成绩为：",d)