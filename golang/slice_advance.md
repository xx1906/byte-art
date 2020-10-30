

# golang 的切片原理以及高级用法

## golang 语言中的灵活切片

最和蔼可亲的切片用法

```golang
// 创建一个切片, 类型为 int, 长度为 0, 分配的空间为 2
// 然后把返回值赋值给 sl
sl := make([]int, 0, 2)
// 将 1, 2 元素 后切片的后部分添加到 sl, 然后返回新的切片
// 最后把新的切片赋值给 sl
sl = append(sl, 1, 2)
fmt.Println(sl, len(sl), cap(sl))// output: [1 2] 2 2
```


也可以通过下标来操作切片元素的值, 读或者写


> 通过下标来操作切片, 如果切片对象的 len 小于等于操作的 index, 通过切片的 index 操作切片对象将会抛出异常

```golang
fmt.Println(sl[0]) // 读取切片的第一个元素的值
sl[0] = 999  // 写(修改)切片第一个元素的值
fmt.Println(sl[0]) // 读切片第一个元素的值
```

> [slice 源码](https://github.com/golang/go/blob/master/src/runtime/slice.go)

```golang
type slice struct {
	array unsafe.Pointer
	len   int
	cap   int
}
```

* array: 是切片指向的底层数组
* len  : 是当前切片使用底层数组的长度
* cap  : 当前切片底层数组的容量

**一般来说, cap 的都是大于等于 len 的, 特殊的修改手段除外**

## 切片的底层图示

## 切片的扩容

* 如果 cap > old.cap * 2 则, newcap = cap
* 如果 old.cap * 2 <  1024, 则 newcap = old.cap * 2
* 如果 old.cap * 2 >= 1024 则 newcap = old.cap * 1.25, 这个和 c++ 中 vector 底层的算法一致



确定了新切片的容量的大小之后, 分配一个新的空间, 然后把 old.array 的内容拷贝到新的切片的 array

```golang

func growslice(et *_type, old slice, cap int) slice {
	if raceenabled {
		callerpc := getcallerpc()
		racereadrangepc(old.array, uintptr(old.len*int(et.size)), callerpc, funcPC(growslice))
	}
	if msanenabled {
		msanread(old.array, uintptr(old.len*int(et.size)))
	}

	// 如果传入的容量比原来的容量还要小, 直接 panic
	if cap < old.cap {
		panic(errorString("growslice: cap out of range"))
	}

	if et.size == 0 {
		// append should not create a slice with nil pointer but non-zero len.
		// We assume that append doesn't need to preserve old.array in this case.
		return slice{unsafe.Pointer(&zerobase), old.len, cap}
	}
	
	newcap := old.cap
	// 默认新的 cap 的值, 直接等于原来的两倍
	// 如果 cap 大于原来切片的两倍, 直接使用新的 cap
	// 
	// 如果新的 cap 没有大于原来的两倍
	// 处理如下: 
	// 1. 如果 old.cap * 2 < 1024, 直接使用 newcap = old.cap * 2
	// 2. 如果原来的old.cap * 2 >= 1024, newcap = (old.cap) * 1.25
	doublecap := newcap + newcap
	if cap > doublecap {
		newcap = cap
	} else {
		if old.len < 1024 {
			newcap = doublecap
		} else {
			// Check 0 < newcap to detect overflow
			// and prevent an infinite loop.
			for 0 < newcap && newcap < cap {
				newcap += newcap / 4
			}
			// Set newcap to the requested cap when
			// the newcap calculation overflowed.
			if newcap <= 0 {
				newcap = cap
			}
		}
	}

	var overflow bool
	var lenmem, newlenmem, capmem uintptr
	// Specialize for common values of et.size.
	// For 1 we don't need any division/multiplication.
	// For sys.PtrSize, compiler will optimize division/multiplication into a shift by a constant.
	// For powers of 2, use a variable shift.
	switch {
	case et.size == 1:
		lenmem = uintptr(old.len)
		newlenmem = uintptr(cap)
		capmem = roundupsize(uintptr(newcap))
		overflow = uintptr(newcap) > maxAlloc
		newcap = int(capmem)
	case et.size == sys.PtrSize:
		lenmem = uintptr(old.len) * sys.PtrSize
		newlenmem = uintptr(cap) * sys.PtrSize
		capmem = roundupsize(uintptr(newcap) * sys.PtrSize)
		overflow = uintptr(newcap) > maxAlloc/sys.PtrSize
		newcap = int(capmem / sys.PtrSize)
	case isPowerOfTwo(et.size):
		var shift uintptr
		if sys.PtrSize == 8 {
			// Mask shift for better code generation.
			shift = uintptr(sys.Ctz64(uint64(et.size))) & 63
		} else {
			shift = uintptr(sys.Ctz32(uint32(et.size))) & 31
		}
		lenmem = uintptr(old.len) << shift
		newlenmem = uintptr(cap) << shift
		capmem = roundupsize(uintptr(newcap) << shift)
		overflow = uintptr(newcap) > (maxAlloc >> shift)
		newcap = int(capmem >> shift)
	default:
		lenmem = uintptr(old.len) * et.size
		newlenmem = uintptr(cap) * et.size
		capmem, overflow = math.MulUintptr(et.size, uintptr(newcap))
		capmem = roundupsize(capmem)
		newcap = int(capmem / et.size)
	}

	// The check of overflow in addition to capmem > maxAlloc is needed
	// to prevent an overflow which can be used to trigger a segfault
	// on 32bit architectures with this example program:
	//
	// type T [1<<27 + 1]int64
	//
	// var d T
	// var s []T
	//
	// func main() {
	//   s = append(s, d, d, d, d)
	//   print(len(s), "\n")
	// }
	if overflow || capmem > maxAlloc {
		panic(errorString("growslice: cap out of range"))
	}

	var p unsafe.Pointer
	if et.ptrdata == 0 {
		p = mallocgc(capmem, nil, false)
		// The append() that calls growslice is going to overwrite from old.len to cap (which will be the new length).
		// Only clear the part that will not be overwritten.
		memclrNoHeapPointers(add(p, newlenmem), capmem-newlenmem)
	} else {
		// Note: can't use rawmem (which avoids zeroing of memory), because then GC can scan uninitialized memory.
		p = mallocgc(capmem, et, true)
		if lenmem > 0 && writeBarrier.enabled {
			// Only shade the pointers in old.array since we know the destination slice p
			// only contains nil pointers because it has been cleared during alloc.
			bulkBarrierPreWriteSrcOnly(uintptr(p), uintptr(old.array), lenmem-et.size+et.ptrdata)
		}
	}
	memmove(p, old.array, lenmem)

	return slice{p, old.len, newcap}
}

```





## 通过指针来操作切片底层, 慎用

* 切片底层的首地址指向的是切片底层的是数组

  ````golang
  sl := []int{0}
  ptr := *(*uintptr)(unsafe.Pointer(&sl))
  ````

* 切片底层的首地址加上 `unsafe.Sizeof(int(0))` 的地址为指向切片底层的 `len` 字段的地址

* 切片底层的首地址加上 `unsafe.Sizeof(int(0)) * 2` 的地址为指向切片底层的 `cap `字段的地址



```golang
sl := []int{0}
ptr := *(*uintptr)(unsafe.Pointer(&sl))
fmt.Println(unsafe.Pointer(ptr))
// 扩容之前的切片的指向数组的地址
fmt.Printf("ptr is %x\n", ptr)
sl = append(sl, 1, 2, 3, 4, 5, 6, 7)
// 切片扩容之后, 底层指向数组的地址
ptr = *(*uintptr)(unsafe.Pointer(&sl))
fmt.Printf("ptr is %x\n", ptr)
fmt.Printf("type is %T\n", ptr)

fmt.Printf("before: len pointer:%p, len data:%v\n", (*int)(unsafe.Pointer(uintptr(uintptr(unsafe.Pointer(&sl))+unsafe.Sizeof(int(0))))),
*(*int)(unsafe.Pointer(uintptr(uintptr(unsafe.Pointer(&sl)) + unsafe.Sizeof(int(0))))))
fmt.Printf("before: cap pointer:%p, cap data:%v\n", (*int)(unsafe.Pointer(uintptr(uintptr(unsafe.Pointer(&sl))+2*unsafe.Sizeof(int(0))))),
*(*int)(unsafe.Pointer(uintptr(uintptr(unsafe.Pointer(&sl)) + 2*unsafe.Sizeof(int(0))))))

sl = append(sl, 8)
fmt.Printf("after: before: len pointer:%p, len data:%v\n", (*int)(unsafe.Pointer(uintptr(uintptr(unsafe.Pointer(&sl))+unsafe.Sizeof(int(0))))),
*(*int)(unsafe.Pointer(uintptr(uintptr(unsafe.Pointer(&sl)) + unsafe.Sizeof(int(0))))))
fmt.Printf("after: cap pointer:%p, cap data:%v\n", (*int)(unsafe.Pointer(uintptr(uintptr(unsafe.Pointer(&sl))+2*unsafe.Sizeof(int(0))))),
*(*int)(unsafe.Pointer(uintptr(uintptr(unsafe.Pointer(&sl)) + 2*unsafe.Sizeof(int(0))))))
fmt.Println(*(*[3]int)(unsafe.Pointer(ptr)))

// 通过数组的偏移量来操作数组
ptr = *(*uintptr)(unsafe.Pointer(&sl))
//把 ptr + 偏移地址 int * 2 之后的空间解析为长度为 5 的 int 类型的数组输出
fmt.Println(*(*[5]int)(unsafe.Pointer(ptr + uintptr(unsafe.Sizeof(int(0))*2))))

// 通过修改底层数组的第一个元素来修改切片的值
// 这个行为归类为未定义的
(*(*[5]int)(unsafe.Pointer(ptr)))[0] = 999
fmt.Println(sl)
```



