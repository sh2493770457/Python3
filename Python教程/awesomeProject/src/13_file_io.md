# Go语言文件输入输出

Go语言提供了丰富的文件操作功能，支持基本的文件读写、目录操作以及更高级的文件处理。本章将详细介绍Go语言中的文件输入输出操作及相关最佳实践。

## 1. 文件写入

Go语言提供了多种文件写入方式，从简单到复杂，可以满足不同的需求。

### 1.1 使用ioutil.WriteFile

最简单的文件写入方式是使用`ioutil.WriteFile`函数，它可以一次性将数据写入文件：

```go
// 使用ioutil.WriteFile（简单方式）
data := []byte("你好，Go语言文件处理！\n这是第二行。")
err := ioutil.WriteFile("example1.txt", data, 0644)
if err != nil {
	fmt.Println("  写入文件错误:", err)
	return
}
fmt.Println("  成功写入example1.txt")
```

参数说明：
- 第一个参数是文件路径
- 第二个参数是要写入的字节切片
- 第三个参数是文件权限（仅在Unix/Linux系统上有效）

### 1.2 使用os.Create和Write

对于需要更多控制的场景，可以使用`os.Create`创建文件，然后使用`Write`或`WriteString`方法写入数据：

```go
// 使用os.Create和Write
file, err := os.Create("example2.txt")
if err != nil {
	fmt.Println("  创建文件错误:", err)
	return
}
defer file.Close() // 确保文件最终被关闭

// 写入字符串
bytesWritten, err := file.WriteString("这是使用os.File.WriteString写入的内容。\n")
if err != nil {
	fmt.Println("  写入字符串错误:", err)
	return
}
fmt.Printf("  已写入%d字节到example2.txt\n", bytesWritten)

// 写入字节切片
bytesWritten, err = file.Write([]byte("这是使用os.File.Write写入的字节数据。\n"))
if err != nil {
	fmt.Println("  写入字节错误:", err)
	return
}
fmt.Printf("  已写入%d字节到example2.txt\n", bytesWritten)
```

注意事项：
- 使用`defer file.Close()`确保文件被正确关闭，即使发生错误
- `Write`方法接受字节切片，而`WriteString`接受字符串

### 1.3 使用bufio.Writer

对于需要频繁写入的场景，使用带缓冲的写入器可以提高性能：

```go
// 使用bufio.Writer（带缓冲的写入，效率更高）
file, err = os.Create("example3.txt")
if err != nil {
	fmt.Println("  创建文件错误:", err)
	return
}
defer file.Close()

writer := bufio.NewWriter(file)

// 写入多行数据
for i := 1; i <= 5; i++ {
	fmt.Fprintf(writer, "这是bufio.Writer写入的第%d行\n", i)
}

// 确保缓冲区中的数据被写入文件
err = writer.Flush()
if err != nil {
	fmt.Println("  刷新缓冲区错误:", err)
	return
}
fmt.Println("  成功写入example3.txt")
```

`bufio.Writer`的特点：
- 减少系统调用次数，提高写入效率
- 需要手动调用`Flush()`方法将缓冲区数据写入底层文件
- 适合频繁写入小块数据的场景

## 2. 文件读取

Go语言同样提供了多种文件读取方式，可以根据需求选择合适的方法。

### 2.1 使用ioutil.ReadFile

最简单的文件读取方式是使用`ioutil.ReadFile`函数，它可以一次性读取整个文件内容：

```go
// 使用ioutil.ReadFile（一次性读取整个文件内容）
data, err := ioutil.ReadFile("example1.txt")
if err != nil {
	fmt.Println("  读取文件错误:", err)
	return
}
fmt.Println("  example1.txt内容:")
fmt.Println("  " + string(data))
```

这种方法简单直接，但不适合读取大文件，因为它会将整个文件内容加载到内存中。

### 2.2 使用os.Open和Read

对于需要更多控制的场景，可以使用`os.Open`打开文件，然后使用`Read`方法读取数据：

