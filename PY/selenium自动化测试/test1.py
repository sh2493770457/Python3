import re

#.*和*贪婪和非贪婪，贪婪尽可能匹配更多的字符
content = 'Hello Sunhua 24937 70457 World_this is a Regex Demo'
pattern = 'Hello\s\S{6}\s\d{5}\s\d{5}\s\w{10}\s.*Demo'  # 你的正则表达式
result = re.search(pattern, content)  # 在字符串中搜索匹配项
print(result)

#findall返回值为list元组类型
t = re.findall(pattern, content)  # 使用 re.findall() 找到所有匹配项
print(t)

#返回值为迭代器，用.group（）输出
y=re.finditer(pattern,content)
for match in y:
    print(match.group())

m=re.compile(content)
print(m)

