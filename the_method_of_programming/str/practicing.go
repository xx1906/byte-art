package str

// 字符串反转
func ShiftRotate(data []byte, n int) {
	var helper func(data []byte, start, end int)

	helper = func(data []byte, start, end int) {
		for start < end {
			data[start], data[end] = data[end], data[start]
			start++
			end--
		}
	}
	var m = n % len(data)
	helper(data, 0, m)
	helper(data, m+1, len(data)-1)
	helper(data, 0, len(data)-1)
}

// 字符串左右移动函数
// 给定一个字符串， 这个字符串为 * 号和 26 个字母的任意组合。 现在需要把字符串中的 * 号 都移动到最左侧
// 而把字符串中的字母移动到最右侧， 并保持相对的顺序不变， 要求时间复杂度和空间复杂度最小
func ShiftStrLeftMove(data []byte) {
	var sn, ss = len(data) - 1, len(data) - 1
	for {
		for sn >= 0 && data[sn] != '*' {
			sn--
		}
		if sn < 0 {
			break
		}
		for ss >= 0 && (ss >= sn || data[ss] == '*') {
			ss--
		}

		if ss < 0 {
			break
		}
		data[sn], data[ss] = data[ss], data[sn]
	}
}
