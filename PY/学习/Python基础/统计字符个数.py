#输入一行字符，分别统计其中出现的英文字母、空格、数字和其它字符的个数
s=input("请输入一行字符：\n")
lettrers=0 #字母
space=0 #空格
digit=0 #数字
others=0   #其他
for c in s:
    if c.isalpha(): #判断是否为字母
        lettrers+=1
    elif c.isspace(): #判断是否为空格
        space+=1
    elif c.isdigit(): #判断是否为数字
        digit+=1
    else: #其他
        others+=1
print("有{}个字母，{}个空格，{}个数字，{}个其他字符".format(lettrers,space,digit,others))