# golang 切片相关的数据

> go版本 1.15.5, 源代码位置:`/src/runtime/slice.go`


### 源代码相关

```go
type slice struct {
	array unsafe.Pointer
	len   int
	cap   int
}
```

> 没导出的 `slice` 定义， 只有三个字段，
>
> array: 类型为 `unsafe.Pointer`, 然而`unsafe.Ponter` 又是 `type Pointer *ArbitraryType`, 到底 `unsafe.Pointer` 是`*ArbitraryType`, 然而 `type ArbitraryType int`, 所以 `array` 字段的真实类型其实就是 `*int` 。 没错他就是一个指针类型, 并且还随着机器字长的变化而变化， 默认 32 位机器是 4 个字节的大小， 64 位机器是 8 个字节大小。
>
>  
>
> len: 是 slice 对象中， array 已经使用的空间的长度
>
> cap: 是 slice 对象已经分配的长度， 也是 array 的长度



### makeslicecopy 

> **什么情况下会触发这个函数**: 
>
> ```
> s6 := []int{1, 2, 3, 4, 5, 6}
> s7 := make([]int, len(s6)+1)
> copy(s7, s6)
> ```

```go
func makeslicecopy(et *_type, tolen int, fromlen int, from unsafe.Pointer) unsafe.Pointer {
	var tomem, copymem uintptr
	if uintptr(tolen) > uintptr(fromlen) {
		var overflow bool
		tomem, overflow = math.MulUintptr(et.size, uintptr(tolen))
		if overflow || tomem > maxAlloc || tolen < 0 {
			panicmakeslicelen()
		}
		copymem = et.size * uintptr(fromlen)
	} else {
		// fromlen is a known good length providing and equal or greater than tolen,
		// thereby making tolen a good slice length too as from and to slices have the
		// same element width.
		tomem = et.size * uintptr(tolen)
		copymem = tomem
	}

	var to unsafe.Pointer
	if et.ptrdata == 0 {
		to = mallocgc(tomem, nil, false)
		if copymem < tomem {
			memclrNoHeapPointers(add(to, copymem), tomem-copymem)
		}
	} else {
		// Note: can't use rawmem (which avoids zeroing of memory), because then GC can scan uninitialized memory.
		to = mallocgc(tomem, et, true)
		if copymem > 0 && writeBarrier.enabled {
			// Only shade the pointers in old.array since we know the destination slice to
			// only contains nil pointers because it has been cleared during alloc.
			bulkBarrierPreWriteSrcOnly(uintptr(to), uintptr(from), copymem)
		}
	}

	if raceenabled {
		callerpc := getcallerpc()
		pc := funcPC(makeslicecopy)
		racereadrangepc(from, copymem, callerpc, pc)
	}
	if msanenabled {
		msanread(from, copymem)
	}

	memmove(to, from, copymem)

	return to
}
```

###### 参数表示什么意思？

1. `et *_type`， 类型为 `*_type` 这是一个内置的类型，`interface{}` 里面也有它的身影！ 也许你会问， 我是怎么知道的？ 我的答案是: 你猜！

   > 那么这个类型有什么用的？ 这个是给 go 程序来确定类型的元数据的， 包括 go 对象占用空间的大小， 对齐方式，哈希值等信息

2. `tolen int`, 类型为 int, 表示复制到目标 `slice` 的长度

3. `fromlen int`, 类型是 int, 表示复制源 `slice` 的长度

4. `from unsafe.Pointer`, 类型是 unsafe.Pointer， 表示复制源数据的起始地址, 加上参数 `fromlen` 可以确定需要复制多少的内容

5. **返回参数是 unsafe.Pointer**， 可以组合 tolen 确定复制之后， `slice` 对象的 `len` 的值



> 发现了： Note: can't use rawmem (which avoids zeroing of memory), because then GC can scan uninitialized memory.
>
> 原来是这样

###### 复制的过程 --- 确定需要复制多少字节内容

```go
var tomem, copymem uintptr
if uintptr(tolen) > uintptr(fromlen) {
  var overflow bool
  tomem, overflow = math.MulUintptr(et.size, uintptr(tolen))
  if overflow || tomem > maxAlloc || tolen < 0 {
    panicmakeslicelen()
  }
  copymem = et.size * uintptr(fromlen)
} else {
  // fromlen is a known good length providing and equal or greater than tolen,
  // thereby making tolen a good slice length too as from and to slices have the
  // same element width.
  tomem = et.size * uintptr(tolen)
  copymem = tomem
}
```

