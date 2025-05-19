# Go语言结构体

结构体是Go语言中用户自定义的复合数据类型，允许将不同类型的数据组合在一起。本章将详细介绍Go语言中结构体的定义、使用和各种特性。

## 1. 结构体基础

### 1.1 结构体定义

Go语言中的结构体使用`type`和`struct`关键字定义，基本语法如下：

```go
type 结构体名 struct {
    字段名1 类型1
    字段名2 类型2
    // 更多字段...
}
```

例如，定义一个学生结构体：

```go
type Student struct {
    ID      int
    Name    string
    Age     int
    Grade   string
    Address string
}
```

### 1.2 结构体标签

结构体字段可以有标签（tag），这些标签通常用于序列化/反序列化：

```go
type Employee struct {
    ID        int    `json:"id"`
    FirstName string `json:"first_name"`
    LastName  string `json:"last_name"`
    Age       int    `json:"age,omitempty"` // omitempty表示如果为零值则忽略此字段
    Salary    int    `json:"-"`             // - 表示该字段不参与序列化
}
```

标签是结构体元数据，可以通过反射获取，常用于：
- JSON/XML序列化控制
- 数据库映射
- 表单验证

## 2. 结构体实例化

### 2.1 创建结构体实例的方法

```go
// 方式1：按字段顺序初始化（不推荐，因为容易出错）
s1 := Student{1001, "张三", 18, "高三", "北京市"}

// 方式2：按字段名称初始化（推荐，清晰明了）
s2 := Student{
    ID:      1002,
    Name:    "李四",
    Age:     17,
    Grade:   "高二",
    Address: "上海市",
}

// 方式3：先声明，后赋值
var s3 Student
s3.ID = 1003
s3.Name = "王五"
s3.Age = 16
s3.Grade = "高一"
s3.Address = "广州市"

// 方式4：使用new函数创建（返回指针）
s4 := new(Student)
s4.ID = 1004
s4.Name = "赵六"
s4.Age = 17
s4.Grade = "高二"
```

### 2.2 结构体指针

Go语言中可以直接使用结构体指针访问字段，不需要像C语言那样使用`->`操作符：

```go
p1 := &Student{
    ID:   1005,
    Name: "小明",
    Age:  18,
}

// 使用指针访问字段（Go语言中可以直接使用p1.Name，不需要使用(*p1).Name）
fmt.Println("p1.Name:", p1.Name)
p1.Grade = "高三"
fmt.Println("修改后:", *p1)
```

## 3. 嵌套结构体

### 3.1 普通嵌套

结构体可以包含其他结构体作为字段：

```go
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

// 使用方式
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
```

### 3.2 匿名嵌套（嵌入式结构体）

Go支持匿名嵌套结构体，可以直接访问嵌入结构体的字段：

```go
type Animal struct {
    Name string
    Age  int
}

type Dog struct {
    Animal        // 匿名嵌入Animal结构体
    Breed  string // Dog特有字段
}

// 使用方式
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
```

匿名嵌套提供了类似继承的机制，但Go语言中更倾向于称之为组合。

## 4. 结构体方法

### 4.1 为结构体定义方法

```go
// 值接收者方法
func (s Student) Info() string {
    return fmt.Sprintf("学生：%s，年龄：%d，年级：%s", s.Name, s.Age, s.Grade)
}

// 指针接收者方法（可以修改结构体字段）
func (s *Student) SetGrade(grade string) {
    s.Grade = grade
}

// 使用方式
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
```

### 4.2 方法继承与覆盖

嵌入式结构体的方法可以被"继承"，也可以被覆盖：

```go
// 为嵌入式结构体定义方法
func (a Animal) Describe() string {
    return fmt.Sprintf("动物：%s，年龄：%d", a.Name, a.Age)
}

// 为Dog定义覆盖Animal的方法
func (d Dog) Describe() string {
    return fmt.Sprintf("狗狗：%s，品种：%s，年龄：%d", d.Name, d.Breed, d.Age)
}

// 使用方式
fmt.Println("Animal.Describe():", dog.Animal.Describe())
fmt.Println("Dog.Describe():", dog.Describe())
```

## 5. 结构体比较

结构体可以使用`==`运算符比较，但前提是所有字段都可比较：

```go
s5 := Student{ID: 1007, Name: "小张", Age: 18}
s6 := Student{ID: 1007, Name: "小张", Age: 18}
s7 := Student{ID: 1008, Name: "小张", Age: 18}

fmt.Println("s5 == s6:", s5 == s6) // 结构体的所有字段都相等时，结构体才相等
fmt.Println("s5 == s7:", s5 == s7)
```

注意：如果结构体包含无法比较的字段（如切片、映射），则该结构体不能直接比较。

## 6. 结构体的内存布局

结构体在内存中是连续分布的，字段按照定义顺序排列，可能会有对齐填充：

```go
type Example struct {
    A byte
    B int64
    C byte
}
```

上面的结构体在64位系统上可能占用24字节而不是10字节，因为需要考虑内存对齐。

## 7. 结构体最佳实践

1. **使用命名字段初始化**：提高代码可读性和维护性
2. **合理使用嵌入式结构体**：组合优于继承
3. **选择合适的接收者类型**：
   - 值接收者：方法不修改结构体，或者结构体较小
   - 指针接收者：方法需要修改结构体，或者结构体较大
4. **使用结构体标签**：为序列化/反序列化提供元数据
5. **避免过大的结构体**：拆分为多个小结构体，提高可维护性

## 8. 练习题

1. 设计一个`Book`结构体，包含书名、作者、价格等字段，并为其实现打折和显示信息的方法
2. 创建一个`Employee`结构体和一个`Manager`结构体，其中`Manager`嵌入`Employee`，并添加管理团队大小的字段
3. 实现一个简单的学生管理系统，使用结构体表示学生，并提供添加、删除、查找学生的功能
4. 设计一个带有JSON标签的结构体，并实现序列化和反序列化
5. 创建一个几何形状的层次结构，使用结构体和接口实现不同形状的面积和周长计算

通过本章的学习，你应该能够理解并熟练使用Go语言中的结构体，包括结构体的定义、实例化、嵌套、方法和比较等特性。结构体是Go语言中组织和管理复杂数据的重要工具，掌握它们对于构建复杂的Go应用程序至关重要。