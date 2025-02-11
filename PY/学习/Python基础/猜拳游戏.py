#用python设计猜拳游戏
import random
while True:
    c=random.randint(1,3)
    #让用户换行输入
    p=int(input("请输入你的选择（1.剪刀 2.石头 3.布）："))

    if p==c:
        print("平局")
        continue
    elif(p==1 and c==3) or (p==2 and c==1) or (p==3 and c==2):
            print("你赢了")
            continue
    else:
          print("你输了")
          break