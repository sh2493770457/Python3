#画五角星
import turtle as t
t.color('red','red')
t.begin_fill()
for i in range(5):
    t.forward(100)
    t.right(144)
t.end_fill()

#绘制五角星
#import turtle as t
# a = input('你是哪所学校：')
# b = input('你是哪个班级的同学：')
# c = input('你的名字：')
# print(a+b+c+'同学',',欢迎你来到我的程序设计')
# print(" ")
# d = input("今天要不要让我画一个五角星给你看看？")
# t.setup(1000, 1000, 0, 0)
# t.pensize(20)
# t.pencolor("red")
# t.seth(0)
# t.fd(400)
# t.seth(-144)
# t.fd(400)
# t.seth(-144-144)
# t.fd(400)
# t.seth(-144-144-144)
# t.fd(400)
# t.seth(-144-144-144-144)
# t.fd(400)
