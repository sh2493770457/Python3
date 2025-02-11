import random
import turtle
import math

# 设置常量
SCREEN_WIDTH = 1000
SCREEN_HEIGHT = 500
NUM_STARS = 100
COLORS = ['skyblue', 'white', 'cyan', 'aqua', 'pink', 'green']

# 初始化Turtle
turtle.setup(1.0, 1.0)
turtle.screensize(1.0, 1.0)
turtle.bgcolor('black')
t = turtle.Pen()
t.ht()


class Meteor:
    def __init__(self):
        self.r = random.uniform(50, 100)
        self.t = random.uniform(1, 3)
        self.x = random.uniform(-2000, SCREEN_WIDTH)
        self.y = random.uniform(0, SCREEN_HEIGHT)
        self.speed = random.uniform(5, 10)
        self.color = random.choice(COLORS)
        self.outline = 1

    def star(self):
        t.pensize(self.outline)
        t.penup()
        t.goto(self.x, self.y)
        t.pendown()
        t.color(self.color)
        t.begin_fill()
        t.fillcolor(self.color)
        t.setheading(-30)
        t.right(self.t)
        t.forward(self.r)
        t.left(self.t)
        t.circle(self.r * math.sin(math.radians(self.t)), 180)
        t.left(self.t)
        t.forward(self.r)
        t.end_fill()

    def move(self):
        if self.y >= -SCREEN_HEIGHT:
            self.y -= self.speed
            self.x += 2 * self.speed
        else:
            self.reset()

    def reset(self):
        self.r = random.uniform(50, 100)
        self.t = random.uniform(1, 3)
        self.x = random.uniform(-2000, SCREEN_WIDTH)
        self.y = SCREEN_HEIGHT
        self.speed = random.uniform(5, 10)
        self.color = random.choice(COLORS)
        self.outline = 1


# 创建星星列表
stars = [Meteor() for _ in range(NUM_STARS)]

try:
    # 绘图循环
    while True:
        turtle.tracer(0)
        t.clear()
        for i, star in enumerate(stars):
            star.move()
            star.star()
        turtle.update()

except turtle.Terminator:
    # 忽略绘图结束异常
    pass
