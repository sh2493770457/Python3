// 11_error_handling.go - 错误处理示例
package main

import (
	"errors"
	"fmt"
	"io/ioutil"
	"os"
	"strconv"
)

// 基本错误处理
func basicErrorHandling() {
	fmt.Println("基本错误处理示例:")
	
	// 尝试打开文件
	file, err := os.Open("不存在的文件.txt")
	if err != nil {
		fmt.Println("  错误:", err)
	} else {
		defer file.Close()
		fmt.Println("  文件打开成功")
	}
	
	// 尝试进行类型转换
	str := "123"
	num, err := strconv.Atoi(str)
	if err != nil {
		fmt.Println("  转换错误:", err)
	} else {
		fmt.Printf("  转换成功: %q -> %d\n", str, num)
	}
	
	// 错误转换失败的情况
	str = "abc"
	num, err = strconv.Atoi(str)
	if err != nil {
		fmt.Println("  转换错误:", err)
	} else {
		fmt.Printf("  转换成功: %q -> %d\n", str, num)
	}
}

// 创建自定义错误
func customErrors() {
	fmt.Println("\n自定义错误示例:")
	
	// 方法1: 使用errors.New创建简单错误
	err1 := errors.New("这是一个自定义错误")
	fmt.Println("  错误1:", err1)
	
	// 方法2: 使用fmt.Errorf创建带格式的错误
	name := "张三"
	age := -5
	err2 := fmt.Errorf("无效的年龄 %d （用户: %s）", age, name)
	fmt.Println("  错误2:", err2)
	
	// 方法3: 自定义错误类型
	result, err := divide(10, 0)
	if err != nil {
		fmt.Println("  除法错误:", err)
		
		// 使用类型断言判断具体错误类型
		if divErr, ok := err.(*DivisionError); ok {
			fmt.Printf("  这是除法错误: %d / %d, 原因: %s\n",
				divErr.Dividend, divErr.Divisor, divErr.Reason)
		}
	} else {
		fmt.Println("  结果:", result)
	}
	
	// 正常情况
	result, err = divide(10, 2)
	if err != nil {
		fmt.Println("  除法错误:", err)
	} else {
		fmt.Println("  结果:", result)
	}
}

// 自定义错误类型
type DivisionError struct {
	Dividend int
	Divisor  int
	Reason   string
}

// 实现error接口
func (e *DivisionError) Error() string {
	return fmt.Sprintf("除法错误: %d / %d, 原因: %s", e.Dividend, e.Divisor, e.Reason)
}

// 返回自定义错误的函数
func divide(dividend, divisor int) (int, error) {
	if divisor == 0 {
		return 0, &DivisionError{
			Dividend: dividend,
			Divisor:  divisor,
			Reason:   "除数不能为零",
		}
	}
	return dividend / divisor, nil
}

// 错误包装和展开
func errorWrapping() {
	fmt.Println("\n错误包装示例:")
	
	err := readFile("不存在的文件.txt")
	if err != nil {
		fmt.Println("  读取文件错误:", err)
		
		// 从包装的错误中提取原始错误
		if errors.Is(err, os.ErrNotExist) {
			fmt.Println("  错误类型: 文件不存在")
		}
	}
}

// 包装错误
func readFile(filename string) error {
	data, err := ioutil.ReadFile(filename)
	if err != nil {
		// 包装错误，添加上下文信息
		return fmt.Errorf("读取文件 %s 失败: %w", filename, err)
	}
	
	fmt.Println("  文件内容:", string(data))
	return nil
}

// 使用defer和recover处理panic
func deferRecoverDemo() {
	fmt.Println("\ndefer和recover处理panic示例:")
	
	fmt.Println("  调用可能发生panic的函数...")
	result := safeDivide(10, 0)
	fmt.Println("  函数调用返回:", result)
	
	fmt.Println("  程序继续执行...")
	
	// 演示不同的panic情况
	demoMultiplePanics()
}

// 使用recover捕获panic的安全函数
func safeDivide(a, b int) int {
	// 延迟函数会在函数返回前执行
	defer func() {
		// recover()返回panic的值，如果没有panic则为nil
		if r := recover(); r != nil {
			fmt.Printf("  捕获到panic: %v\n", r)
		}
	}()
	
	// 可能引发panic的代码
	if b == 0 {
		panic("除数不能为零")
	}
	
	return a / b
}

// 演示多个panic和recover的情况
func demoMultiplePanics() {
	defer fmt.Println("  主函数的defer执行")
	
	// 捕获第一个panic
	defer func() {
		if r := recover(); r != nil {
			fmt.Printf("  捕获到第二个panic: %v\n", r)
		}
	}()
	
	// 调用可能发生panic的函数
	func() {
		defer func() {
			if r := recover(); r != nil {
				fmt.Printf("  捕获到第一个panic: %v\n", r)
				// 抛出一个新的panic
				panic("第二个panic: 嵌套panic示例")
			}
		}()
		
		// 引发一个panic
		panic("第一个panic: 初始异常")
	}()
	
	// 如果所有panic都被处理，这里的代码会执行
	fmt.Println("  这行不会执行，因为第二个panic会传播到上一级")
}

// 使用变量存储和检查多个错误
func multipleErrors() {
	fmt.Println("\n处理多个错误示例:")
	
	// 模拟多个操作，每个操作可能产生错误
	var errs []error
	
	// 操作1
	if err := operation1(); err != nil {
		errs = append(errs, fmt.Errorf("操作1失败: %w", err))
	}
	
	// 操作2
	if err := operation2(); err != nil {
		errs = append(errs, fmt.Errorf("操作2失败: %w", err))
	}
	
	// 检查是否有错误发生
	if len(errs) > 0 {
		fmt.Println("  发生了以下错误:")
		for i, err := range errs {
			fmt.Printf("  %d. %v\n", i+1, err)
		}
	} else {
		fmt.Println("  所有操作成功完成")
	}
}

// 模拟可能失败的操作
func operation1() error {
	return errors.New("网络超时")
}

func operation2() error {
	// 模拟操作成功
	return nil
}

func main() {
	// 基本错误处理
	basicErrorHandling()
	
	// 自定义错误
	customErrors()
	
	// 错误包装
	errorWrapping()
	
	// 使用defer和recover处理panic
	deferRecoverDemo()
	
	// 处理多个错误
	multipleErrors()
} 