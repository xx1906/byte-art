// 栈, 线性表的另外一种操作规范: 后进先出表
// 和队列的不同是: 栈只能操作队头, 如果把栈看作队列, 那么, 出队列是队头, 如队列也是队头
// 队列的操作: 出队列是队头, 入队列是队尾
//
// 栈有实际的应用么?
// 有的: 比如编译器的代码解析, 电子计算器的实现, 浏览器的浏览记录, 函数调用时现场数据的保存 等等
// 还有一个用的非常多的场景, BFS 搜索使用栈来实现
package main

import (
	"errors"
	"fmt"
)

type adt int

type myStack struct {
	head *node // 堆栈的指针
}

type node struct {
	data adt   // 数据域
	next *node // 指针域
}

func NewMyStack() *myStack {
	return &myStack{head: nil}
}

// 判断栈是否为空
func (c *myStack) Empty() bool {
	return c.head == nil
}

// 入栈操作
func (c *myStack) Push(data adt) {
	tmp := &node{data: data}
	tmp.next = c.head
	c.head = tmp
}

// 出栈
func (c *myStack) Pop() (data *node, err error) {
	if c.Empty() {
		err = errors.New("stack is empty")
		return nil, err
	}

	data = c.head
	c.head = c.head.next
	data.next = nil
	return data, err
}

func main() {
	stack := NewMyStack()
	stack.Push(23)
	fmt.Println(stack.Pop())
	fmt.Println(stack.Pop())
	fmt.Println(stack.Empty()) // true
	stack.Push(2)
	fmt.Println(stack.Empty()) // false
	for !stack.Empty() {
		_, _ = stack.Pop()
	}
	for i := 0; i < 10; i++ {
		stack.Push(adt(i))
	}

	for !stack.Empty() {
		fmt.Println(stack.Pop())
	}
}
