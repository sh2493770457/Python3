#绘制太阳花
import turtle as t
t.pensize(12)
t.speed(10)
t.color('yellow','red')
t.begin_fill()
for i in range(50):
    t.forward(200)
    t.left(170)
    t.forward(200)
t.end_fill()
t.done()