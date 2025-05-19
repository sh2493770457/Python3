# Go语言并发编程

Go语言的并发模型基于CSP（Communicating Sequential Processes，通信顺序进程）理论，通过goroutine和channel提供了简单而强大的并发编程支持。本章将详细介绍Go语言中的并发编程特性和实践。

## 1. Goroutine基础

### 1.1 什么是Goroutine

Goroutine是Go语言中的轻量级线程，由Go运行时（runtime）管理。与操作系统线程相比，goroutine具有以下特点：

- **轻量级**：创建一个goroutine的栈内存消耗仅约2KB（可动态增长）
- **高效调度**：Go运行时会自动在多个操作系统线程上调度goroutine
- **简单创建**：使用`go`关键字即可启动一个goroutine

### 1.2 创建Goroutine

使用`go`关键字后跟一个函数调用即可创建并启动一个goroutine：

```go
// 简单的goroutine示例
func simpleGoroutine() {
	fmt.Println("简单goroutine示例:")
	
	// 启动一个goroutine
	go func() {
		fmt.Println("  在goroutine中执行")
	}()
	
	// 主函数继续执行
	fmt.Println("  在主goroutine中执行")
	
	// 给goroutine一些执行时间
	time.Sleep(100 * time.Millisecond)
}
```

在上面的例子中：
1. 我们使用`go`关键字启动了一个匿名函数作为goroutine
2. 主goroutine和新创建的goroutine并发执行
3. 使用`time.Sleep`给新goroutine一些执行时间（实际应用中应使用同步机制）

### 1.3 多个Goroutine

可以轻松创建多个goroutine：

```go
// 启动多个goroutine
func multipleGoroutines() {
	fmt.Println("\n多个goroutine示例:")
	
	for i := 1; i <= 3; i++ {
		// 使用匿名函数捕获循环变量
		go func(id int) {
			fmt.Printf("  goroutine %d 执行\n", id)
		}(i) // 注意：将循环变量i传递给匿名函数
	}
	
	// 给goroutine一些执行时间
	time.Sleep(100 * time.Millisecond)
}
```

**注意**：在循环中启动goroutine时，需要特别注意循环变量的捕获问题。上面的例子通过将循环变量作为参数传递给匿名函数来解决这个问题。

## 2. Goroutine同步

### 2.1 使用WaitGroup

`sync.WaitGroup`是Go语言中用于等待一组goroutine完成执行的同步原语：

```go
// 使用WaitGroup等待goroutine完成
func waitGroupDemo() {
	fmt.Println("\nWaitGroup示例:")
	
	var wg sync.WaitGroup
	
	// 启动3个goroutine
	for i := 1; i <= 3; i++ {
		wg.Add(1) // 增加WaitGroup计数
		
		go func(id int) {
			defer wg.Done() // 在函数返回时减少WaitGroup计数
			
			// 模拟工作
			duration := time.Duration(rand.Intn(500)) * time.Millisecond
			fmt.Printf("  goroutine %d 开始执行，持续 %v\n", id, duration)
			time.Sleep(duration)
			fmt.Printf("  goroutine %d 执行完成\n", id)
		}(i)
	}
	
	fmt.Println("  等待所有goroutine完成...")
	wg.Wait() // 等待所有goroutine完成
	fmt.Println("  所有goroutine已完成")
}
```

`WaitGroup`的使用步骤：
1. 创建一个`sync.WaitGroup`实例
2. 在启动goroutine前调用`wg.Add(n)`增加计数器（n为goroutine数量）
3. 在每个goroutine完成时调用`wg.Done()`减少计数器
4. 在主goroutine中调用`wg.Wait()`等待计数器归零

## 3. Channel（通道）

Channel是Go语言中goroutine之间的通信机制，它提供了一种类型安全的方式来发送和接收值。

### 3.1 Channel基础

创建channel的语法：

```go
// 创建一个传递int类型的channel
ch := make(chan int)

// 创建一个带缓冲区的channel
bufferedCh := make(chan string, 10) // 缓冲区大小为10
```

发送和接收操作：