分两种请求： 

1. 如果目标的长度比源切片的长度要长; 需要判断目标的长度 乘以 `size` (切片存放的数据的占用空间的大小) 的长度是否会触发溢出(此时没有真正地分配内存空间)

   > 在不触发内存溢出的条件下:
   >
   > tomem = tolen * et.size 
   >
   > copymen = et.size * fromlen 

2. 如果目标的长度不大于源切片的长度

   > tomem = et.size * tolen
   >
   > copymen = tomem



###### 复制的过程 -- 分配地址空间

 ```go
var to unsafe.Pointer
if et.ptrdata == 0 {
  to = mallocgc(tomem, nil, false)
  if copymem < tomem {
    memclrNoHeapPointers(add(to, copymem), tomem-copymem)
  }
} else {
  // Note: can't use rawmem (which avoids zeroing of memory), because then GC can scan uninitialized memory.
  to = mallocgc(tomem, et, true)
  if copymem > 0 && writeBarrier.enabled {
    // Only shade the pointers in old.array since we know the destination slice to
    // only contains nil pointers because it has been cleared during alloc.
    bulkBarrierPreWriteSrcOnly(uintptr(to), uintptr(from), copymem)
  }
}
 ```

> 真正分配内存中的时候， 可能会触发 panic
>
> 分配内存的函数: mallocgc(size uintptr, typ *_type, needzero bool) unsafe.Pointer 
>
> 参数说明： 
>
> size: 会申请 size 个字节的内存空间
>
> typ: 
>
> Needzero:
>
> ------------
>
> 小于等于 32 kb 大小是从 `P` 空闲的空间中获取
>
> 大对象（大于32kb） 从堆中分配内存空间



###### 复制过程 --- 可能不顺利

> 判断一下是否触发了程序 panic

```go
if raceenabled {
  callerpc := getcallerpc()
  pc := funcPC(makeslicecopy)
  racereadrangepc(from, copymem, callerpc, pc)
}
if msanenabled {
  msanread(from, copymem)
}
```



###### 复制过程 --- 直接一个 memove 搞定

```go
memmove(to, from, copymem)
```

###### --- 复制收尾

返回分配空间后的指针的首地址



###### makeslicecopy 什么情况下会触发？

```go
s6 := []int{1, 2, 3, 4, 5, 6}
s7 := make([]int, len(s6)+1)
copy(s7, s6)
fmt.Println(s7)
```





### makeslice 

###### makeslice --- 判断参数书否合法

```go
mem, overflow := math.MulUintptr(et.size, uintptr(cap))
if overflow || mem > maxAlloc || len < 0 || len > cap {
  // NOTE: Produce a 'len out of range' error instead of a
  // 'cap out of range' error when someone does make([]T, bignumber).
  // 'cap out of range' is true too, but since the cap is only being
  // supplied implicitly, saying len is clearer.
  // See golang.org/issue/4085.
  mem, overflow := math.MulUintptr(et.size, uintptr(len))
  if overflow || mem > maxAlloc || len < 0 {
    panicmakeslicelen()
  }
  panicmakeslicecap()
}
```

> 判断参数是否合法， 预分配的空间是否会触发最大的分配空间
>
> 1. 优先 panic len 不符合条件
> 2. 如果 len 符合条件， panic cap 

****



###### makeslice -- 分配空间并返回

> Tips: 如果使用 make 的时候， 不给 cap 值， 默认 cap = len
>
> 如果 cap < len, 触发 panic



### growslice

###### growslice --- 有用的注释

