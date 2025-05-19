// 04_control_flow.go - 控制流示例（条件语句和循环）
package main

import "fmt"

func main() {
	// if-else 条件语句
	fmt.Println("条件语句示例:")
	
	age := 18
	
	if age >= 18 {
		fmt.Println("成年人")
	} else {
		fmt.Println("未成年人")
	}
	
	// if语句可以包含一个初始化语句
	if score := 85; score >= 90 {
		fmt.Println("优秀")
	} else if score >= 80 {
		fmt.Println("良好")
	} else if score >= 60 {
		fmt.Println("及格")
	} else {
		fmt.Println("不及格")
	}
	
	// switch语句
	fmt.Println("\nswitch语句示例:")
	
	day := "星期三"
	
	switch day {
	case "星期一":
		fmt.Println("Monday")
	case "星期二":
		fmt.Println("Tuesday")
	case "星期三":
		fmt.Println("Wednesday")
	case "星期四":
		fmt.Println("Thursday")
	case "星期五":
		fmt.Println("Friday")
	case "星期六", "星期日": // 多个匹配条件
		fmt.Println("Weekend")
	default:
		fmt.Println("无效的日期")
	}
	
	// switch无表达式形式
	score := 85
	switch {
	case score >= 90:
		fmt.Println("优秀")
	case score >= 80:
		fmt.Println("良好")
	case score >= 60:
		fmt.Println("及格")
	default:
		fmt.Println("不及格")
	}
	
	// fallthrough关键字（继续执行下一个case）
	num := 75
	switch {
	case num < 100:
		fmt.Println("num小于100")
		fallthrough
	case num < 200:
		fmt.Println("num小于200")
	}
	
	// for循环 - 标准形式
	fmt.Println("\nfor循环示例:")
	
	fmt.Println("标准for循环:")
	for i := 1; i <= 5; i++ {
		fmt.Printf("%d ", i)
	}
	fmt.Println()
	
	// for循环 - 类似while循环
	fmt.Println("类似while循环的for:")
	j := 1
	for j <= 5 {
		fmt.Printf("%d ", j)
		j++
	}
	fmt.Println()
	
	// for循环 - 无限循环（使用break退出）
	fmt.Println("无限循环使用break:")
	count := 1
	for {
		if count > 5 {
			break // 退出循环
		}
		fmt.Printf("%d ", count)
		count++
	}
	fmt.Println()
	
	// for循环 - continue语句
	fmt.Println("使用continue跳过偶数:")
	for i := 1; i <= 10; i++ {
		if i%2 == 0 {
			continue // 跳过当前迭代
		}
		fmt.Printf("%d ", i)
	}
	fmt.Println()
	
	// for-range循环（遍历数组、切片、映射等）
	fmt.Println("\nfor-range循环示例:")
	
	// 遍历数组
	numbers := [5]int{1, 2, 3, 4, 5}
	fmt.Println("遍历数组:")
	for index, value := range numbers {
		fmt.Printf("索引:%d 值:%d\n", index, value)
	}
	
	// 遍历字符串（按Unicode字符遍历）
	str := "你好Go"
	fmt.Println("\n遍历字符串:")
	for index, char := range str {
		fmt.Printf("位置:%d 字符:%c Unicode:%d\n", index, char, char)
	}
	
	// 遍历映射
	capitals := map[string]string{
		"中国": "北京",
		"日本": "东京",
		"美国": "华盛顿",
	}
	fmt.Println("\n遍历映射:")
	for country, capital := range capitals {
		fmt.Printf("国家:%s 首都:%s\n", country, capital)
	}
} 