// 06_maps.go - 映射(map)示例
package main

import "fmt"

func main() {
	// 映射（map）声明和初始化
	fmt.Println("映射示例:")
	
	// 方式1：使用make函数创建映射
	var m1 = make(map[string]int)
	fmt.Println("空映射m1:", m1)
	
	// 方式2：使用字面量创建并初始化映射
	m2 := map[string]string{
		"name":    "张三",
		"city":    "北京",
		"country": "中国",
	}
	fmt.Println("初始化的映射m2:", m2)
	
	// 映射操作
	fmt.Println("\n映射操作示例:")
	
	// 添加或修改元素
	scores := make(map[string]int)
	
	// 添加新元素
	scores["张三"] = 90
	scores["李四"] = 85
	scores["王五"] = 78
	fmt.Println("添加元素后的scores:", scores)
	
	// 修改元素
	scores["张三"] = 95
	fmt.Println("修改后的scores:", scores)
	
	// 获取元素
	score := scores["张三"]
	fmt.Println("张三的分数:", score)
	
	// 获取不存在的键
	unknownScore := scores["赵六"] // 返回值类型的零值（int的零值为0）
	fmt.Println("赵六的分数:", unknownScore)
	
	// 检查键是否存在
	value, exists := scores["李四"]
	if exists {
		fmt.Println("李四的分数:", value)
	} else {
		fmt.Println("李四不存在")
	}
	
	// 再次检查，这次是不存在的键
	value, exists = scores["赵六"]
	if exists {
		fmt.Println("赵六的分数:", value)
	} else {
		fmt.Println("赵六不存在")
	}
	
	// 删除元素
	delete(scores, "王五")
	fmt.Println("删除王五后的scores:", scores)
	
	// 删除不存在的键（不会报错）
	delete(scores, "赵六")
	
	// 获取映射长度
	fmt.Println("scores映射长度:", len(scores))
	
	// 遍历映射
	fmt.Println("\n遍历映射示例:")
	
	// 创建一个示例映射
	cities := map[string]int{
		"北京": 2100,
		"上海": 2400,
		"广州": 1500,
		"深圳": 1300,
		"杭州": 900,
	}
	
	// 遍历键和值
	fmt.Println("各城市人口(万):")
	for city, population := range cities {
		fmt.Printf("%s: %d\n", city, population)
	}
	
	// 只遍历键
	fmt.Println("\n城市列表:")
	for city := range cities {
		fmt.Println(city)
	}
	
	// 嵌套映射（映射的值为另一个映射）
	fmt.Println("\n嵌套映射示例:")
	
	// 创建嵌套映射
	students := map[string]map[string]string{
		"张三": {
			"age":    "20",
			"major":  "计算机科学",
			"school": "北京大学",
		},
		"李四": {
			"age":    "22",
			"major":  "数学",
			"school": "清华大学",
		},
	}
	
	// 访问嵌套映射
	fmt.Println("张三的信息:", students["张三"])
	fmt.Println("张三的学校:", students["张三"]["school"])
	
	// 向嵌套映射添加新元素
	students["王五"] = map[string]string{
		"age":    "21",
		"major":  "物理",
		"school": "复旦大学",
	}
	
	// 打印整个嵌套映射
	fmt.Println("\n所有学生信息:")
	for name, info := range students {
		fmt.Printf("%s: %v\n", name, info)
	}
	
	// 映射的零值为nil
	var nilMap map[string]int
	fmt.Println("\n空映射相关:")
	fmt.Printf("nilMap: %v, 是否为nil: %t\n", nilMap, nilMap == nil)
	
	// 不能对nil映射赋值，会导致panic
	// nilMap["test"] = 1 // 这会引发panic
	
	// 空映射不等于nil
	emptyMap := map[string]int{}
	fmt.Printf("emptyMap: %v, 是否为nil: %t\n", emptyMap, emptyMap == nil)
	
	// 可以对空映射赋值
	emptyMap["test"] = 1
	fmt.Println("添加元素后的emptyMap:", emptyMap)
} 