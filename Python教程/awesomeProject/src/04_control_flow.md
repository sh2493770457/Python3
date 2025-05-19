# Go语言控制流

## 概述

本文档详细介绍了Go语言中的控制流结构，包括条件语句（if-else、switch）和循环语句（for）。控制流决定了程序执行的路径，是编程语言中最基本的逻辑控制机制。相比其他语言，Go的控制结构简洁而强大，同时去除了一些可能导致错误的特性。

## 源代码

```go
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
```

## 条件语句

### 1. if-else语句

Go语言的if语句语法如下：

```go
if 条件 {
    // 条件为true时执行的代码
} else if 另一个条件 {
    // 另一个条件为true时执行的代码
} else {
    // 所有条件都为false时执行的代码
}
```

#### 特点：

1. **条件表达式不需要括号**：与C、Java等语言不同，Go的if条件表达式不需要括号包围
2. **必须使用花括号**：即使只有一行代码，也必须使用花括号
3. **花括号必须与if在同一行**：这是Go的代码风格规范
4. **可以在条件判断前执行一个简单的语句**：

```go
if score := calculateScore(); score >= 90 {
    // 使用score变量
}
```

这个特性非常有用，它允许你将变量的作用域限制在if语句内部。

### 2. switch语句

Go的switch语句比其他语言更灵活和强大：

```go
switch 表达式 {
case 值1:
    // 当表达式等于值1时执行的代码
case 值2, 值3:
    // 当表达式等于值2或值3时执行的代码
default:
    // 当表达式不匹配任何case时执行的代码
}
```

#### 特点：

1. **自动break**：与C、Java不同，Go的switch语句会自动在每个case末尾隐式添加break，不会自动贯穿到下一个case
2. **可以使用fallthrough**：如果需要贯穿到下一个case，可以使用fallthrough关键字
3. **case可以有多个值**：用逗号分隔，如`case "星期六", "星期日":`
4. **无表达式switch**：可以省略表达式，相当于`switch true`，每个case都是一个布尔表达式：

```go
switch {
case score >= 90:
    fmt.Println("优秀")
case score >= 80:
    fmt.Println("良好")
// ...
}
```

5. **类型switch**：用于检查接口变量的动态类型（将在接口章节详细介绍）

## 循环语句

Go语言只有一种循环结构：for循环。但它非常灵活，可以实现其他语言中while、do-while和传统for循环的功能。

### 1. 标准for循环

```go
for 初始化语句; 条件表达式; 后置语句 {
    // 循环体
}
```

例如：
```go
for i := 1; i <= 5; i++ {
    fmt.Printf("%d ", i)
}
```

### 2. 类似while循环的for

```go
for 条件表达式 {
    // 循环体
}
```

例如：
```go
j := 1
for j <= 5 {
    fmt.Printf("%d ", j)
    j++
}
```

### 3. 无限循环

```go
for {
    // 循环体
    if 条件 {
        break // 使用break退出循环
    }
}
```

### 4. for-range循环

for-range用于遍历数组、切片、字符串、映射或通道：

```go
for 索引, 值 := range 集合 {
    // 使用索引和值
}
```

根据不同类型，range的行为略有不同：
- **数组/切片**：返回索引和元素值
- **字符串**：返回字节索引和Unicode码点（rune）
- **映射**：返回键和值
- **通道**：只返回通道中的值

如果不需要某个返回值，可以使用`_`忽略：
```go
// 只需要值，忽略索引
for _, value := range numbers {
    fmt.Println(value)
}

// 只需要索引，忽略值
for index, _ := range numbers {
    // 或者简写为: for index := range numbers
    fmt.Println(index)
}
```

## 控制语句

### 1. break

`break`语句用于立即退出当前循环或switch语句：

```go
for {
    // 无限循环
    if condition {
        break // 退出循环
    }
}
```

### 2. continue

`continue`语句用于跳过当前循环迭代的剩余部分，继续下一次迭代：

```go
for i := 1; i <= 10; i++ {
    if i%2 == 0 {
        continue // 跳过偶数
    }
    fmt.Printf("%d ", i) // 只打印奇数
}
```

### 3. goto

虽然Go支持`goto`语句，但不推荐使用，因为它可能导致代码难以理解和维护。在绝大多数情况下，有更好的结构化方法来替代`goto`。

## Go控制流的特点与注意事项

1. **简洁性**：Go的控制结构语法简洁，消除了不必要的括号和关键字
2. **显式性**：Go强制使用花括号，避免了因缺少括号导致的错误
3. **没有do-while循环**：可以使用for循环模拟
4. **没有三元运算符(?:)**：使用if-else代替
5. **范围循环的效率**：range创建值的副本，如果处理大对象，可能需要使用索引直接访问原始元素

## 最佳实践

1. 优先使用for-range遍历集合，代码更简洁清晰
2. 在条件允许时，在if语句中初始化变量以限制其作用域
3. 对于简单的条件分支，使用if-else；对于多条件分支，优先使用switch
4. 避免深层嵌套的条件语句，可以通过提前返回或继续等方式减少嵌套
5. for循环中操作索引时要小心边界条件 