package rotate_str

// 使用三步反转法解决

func ReverseStr(data []byte, from, to int) {
	for from < to {
		data[from], data[to] = data[to], data[from]
		from++
		to--
	}
}

func LeftRotateString(data []byte, m int) {
	m = m % len(data)
	//fmt.Println(string(data))

	ReverseStr(data, 0, m-1)
	ReverseStr(data, m, len(data)-1)
	ReverseStr(data, 0, len(data)-1)
	//fmt.Println(string(data))
}
