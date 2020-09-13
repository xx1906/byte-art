// 队列, 底层的实现可以是数组或者链表, 优先级队列整一个二叉树
// 队列和数组链表的不同就是数据的操作
// 数组支持随机访问, 也就是可以通过偏移量对数据进行随机的访问 比如 arr[1], arr[2]
// 单纯的链表不支持数据的随机访问
//
package main

import (
	"errors"
	"fmt"
)

type adt int
type myQueue struct {
	head *node // 指向头结点
	tail *node // 指向尾部节点
}

type node struct {
	data adt
	next *node
}

// 新建造一个链表, 链表的表头和尾部都是指向同一个指针的地址
func NewQueue() *myQueue {
	q := &myQueue{}
	// 下面两句在 go 中其实是多余的
	q.head = nil
	q.tail = q.head
	return q
}

// 出队列
func (c *myQueue) Pop() (n *node, err error) {
	// 如果链表的头结点指向为空, 表示队列已经为空了
	// 等价于调用 Empty() 函数
	if c.head == nil {
		err = errors.New("queue is empty")
		return nil, err
	}
	n = c.head
	c.head = c.head.next
	// 如果是最后一个出队列, 需要设置为空
	if c.head == nil {
		c.tail = nil
	}
	n.next = nil
	return n, err
}

// 判断链表是否为空
func (c *myQueue) Empty() bool {
	return c.head == nil
}

// 新增元素进入队列
func (c *myQueue) Push(data adt) {
	tmp := &node{data: data}
	// 如果是第一个数据入队列, 需要将头结点指向当前的节点
	if c.head == c.tail && c.head == nil {
		c.head = tmp
		c.head.next = nil
		c.tail = tmp
		return
	}

	c.tail.next = tmp
	c.tail = tmp
}
func main() {
	queue := NewQueue()
	fmt.Println(queue.Empty())
	queue.Push(12)
	fmt.Println(queue.Pop())
	fmt.Println(queue.Pop())
	queue.Push(2)
	queue.Push(213)
	queue.Push(223)
	queue.Push(233)
	for !queue.Empty() {
		fmt.Println(queue.Pop())
		fmt.Println("---------")
	}

	for v := queue.tail; v != nil; v = v.next {
		fmt.Println(v)
	}
	for v := queue.head; v != nil; v = v.next {
		fmt.Println(v, "head")
	}
}
