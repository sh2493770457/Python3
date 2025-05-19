# Go语言变量和数据类型

## 概述

本文档详细介绍了Go语言中变量的声明、初始化方式以及基本数据类型。变量是编程中最基础的概念，用于存储程序运行过程中的数据。Go语言具有强类型系统，每个变量都有特定的类型。

## 源代码

```go
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
```

## 变量声明方式

Go语言提供了多种变量声明和初始化的方式：

### 1. 使用var关键字（显式指定类型）

```go
var name string = "张三"
var age int = 25
var isActive bool = true
```

这种方式明确指定了变量的类型，使代码更加清晰明了。

### 2. 使用var关键字（类型推断）

```go
var country = "中国"
var population = 14.0e8 // 14亿，用科学计数法表示
```

Go编译器会根据赋值自动推断变量类型，简化代码编写。

### 3. 使用简短声明（:=）

```go
city := "北京"
temperature := 23.5
```

这是最简洁的声明方式，但只能在函数内部使用，不能用于全局变量。变量类型由右侧表达式推断。

### 4. 多变量声明

```go
var a, b, c int = 1, 2, 3  // 同类型多变量
x, y, z := 10, "hello", true  // 不同类型多变量
```

Go允许在一行中声明多个变量，可以是相同类型，也可以是不同类型。

### 5. 变量分组声明

```go
var (
    integer int = 100
    floatNum float64 = 3.14
    // 更多变量...
)
```

通过分组提高代码可读性，常用于声明多个相关变量。

## 常量声明

常量是在程序运行过程中不可修改的值：

```go
const PI = 3.1415926
const (
    StatusOK = 200
    StatusNotFound = 404
)
```

常量通常使用大写字母命名，可以单独声明或分组声明。常量的类型可以是字符、字符串、布尔值或数值。

## Go的基本数据类型

### 1. 整数类型

- `int`：根据系统位数决定（32位系统为32位，64位系统为64位）
- `int8`：8位有符号整数 (-128 到 127)
- `int16`：16位有符号整数 (-32768 到 32767)
- `int32`：32位有符号整数 (-2^31 到 2^31-1)
- `int64`：64位有符号整数 (-2^63 到 2^63-1)
- `uint`：无符号整数
- `uint8`/`byte`：8位无符号整数 (0 到 255)
- `uint16`：16位无符号整数 (0 到 65535)
- `uint32`：32位无符号整数 (0 到 2^32-1)
- `uint64`：64位无符号整数 (0 到 2^64-1)
- `uintptr`：无符号整数，足够存放指针

### 2. 浮点类型

- `float32`：32位浮点数
- `float64`：64位浮点数（默认和推荐）

### 3. 复数类型

- `complex64`：由两个float32组成
- `complex128`：由两个float64组成（默认）

```go
complex128Num := 1+2i
```

### 4. 字符串类型

- `string`：字符串是不可变的字符序列

```go
str := "Go编程"
```

### 5. 布尔类型

- `bool`：只能是`true`或`false`

```go
boolean := false
```

### 6. 字符类型

- `byte`：等同于`uint8`，表示ASCII字符
- `rune`：等同于`int32`，表示Unicode码点（可以存储中文等字符）

```go
byteVal := 'A'    // ASCII字符
runeVal := '中'    // Unicode字符
```

## 零值

在Go中，未显式初始化的变量会被赋予其类型的"零值"：

- 数值类型（整数、浮点数、复数）：`0`
- 布尔类型：`false`
- 字符串：`""` (空字符串)
- 指针、接口等：`nil`

## 类型转换

Go不支持隐式类型转换，必须显式进行类型转换：

```go
var i int = 42
var f float64 = float64(i)
var u uint = uint(f)
```

## 格式化输出

`fmt.Printf`用于格式化输出变量信息：

- `%v`：打印值
- `%T`：打印类型
- `%d`：整数
- `%f`：浮点数
- `%t`：布尔值
- `%s`：字符串
- `%c`：字符

## 重点注意事项

1. Go是强类型语言，变量必须先声明后使用
2. 声明的变量如果不使用，Go编译器会报错
3. `:=`只能用于函数内部，不能用于全局变量
4. Go中的变量名区分大小写
5. 优先使用驼峰命名法（如`firstName`）
6. 变量作用域遵循最小可见性原则

## 练习建议

1. 尝试声明不同类型的变量并输出它们的零值
2. 使用不同的格式化动词（`%v`, `%T`, `%d`等）打印变量
3. 尝试各种类型转换，观察结果
4. 尝试使用未声明的变量，观察编译错误
5. 声明但不使用变量，观察编译警告 