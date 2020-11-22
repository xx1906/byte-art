package main

import (
	"fmt"
	"unsafe"
)

func main() {
	// 有相同内存布局的类型转换
	var i64 int64 = 9
	var f64 = *(*float64)(unsafe.Pointer(&i64))
	fmt.Println("pattern 1:", f64)
	var array [16]int
	// 使用偏移量来访问数组
	*(*int)(unsafe.Pointer(uintptr(unsafe.Pointer(&array)) + unsafe.Sizeof(int(0))*2)) = 2006
	fmt.Println("pattern 3:", array)
}
