# Go语言数组和切片

## 概述

本文档详细介绍了Go语言中的数组和切片数据结构。数组是固定长度的元素序列，而切片是对数组的灵活抽象，提供了动态大小的功能。这两种数据结构在Go编程中广泛使用，是掌握Go语言的核心概念。

## 源代码

```go
// 05_arrays_slices.go - 数组和切片示例
package main

import "fmt"

func main() {
	// 数组声明和初始化
	fmt.Println("数组示例:")
	
	// 方式1：声明固定大小的数组
	var arr1 [5]int // 默认值为零值（int的零值为0）
	fmt.Println("arr1 (零值数组):", arr1)
	
	// 方式2：声明并初始化数组
	var arr2 = [5]int{1, 2, 3, 4, 5}
	fmt.Println("arr2 (完整初始化):", arr2)
	
	// 方式3：声明并部分初始化数组
	var arr3 = [5]int{1, 2, 3}
	fmt.Println("arr3 (部分初始化):", arr3) // 未指定的元素为零值
	
	// 方式4：让编译器计算数组长度
	arr4 := [...]int{10, 20, 30, 40, 50}
	fmt.Println("arr4 (自动计算长度):", arr4)
	fmt.Println("arr4的长度:", len(arr4))
	
	// 方式5：指定索引的初始化
	arr5 := [5]int{0: 100, 2: 300, 4: 500}
	fmt.Println("arr5 (指定索引初始化):", arr5)
	
	// 数组访问和修改
	arr6 := [5]int{1, 2, 3, 4, 5}
	fmt.Printf("arr6[2] = %d\n", arr6[2]) // 访问元素
	arr6[2] = 30                          // 修改元素
	fmt.Println("修改后的arr6:", arr6)
	
	// 多维数组
	var matrix [3][4]int // 3行4列的二维数组
	matrix[0][0] = 1
	matrix[1][2] = 2
	fmt.Println("\n二维数组matrix:")
	for i := 0; i < 3; i++ {
		for j := 0; j < 4; j++ {
			fmt.Printf("%d ", matrix[i][j])
		}
		fmt.Println()
	}
	
	// 初始化二维数组
	grid := [2][3]int{
		{1, 2, 3},   // 第一行
		{4, 5, 6},   // 第二行
	}
	fmt.Println("初始化的二维数组grid:", grid)
	
	// 切片（动态数组）
	fmt.Println("\n切片示例:")
	
	// 方式1：通过make函数创建切片
	slice1 := make([]int, 5)      // 长度为5，容量为5的切片
	fmt.Println("slice1:", slice1)
	
	slice2 := make([]int, 3, 5)   // 长度为3，容量为5的切片
	fmt.Printf("slice2: %v, 长度: %d, 容量: %d\n", slice2, len(slice2), cap(slice2))
	
	// 方式2：通过字面量创建切片
	slice3 := []int{1, 2, 3, 4, 5}
	fmt.Printf("slice3: %v, 长度: %d, 容量: %d\n", slice3, len(slice3), cap(slice3))
	
	// 方式3：从数组创建切片
	arr := [5]int{10, 20, 30, 40, 50}
	slice4 := arr[1:4]  // 包含索引1到3的元素（20,30,40）
	fmt.Printf("原数组: %v\n", arr)
	fmt.Printf("slice4: %v, 长度: %d, 容量: %d\n", slice4, len(slice4), cap(slice4))
	
	// 切片的修改会影响底层数组
	slice4[0] = 200
	fmt.Println("修改切片后的原数组:", arr)
	
	// 切片操作
	fmt.Println("\n切片操作示例:")
	numbers := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
	fmt.Println("原始切片:", numbers)
	
	// 切片的切片
	slice5 := numbers[2:8]
	fmt.Println("numbers[2:8]:", slice5)
	
	// 省略下界默认为0
	slice6 := numbers[:5]
	fmt.Println("numbers[:5]:", slice6)
	
	// 省略上界默认为切片长度
	slice7 := numbers[5:]
	fmt.Println("numbers[5:]:", slice7)
	
	// 使用append()向切片添加元素
	slice8 := []int{1, 2, 3}
	fmt.Printf("原始slice8: %v, 长度: %d, 容量: %d\n", slice8, len(slice8), cap(slice8))
	
	// 添加一个元素
	slice8 = append(slice8, 4)
	fmt.Printf("添加后slice8: %v, 长度: %d, 容量: %d\n", slice8, len(slice8), cap(slice8))
	
	// 添加多个元素
	slice8 = append(slice8, 5, 6, 7)
	fmt.Printf("再次添加后slice8: %v, 长度: %d, 容量: %d\n", slice8, len(slice8), cap(slice8))
	
	// 添加另一个切片的所有元素（使用...展开切片）
	otherSlice := []int{8, 9, 10}
	slice8 = append(slice8, otherSlice...)
	fmt.Println("添加另一个切片后:", slice8)
	
	// 使用copy()复制切片
	src := []int{1, 2, 3, 4, 5}
	dst := make([]int, len(src))
	count := copy(dst, src)
	fmt.Printf("复制切片: %v, 复制的元素个数: %d\n", dst, count)
	
	// 切片的零值为nil
	var nilSlice []int
	fmt.Println("\n空切片相关:")
	fmt.Printf("nilSlice: %v, 长度: %d, 是否为nil: %t\n", nilSlice, len(nilSlice), nilSlice == nil)
	
	// 空切片不等于nil
	emptySlice := []int{}
	fmt.Printf("emptySlice: %v, 长度: %d, 是否为nil: %t\n", emptySlice, len(emptySlice), emptySlice == nil)
}
```

## 数组（Arrays）

