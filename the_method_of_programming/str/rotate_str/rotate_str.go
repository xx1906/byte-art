package rotate_str

func LeftShiftOne(str []byte) {
	if len(str) <= 0 {
		return
	}
	var firstChar = str[0]
	for i := 1; i < len(str); i++ {
		str[i-1] = str[i]
	}
	str[len(str)-1] = firstChar
}

func LeftRotateStringForce(str []byte, m int) {
	for ; m > 0; m-- {
		LeftShiftOne(str)
	}
}
