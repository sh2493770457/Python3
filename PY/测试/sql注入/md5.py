import hashlib
import os

# TODO:上传的文件名
file_name = "sss.php"
# TODO:定义一个空列表
file_names = []

# TODO:生成所有可能的文件名
for rand_num in range(1, 1000):
    # TODO:md5哈希
    base_char = f"{file_name}{rand_num}".encode()
    hash_char = hashlib.md5(base_char)
    md5_hash_char = hash_char.hexdigest()

    # TODO:生成文件名
    end_name = f"{md5_hash_char}.php"
    file_names.append(end_name)

# TODO:定义桌面路径
desktop_path = os.path.join(os.path.expanduser("~"), "Desktop", "path.txt")
with open(desktop_path, 'w') as f:
    for name in file_names:
        f.write(name + '\n')
print(f"文件名已保存到桌面: {desktop_path}")

