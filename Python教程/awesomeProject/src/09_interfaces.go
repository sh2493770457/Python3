// 09_interfaces.go - 接口示例
package main

import (
	"fmt"
	"math"
)

// 定义Shape接口
type Shape interface {
	Area() float64   // 计算面积
	Perimeter() float64 // 计算周长
}

// 实现Shape接口的Rectangle结构体
type Rectangle struct {
	Width  float64
	Height float64
}

// Rectangle的Area方法
func (r Rectangle) Area() float64 {
	return r.Width * r.Height
}

// Rectangle的Perimeter方法
func (r Rectangle) Perimeter() float64 {
	return 2 * (r.Width + r.Height)
}

// 实现Shape接口的Circle结构体
type Circle struct {
	Radius float64
}

// Circle的Area方法
func (c Circle) Area() float64 {
	return math.Pi * c.Radius * c.Radius
}

// Circle的Perimeter方法（圆的周长）
func (c Circle) Perimeter() float64 {
	return 2 * math.Pi * c.Radius
}

// 打印形状信息的函数（接受Shape接口类型的参数）
func PrintShapeInfo(s Shape) {
	fmt.Printf("面积: %.2f\n", s.Area())
	fmt.Printf("周长: %.2f\n", s.Perimeter())
}

// 多接口示例
type Writer interface {
	Write([]byte) (int, error)
}

type Closer interface {
	Close() error
}

// 组合接口
type WriterCloser interface {
	Writer
	Closer
}

// 实现WriterCloser接口的类型
type BufferedWriterCloser struct {
	buffer []byte
}

func (bwc *BufferedWriterCloser) Write(data []byte) (int, error) {
	// 将数据追加到缓冲区
	bwc.buffer = append(bwc.buffer, data...)
	return len(data), nil
}

func (bwc *BufferedWriterCloser) Close() error {
	// 模拟关闭操作，打印缓冲区内容
	fmt.Println("关闭并输出缓冲区内容:", string(bwc.buffer))
	bwc.buffer = nil
	return nil
}

// 空接口示例
func PrintAnything(v interface{}) {
	fmt.Printf("值: %v, 类型: %T\n", v, v)
}

// 类型断言示例
func GetType(i interface{}) {
	switch v := i.(type) {
	case int:
		fmt.Printf("整数类型: %d\n", v)
	case string:
		fmt.Printf("字符串类型: %s\n", v)
	case bool:
		fmt.Printf("布尔类型: %t\n", v)
	case float64:
		fmt.Printf("浮点类型: %.2f\n", v)
	case Shape:
		fmt.Printf("形状接口: 面积 = %.2f\n", v.Area())
	default:
		fmt.Printf("未知类型: %T\n", v)
	}
}

func main() {
	fmt.Println("接口示例:")
	
	// 创建Rectangle和Circle实例
	r := Rectangle{Width: 5, Height: 3}
	c := Circle{Radius: 2.5}
	
	// 使用接口变量
	var s Shape
	
	// Rectangle实现了Shape接口
	s = r
	fmt.Println("矩形:")
	fmt.Printf("面积: %.2f\n", s.Area())
	fmt.Printf("周长: %.2f\n", s.Perimeter())
	
	// Circle也实现了Shape接口
	s = c
	fmt.Println("\n圆形:")
	fmt.Printf("面积: %.2f\n", s.Area())
	fmt.Printf("周长: %.2f\n", s.Perimeter())
	
	// 使用接受接口的函数
	fmt.Println("\n使用PrintShapeInfo函数:")
	fmt.Println("矩形信息:")
	PrintShapeInfo(r)
	fmt.Println("圆形信息:")
	PrintShapeInfo(c)
	
	// 组合接口示例
	fmt.Println("\n组合接口示例:")
	var wc WriterCloser = &BufferedWriterCloser{}
	wc.Write([]byte("你好，"))
	wc.Write([]byte("Go语言！"))
	wc.Close()
	
	// 空接口示例
	fmt.Println("\n空接口示例:")
	PrintAnything(42)
	PrintAnything("你好Go")
	PrintAnything(true)
	PrintAnything(3.14159)
	PrintAnything(r)    // Rectangle类型
	PrintAnything(c)    // Circle类型
	
	// 类型断言示例
	fmt.Println("\n类型断言示例:")
	var i interface{} = 42
	
	// 尝试将接口变量转换为具体类型
	value, ok := i.(int)
	if ok {
		fmt.Println("i是整数类型:", value)
	} else {
		fmt.Println("i不是整数类型")
	}
	
	// 错误的类型断言
	value2, ok := i.(string)
	if ok {
		fmt.Println("i是字符串类型:", value2)
	} else {
		fmt.Println("i不是字符串类型")
	}
	
	// 使用类型开关进行多类型判断
	fmt.Println("\n类型开关示例:")
	GetType(42)
	GetType("Go语言")
	GetType(true)
	GetType(3.14)
	GetType(r)    // Rectangle实现了Shape接口
	GetType([]int{1, 2, 3})
	
	// 接口的零值为nil
	fmt.Println("\n接口的零值示例:")
	var empty Shape // 零值为nil
	fmt.Printf("empty == nil: %t\n", empty == nil)
	
	// 接口与nil的比较
	var ptr *Rectangle = nil
	var nilShape Shape = ptr
	
	// 虽然接口持有的值为nil，但接口本身不为nil
	fmt.Printf("ptr == nil: %t\n", ptr == nil)
	fmt.Printf("nilShape == nil: %t\n", nilShape == nil)
	
	// 这是因为接口变量包含类型信息和值信息
	fmt.Printf("nilShape的类型: %T\n", nilShape)
} 