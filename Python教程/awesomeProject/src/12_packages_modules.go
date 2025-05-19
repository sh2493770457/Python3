// 12_packages_modules.go - 包和模块示例
package main

import (
	"fmt"
	"math"      // 标准库包
	"math/rand" // 子包
	"strings"   // 字符串处理
	"time"      // 时间相关功能
	
	// 导入第三方包，需要先通过go get命令安装
	// 例如：go get github.com/user/package
	// 导入示例: "github.com/user/package"
)

// 定义常量
const (
	AppName    = "Go学习示例"
	AppVersion = "1.0.0"
)

// 定义结构体
type Circle struct {
	Radius float64
}

// 结构体方法
func (c Circle) Area() float64 {
	return math.Pi * c.Radius * c.Radius
}

// 函数可导出（首字母大写）
func PrintInfo() {
	fmt.Println("这是一个可导出的函数，其他包可以使用")
}

// 函数不可导出（首字母小写）
func privateFunc() {
	fmt.Println("这是一个私有函数，只能在当前包中使用")
}

// 演示如何使用其他包的功能
func demoPackages() {
	fmt.Println("包使用示例:")
	
	// 使用math包的函数
	fmt.Printf("  圆周率: %.5f\n", math.Pi)
	fmt.Printf("  2的平方根: %.2f\n", math.Sqrt(2))
	fmt.Printf("  2的3次方: %.0f\n", math.Pow(2, 3))
	
	// 使用strings包处理字符串
	s := "  Hello, Go!  "
	fmt.Printf("  原始字符串: %q\n", s)
	fmt.Printf("  去除空格: %q\n", strings.TrimSpace(s))
	fmt.Printf("  是否包含'Go': %t\n", strings.Contains(s, "Go"))
	fmt.Printf("  替换'Go'为'Golang': %q\n", strings.Replace(s, "Go", "Golang", 1))
	
	// 使用time包
	now := time.Now()
	fmt.Printf("  当前时间: %s\n", now.Format("2006-01-02 15:04:05"))
	fmt.Printf("  一小时后: %s\n", now.Add(time.Hour).Format("15:04:05"))
	
	// 使用rand包生成随机数
	// 设置随机数种子，否则每次运行生成的随机数相同
	rand.Seed(time.Now().UnixNano())
	fmt.Printf("  随机数: %d\n", rand.Intn(100)) // 0-99之间的随机数
}

// 包初始化函数，程序启动时会自动调用
// 每个包可以有多个init函数，它们会按照声明顺序执行
// init函数在main函数之前执行
func init() {
	fmt.Println("init函数已执行")
	fmt.Printf("应用名称: %s, 版本: %s\n", AppName, AppVersion)
}

/*
模块相关说明（这里是注释，不是可执行代码）

1. 创建模块:
   go mod init example.com/myproject

2. 添加依赖:
   go get github.com/user/package

3. 更新依赖:
   go get -u github.com/user/package

4. 移除未使用的依赖:
   go mod tidy

5. 查看依赖:
   go list -m all

6. 模块文件:
   - go.mod: 定义模块路径和依赖需求
   - go.sum: 包含依赖包的加密哈希值，确保一致性

7. 模块示例结构:
   myproject/
     ├── go.mod
     ├── go.sum
     ├── main.go
     ├── pkg1/
     │    └── pkg1.go
     └── pkg2/
          └── pkg2.go
*/

func main() {
	// 演示包的使用
	demoPackages()
	
	// 使用自定义类型
	circle := Circle{Radius: 5}
	fmt.Printf("\n半径为5的圆面积: %.2f\n", circle.Area())
	
	// 调用导出的函数
	PrintInfo()
	
	// 调用私有函数
	privateFunc()
} 