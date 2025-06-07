# 读取文件并处理
with open("C:\\Users\\24937\\Desktop\\资产.txt", "r", encoding="utf-8") as f:
    lines = f.readlines()
    # 遍历每一行
    for line in lines:
        # 去除行尾的换行符
        line = line.strip()
        # 检查行是否为空，避免拼接空行
        if line:
            # 去掉双引号
            line = line.replace('"', '')
            print(line)
            with open("C:\\Users\\24937\\Desktop\\final.txt", "a", encoding="utf-8") as f:
                f.write(line + "\n")
