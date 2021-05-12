package str_permutation

import "fmt"

// 字符串全排列的递归算法

func CalcAllPermutation(data []byte, from, to int) {
	if to < 1 {
		return
	}
	if from == to {
		for i := 0; i <= to; i++ {
			fmt.Printf(", %c", data[i])
		}
		fmt.Println()
	} else {
		for j := from; j <= to; j++ {
			data[j], data[from] = data[from], data[j]
			CalcAllPermutation(data, from+1, to)
			data[j], data[from] = data[from], data[j]

		}
	}
}