```go
// 使用os.Open和Read
file, err := os.Open("example2.txt")
if err != nil {
	fmt.Println("  打开文件错误:", err)
	return
}
defer file.Close()

// 创建一个缓冲区
buffer := make([]byte, 1024)
bytesRead, err := file.Read(buffer)
if err != nil {
	fmt.Println("  读取文件错误:", err)
	return
}

fmt.Printf("  从example2.txt读取了%d字节:\n", bytesRead)
fmt.Println("  " + string(buffer[:bytesRead]))
```

这种方法允许控制每次读取的数据量，适合处理大文件。

### 2.3 使用bufio.Scanner

对于需要按行读取文件的场景，`bufio.Scanner`是最佳选择：

```go
// 使用bufio.Scanner（逐行读取）
file, err = os.Open("example3.txt")
if err != nil {
	fmt.Println("  打开文件错误:", err)
	return
}
defer file.Close()

scanner := bufio.NewScanner(file)
fmt.Println("  example3.txt内容(逐行):")
lineCount := 0

// 逐行扫描
for scanner.Scan() {
	lineCount++
	fmt.Printf("  第%d行: %s\n", lineCount, scanner.Text())
}

if err := scanner.Err(); err != nil {
	fmt.Println("  扫描文件错误:", err)
}
```

`bufio.Scanner`的特点：
- 默认按行读取文件
- 可以通过设置`Split`函数自定义分隔方式
- 每次调用`Scan()`方法读取下一个单元（默认为一行）
- 使用`Text()`或`Bytes()`方法获取当前单元的内容

## 3. 文件和目录操作

Go语言提供了丰富的文件和目录操作功能，包括检查文件是否存在、创建目录、重命名文件等。

### 3.1 检查文件是否存在

```go
// 检查文件是否存在
filePath := "example1.txt"
if _, err := os.Stat(filePath); err == nil {
	fmt.Printf("  文件 %s 存在\n", filePath)
} else if os.IsNotExist(err) {
	fmt.Printf("  文件 %s 不存在\n", filePath)
} else {
	fmt.Printf("  检查文件 %s 时出错: %v\n", filePath, err)
}
```

### 3.2 创建目录

```go
// 创建目录
dirPath := "testdir"
err := os.Mkdir(dirPath, 0755)
if err != nil && !os.IsExist(err) {
	fmt.Printf("  创建目录错误: %v\n", err)
} else {
	fmt.Printf("  目录 %s 已创建\n", dirPath)
}

// 创建嵌套目录
nestedDirPath := filepath.Join("testdir", "subdir", "deepdir")
err = os.MkdirAll(nestedDirPath, 0755)
if err != nil {
	fmt.Printf("  创建嵌套目录错误: %v\n", err)
} else {
	fmt.Printf("  嵌套目录 %s 已创建\n", nestedDirPath)
}
```

`os.Mkdir`只能创建一级目录，而`os.MkdirAll`可以创建多级目录。

### 3.3 获取当前工作目录

```go
// 获取当前工作目录
currentDir, err := os.Getwd()
if err != nil {
	fmt.Printf("  获取当前目录错误: %v\n", err)
} else {
	fmt.Printf("  当前工作目录: %s\n", currentDir)
}
```

### 3.4 文件重命名和移动

```go
// 文件重命名
oldPath := "example1.txt"
newPath := filepath.Join(dirPath, "renamed.txt")
err = os.Rename(oldPath, newPath)
if err != nil {
	fmt.Printf("  重命名文件错误: %v\n", err)
} else {
	fmt.Printf("  已将 %s 重命名为 %s\n", oldPath, newPath)
}

// 将文件移回原来的位置
err = os.Rename(newPath, oldPath)
if err != nil {
	fmt.Printf("  移回文件错误: %v\n", err)
} else {
	fmt.Printf("  已将文件移回到 %s\n", oldPath)
}
```

在Go语言中，重命名和移动文件使用同一个函数`os.Rename`。

### 3.5 列出目录内容

