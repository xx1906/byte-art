// 线性表: 数组描述
// golang 中切片的底层是一个数组, 但是切片不是传统语言的数组, 比如c或者c++中的数组, golang 中的切片更加像c中的柔性数组
//
package main

import (
	"errors"
	"fmt"
)

// 数组: 底层一个连续的内存空间, 数组是一个类型, (基础数据类型加上数组的长度组成)
// 这个是数组, 数组的长度是 12, 基础的类型是 int(注意, int 在 go 语言中, 长度是未定义的, 具体的占用的内存空间需要看操作系统的位数)
// 还有一个重要的点是: 数组在建立之前, 需要确定数组的长度
// var arr [12]int
//
// 这个 arr 不是数组, 不过它的底层是数组,
// var arr []int

type adt int

func defaultAdt() adt {
	return adt(0)
}

// 如何判断一个数组和切片的区别
func init() {
	var array [12]int
	var sl []int
	fmt.Printf("array:%T, sl:%T\n", array, sl) // output:array:[12]int, sl:[]int
}

type myArray struct {
	arr []adt  // 底层的切片
	ln  uint32 // 长度 等价于 len(arr)
	cap uint32 // 容量 等价于 cap(arr)
}

// 新建一个容量为 cap 长度的数组, 长度为0, 容量为 cap
func NewMyArray(cap uint32) *myArray {

	return &myArray{
		arr: make(
			[]adt, // 放一个切片
			cap,   // 初始容量
			cap,   // 最大的容量
		),
		cap: cap, // 容量
		ln:  0,   // 初始的长度
	}
}

// 往数组中插入数据
// idx  是插入的位置
// data 是插入的数据
func (c *myArray) Insert(idx uint32, data adt) (err error, ok bool) {
	// 如果数组的长度不够了, 启动数组的扩容
	if c.ln >= c.cap {
		c.extend()
	}

	// 如果要插入的位置要大于当前的长度, 返回错误
	if idx > c.ln {
		err = errors.New(fmt.Sprintf("idx:%d large than ln:%d", idx, c.ln))
		return err, ok
	}

	// 把数据移动一个位置
	for i := c.ln; i > idx; i-- {
		c.arr[i] = c.arr[i-1]
	}

	// 赋值
	c.arr[idx] = data
	c.ln++
	return err, true
}

// 数组扩容, 长度是原来容量的两倍
func (c *myArray) extend() {
	newCap := c.cap * 2

	newArr := make([]adt, newCap, newCap)
	c.cap = newCap
	// 把原来的位置整体拷贝过去
	copy(newArr, c.arr)
	c.arr = newArr

}

// 返回数组的长度

func (c *myArray) Len() uint32 {
	return c.ln
}

// 返回数组的容量
func (c *myArray) Cap() uint32 {
	return c.cap
}

// 删除数组的底层数据信息
// idx 是需要删除数据的位置
func (c *myArray) Delete(idx uint32) (err error, data adt) {
	// 如果 idx 大于当前的长度, 直接报错就好了
	if idx >= c.ln {
		err = errors.New(fmt.Sprintf("idx:%d is larger than %d", idx, c.ln))
		return err, data
	}

	data = c.arr[idx]
	for i := idx; i < c.ln-1; i++ {
		c.arr[i] = c.arr[i+1]
	}
	c.ln--
	return nil, data
}

// 软删除数据, 其实就是把数组最后一个赋值到需要删除数据的位置
// 然后把数组的最后一个数据设置为默认值
// 再把数组的长度减一
func (c *myArray) SoftDelete(idx uint32) (err error, data adt) {
	// 如果 idx 大于当前的长度, 直接报错就好了
	if idx >= c.ln {
		err = errors.New(fmt.Sprintf("idx:%d is larger than %d", idx, c.ln))
		return err, data
	}
	data = c.arr[idx]
	c.arr[idx] = c.arr[c.ln-1]
	c.arr[c.ln-1] = defaultAdt() // 设置为默认值
	c.ln--
	return nil, data
}
func main() {
	var my = NewMyArray(2)
	fmt.Printf("data:%#v\n", my)

	_, _ = my.Insert(0, 13)
	_, _ = my.Insert(0, 23)
	_, _ = my.Insert(0, 33)
	_, _ = my.Insert(0, 43)
	_, _ = my.Insert(0, 53)
	_, _ = my.Insert(0, 63)
	_, _ = my.Insert(0, 73)
	_, _ = my.Insert(0, 83)
	_, _ = my.Insert(0, 93)
	_, _ = my.Insert(0, 103)
	_, _ = my.Insert(1, 113)

	var data adt
	var err error
	err, data = my.SoftDelete(0)
	if err != nil {
		fmt.Printf("soft delete,err:%s,%v\n", err, data)
	}

	fmt.Printf("data:%#v\n", my)
}

// 空一行
