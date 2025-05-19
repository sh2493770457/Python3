# Go语言函数

函数是Go语言中的基本构建块，它们允许我们将代码组织成可重用的块。本章将详细介绍Go语言中函数的定义、使用和各种特性。

## 1. 函数基础

### 1.1 函数定义

Go语言中的函数使用`func`关键字定义，基本语法如下：

```go
func 函数名(参数列表) 返回值类型 {
    // 函数体
    return 返回值
}
```

例如，一个简单的无参数无返回值的函数：

```go
func sayHello() {
    fmt.Println("你好，世界！")
}
```

### 1.2 函数参数

Go语言中的函数可以接受零个或多个参数：

```go
// 带参数的函数
func greet(name string) {
    fmt.Printf("你好，%s！\n", name)
}

// 多参数函数
func add(a, b int) {
    fmt.Printf("%d + %d = %d\n", a, b, a+b)
}
```

当多个连续参数类型相同时，可以只在最后一个参数后面声明类型。

### 1.3 函数返回值

Go函数可以返回零个或多个值：

```go
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
```

### 1.4 命名返回值

Go允许在函数定义时为返回值命名，这些命名的返回值会被初始化为对应类型的零值：

```go
func rectangleProperties(length, width float64) (area, perimeter float64) {
    area = length * width
    perimeter = 2 * (length + width)
    return // 裸返回，自动返回命名的返回值
}
```

## 2. 高级函数特性

### 2.1 可变参数函数

Go支持可变参数函数，允许函数接受任意数量的参数：

```go
// 可变参数函数
func sum(numbers ...int) int {
    total := 0
    for _, num := range numbers {
        total += num
    }
    return total
}

// 调用方式
sum(1, 2, 3, 4, 5)           // 直接传递参数
numbers := []int{10, 20, 30}
sum(numbers...)               // 使用...展开切片
```

### 2.2 defer语句

`defer`语句会将函数调用推迟到外层函数返回之前执行，常用于资源清理：

```go
func deferDemo() {
    defer fmt.Println("这句话会在函数结束时打印") // 最后执行
    
    fmt.Println("函数开始执行")
    fmt.Println("函数中间的操作")
    // 函数结束前会执行defer语句
}
```

多个`defer`语句按照后进先出（LIFO）的顺序执行：

```go
func multipleDeferDemo() {
    defer fmt.Println("第一个defer") // 最后执行
    defer fmt.Println("第二个defer") // 倒数第二个执行
    defer fmt.Println("第三个defer") // 最先执行
    
    fmt.Println("函数主体")
}
```

### 2.3 函数作为值

Go语言中函数是一等公民，可以作为值传递：

```go
// 函数作为值传递
func calculate(operation func(int, int) int, a, b int) int {
    return operation(a, b)
}

// 定义几个可以作为参数的函数
func addFunc(x, y int) int { return x + y }
func subFunc(x, y int) int { return x - y }
func mulFunc(x, y int) int { return x * y }

// 使用方式
calculate(addFunc, 10, 5) // 结果: 15
calculate(subFunc, 10, 5) // 结果: 5
calculate(mulFunc, 10, 5) // 结果: 50
```

### 2.4 匿名函数和闭包

Go支持匿名函数，可以在需要时定义并立即调用：

```go
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
```

闭包是引用了外部变量的匿名函数：

```go
// 返回一个闭包函数
func makePowerFunc(power int) func(int) int {
    return func(base int) int {
        result := 1
        for i := 0; i < power; i++ {
            result *= base
        }
        return result
    }
}

// 使用闭包
square := makePowerFunc(2)   // 创建计算平方的函数
cube := makePowerFunc(3)     // 创建计算立方的函数

fmt.Println(square(5))       // 输出: 25
fmt.Println(cube(5))         // 输出: 125
```

### 2.5 指针参数

Go函数可以接受指针参数，允许函数修改调用者的变量：

```go
// 使用指针参数修改变量
func increment(value *int) {
    *value++ // 解引用并自增
}

// 使用方式
count := 10
increment(&count) // 传递count的地址
fmt.Println(count) // 输出: 11
```

### 2.6 递归函数

Go支持递归函数，即函数调用自身：

```go
// 递归函数示例：阶乘
func factorial(n int) int {
    if n <= 1 {
        return 1
    }
    return n * factorial(n-1)
}

// 使用方式
fmt.Println(factorial(5)) // 输出: 120 (5*4*3*2*1)
```

## 3. 方法

方法是与特定类型关联的函数：

```go
// 定义结构体
type Person struct {
    FirstName string
    LastName  string
    Age       int
}

// 为Person结构体定义方法（值接收者）
func (p Person) FullName() string {
    return p.FirstName + " " + p.LastName
}

// 使用指针接收者的方法（可以修改结构体的值）
func (p *Person) Birthday() {
    p.Age++
}

// 使用方式
person := Person{
    FirstName: "李",
    LastName:  "明",
    Age:       25,
}

fmt.Println(person.FullName()) // 输出: 李 明
person.Birthday()              // 调用方法增加年龄
fmt.Println(person.Age)        // 输出: 26
```

值接收者和指针接收者的区别：
- 值接收者：方法操作的是结构体的副本，不会修改原结构体
- 指针接收者：方法操作的是结构体本身，可以修改原结构体

## 4. 函数最佳实践

1. **保持函数简短**：每个函数应该只做一件事，并且做好
2. **使用有意义的函数名**：函数名应该清晰表达其功能
3. **限制参数数量**：参数过多时考虑使用结构体封装
4. **使用多返回值处理错误**：Go惯用法是返回结果和错误
5. **注意闭包中变量捕获**：特别是在循环中创建闭包时
6. **合理使用defer**：用于资源清理和异常处理

## 5. 练习题

1. 编写一个函数，接受一个整数切片，返回切片中的最大值和最小值
2. 实现一个函数，可以对任意类型的切片进行反转
3. 创建一个计算器程序，支持加、减、乘、除四种操作，使用函数作为参数实现
4. 编写一个递归函数计算斐波那契数列的第n项
5. 为一个自定义的`Book`结构体实现多个方法，如获取完整信息、更新价格等

通过本章的学习，你应该能够理解并熟练使用Go语言中的函数特性，包括基本函数、高级特性和方法的定义与使用。函数是Go程序的基本构建块，掌握它们对于编写高效、可维护的Go代码至关重要。