```go
// 列出目录内容
fmt.Println("  testdir目录内容:")
files, err := ioutil.ReadDir(dirPath)
if err != nil {
	fmt.Printf("  读取目录错误: %v\n", err)
} else {
	for _, file := range files {
		fileType := "文件"
		if file.IsDir() {
			fileType = "目录"
		}
		fmt.Printf("  - %s (%s, %d字节)\n", file.Name(), fileType, file.Size())
	}
}
```

## 4. 文件路径操作

Go语言的`path/filepath`包提供了处理文件路径的功能，这些功能是跨平台的。

### 4.1 路径拼接

```go
// 路径拼接
path1 := filepath.Join("dir", "subdir", "file.txt")
fmt.Println("拼接路径:", path1)
```

使用`filepath.Join`而不是手动拼接字符串可以确保路径分隔符在不同操作系统上的正确性。

### 4.2 获取文件信息

```go
// 获取文件信息
fileInfo, err := os.Stat("example1.txt")
if err != nil {
	fmt.Println("获取文件信息错误:", err)
} else {
	fmt.Println("文件名:", fileInfo.Name())
	fmt.Println("大小:", fileInfo.Size(), "字节")
	fmt.Println("权限:", fileInfo.Mode())
	fmt.Println("修改时间:", fileInfo.ModTime())
	fmt.Println("是目录:", fileInfo.IsDir())
}
```

### 4.3 分割路径

```go
// 分割路径
dir, file := filepath.Split("/path/to/file.txt")
fmt.Println("目录:", dir)
fmt.Println("文件:", file)

// 获取文件扩展名
ext := filepath.Ext("/path/to/file.txt")
fmt.Println("扩展名:", ext)

// 获取不带扩展名的文件名
base := filepath.Base("/path/to/file.txt")
fmt.Println("基本名称:", base)
baseWithoutExt := strings.TrimSuffix(base, ext)
fmt.Println("不带扩展名的名称:", baseWithoutExt)
```

## 5. 临时文件和目录

Go语言的`ioutil`包提供了创建临时文件和目录的功能。

### 5.1 创建临时文件

```go
// 创建临时文件
tempFile, err := ioutil.TempFile("", "example*.txt")
if err != nil {
	fmt.Println("创建临时文件错误:", err)
	return
}
defer os.Remove(tempFile.Name()) // 确保程序结束时删除临时文件
defer tempFile.Close()

fmt.Println("创建的临时文件:", tempFile.Name())

// 写入临时文件
_, err = tempFile.WriteString("这是临时文件的内容\n")
if err != nil {
	fmt.Println("写入临时文件错误:", err)
	return
}
```

### 5.2 创建临时目录

```go
// 创建临时目录
tempDir, err := ioutil.TempDir("", "example-dir-*")
if err != nil {
	fmt.Println("创建临时目录错误:", err)
	return
}
defer os.RemoveAll(tempDir) // 确保程序结束时删除临时目录

fmt.Println("创建的临时目录:", tempDir)
```

## 6. 文件权限和所有权

在Unix/Linux系统上，Go语言允许设置文件的权限和所有权。

### 6.1 设置文件权限

```go
// 设置文件权限
err = os.Chmod("example1.txt", 0644) // rw-r--r--
if err != nil {
	fmt.Println("设置文件权限错误:", err)
} else {
	fmt.Println("已设置文件权限")
}
```

### 6.2 设置文件所有权（仅Unix/Linux）

```go
// 设置文件所有者和组（仅在Unix/Linux系统上有效）
// err = os.Chown("example1.txt", uid, gid)
// if err != nil {
//     fmt.Println("设置文件所有权错误:", err)
// } else {
//     fmt.Println("已设置文件所有权")
// }
```

## 7. 文件锁定

Go语言没有内置的文件锁定机制，但可以使用第三方库或系统调用实现。

```go
// 文件锁定示例（使用系统调用，仅适用于Unix/Linux）
// 在Windows上需要使用其他方法

// import "syscall"

// 获取独占锁
// func lockFile(file *os.File) error {
//     return syscall.Flock(int(file.Fd()), syscall.LOCK_EX)
// }

// 释放锁
// func unlockFile(file *os.File) error {
//     return syscall.Flock(int(file.Fd()), syscall.LOCK_UN)
// }
```