```go
// 发送值到channel
ch <- 42

// 从channel接收值
value := <-ch

// 接收值并检查channel是否已关闭
value, ok := <-ch
```

### 3.2 无缓冲Channel

无缓冲channel的发送和接收操作是同步的，发送操作会阻塞直到有接收方准备好接收：

```go
// 使用无缓冲channel进行同步
func unbufferedChannel() {
	fmt.Println("\n无缓冲channel示例:")
	
	// 创建无缓冲channel
	done := make(chan bool)
	
	go func() {
		fmt.Println("  goroutine执行中...")
		time.Sleep(500 * time.Millisecond)
		fmt.Println("  goroutine工作完成")
		
		// 发送完成信号
		done <- true
	}()
	
	// 等待goroutine完成
	fmt.Println("  等待goroutine完成...")
	<-done // 阻塞，直到接收到值
	fmt.Println("  主函数继续执行")
}
```

### 3.3 带缓冲Channel

带缓冲channel在缓冲区满之前，发送操作不会阻塞：

```go
// 使用带缓冲的channel
func bufferedChannel() {
	fmt.Println("\n带缓冲channel示例:")
	
	// 创建带有3个缓冲区的channel
	ch := make(chan int, 3)
	
	// 发送3个值（不会阻塞，因为缓冲区足够）
	ch <- 1
	ch <- 2
	ch <- 3
	fmt.Println("  发送了3个值到带缓冲的channel")
	
	// 如果继续发送，将会阻塞，直到有空间
	// ch <- 4 // 会阻塞
	
	// 接收值
	fmt.Printf("  接收的值: %d\n", <-ch)
	fmt.Printf("  接收的值: %d\n", <-ch)
	fmt.Printf("  接收的值: %d\n", <-ch)
}
```

### 3.4 Channel通信

Channel可以用于goroutine之间的数据传递：

```go
// 多个goroutine之间的通信
func channelCommunication() {
	fmt.Println("\nchannel通信示例:")
	
	// 创建channel
	ch := make(chan string)
	
	// 发送者goroutine
	go func() {
		// 发送多个值
		messages := []string{"你好", "Go语言", "并发编程", "很有趣"}
		for _, msg := range messages {
			fmt.Printf("  发送: %s\n", msg)
			ch <- msg // 发送消息
			time.Sleep(100 * time.Millisecond)
		}
		close(ch) // 关闭channel
	}()
	
	// 接收消息（使用for-range循环）
	for msg := range ch {
		fmt.Printf("  接收: %s\n", msg)
	}
	
	fmt.Println("  channel已关闭，通信完成")
}
```

### 3.5 关闭Channel

发送者可以关闭channel以表示不再发送数据：

```go
// 关闭channel
close(ch)
```

接收者可以通过第二个返回值检查channel是否已关闭：

```go
value, ok := <-ch
if !ok {
    // channel已关闭
}
```

也可以使用`for-range`循环，它会在channel关闭时自动退出：

```go
for value := range ch {
    // 处理value
}
// 当channel关闭时，循环结束
```

## 4. Select语句

`select`语句用于在多个channel操作中进行选择，类似于`switch`语句，但专门用于channel操作。

### 4.1 基本用法

```go
// 使用select多路复用
func selectDemo() {
	fmt.Println("\nselect多路复用示例:")
	
	ch1 := make(chan string)
	ch2 := make(chan string)
	
	// 在goroutine中向两个channel发送数据
	go func() {
		time.Sleep(100 * time.Millisecond)
		ch1 <- "来自channel 1的消息"
	}()
	
	go func() {
		time.Sleep(200 * time.Millisecond)
		ch2 <- "来自channel 2的消息"
	}()
	
	// 使用select等待两个channel
	for i := 0; i < 2; i++ {
		select {
		case msg1 := <-ch1:
			fmt.Println("  接收:", msg1)
		case msg2 := <-ch2:
			fmt.Println("  接收:", msg2)
		}
	}
}
```

`select`语句的特点：
- 如果多个case同时就绪，select会随机选择一个执行
- 如果没有case就绪，select会阻塞，直到有一个case就绪
- 可以使用`default`子句处理所有case都不就绪的情况

