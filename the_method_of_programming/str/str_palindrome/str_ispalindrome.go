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

// 举一反三的问题，判断链表是否是回文链表， 可以使用冲中间向两头扫的方式，
// 使用快慢指针的方式
// 经典的方式是使用快慢指针， 先定位到链表的中间，再将链表的后半段逆置， 然后再采用从中间到两头扫的思想。

// 栈回文
// 判断一个栈是否是回文
// 需要根据栈的特点， 可以将字符串全部压栈， 再一次将各个字符串出栈， 得到原来字符串的逆置字符串， 将逆置字符串中的各个字符和原字符串中的各个字符
// 进行比较， 如果完全一致， 则为回文串