## 8. 文件监控

Go语言可以使用第三方库（如`fsnotify`）监控文件系统变化。

```go
// 使用fsnotify监控文件变化
// 需要先安装: go get github.com/fsnotify/fsnotify

// import "github.com/fsnotify/fsnotify"

// func watchFile() {
//     watcher, err := fsnotify.NewWatcher()
//     if err != nil {
//         fmt.Println("创建监视器错误:", err)
//         return
//     }
//     defer watcher.Close()

//     done := make(chan bool)
//     go func() {
//         for {
//             select {
//             case event, ok := <-watcher.Events:
//                 if !ok {
//                     return
//                 }
//                 fmt.Printf("事件: %s %s\n", event.Name, event.Op)
//             case err, ok := <-watcher.Errors:
//                 if !ok {
//                     return
//                 }
//                 fmt.Println("监视器错误:", err)
//             }
//         }
//     }()

//     err = watcher.Add("example1.txt")
//     if err != nil {
//         fmt.Println("添加监视文件错误:", err)
//         return
//     }
//     <-done
// }
```

## 9. 文件压缩和解压

Go语言标准库提供了对zip、gzip、tar等格式的支持。

### 9.1 Zip文件操作

```go
// 创建zip文件
// import "archive/zip"

// func createZipFile() {
//     // 创建zip文件
//     zipFile, err := os.Create("archive.zip")
//     if err != nil {
//         fmt.Println("创建zip文件错误:", err)
//         return
//     }
//     defer zipFile.Close()

//     // 创建zip写入器
//     zipWriter := zip.NewWriter(zipFile)
//     defer zipWriter.Close()

//     // 添加文件到zip
//     for _, file := range []string{"example1.txt", "example2.txt"} {
//         fileToZip, err := os.Open(file)
//         if err != nil {
//             fmt.Println("打开文件错误:", err)
//             continue
//         }
//         defer fileToZip.Close()

//         info, err := fileToZip.Stat()
//         if err != nil {
//             fmt.Println("获取文件信息错误:", err)
//             continue
//         }

//         header, err := zip.FileInfoHeader(info)
//         if err != nil {
//             fmt.Println("创建文件头错误:", err)
//             continue
//         }
//         header.Name = file

//         writer, err := zipWriter.CreateHeader(header)
//         if err != nil {
//             fmt.Println("创建文件头错误:", err)
//             continue
//         }

//         _, err = io.Copy(writer, fileToZip)
//         if err != nil {
//             fmt.Println("写入文件错误:", err)
//             continue
//         }
//     }

//     fmt.Println("成功创建zip文件")
// }
```

## 10. 文件操作最佳实践

1. **始终关闭文件**：使用`defer file.Close()`确保文件被正确关闭
2. **错误处理**：检查每个文件操作的错误返回值
3. **使用缓冲IO**：对于频繁的小块读写，使用`bufio`包提高性能
4. **路径处理**：使用`filepath`包处理文件路径，确保跨平台兼容性
5. **资源清理**：使用`defer`确保临时文件和目录被删除
6. **权限设置**：设置适当的文件权限，特别是在多用户环境中
7. **大文件处理**：对于大文件，使用流式处理而不是一次性读取全部内容

## 11. 练习题

1. 编写一个程序，将一个文本文件的内容复制到另一个文件中
2. 实现一个简单的日志系统，可以将日志写入文件并按日期滚动
3. 创建一个程序，递归列出指定目录下的所有文件和子目录
4. 编写一个配置文件读取器，支持读取和修改INI格式的配置文件
5. 实现一个文件监控程序，当指定文件发生变化时执行特定操作

通过本章的学习，你应该能够理解并熟练使用Go语言中的文件输入输出操作，包括文件读写、目录操作、路径处理等。这些知识对于开发各种需要文件操作的应用程序至关重要。