### 4.2 超时处理

`select`结合`time.After`可以实现超时处理：

```go
// 带超时的select
func selectWithTimeout() {
	fmt.Println("\n带超时的select示例:")
	
	ch := make(chan string)
	
	go func() {
		time.Sleep(500 * time.Millisecond)
		ch <- "处理完成"
	}()
	
	select {
	case result := <-ch:
		fmt.Println("  接收到结果:", result)
	case <-time.After(300 * time.Millisecond):
		fmt.Println("  操作超时")
	}
}
```

## 5. 互斥锁和读写锁

当多个goroutine需要访问共享资源时，可以使用互斥锁（Mutex）和读写锁（RWMutex）来保护数据。

### 5.1 互斥锁（Mutex）

```go
// 使用互斥锁保护共享资源
func mutexDemo() {
	fmt.Println("\n互斥锁示例:")
	
	// 共享变量
	var counter int
	var mutex sync.Mutex
	var wg sync.WaitGroup
	
	// 启动10个goroutine，每个goroutine增加计数器10次
	for i := 1; i <= 10; i++ {
		wg.Add(1)
		go func(id int) {
			defer wg.Done()
			
			for j := 1; j <= 10; j++ {
				// 加锁
				mutex.Lock()
				counter++
				fmt.Printf("  goroutine %d: 计数器 = %d\n", id, counter)
				// 解锁
				mutex.Unlock()
				
				// 随机休眠一段时间
				time.Sleep(time.Duration(rand.Intn(10)) * time.Millisecond)
			}
		}(i)
	}
	
	wg.Wait()
	fmt.Printf("  最终计数器值: %d\n", counter)
}
```

### 5.2 读写锁（RWMutex）

当共享资源的读操作远多于写操作时，使用读写锁可以提高并发性能：

```go
// 使用读写锁
func rwMutexDemo() {
	fmt.Println("\n读写锁示例:")
	
	var rwMutex sync.RWMutex
	var data = make(map[string]string)
	var wg sync.WaitGroup
	
	// 写入数据的goroutine
	wg.Add(1)
	go func() {
		defer wg.Done()
		
		for i := 1; i <= 5; i++ {
			// 获取写锁
			rwMutex.Lock()
			key := fmt.Sprintf("key%d", i)
			data[key] = fmt.Sprintf("value%d", i)
			fmt.Printf("  写入: %s = %s\n", key, data[key])
			// 释放写锁
			rwMutex.Unlock()
			
			time.Sleep(100 * time.Millisecond)
		}
	}()
	
	// 读取数据的goroutine（3个）
	for i := 1; i <= 3; i++ {
		wg.Add(1)
		go func(id int) {
			defer wg.Done()
			
			for j := 1; j <= 10; j++ {
				// 获取读锁
				rwMutex.RLock()
				if len(data) > 0 {
					for k, v := range data {
						fmt.Printf("  读取器 %d: %s = %s\n", id, k, v)
					}
				}
				// 释放读锁
				rwMutex.RUnlock()
				
				time.Sleep(50 * time.Millisecond)
			}
		}(i)
	}
	
	wg.Wait()
}
```

读写锁的特点：
- 允许多个读操作同时进行
- 写操作需要独占锁
- 适合读多写少的场景

## 6. 原子操作

对于简单的计数器等操作，可以使用`sync/atomic`包提供的原子操作，它比互斥锁更轻量级：

```go
// 原子操作示例
func atomicOperations() {
	fmt.Println("\n原子操作示例:")
	
	var counter int64
	var wg sync.WaitGroup
	
	// 启动10个goroutine，每个增加计数器10次
	for i := 1; i <= 10; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			
			for j := 1; j <= 10; j++ {
				// 原子地增加计数器
				atomic.AddInt64(&counter, 1)
				time.Sleep(time.Duration(rand.Intn(10)) * time.Millisecond)
			}
		}()
	}
	
	wg.Wait()
	fmt.Printf("  最终计数器值: %d\n", atomic.LoadInt64(&counter))
}
```

## 7. 并发模式

### 7.1 生产者-消费者模式

