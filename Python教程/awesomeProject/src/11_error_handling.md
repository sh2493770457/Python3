# Go语言错误处理

Go语言采用了显式的错误处理机制，通过返回值而非异常来处理错误。这种设计鼓励开发者主动检查和处理错误，提高代码的健壮性。本章将详细介绍Go语言中的错误处理机制和最佳实践。

## 1. 错误处理基础

### 1.1 错误类型

Go语言中的错误是实现了`error`接口的任何类型。`error`接口只有一个方法：

```go
type error interface {
    Error() string
}
```

标准库中的`errors`包提供了创建简单错误的函数：

```go
// 创建一个简单的错误
err := errors.New("这是一个错误")

// 使用fmt.Errorf创建格式化的错误
name := "张三"
age := -5
err := fmt.Errorf("无效的年龄 %d （用户: %s）", age, name)
```

### 1.2 错误处理模式

Go语言中处理错误的标准模式是检查函数返回的错误值：

```go
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
```

这种模式的特点：
1. 错误作为函数的最后一个返回值
2. 调用者通过检查错误值是否为`nil`来判断操作是否成功
3. 如果错误不为`nil`，应立即处理错误或将其传递给上层调用者

## 2. 自定义错误

### 2.1 创建自定义错误类型

对于复杂的错误情况，可以创建自定义错误类型：

```go
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
```

使用自定义错误类型的优势：
1. 可以包含更多的上下文信息
2. 允许调用者通过类型断言获取详细错误信息
3. 提供更精确的错误处理机制

### 2.2 使用自定义错误

```go
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
```

## 3. 错误包装和展开

Go 1.13引入了错误包装功能，允许在不丢失原始错误信息的情况下添加上下文信息。

### 3.1 包装错误

使用`fmt.Errorf`和`%w`动词包装错误：

```go
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
```

### 3.2 展开错误

使用`errors.Is`和`errors.As`函数检查和提取包装的错误：

```go
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
```

`errors.Is`和`errors.As`函数：
- `errors.Is(err, target)` - 检查错误链中是否包含特定错误
- `errors.As(err, &target)` - 将错误链中的第一个匹配类型的错误提取到target中

## 4. Panic和Recover机制

Go语言提供了`panic`和`recover`机制用于处理异常情况。

### 4.1 Panic

`panic`会导致程序崩溃，除非被`recover`捕获：

```go
// 引发panic
func someFunctionThatPanics() {
    panic("发生了严重错误")
}
```

### 4.2 Defer和Recover

`defer`和`recover`可以用来捕获和处理panic：

```go
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
```

使用示例：

```go
// 使用defer和recover处理panic
func deferRecoverDemo() {
	fmt.Println("\ndefer和recover处理panic示例:")
	
	fmt.Println("  调用可能发生panic的函数...")
	result := safeDivide(10, 0)
	fmt.Println("  函数调用返回:", result)
	
	fmt.Println("  程序继续执行...")
}
```

### 4.3 嵌套Panic和Recover

```go
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
```

## 5. 错误处理最佳实践

### 5.1 处理多个错误

```go
// 使用变量存储和检查多个错误
func multipleErrors() {
	fmt.Println("\n处理多个错误示例:")
	
	// 模拟多个操作，每个操作可能产生错误
	var errs []error
	
	// 操作1
	if _, err := os.Open("不存在的文件1.txt"); err != nil {
		errs = append(errs, fmt.Errorf("操作1失败: %w", err))
	}
	
	// 操作2
	if _, err := strconv.Atoi("非数字"); err != nil {
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
```

### 5.2 错误处理策略

在Go语言中，有几种常见的错误处理策略：

1. **传播错误**：将错误返回给调用者
   ```go
   func processFile(filename string) error {
       data, err := readFile(filename)
       if err != nil {
           return err // 或者 return fmt.Errorf("处理文件时出错: %w", err)
       }
       // 处理数据...
       return nil
   }
   ```

2. **重试操作**：在遇到临时错误时尝试重试
   ```go
   func connectWithRetry(url string, maxRetries int) error {
       var err error
       for i := 0; i < maxRetries; i++ {
           err = connect(url)
           if err == nil {
               return nil
           }
           time.Sleep(time.Second * time.Duration(i+1))
       }
       return fmt.Errorf("连接失败，已重试%d次: %w", maxRetries, err)
   }
   ```

3. **记录并继续**：记录错误但不中断程序流程
   ```go
   func processItems(items []string) {
       for _, item := range items {
           err := processItem(item)
           if err != nil {
               log.Printf("处理项目 %s 时出错: %v\n", item, err)
               continue // 继续处理下一项
           }
       }
   }
   ```

4. **停止程序**：遇到致命错误时停止程序
   ```go
   func initializeApp() {
       config, err := loadConfig()
       if err != nil {
           log.Fatalf("无法加载配置: %v\n", err)
           // log.Fatalf会调用os.Exit(1)
       }
       // 继续初始化...
   }
   ```

## 6. 错误与日志

错误处理通常与日志记录结合使用：

```go
// 错误日志示例
func errorLogging() {
	// 设置日志格式
	log.SetPrefix("ERROR: ")
	log.SetFlags(log.Ldate | log.Ltime | log.Lshortfile)
	
	// 尝试打开文件
	_, err := os.Open("不存在的文件.txt")
	if err != nil {
		// 记录错误
		log.Printf("打开文件时出错: %v\n", err)
		
		// 对于致命错误，可以使用log.Fatal
		// log.Fatalf("致命错误: %v\n", err) // 会调用os.Exit(1)
	}
}
```

## 7. 何时使用错误，何时使用Panic

### 7.1 使用错误的情况

- 可预见的问题（文件不存在、网络连接失败等）
- 可恢复的情况
- 作为API的一部分，让调用者决定如何处理错误

### 7.2 使用Panic的情况

- 不可恢复的情况（初始化失败等）
- 编程错误（数组越界、空指针等）
- 在程序启动时检测到的配置或环境问题

## 8. 练习题

1. 创建一个自定义错误类型，用于表示用户输入验证错误
2. 实现一个函数，它尝试打开多个文件，并返回一个包含所有错误的切片
3. 编写一个使用`recover`的函数，可以安全地执行任何可能发生panic的函数
4. 实现一个带有重试机制的HTTP请求函数
5. 设计一个错误处理策略，用于处理数据库操作中的各种错误

通过本章的学习，你应该能够理解并熟练使用Go语言中的错误处理机制，包括基本错误处理、自定义错误、错误包装和panic/recover机制。正确的错误处理是编写健壮Go程序的关键部分。