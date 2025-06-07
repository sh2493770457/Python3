# 设计一款购物车程序，先用列表及其相关循环语句实现菜单式的购物车程序
product_list = [
    ('iphone', 5000),
    ('mac', 12000),
    ('ipad', 2500),
    ('apple watch', 2000),
    ('airpods', 1500),
    ('mac pro', 25000)
]
# 商品列表
shopping_list = []  # 定义一个空列表，用于存放用户购买的商品
salary = input("请输入你的工资：")
# 当输入的内容为数字
if salary.isdigit():
    # 将输入的工资转换为整数
    salary = int(salary)
    while True:
        # 寻黄打印出所有商品列表
        for index, item in enumerate(product_list):
            print(index, item)
        user_choice = input("请输入你想要购买的商品编号：")
        if user_choice.isdigit():  # 当输入的商品编号为数字
            user_choice = int(user_choice)
            if user_choice < len(product_list) and user_choice >= 0:  # 当输入的商品编号在列表范围内
                p_item = product_list[user_choice]  # 获取用户输入的商品
                if p_item[1] <= salary:  # 当用户输入的商品价格小于等于用户输入的工资
                    shopping_list.append(p_item)  # 将用户输入的商品添加到购物车
                    salary -= p_item[1]  # 用户输入的商品价格从工资中扣除
                    print("您购买的商品为%s,余额为%s，交易成功。输入'q'可结算退出" % (p_item, salary))
                else:
                    print("您的余额只剩[%s]，余额不足不能成交." % salary)
            else:
                print("商品不存在！")
        elif user_choice == 'q':  # 当输入商品编号为q时，打印并购买商品的余额并退出程序
            print("---------------------购物清单----------------")
            for p in shopping_list:
                print(p)
            print("您的余额为：", salary)
            exit()
        else:
            print("该商品不存在！")
