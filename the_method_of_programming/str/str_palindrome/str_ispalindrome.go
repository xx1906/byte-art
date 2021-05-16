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
