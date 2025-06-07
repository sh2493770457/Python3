#创建字符串
str1='hello'
str2='python'
print(str1)
print(type(str1))
print(str2)
print(type(str2)) #输出类型为str型
str3=str1+str2    #字符串用+号拼接
print("这是str3：",str3)
str4=','.join(str1) #用join链接
print(str4)
#去掉空格和换行符用split方法
name="  python 学习-5"
print("变换前：",name)
name=name.split()
print("变换后：",name)
#查看字符串是否都是字母或文字，并且至少有一个字符
name1='abcdef'
name2='python21 学习群'
print(name1.isalpha(),name2.isalpha())
#用end=’‘去除换行
print(str1,end=',')
print(str2,end=',')