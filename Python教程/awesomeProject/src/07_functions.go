// 07_functions.go - 函数示例
package main

import (
	"fmt"
	"strings"
)

// 基本函数定义
func sayHello() {
	fmt.Println("你好，世界！")
}

// 带参数的函数
func greet(name string) {
	fmt.Printf("你好，%s！\n", name)
}

// 多参数函数
func add(a, b int) {
	fmt.Printf("%d + %d = %d\n", a, b, a+b)
}

// 带返回值的函数
func multiply(x, y int) int {
	return x * y
}

// 多返回值函数
func divide(dividend, divisor float64) (float64, error) {
	if divisor == 0 {
		return 0, fmt.Errorf("除数不能为零")
	}
	return dividend / divisor, nil
}

// 命名返回值
func rectangleProperties(length, width float64) (area, perimeter float64) {
	area = length * width
	perimeter = 2 * (length + width)
	return // 裸返回，自动返回命名的返回值
}

// 可变参数函数
func sum(numbers ...int) int {
	total := 0
	for _, num := range numbers {
		total += num
	}
	return total
}

// 使用defer关键字
func deferDemo() {
	defer fmt.Println("这句话会在函数结束时打印") // 最后执行
	
	fmt.Println("函数开始执行")
	fmt.Println("函数中间的操作")
	// 函数结束前会执行defer语句
}

// 多个defer语句（后进先出顺序执行）
func multipleDeferDemo() {
	defer fmt.Println("第一个defer") // 最后执行
	defer fmt.Println("第二个defer") // 倒数第二个执行
	defer fmt.Println("第三个defer") // 最先执行
	
	fmt.Println("函数主体")
}

// 函数作为值传递
func calculate(operation func(int, int) int, a, b int) int {
	return operation(a, b)
}

// 定义几个可以作为参数的函数
func addFunc(x, y int) int { return x + y }
func subFunc(x, y int) int { return x - y }
func mulFunc(x, y int) int { return x * y }

// 匿名函数和闭包
func makePowerFunc(power int) func(int) int {
	// 返回一个匿名函数，它是一个闭包，可以访问外部的power变量
	return func(base int) int {
		result := 1
		for i := 0; i < power; i++ {
			result *= base
		}
		return result
	}
}

// 使用指针参数修改变量
func increment(value *int) {
	*value++ // 解引用并自增
}

// 递归函数示例：阶乘
func factorial(n int) int {
	if n <= 1 {
		return 1
	}
	return n * factorial(n-1)
}

// 方法（绑定到结构体的函数）
type Person struct {
	FirstName string
	LastName  string
	Age       int
}

// 为Person结构体定义方法
func (p Person) FullName() string {
	return p.FirstName + " " + p.LastName
}

// 使用指针接收者的方法（可以修改结构体的值）
func (p *Person) Birthday() {
	p.Age++
}

func main() {
	fmt.Println("函数示例:")
	
	// 调用基本函数
	sayHello()
	
	// 调用带参数的函数
	greet("张三")
	
	// 调用多参数函数
	add(5, 3)
	
	// 调用带返回值的函数
	product := multiply(4, 7)
	fmt.Printf("4 × 7 = %d\n", product)
	
	// 调用多返回值函数
	result, err := divide(10, 2)
	if err != nil {
		fmt.Println("错误:", err)
	} else {
		fmt.Printf("10 ÷ 2 = %.2f\n", result)
	}
	
	// 错误情况
	result, err = divide(10, 0)
	if err != nil {
		fmt.Println("错误:", err)
	} else {
		fmt.Printf("结果: %.2f\n", result)
	}
	
	// 调用命名返回值函数
	area, perimeter := rectangleProperties(5, 3)
	fmt.Printf("矩形面积: %.2f, 周长: %.2f\n", area, perimeter)
	
	// 调用可变参数函数
	fmt.Println("总和:", sum(1, 2, 3, 4, 5))
	
	// 使用切片作为可变参数
	numbers := []int{10, 20, 30, 40, 50}
	fmt.Println("切片总和:", sum(numbers...)) // 使用...展开切片
	
	// 调用使用defer的函数
	fmt.Println("\ndefer示例:")
	deferDemo()
	
	// 调用有多个defer的函数
	fmt.Println("\n多个defer示例:")
	multipleDeferDemo()
	
	// 函数作为值传递
	fmt.Println("\n函数作为值传递示例:")
	fmt.Println("加法:", calculate(addFunc, 10, 5))
	fmt.Println("减法:", calculate(subFunc, 10, 5))
	fmt.Println("乘法:", calculate(mulFunc, 10, 5))
	
	// 使用匿名函数
	fmt.Println("\n匿名函数示例:")
	// 定义并立即调用匿名函数
	func() {
		fmt.Println("这是一个匿名函数")
	}()
	
	// 带参数的匿名函数
	func(message string) {
		fmt.Println("匿名函数打印:", message)
	}("你好Go语言")
	
	// 匿名函数赋值给变量
	greetFunc := func(name string) string {
		return "你好，" + name
	}
	fmt.Println(greetFunc("李四"))
	
	// 闭包示例
	fmt.Println("\n闭包示例:")
	square := makePowerFunc(2)   // 创建计算平方的函数
	cube := makePowerFunc(3)     // 创建计算立方的函数
	
	fmt.Println("5的平方:", square(5))
	fmt.Println("5的立方:", cube(5))
	
	// 指针参数示例
	fmt.Println("\n指针参数示例:")
	count := 10
	fmt.Println("调用前:", count)
	increment(&count) // 传递count的地址
	fmt.Println("调用后:", count)
	
	// 递归函数示例
	fmt.Println("\n递归函数示例:")
	fmt.Println("5的阶乘:", factorial(5))
	
	// 结构体方法示例
	fmt.Println("\n结构体方法示例:")
	person := Person{
		FirstName: "李",
		LastName:  "明",
		Age:       25,
	}
	
	fmt.Println("全名:", person.FullName())
	fmt.Println("年龄:", person.Age)
	
	person.Birthday() // 调用使用指针接收者的方法
	fmt.Println("生日后年龄:", person.Age)
} 