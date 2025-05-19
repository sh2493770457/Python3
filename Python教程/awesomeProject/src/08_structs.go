// 08_structs.go - 结构体示例
package main

import "fmt"

// 定义结构体
type Student struct {
	ID      int
	Name    string
	Age     int
	Grade   string
	Address string
}

// 带标签的结构体（用于序列化/反序列化）
type Employee struct {
	ID        int    `json:"id"`
	FirstName string `json:"first_name"`
	LastName  string `json:"last_name"`
	Age       int    `json:"age,omitempty"` // omitempty表示如果为零值则忽略此字段
	Salary    int    `json:"-"`             // - 表示该字段不参与序列化
}

// 嵌套结构体
type Address struct {
	City    string
	Street  string
	ZipCode string
}

type Contact struct {
	Phone  string
	Email  string
	WeChat string
}

type Person struct {
	Name    string
	Age     int
	Address Address  // 嵌套结构体
	Contact Contact  // 嵌套结构体
}

// 匿名字段（嵌入式结构体）
type Animal struct {
	Name string
	Age  int
}

type Dog struct {
	Animal        // 匿名嵌入Animal结构体
	Breed  string // Dog特有字段
}

// 为结构体定义方法
func (s Student) Info() string {
	return fmt.Sprintf("学生：%s，年龄：%d，年级：%s", s.Name, s.Age, s.Grade)
}

// 指针接收者方法（可以修改结构体字段）
func (s *Student) SetGrade(grade string) {
	s.Grade = grade
}

// 为嵌入式结构体定义方法
func (a Animal) Describe() string {
	return fmt.Sprintf("动物：%s，年龄：%d", a.Name, a.Age)
}

// 为Dog定义覆盖Animal的方法
func (d Dog) Describe() string {
	return fmt.Sprintf("狗狗：%s，品种：%s，年龄：%d", d.Name, d.Breed, d.Age)
}

func main() {
	fmt.Println("结构体示例:")
	
	// 创建结构体实例的几种方式
	
	// 方式1：按字段顺序初始化（不推荐，因为容易出错）
	s1 := Student{1001, "张三", 18, "高三", "北京市"}
	fmt.Println("s1:", s1)
	
	// 方式2：按字段名称初始化（推荐，清晰明了）
	s2 := Student{
		ID:      1002,
		Name:    "李四",
		Age:     17,
		Grade:   "高二",
		Address: "上海市",
	}
	fmt.Println("s2:", s2)
	
	// 方式3：先声明，后赋值
	var s3 Student
	s3.ID = 1003
	s3.Name = "王五"
	s3.Age = 16
	s3.Grade = "高一"
	s3.Address = "广州市"
	fmt.Println("s3:", s3)
	
	// 方式4：使用new函数创建（返回指针）
	s4 := new(Student)
	s4.ID = 1004
	s4.Name = "赵六"
	s4.Age = 17
	s4.Grade = "高二"
	fmt.Println("s4:", *s4)
	
	// 结构体指针
	fmt.Println("\n结构体指针示例:")
	p1 := &Student{
		ID:   1005,
		Name: "小明",
		Age:  18,
	}
	
	// 使用指针访问字段（Go语言中可以直接使用p1.Name，不需要使用(*p1).Name）
	fmt.Println("p1.Name:", p1.Name)
	p1.Grade = "高三"
	fmt.Println("修改后:", *p1)
	
	// 嵌套结构体示例
	fmt.Println("\n嵌套结构体示例:")
	person := Person{
		Name: "刘德华",
		Age:  60,
		Address: Address{
			City:    "香港",
			Street:  "某某街道",
			ZipCode: "999077",
		},
		Contact: Contact{
			Phone:  "12345678",
			Email:  "liudehua@example.com",
			WeChat: "ldh_official",
		},
	}
	
	fmt.Println("姓名:", person.Name)
	fmt.Println("城市:", person.Address.City)
	fmt.Println("电话:", person.Contact.Phone)
	
	// 匿名字段（嵌入式结构体）示例
	fmt.Println("\n匿名字段示例:")
	dog := Dog{
		Animal: Animal{
			Name: "小黑",
			Age:  3,
		},
		Breed: "拉布拉多",
	}
	
	// 可以直接访问嵌入式结构体的字段
	fmt.Println("狗名:", dog.Name)      // 等同于 dog.Animal.Name
	fmt.Println("狗龄:", dog.Age)       // 等同于 dog.Animal.Age
	fmt.Println("品种:", dog.Breed)
	
	// 调用嵌入式结构体的方法
	fmt.Println("Animal.Describe():", dog.Animal.Describe())
	fmt.Println("Dog.Describe():", dog.Describe())
	
	// 结构体方法示例
	fmt.Println("\n结构体方法示例:")
	stu := Student{
		ID:    1006,
		Name:  "小红",
		Age:   16,
		Grade: "高一",
	}
	
	// 调用值接收者方法
	info := stu.Info()
	fmt.Println(info)
	
	// 调用指针接收者方法
	stu.SetGrade("高二")
	fmt.Println("修改年级后:", stu.Info())
	
	// 结构体比较
	fmt.Println("\n结构体比较示例:")
	s5 := Student{ID: 1007, Name: "小张", Age: 18}
	s6 := Student{ID: 1007, Name: "小张", Age: 18}
	s7 := Student{ID: 1008, Name: "小张", Age: 18}
	
	fmt.Println("s5 == s6:", s5 == s6) // 结构体的所有字段都相等时，结构体才相等
	fmt.Println("s5 == s7:", s5 == s7)
	
	// 注意：如果结构体包含无法比较的字段（如切片、映射），则该结构体不能直接比较
} 