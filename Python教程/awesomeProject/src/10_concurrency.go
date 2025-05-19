// 10_concurrency.go - 并发编程示例（goroutine和channel）
package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

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
			
			for j := 0; j < 10; j++ {
				// 加锁保护共享变量
				mutex.Lock()
				counter++
				mutex.Unlock()
				
				// 模拟一些其他工作
				time.Sleep(1 * time.Millisecond)
			}
			
			fmt.Printf("  goroutine %d 完成\n", id)
		}(i)
	}
	
	wg.Wait()
	fmt.Printf("  最终计数器值: %d\n", counter)
}

// 生产者-消费者模式
func producerConsumer() {
	fmt.Println("\n生产者-消费者模式示例:")
	
	// 创建数据channel
	jobs := make(chan int, 5)
	results := make(chan int, 5)
	
	// 启动3个工作者（消费者）
	var wg sync.WaitGroup
	for w := 1; w <= 3; w++ {
		wg.Add(1)
		go func(id int) {
			defer wg.Done()
			
			// 处理作业，直到channel关闭
			for job := range jobs {
				fmt.Printf("  工作者 %d 处理作业 %d\n", id, job)
				time.Sleep(100 * time.Millisecond) // 模拟处理时间
				results <- job * 2                 // 发送处理结果
			}
			
			fmt.Printf("  工作者 %d 完成\n", id)
		}(w)
	}
	
	// 生产者发送作业
	go func() {
		for j := 1; j <= 10; j++ {
			jobs <- j
			fmt.Printf("  发送作业 %d\n", j)
		}
		close(jobs) // 关闭jobs channel表示没有更多作业
		fmt.Println("  所有作业已发送")
	}()
	
	// 等待所有工作者完成
	go func() {
		wg.Wait()
		close(results) // 关闭results channel
	}()
	
	// 收集结果
	for result := range results {
		fmt.Printf("  得到结果: %d\n", result)
	}
	
	fmt.Println("  所有工作完成")
}

func main() {
	// 设置随机数种子
	rand.Seed(time.Now().UnixNano())
	
	// 简单的goroutine示例
	simpleGoroutine()
	
	// 启动多个goroutine
	multipleGoroutines()
	
	// 使用WaitGroup等待goroutine完成
	waitGroupDemo()
	
	// 使用无缓冲channel进行同步
	unbufferedChannel()
	
	// 使用带缓冲的channel
	bufferedChannel()
	
	// 多个goroutine之间的通信
	channelCommunication()
	
	// 使用select多路复用
	selectDemo()
	
	// 带超时的select
	selectWithTimeout()
	
	// 使用互斥锁保护共享资源
	mutexDemo()
	
	// 生产者-消费者模式
	producerConsumer()
} 