> // growslice handles slice growth during append.
> // It is passed the slice element type, the old slice, and the desired new minimum capacity,
> // and it returns a new slice with at least that capacity, with the old data
> // copied into it.
> // The new slice's length is set to the old slice's length,
> // NOT to the new requested capacity.
> // This is for codegen convenience. The old slice's length is used immediately
> // to calculate where to write new values during an append.
> // TODO: When the old backend is gone, reconsider this decision.
> // The SSA backend might prefer the new length or to return only ptr/cap and save stack space.
>
> //
>
> // growslice 函数在 append 函数调用时调用
>
> // 函数参数是: 切片元素的类型，旧切片 和最小的容量
>
> // 函数的返回值：至少具有改容量的新切片并且已经老切片的内容已经被复制到新切片中
>
> // 新切片的长度设置为老切片的长度而不是新的容量值
>
> // 



###### growslice --- 条件

```go
if raceenabled {
  callerpc := getcallerpc()
  racereadrangepc(old.array, uintptr(old.len*int(et.size)), callerpc, funcPC(growslice))
}
if msanenabled {
  msanread(old.array, uintptr(old.len*int(et.size)))
}

if cap < old.cap {
  panic(errorString("growslice: cap out of range"))
}
```

1. 判断是否触发竞态
2. 判断是否触发 msan
3. 新容量是否小于 old.cap



####### ---- growslice 特殊的优惠

```go
if et.size == 0 {
  // append should not create a slice with nil pointer but non-zero len.
  // We assume that append doesn't need to preserve old.array in this case.
  return slice{unsafe.Pointer(&zerobase), old.len, cap}
}
```

如果 et.size ==0 直接分配， 什么情况下回事这种情况

> ```go
> var sl = make([]struct{}, 0, 1)
> sl = append(sl, struct{}{})
> sl = append(sl, struct{}{})
> ```
>
> struct{} 不分配空间的情况下就会触发这个条件



###### growslice --- 确定 newcap (预估值)

```go
newcap := old.cap
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
```

1. 如果新的容量比原来的两倍还要大， 直接使用新的容量作为预估

   什么情况下会是这种情况？ 

   ```go
   var s10 = make([]int, 0, 1)
   s10 = append(s10, 1, 2, 3, 4)
   fmt.Println(s10)
   ```

   例子中： newcap = 1, doublecap = 2, cap = 4; 所以就算是 doublecap 也不满足条件， 所以直接使用 cap

2. 条件 `1` 不成立的情况下：

   * 如果原来切片长度小于 1024， newcap 直接翻倍
   * 否则 newcap = old.cap * (5/4)



###### growslice --- 确定newcap 的值

```go

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

```

根据 et.size 来确定分配 newcap 的大小

```go
func isPowerOfTwo(x uintptr) bool {
	return x&(x-1) == 0
} // 这个函数用来判断 x 是否为2 的整次幂
```



###### growslice ---- 分配空间， 复制内存并返回新的切片对象

```go

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
```



### slicecopy

###### slicecopy --- 判断条件

```go
if fmLen == 0 || toLen == 0 {
  return 0
} // 不需要复制任何内容, 所以 copy(dst, src) 任何一个len == 0 都不会出错 

n := fmLen
if toLen < n {
  n = toLen
} // 确定最多能复制多少内容

if width == 0 {
  return n
} // 复制的是空白
```



###### slicecopy --- 真正的复制

```go
size := uintptr(n) * width
if size == 1 { // common case worth about 2x to do here
  // TODO: is this still worth it with new memmove impl?
  *(*byte)(toPtr) = *(*byte)(fmPtr) // known to be a byte pointer
} else {
  memmove(toPtr, fmPtr, size)
}
return n
```

1. 如果复制字节的长度是1， 直接解引用
2. 如果复制字节的长度大于 1， 使用memove 函数



### 小结

1. makeslicecopy 触发情景

   ```go
   s6 := []int{1, 2, 3, 4, 5, 6}
   s7 := make([]int, len(s6)+1)
   copy(s7, s6)
   fmt.Println(s7)
   ```

2. makeslice 

   * len 太大 会触发 `maxAlloc`
   * cap 小于 len 会触发 `panicmakeslicecap`

3. growslice

   *  `et.size == 0` 不会触发内容分配
   * cap > old.cap *2  是， newcap = cap
   * `old.len < 1024` cap = old.cap * 2 
   * `old.len >= 1024`, cap = old.cap * (5/4)

4. slicecopy

   * 如果两个切片的长度不一样， 按照最小的来复制
   * 复制空白时， 直接返回
   * 复制一个字节时， 直接解引用， 不复制

   