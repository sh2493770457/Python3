// 02_variables.go - 变量声明和基本数据类型
package main

import "fmt"

func main() {
	// 变量声明方式一：使用var关键字（显式类型）
	var name string = "张三"
	var age int = 25
	var isActive bool = true
	
	// 变量声明方式二：使用var关键字（类型推断）
	var country = "中国"
	var population = 14.0e8 // 14亿，用科学计数法表示
	
	// 变量声明方式三：简短声明（只能在函数内使用）
	city := "北京"
	temperature := 23.5
	
	// 常量声明
	const PI = 3.1415926
	const (
		StatusOK = 200
		StatusNotFound = 404
	)
	
	// 多变量声明
	var a, b, c int = 1, 2, 3
	x, y, z := 10, "hello", true
	
	// 打印所有变量
	fmt.Println("姓名:", name)
	fmt.Println("年龄:", age)
	fmt.Println("是否活跃:", isActive)
	fmt.Println("国家:", country)
	fmt.Println("人口:", population)
	fmt.Println("城市:", city)
	fmt.Println("温度:", temperature)
	fmt.Println("PI值:", PI)
	fmt.Println("HTTP状态码:", StatusOK, StatusNotFound)
	fmt.Println("多变量a, b, c:", a, b, c)
	fmt.Println("多变量x, y, z:", x, y, z)
	
	// 基本数据类型
	var (
		integer int = 100               // 整数类型
		floatNum float64 = 3.14         // 浮点数类型
		complex128Num complex128 = 1+2i // 复数类型
		str string = "Go编程"           // 字符串类型
		boolean bool = false            // 布尔类型
		byteVal byte = 'A'              // byte类型（uint8的别名）
		runeVal rune = '中'             // rune类型（int32的别名，表示Unicode码点）
	)
	
	fmt.Printf("整数类型 (int): %v, 类型: %T\n", integer, integer)
	fmt.Printf("浮点类型 (float64): %v, 类型: %T\n", floatNum, floatNum)
	fmt.Printf("复数类型 (complex128): %v, 类型: %T\n", complex128Num, complex128Num)
	fmt.Printf("字符串类型 (string): %v, 类型: %T\n", str, str)
	fmt.Printf("布尔类型 (bool): %v, 类型: %T\n", boolean, boolean)
	fmt.Printf("字节类型 (byte): %v (%c), 类型: %T\n", byteVal, byteVal, byteVal)
	fmt.Printf("字符类型 (rune): %v (%c), 类型: %T\n", runeVal, runeVal, runeVal)
} 