```go
// 生产者-消费者模式
func producerConsumer() {
	fmt.Println("\n生产者-消费者模式:")
	
	jobs := make(chan int, 5)    // 工作队列
	results := make(chan int, 5) // 结果队列
	
	// 启动3个工作者（消费者）
	var wg sync.WaitGroup
	for w := 1; w <= 3; w++ {
		wg.Add(1)
		go func(id int) {
			defer wg.Done()
			
			fmt.Printf("  工作者 %d 开始处理工作\n", id)
			for job := range jobs {
				fmt.Printf("  工作者 %d 处理工作 %d\n", id, job)
				time.Sleep(100 * time.Millisecond) // 模拟处理时间
				results <- job * 2 // 将结果发送到结果队列
			}
			fmt.Printf("  工作者 %d 完成\n", id)
		}(w)
	}
	
	// 生产者：发送5个工作到队列
	go func() {
		for j := 1; j <= 5; j++ {
			fmt.Printf("  发送工作 %d 到队列\n", j)
			jobs <- j
		}
		close(jobs) // 关闭工作队列，通知工作者没有更多工作
		fmt.Println("  所有工作已发送")
	}()
	
	// 收集所有结果
	go func() {
		for i := 1; i <= 5; i++ {
			result := <-results
			fmt.Printf("  接收结果: %d\n", result)
		}
	}()
	
	// 等待所有工作者完成
	wg.Wait()
	fmt.Println("  所有工作者已完成")
}
```

### 7.2 扇出扇入模式

```go
// 扇出扇入模式
func fanOutFanIn() {
	fmt.Println("\n扇出扇入模式:")
	
	// 创建输入和输出channel
	input := make(chan int, 10)
	output := make(chan int, 10)
	
	// 扇出：启动3个worker处理输入
	var wg sync.WaitGroup
	for i := 1; i <= 3; i++ {
		wg.Add(1)
		go func(id int) {
			defer wg.Done()
			for val := range input {
				// 模拟处理
				time.Sleep(time.Duration(rand.Intn(100)) * time.Millisecond)
				fmt.Printf("  工作者 %d 处理值 %d\n", id, val)
				output <- val * val // 将结果发送到输出channel
			}
		}(i)
	}
	
	// 关闭输入channel的goroutine
	go func() {
		// 发送10个数字到输入channel
		for i := 1; i <= 10; i++ {
			input <- i
		}
		close(input) // 关闭输入，通知worker没有更多数据
		
		// 等待所有worker完成
		wg.Wait()
		close(output) // 关闭输出channel
	}()
	
	// 扇入：从输出channel收集所有结果
	var results []int
	for result := range output {
		results = append(results, result)
	}
	
	// 打印结果
	fmt.Println("  处理结果:", results)
}
```

## 8. 并发陷阱与最佳实践

### 8.1 常见陷阱

1. **竞态条件**：多个goroutine同时访问共享数据可能导致不确定的结果
2. **死锁**：多个goroutine互相等待对方释放资源
3. **资源泄漏**：忘记关闭channel或停止goroutine
4. **过度并发**：创建过多goroutine导致系统资源耗尽

### 8.2 最佳实践

1. **使用适当的同步机制**：根据场景选择合适的同步工具（WaitGroup、Mutex、Channel等）
2. **避免共享内存**：尽量通过channel通信而非共享内存
3. **合理控制goroutine数量**：使用工作池模式限制并发goroutine数量
4. **正确关闭channel**：由发送方负责关闭channel
5. **使用context管理goroutine生命周期**：优雅地取消或超时处理

## 9. 练习题

1. 实现一个并发的网页爬虫，可以同时爬取多个网页
2. 使用goroutine和channel实现一个简单的任务调度系统
3. 实现一个并发的文件处理程序，可以并行处理多个文件
4. 使用互斥锁实现一个线程安全的缓存
5. 实现一个基于channel的信号量

通过本章的学习，你应该能够理解并熟练使用Go语言中的并发特性，包括goroutine、channel、select语句和各种同步原语。Go语言的并发模型简洁而强大，掌握这些特性将帮助你编写高效、可靠的并发程序。