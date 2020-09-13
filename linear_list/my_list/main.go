// 线性表之: 链表, 链表和数组都是线性表, 二者有什么不同嘛?
//
package main

import (
	"errors"
	"fmt"
)

type adt int
type myList struct {
	head *node
	tail *node
}

type node struct {
	data adt
	next *node
}

func NewMyList() *myList {
	m := myList{}
	m.head = &node{}
	m.tail = m.head
	return &m
}
func (c *myList) Append(data adt) {
	tmp := &node{data: data, next: nil}
	c.tail.next = tmp
	c.tail = tmp
}

func (c *myList) Delete(n *node) (err error) {
	if n == nil {
		return errors.New("invalid params")
	}
	var dummy = c.head
	// 找到上一个节点
	for dummy != nil && dummy.next != n {
		dummy = dummy.next
	}
	if dummy == nil {
		return errors.New(fmt.Sprintf("not found data"))
	}

	// 删除的是头结点
	if n == c.head.next {
		c.head.next = c.head.next.next
		return nil
	}

	if n == c.tail.next {
		// 删除的尾巴节点
		dummy.next = nil
		c.tail = dummy
		return err
	}

	dummy.next = dummy.next.next
	return nil
}

func (c *myList) Find(obj adt) (n *node, err error) {
	for v := c.head.next; v != nil; v = v.next {
		if v.data == obj {
			n = v
			break
		}
	}
	if n != nil {
		err = nil
		return n, err
	}
	err = errors.New(fmt.Sprintf("not found %v", obj))
	return
}

func main() {
	list := NewMyList()
	list.Append(23)
	list.Append(233)
	for v := list.head.next; v != nil; v = v.next {
		fmt.Println(v.data)
	}
	fmt.Println(list.Find(233))
	fmt.Println(list.Find(23))
	fmt.Println(list.Find(2343))
	find, err := list.Find(233)
	err = list.Delete(find)
	fmt.Println(err)

	find, err = list.Find(23)
	err = list.Delete(find)
	fmt.Println(err)

	find, err = list.Find(233)
	err = list.Delete(find)
	fmt.Println(err)

	fmt.Println("-------------------")
	for v := list.head.next; v != nil; v = v.next {
		fmt.Println(v.data)
	}
	fmt.Println("-------------------")
}
