import hashlib
import random
import textwrap

import requests
from colorama import Fore, Style, init

# 初始化 colorama
init(autoreset=True)

# 百度翻译 API 账号信息
APP_ID = "xxxx"
SECRET_KEY = "xxxx"

# 百度翻译 API 限制：单次最多 6000 个字符
MAX_LEN = 6000


def translate(q, from_lang="auto", to_lang="auto"):
    """ 翻译文本，支持自动拆分 """
    url = "https://fanyi-api.baidu.com/api/trans/vip/translate"
    translations = []

    # 将长文本按最大长度拆分
    chunks = textwrap.wrap(q, MAX_LEN, break_long_words=False, break_on_hyphens=False)

    for chunk in chunks:
        salt = str(random.randint(32768, 65536))
        sign_str = APP_ID + chunk + salt + SECRET_KEY
        sign = hashlib.md5(sign_str.encode("utf-8")).hexdigest()

        params = {
            "q": chunk,
            "from": from_lang,
            "to": to_lang,
            "appid": APP_ID,
            "salt": salt,
            "sign": sign
        }

        response = requests.get(url, params=params)
        result = response.json()

        if "trans_result" in result:
            translations.extend([item["dst"] for item in result["trans_result"]])
        else:
            return f"{Fore.RED}翻译失败: {result} (╥﹏╥) {Style.RESET_ALL}"

    return "\n".join(translations)


if __name__ == "__main__":
    print(Fore.CYAN + "🌍 欢迎使用炫酷翻译工具！输入 'exit' 退出" + Style.RESET_ALL)

    while True:
        print(Fore.YELLOW + "💬 请输入要翻译的内容 (回车结束输入):" + Style.RESET_ALL)
        lines = []
        while True:
            line = input()
            if line.strip() == "":  # 监听回车结束输入
                break
            lines.append(line)
        text = "\n".join(lines)

        if text.lower() == "exit":
            print(Fore.GREEN + "感谢使用翻译工具！👋 (≧▽≦)/" + Style.RESET_ALL)
            break

        translation = translate(text)
        print(Fore.MAGENTA + f"✨ 翻译结果:\n{translation} ✨" + Style.RESET_ALL)
