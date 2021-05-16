package str_palindrome

// 给定一个字符串， 如何判断这个字符串是否是回文串？

func IsPalindrome(data []byte) (ans bool) {
	ans = true
	for back, front := len(data)-1, 0; front < back; back, front = front+1, back-1 {
		if data[front] != data[back] {
			ans = false
			break
		}
	}
	return
}

// 从两边向中间扫描是否是回文字符串
func IsPalindromeWithMiddle(data []byte) (ans bool) {

	// 默认是回文串
	ans = true
	var front = 0
	var back = 0
	// 字符个数是 2 的倍数
	if len(data)%2 == 0 {
		front = len(data)/2 - 1
		back = len(data) / 2
	} else {
		// 基数个数值
		front = len(data) / 2
		back = len(data) / 2
	}
	for front >= 0 && back < len(data) {
		//fmt.Println(data[front], data[back])
		if data[front] != data[back] {
			ans = false
			break
		}
		front--
		back++
	}
	return
}
