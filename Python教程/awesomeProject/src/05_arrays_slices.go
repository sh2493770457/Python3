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