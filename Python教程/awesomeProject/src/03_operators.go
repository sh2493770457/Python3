// 03_operators.go - 运算符示例
package main

import "fmt"

func main() {
	// 算术运算符
	a, b := 10, 3
	
	fmt.Println("算术运算符示例:")
	fmt.Printf("a = %d, b = %d\n", a, b)
	fmt.Printf("a + b = %d\n", a+b)  // 加法
	fmt.Printf("a - b = %d\n", a-b)  // 减法
	fmt.Printf("a * b = %d\n", a*b)  // 乘法
	fmt.Printf("a / b = %d\n", a/b)  // 整数除法（结果为整数）
	fmt.Printf("a %% b = %d\n", a%b) // 取余
	
	// 浮点数除法
	c, d := 10.0, 3.0
	fmt.Printf("浮点数除法 %.1f / %.1f = %.2f\n", c, d, c/d)
	
	// 自增和自减
	x := 5
	x++ // 自增
	fmt.Printf("x自增后 = %d\n", x)
	x-- // 自减
	fmt.Printf("x自减后 = %d\n", x)
	
	// 比较运算符
	fmt.Println("\n比较运算符示例:")
	fmt.Printf("a == b: %t\n", a == b) // 等于
	fmt.Printf("a != b: %t\n", a != b) // 不等于
	fmt.Printf("a > b: %t\n", a > b)   // 大于
	fmt.Printf("a < b: %t\n", a < b)   // 小于
	fmt.Printf("a >= b: %t\n", a >= b) // 大于等于
	fmt.Printf("a <= b: %t\n", a <= b) // 小于等于
	
	// 逻辑运算符
	p, q := true, false
	
	fmt.Println("\n逻辑运算符示例:")
	fmt.Printf("p = %t, q = %t\n", p, q)
	fmt.Printf("p && q (与): %t\n", p && q)  // 逻辑与
	fmt.Printf("p || q (或): %t\n", p || q)  // 逻辑或
	fmt.Printf("!p (非): %t\n", !p)          // 逻辑非
	
	// 位运算符
	m, n := 60, 13 // 二进制: 60 = 00111100, 13 = 00001101
	
	fmt.Println("\n位运算符示例:")
	fmt.Printf("m = %d (%08b), n = %d (%08b)\n", m, m, n, n)
	fmt.Printf("m & n (按位与): %d (%08b)\n", m&n, m&n)    // 按位与
	fmt.Printf("m | n (按位或): %d (%08b)\n", m|n, m|n)    // 按位或
	fmt.Printf("m ^ n (按位异或): %d (%08b)\n", m^n, m^n)  // 按位异或
	fmt.Printf("m << 2 (左移): %d (%08b)\n", m<<2, m<<2)   // 左移
	fmt.Printf("m >> 2 (右移): %d (%08b)\n", m>>2, m>>2)   // 右移
	
	// 赋值运算符
	v := 10
	fmt.Println("\n赋值运算符示例:")
	fmt.Printf("初始 v = %d\n", v)
	
	v += 5  // 等同于 v = v + 5
	fmt.Printf("v += 5: %d\n", v)
	
	v -= 3  // 等同于 v = v - 3
	fmt.Printf("v -= 3: %d\n", v)
	
	v *= 2  // 等同于 v = v * 2
	fmt.Printf("v *= 2: %d\n", v)
	
	v /= 4  // 等同于 v = v / 4
	fmt.Printf("v /= 4: %d\n", v)
	
	v %= 2  // 等同于 v = v % 2
	fmt.Printf("v %%= 2: %d\n", v)
} 