#用for求100以内的素数
for i in range(2,101):#从2开始，到100结束
        for j in range(2,i):  #从2开始，到i-1结束
            if i%j==0:   #判断i是否能被j整除
                break   #能整除，跳出循环
        else:
         print(i)  #不能整除，打印i