数组是一个具有固定长度的元素序列，数组的长度是其类型的一部分，这意味着[5]int和[10]int是不同的类型。

### 数组声明和初始化

Go语言提供了多种声明和初始化数组的方式：

#### 1. 声明固定大小的数组（默认零值）

```go
var arr1 [5]int // 创建一个长度为5的int数组，所有元素初始化为0
```

#### 2. 声明并完整初始化数组

```go
var arr2 = [5]int{1, 2, 3, 4, 5} // 明确指定数组长度和所有元素值
```

#### 3. 声明并部分初始化数组

```go
var arr3 = [5]int{1, 2, 3} // 只初始化前三个元素，其余为零值
```

#### 4. 让编译器计算数组长度

```go
arr4 := [...]int{10, 20, 30, 40, 50} // 编译器根据元素个数确定长度
```

#### 5. 使用索引初始化特定位置的元素

```go
arr5 := [5]int{0: 100, 2: 300, 4: 500} // 初始化索引0,2,4的元素，其余为零值
```

### 数组访问和修改

使用方括号和索引访问或修改数组元素：

```go
arr6 := [5]int{1, 2, 3, 4, 5}
fmt.Printf("arr6[2] = %d\n", arr6[2]) // 访问第三个元素（索引从0开始）
arr6[2] = 30                          // 修改第三个元素
```

### 多维数组

Go支持多维数组：

```go
var matrix [3][4]int // 3行4列的二维数组

// 初始化二维数组
grid := [2][3]int{
    {1, 2, 3},   // 第一行
    {4, 5, 6},   // 第二行
}
```

### 数组的特点

1. **固定长度**：数组一旦创建，其长度不能改变
2. **值类型**：数组是值类型，在函数间传递时会创建副本
3. **比较**：相同类型的数组可以使用==和!=操作符比较
4. **内存连续**：数组元素在内存中连续存储

## 切片（Slices）

切片是对数组的灵活抽象，提供了动态大小的视图。切片本身不存储数据，而是引用底层数组的一段连续区域。

### 切片的结构

切片由三部分组成：
- 指向底层数组的指针
- 切片的长度（len）：当前元素个数
- 切片的容量（cap）：从起始位置到底层数组末尾的元素个数

### 切片创建方式

#### 1. 使用make函数创建

```go
slice1 := make([]int, 5)      // 长度和容量都为5
slice2 := make([]int, 3, 5)   // 长度为3，容量为5
```

#### 2. 使用切片字面量创建

```go
slice3 := []int{1, 2, 3, 4, 5} // 注意与数组的区别：没有指定长度
```

#### 3. 从现有数组或切片创建

```go
arr := [5]int{10, 20, 30, 40, 50}
slice4 := arr[1:4]  // 包含arr[1]、arr[2]和arr[3]
```

切片表达式`arr[low:high]`创建一个从索引low到high-1的切片。

### 切片操作

#### 1. 切片的切片

可以从一个切片再创建新的切片：

```go
numbers := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
slice5 := numbers[2:8] // 包含索引2到7的元素
```

#### 2. 省略下界或上界

```go
slice6 := numbers[:5]  // 等同于numbers[0:5]
slice7 := numbers[5:]  // 等同于numbers[5:len(numbers)]
```

#### 3. 使用append()添加元素

`append()`函数用于向切片添加元素，如果超出容量，会自动分配更大的底层数组：

```go
slice8 := []int{1, 2, 3}
slice8 = append(slice8, 4)           // 添加单个元素
slice8 = append(slice8, 5, 6, 7)     // 添加多个元素
slice8 = append(slice8, otherSlice...) // 添加另一个切片的所有元素
```

#### 4. 使用copy()复制切片

```go
src := []int{1, 2, 3, 4, 5}
dst := make([]int, len(src))
count := copy(dst, src) // 返回复制的元素个数
```

### 切片的零值和空切片

切片的零值是`nil`：

```go
var nilSlice []int // nilSlice == nil，长度和容量都是0
```

空切片不等于`nil`：

```go
emptySlice := []int{} // emptySlice != nil，但长度和容量仍然是0
```

### 切片的特点

1. **动态大小**：切片的长度可以随着元素的添加而增长
2. **引用类型**：切片是引用类型，多个切片可以共享底层数组
3. **修改传播**：修改切片会影响共享该底层数组的其他切片
4. **高效传递**：传递切片给函数只会复制切片头部结构，不会复制底层数组

## 数组与切片的区别

| 特性 | 数组 | 切片 |
|------|------|------|
| 长度 | 固定，是类型的一部分 | 动态，可以增长 |
| 声明 | `var a [5]int` | `var s []int` |
| 类型 | 值类型 | 引用类型 |
| 传递方式 | 副本（值传递） | 引用（引用传递） |
| 比较 | 可以用==和!= | 只能与nil比较 |
| 内存 | 在栈上分配（小数组） | 在堆上分配 |

## 最佳实践

1. **优先使用切片**：在大多数情况下，切片比数组更灵活
2. **预先分配容量**：当知道切片大约需要多少元素时，预先分配适当的容量可以减少内存分配
3. **注意共享底层数组**：修改一个切片可能会影响其他共享底层数组的切片
4. **使用copy()创建独立副本**：当需要独立的切片副本时，使用copy()函数
5. **使用append()扩展切片**：而不是手动调整大小
6. **检查切片是否为nil**：在访问切片前检查是否为nil可以避免panic

## 性能考虑

1. 对于小的、固定大小的序列，数组可能性能更好
2. 切片的自动扩容可能导致多次内存分配，预先指定合适的容量可以优化性能
3. 在函数间传递大数组会导致完整复制，影响性能；而传递切片则只复制切片头部结构
``` 