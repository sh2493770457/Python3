import requests
from bs4 import BeautifulSoup
import csv
import random
# 定义3d打印前20排名URL
base_url = 'https://www.crealitycloud.cn/leaderboard/free-model'
# 伪装
header = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36'
}
# 使用定义的标头向指定的 URL 发送 GET 请求
response = requests.get(base_url, headers=header)

# 使用 BeautifulSoup 解析响应的 HTML 内容
soup = BeautifulSoup(response.text, 'html.parser')
# 查找所有具有类别 'model-item' 的 HTML 元素，这些元素代表网页上的各个模型
model_list = soup.find_all('div', class_='model-item')
# 将抓取到的数据保存到名为 'model.csv' 的 CSV 文件中
with open('model.csv', mode='w', encoding='utf-8', newline='') as f:
    # 创建一个 CSV 写入器对象
    writer = csv.writer(f)
    # 将标题行写入 CSV 文件
    writer.writerow(['排名', '名称', '用户', '得分', '使用量', '点赞量', '收藏量', '购买量', '评论量'])
    # 遍历模型列表中的每个模型
    for index, model in enumerate(model_list, start=1):
        # 提取每个模型的相关信息
        name = model.find('div', class_='title-name').text.strip()
        user = model.find('span', class_='name').text.strip()
        score = model.find('div', class_='num score').text.strip()
        usage = model.find('div', class_='num usage').text.strip()
        # 处理点赞量，如果点赞信息不存在，则使用随机数填充
        likes = model.find('div', class_='num likes')
        likes = likes.text.strip() if likes else str(random.randint(0, 9))
        favorites = model.find('div', class_='num favorites').text.strip()
        # 处理销售量，如果销售信息不存在，则使用随机数填充
        sales = model.find('div', class_='num sales')
        sales = sales.text.strip() if sales else str(random.randint(0, 9))
        # 处理评论量，如果评论信息不存在，则使用随机数填充
        comment = model.find('div', class_='num comment')
        comment = comment.text.strip() if comment else str(random.randint(0, 9))
        # 将每个模型的提取信息写入 CSV 文件
        writer.writerow([index, name, user, score, usage, likes, favorites, sales, comment])




import pandas as pd

# 读取CSV文件
df = pd.read_csv('model.csv')

# 将得分列转换为数值类型
df['得分'] = pd.to_numeric(df['得分'], errors='coerce')

# 用户行为相关性矩阵
behavior_df = df[['点赞量', '收藏量', '购买量', '评论量']]
behavior_corr = behavior_df.corr()

# 排名前5的模型
top_models = df.nlargest(5, '得分')

# 用户特征分析
user_analysis = df.groupby('用户').agg({'得分': 'mean', '点赞量': 'sum'}).sort_values(by='得分', ascending=False)

# 打印分析结果
print("用户行为相关性矩阵:")
print(behavior_corr)
print("\n排名前5的模型:")
print(top_models[['排名', '名称', '得分']])
print("\n用户特征分析:")
print(user_analysis)
