package go_gist

// 判断一个数是否是 2 的整次幂
// 摘自 runtime/slice.go#isPowerOfTwo
func isPowerOfTwo(x uintptr) bool {
	return x&(x-1) == 0
}
