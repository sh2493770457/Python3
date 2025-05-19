# Go语言映射（Map）

映射是Go语言中的一种内置类型，用于存储键值对（key-value）集合。映射是无序的，每次遍历时的顺序可能不同。

## 目录

1. [映射的声明和初始化](#映射的声明和初始化)
2. [映射的基本操作](#映射的基本操作)
3. [遍历映射](#遍历映射)
4. [嵌套映射](#嵌套映射)
5. [映射的零值和空映射](#映射的零值和空映射)
6. [最佳实践](#最佳实践)

## 映射的声明和初始化

在Go语言中，声明和初始化映射有多种方式：

```go
// 方式1：使用make函数创建映射
var m1 = make(map[string]int)

// 方式2：使用字面量创建并初始化映射
m2 := map[string]string{
    "name":    "张三",
    "city":    "北京",
    "country": "中国",
}
```

映射的类型表示为`map[KeyType]ValueType`，其中：
- `KeyType`：必须是可以通过`==`或`!=`操作符比较的类型，如布尔型、数字型、字符串、指针、接口等
- `ValueType`：可以是任意类型，包括另一个映射

## 映射的基本操作

### 添加和修改元素

使用赋值语句可以添加新元素或修改现有元素：

```go
scores := make(map[string]int)

// 添加新元素
scores["张三"] = 90
scores["李四"] = 85

// 修改元素
scores["张三"] = 95
```

### 获取元素

通过键来获取对应的值：

```go
score := scores["张三"]
```

如果键不存在，会返回值类型的零值（如int的零值为0）。

### 检查键是否存在

可以使用双赋值形式检查键是否存在：

```go
value, exists := scores["李四"]
if exists {
    fmt.Println("李四的分数:", value)
} else {
    fmt.Println("李四不存在")
}
```

### 删除元素

使用内置的`delete`函数删除元素：

```go
delete(scores, "王五")
```

即使键不存在，删除操作也不会报错。

### 获取映射长度

使用内置的`len`函数获取映射中元素的数量：

```go
length := len(scores)
```

## 遍历映射

可以使用`for range`循环遍历映射的所有键值对：

```go
// 遍历键和值
for city, population := range cities {
    fmt.Printf("%s: %d\n", city, population)
}

// 只遍历键
for city := range cities {
    fmt.Println(city)
}
```

需要注意的是，映射的遍历顺序是不确定的，每次运行可能会不同。

## 嵌套映射

映射的值可以是另一个映射，形成嵌套结构：

```go
students := map[string]map[string]string{
    "张三": {
        "age":    "20",
        "major":  "计算机科学",
        "school": "北京大学",
    },
    "李四": {
        "age":    "22",
        "major":  "数学",
        "school": "清华大学",
    },
}

// 访问嵌套映射
fmt.Println("张三的学校:", students["张三"]["school"])
```

## 映射的零值和空映射

映射的零值是`nil`：

```go
var nilMap map[string]int // nilMap == nil
```

需要注意的是：
- 不能对`nil`映射赋值，会导致panic
- 空映射不等于`nil`：

```go
emptyMap := map[string]int{} // emptyMap != nil
```

空映射已经初始化，可以安全地添加元素：

```go
emptyMap["test"] = 1 // 正常工作
```

## 最佳实践

1. **始终在使用前初始化映射**：使用`make`函数或字面量语法创建映射，避免对`nil`映射操作。

2. **检查键是否存在**：在获取值之前先检查键是否存在，避免使用零值导致逻辑错误。

3. **合理选择键类型**：键类型必须是可比较的，常用的键类型有字符串、整数、结构体等。

4. **不依赖遍历顺序**：映射的遍历顺序是不确定的，不要依赖遍历顺序来实现业务逻辑。

5. **预分配容量**：如果事先知道映射的大致容量，可以在创建时指定：`make(map[string]int, 100)`，这样可以减少映射扩容的次数。

## 完整示例代码

```go
package main

import "fmt"

func main() {
    // 映射（map）声明和初始化
    fmt.Println("映射示例:")
    
    // 方式1：使用make函数创建映射
    var m1 = make(map[string]int)
    fmt.Println("空映射m1:", m1)
    
    // 方式2：使用字面量创建并初始化映射
    m2 := map[string]string{
        "name":    "张三",
        "city":    "北京",
        "country": "中国",
    }
    fmt.Println("初始化的映射m2:", m2)
    
    // 映射操作
    fmt.Println("\n映射操作示例:")
    
    // 添加或修改元素
    scores := make(map[string]int)
    
    // 添加新元素
    scores["张三"] = 90
    scores["李四"] = 85
    scores["王五"] = 78
    fmt.Println("添加元素后的scores:", scores)
    
    // 修改元素
    scores["张三"] = 95
    fmt.Println("修改后的scores:", scores)
    
    // 获取元素
    score := scores["张三"]
    fmt.Println("张三的分数:", score)
    
    // 获取不存在的键
    unknownScore := scores["赵六"] // 返回值类型的零值（int的零值为0）
    fmt.Println("赵六的分数:", unknownScore)
    
    // 检查键是否存在
    value, exists := scores["李四"]
    if exists {
        fmt.Println("李四的分数:", value)
    } else {
        fmt.Println("李四不存在")
    }
    
    // 再次检查，这次是不存在的键
    value, exists = scores["赵六"]
    if exists {
        fmt.Println("赵六的分数:", value)
    } else {
        fmt.Println("赵六不存在")
    }
    
    // 删除元素
    delete(scores, "王五")
    fmt.Println("删除王五后的scores:", scores)
    
    // 获取映射长度
    fmt.Println("scores映射长度:", len(scores))
    
    // 遍历映射
    fmt.Println("\n遍历映射示例:")
    
    // 创建一个示例映射
    cities := map[string]int{
        "北京": 2100,
        "上海": 2400,
        "广州": 1500,
        "深圳": 1300,
        "杭州": 900,
    }
    
    // 遍历键和值
    fmt.Println("各城市人口(万):")
    for city, population := range cities {
        fmt.Printf("%s: %d\n", city, population)
    }
    
    // 只遍历键
    fmt.Println("\n城市列表:")
    for city := range cities {
        fmt.Println(city)
    }
    
    // 嵌套映射（映射的值为另一个映射）
    fmt.Println("\n嵌套映射示例:")
    
    // 创建嵌套映射
    students := map[string]map[string]string{
        "张三": {
            "age":    "20",
            "major":  "计算机科学",
            "school": "北京大学",
        },
        "李四": {
            "age":    "22",
            "major":  "数学",
            "school": "清华大学",
        },
    }
    
    // 访问嵌套映射
    fmt.Println("张三的信息:", students["张三"])
    fmt.Println("张三的学校:", students["张三"]["school"])
    
    // 向嵌套映射添加新元素
    students["王五"] = map[string]string{
        "age":    "21",
        "major":  "物理",
        "school": "复旦大学",
    }
    
    // 打印整个嵌套映射
    fmt.Println("\n所有学生信息:")
    for name, info := range students {
        fmt.Printf("%s: %v\n", name, info)
    }
    
    // 映射的零值为nil
    var nilMap map[string]int
    fmt.Println("\n空映射相关:")
    fmt.Printf("nilMap: %v, 是否为nil: %t\n", nilMap, nilMap == nil)
    
    // 不能对nil映射赋值，会导致panic
    // nilMap["test"] = 1 // 这会引发panic
    
    // 空映射不等于nil
    emptyMap := map[string]int{}
    fmt.Printf("emptyMap: %v, 是否为nil: %t\n", emptyMap, emptyMap == nil)
    
    // 可以对空映射赋值
    emptyMap["test"] = 1
    fmt.Println("添加元素后的emptyMap:", emptyMap)
} 