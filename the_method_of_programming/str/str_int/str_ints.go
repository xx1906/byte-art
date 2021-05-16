package str_int

import (
	"math"
	"unicode"
)

// 将字符串转为 int 整数， 输入一个由数字组成的组字符串， 请把它转成整数并输出

// 问题看起来很简单， 实际上是有很多细节点的。

// 1. 判断字符串是否为空
// 2. 处理前置空格问题
// 3. 符号问题， 正负号
// 4. 输入的字符串不能太长，否则转换成整数后可能会导致整数溢出问题

// 整数溢出的问题， 取最大或者最小的 int 类型， 即大于正整数 返回 MAX_INT 或者小于负整数时返回 MIN_INT

func StrToInt(data []byte) int32 {
	const maxInt32 = math.MaxInt32
	const minInt32 = math.MinInt32
	var ans int32 = 0
	if len(data) == 0 {
		ans = 0
		return ans
	}
	var idx = 0

	for unicode.IsSpace(rune(data[idx])) {
		idx++
	}

	var sign = 1
	// 确定符号
	if data[idx] == '+' || data[idx] == '-' {
		if data[idx] == '-' {
			sign = -1
		}
		idx++
	}

	//
	for idx < len(data) {
		var c = int32(data[idx] - '0')
		// 无效数字
		if !(c >= 0 && c <= 9) {
			ans = 0
			break
		}
		idx++
		ans = ans*10 + c

		// 判断是否溢出
		if ans > maxInt32 {
			if sign == -1 {
				return minInt32
			}
			return maxInt32
		}
	}

	if sign == -1 {
		ans = -ans
	}
	return ans
}
