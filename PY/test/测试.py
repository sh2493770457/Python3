import pandas as pd

# 读取CSV文件
breakfast_file = '早餐.csv'
lunch_file = '午餐.csv'

breakfast_df = pd.read_csv(breakfast_file)
lunch_df = pd.read_csv(lunch_file)

# 检查列名是否正确，并重命名列
correct_columns = ['序号', '食物名称', '主要成分', '食物编码', '可食部（克/份）', '价格（元/份）', '是否可半份', '蛋白质',
                   '脂肪', '碳水化合物', '不溶性膳食纤维', '酒精（乙醇）', '钙', '铁', '锌', '总维生素A', '硫胺素',
                   '核黄素', '维生素C', '异亮氨酸', '亮氨酸', '赖氨酸', '含硫氨基酸（SAA）', '芳香族氨基酸（AAA）', '苏氨酸',
                   '色氨酸', '缬氨酸']

#
lunch_df.columns = correct_columns

#计算每餐的总能量和各营养素含量
breakfast_df['总能量'] = breakfast_df['蛋白质'] * 4 + breakfast_df['脂肪'] * 9 + breakfast_df['碳水化合物'] * 4
lunch_df['总能量'] = lunch_df['蛋白质'] * 4 + lunch_df['脂肪'] * 9 + lunch_df['碳水化合物'] * 4
total_breakfast_energy = breakfast_df['总能量'].sum()
total_lunch_energy = lunch_df['总能量'].sum()
total_breakfast_protein = breakfast_df['蛋白质'].sum()
total_lunch_protein = lunch_df['蛋白质'].sum()
total_breakfast_fat = breakfast_df['脂肪'].sum()
total_lunch_fat = lunch_df['脂肪'].sum()
total_breakfast_carbs = breakfast_df['碳水化合物'].sum()
total_lunch_carbs = lunch_df['碳水化合物'].sum()

print("早餐总能量:", total_breakfast_energy,
      "午餐总能量:", total_lunch_energy,
      "早餐总蛋白质:", total_breakfast_protein,
      "午餐总蛋白质:", total_lunch_protein,
      "早餐总脂肪:", total_breakfast_fat,
      "午餐总脂肪:", total_lunch_fat,
      "早餐总碳水化合物:", total_breakfast_carbs,
      "午餐总碳水化合物:", total_lunch_carbs)


total_daily_energy = 1900  # 女生的总能量需求
breakfast_energy_ratio = (total_breakfast_energy / total_daily_energy) * 100
lunch_energy_ratio = (total_lunch_energy / total_daily_energy) * 100

print("早餐能量比例:", breakfast_energy_ratio,
      "午餐能量比例:", lunch_energy_ratio)

breakfast_aa_scores = breakfast_df[['异亮氨酸', '亮氨酸', '赖氨酸', '含硫氨基酸（SAA）', '芳香族氨基酸（AAA）', '苏氨酸', '色氨酸', '缬氨酸']].sum()
lunch_aa_scores = lunch_df[['异亮氨酸', '亮氨酸', '赖氨酸', '含硫氨基酸（SAA）', '芳香族氨基酸（AAA）', '苏氨酸', '色氨酸', '缬氨酸']].sum()
print("   ")
print("早餐氨基酸评分:\n", breakfast_aa_scores)
print("   ")
print("午餐氨基酸评分:\n", lunch_aa_scores)




# 计算每项食物的总能量
breakfast_df['总能量'] = breakfast_df['蛋白质'] * 4 + breakfast_df['脂肪'] * 9 + breakfast_df['碳水化合物'] * 4
lunch_df['总能量'] = lunch_df['蛋白质'] * 4 + lunch_df['脂肪'] * 9 + lunch_df['碳水化合物'] * 4

# 设置目标能量值
target_breakfast_energy = 570
target_lunch_energy = 760

# 动态规划函数
def find_meals(df, target_energy):
    # 创建一个数组，存储达到各种能量级别的最小食物组合数量
    min_combos = [float('inf')] * (target_energy + 1)
    min_combos[0] = 0
    for index, row in df.iterrows():
        calorie = int(row['总能量'])
        for j in range(target_energy, calorie - 1, -1):
            if min_combos[j - calorie] != float('inf'):
                min_combos[j] = min(min_combos[j], min_combos[j - calorie] + 1)
    # 查找实际组合
    if min_combos[target_energy] == float('inf'):
        return None
    else:
        # 从最后开始回溯找到组合
        result = []
        current_energy = target_energy
        while current_energy > 0:
            for index, row in df.iterrows():
                calorie = int(row['总能量'])
                if current_energy >= calorie and min_combos[current_energy - calorie] == min_combos[current_energy] - 1:
                    result.append(row)
                    current_energy -= calorie
                    break
        return result

# 使用动态规划找到解决方案
valid_breakfasts = find_meals(breakfast_df, target_breakfast_energy)
valid_lunches = find_meals(lunch_df, target_lunch_energy)

if valid_breakfasts:
    print(f"早餐组合（接近 {target_breakfast_energy} 卡路里）:")
    for item in valid_breakfasts:
        print(item)
else:
    print("没有找到合适的早餐组合。")

if valid_lunches:
    print(f"\n午餐组合（接近 {target_lunch_energy} 卡路里）:")
    for item in valid_lunches:
        print(item)
else:
    print("没有找到合适的午餐组合。")


# #将组合写入txt
# with open('早餐.txt', 'w',encoding="utf-8") as f:
#     for item in valid_breakfasts:
#         f.write(str(item) + '\n')
#
# with open('午餐.txt', 'w',encoding="utf-8") as f:
#     for item in valid_lunches:
#         f.write(str(item) + '\n')
