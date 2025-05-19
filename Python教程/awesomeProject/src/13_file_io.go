// 13_file_io.go - 文件输入输出操作示例
package main

import (
	"bufio"
	"fmt"
	"io/ioutil"
	"os"
	"path/filepath"
)

// 写入文件的基本方法
func writeFile() {
	fmt.Println("写入文件示例:")
	
	// 1. 使用ioutil.WriteFile（简单方式）
	data := []byte("你好，Go语言文件处理！\n这是第二行。")
	err := ioutil.WriteFile("example1.txt", data, 0644)
	if err != nil {
		fmt.Println("  写入文件错误:", err)
		return
	}
	fmt.Println("  成功写入example1.txt")
	
	// 2. 使用os.Create和Write
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
	
	// 3. 使用bufio.Writer（带缓冲的写入，效率更高）
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
}

// 读取文件的基本方法
func readFile() {
	fmt.Println("\n读取文件示例:")
	
	// 1. 使用ioutil.ReadFile（一次性读取整个文件内容）
	data, err := ioutil.ReadFile("example1.txt")
	if err != nil {
		fmt.Println("  读取文件错误:", err)
		return
	}
	fmt.Println("  example1.txt内容:")
	fmt.Println("  " + string(data))
	
	// 2. 使用os.Open和Read
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
	
	// 3. 使用bufio.Scanner（逐行读取）
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
}

// 文件和目录操作的基本方法
func fileDirectoryOperations() {
	fmt.Println("\n文件和目录操作示例:")
	
	// 1. 检查文件是否存在
	filePath := "example1.txt"
	if _, err := os.Stat(filePath); err == nil {
		fmt.Printf("  文件 %s 存在\n", filePath)
	} else if os.IsNotExist(err) {
		fmt.Printf("  文件 %s 不存在\n", filePath)
	} else {
		fmt.Printf("  检查文件 %s 时出错: %v\n", filePath, err)
	}
	
	// 2. 创建目录
	dirPath := "testdir"
	err := os.Mkdir(dirPath, 0755)
	if err != nil && !os.IsExist(err) {
		fmt.Printf("  创建目录错误: %v\n", err)
	} else {
		fmt.Printf("  目录 %s 已创建\n", dirPath)
	}
	
	// 3. 创建嵌套目录
	nestedDirPath := filepath.Join("testdir", "subdir", "deepdir")
	err = os.MkdirAll(nestedDirPath, 0755)
	if err != nil {
		fmt.Printf("  创建嵌套目录错误: %v\n", err)
	} else {
		fmt.Printf("  嵌套目录 %s 已创建\n", nestedDirPath)
	}
	
	// 4. 获取当前工作目录
	currentDir, err := os.Getwd()
	if err != nil {
		fmt.Printf("  获取当前目录错误: %v\n", err)
	} else {
		fmt.Printf("  当前工作目录: %s\n", currentDir)
	}
	
	// 5. 文件重命名
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
	
	// 6. 列出目录内容
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
}

// 文件信息和属性获取
func fileInfoAndAttributes() {
	fmt.Println("\n文件信息和属性示例:")
	
	filePath := "example1.txt"
	
	// 获取文件信息
	fileInfo, err := os.Stat(filePath)
	if err != nil {
		fmt.Printf("  获取文件信息错误: %v\n", err)
		return
	}
	
	// 打印文件属性
	fmt.Printf("  文件名: %s\n", fileInfo.Name())
	fmt.Printf("  大小: %d字节\n", fileInfo.Size())
	fmt.Printf("  权限: %v\n", fileInfo.Mode())
	fmt.Printf("  修改时间: %v\n", fileInfo.ModTime())
	fmt.Printf("  是目录吗: %t\n", fileInfo.IsDir())
	
	// 文件路径操作
	fmt.Println("\n  文件路径操作:")
	fmt.Printf("  绝对路径: %s\n", filepath.Abs(filePath))
	fmt.Printf("  基本名称: %s\n", filepath.Base(filePath))
	fmt.Printf("  所在目录: %s\n", filepath.Dir(filePath))
	fmt.Printf("  扩展名: %s\n", filepath.Ext(filePath))
	
	// 路径分隔符
	fmt.Printf("  路径分隔符: %c\n", filepath.Separator)
}

// 清理创建的文件和目录
func cleanup() {
	fmt.Println("\n清理示例文件和目录:")
	
	// 删除文件
	filesToDelete := []string{"example1.txt", "example2.txt", "example3.txt"}
	for _, file := range filesToDelete {
		err := os.Remove(file)
		if err != nil {
			fmt.Printf("  删除文件 %s 错误: %v\n", file, err)
		} else {
			fmt.Printf("  已删除文件 %s\n", file)
		}
	}
	
	// 递归删除目录及其内容
	err := os.RemoveAll("testdir")
	if err != nil {
		fmt.Printf("  删除目录错误: %v\n", err)
	} else {
		fmt.Println("  已删除目录 testdir 及其内容")
	}
}

func main() {
	// 写入文件
	writeFile()
	
	// 读取文件
	readFile()
	
	// 文件和目录操作
	fileDirectoryOperations()
	
	// 文件信息和属性
	fileInfoAndAttributes()
	
	// 最后清理创建的文件和目录
	cleanup()
} 