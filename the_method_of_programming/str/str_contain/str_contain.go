package str_contain

// 字符串包含的写法
//  描述： 给定长字符串 a 和一短字符串 b。请问如何快速判断字符串 b 中的所有字符是否都在长字符串中？ 请编写函数
// 实现，
// 比如：如果字符串 a 是 "ABCD"， 字符串 b 是 "BDA", 答案是 true, 因为字符串 b 中的字母都在字符串 a 中， 或者说 b 是 a 的子集
//
// 如果 字符串 a 是 "ABCD", 字符串 b 是 "BCE", 答案是 false, 因为字符串 b 中的字母 E 不在字符串 a 中。
//
// 如果 字符串 a 是 "ABCD", 字符串 b 是 "AA", 答案是 true, 因为字符串 b 中的字母包含在字符串 a 中。

// 字符串包含的暴力解法
func StrContainForce(s1, s2 string) bool {
	var ans bool = true
	for i := 0; i < len(s1); i++ {
		var j = 0
		for j = 0; j < len(s2) && s1[i] != s2[j]; j++ {
		}
		// 超出了界限
		if j == len(s2) {
			ans = false
			break
		}

	}
	return ans
}

// 计数法, s2 包含 s1 中所有的字母
func StringContainCount(s1, s2 []byte) bool {
	var buffA [26]byte
	var buffB [26]byte
	for _, v := range s1 {
		buffA[v-'a']++
	}
	for _, v := range s2 {
		buffB[v-'a']++
	}
	for i := 0; i < len(buffA); i++ {
		if buffB[i] < buffA[i] {
			return false
		}
	}
	return true
}
