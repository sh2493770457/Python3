#用for计算1-100的累加和
sum=0
counter=1
for counter in range(1,101):
    sum+=counter
    counter+=1
print('1-100的累加和为：{}'.format(sum))