package main

import (
	"fmt"
	"math/rand"
	"time"
)

// 在冒泡排序那里提了一点快排
//
func quickSort(arr []int, lo, hi int, compare func(i, j int) bool) {
	// 递归的出口, 这一步, 千万别忘记了
	if lo > hi {
		return
	}
	var i, j, pov = lo, hi, arr[lo]
	for i < j {
		for /* arr[j] >= pov*/ compare(j, lo) && i < j {
			j--
		}
		for /*arr[i] <= pov*/ !compare(i, lo) && i < j {
			i++
		}
		if i != j {
			arr[i], arr[j] = arr[j], arr[i]
		}
	}

	// 调整基准数字的位置
	arr[i], arr[lo] = pov, arr[i]
	// 排序归位之后基准数字的左边
	quickSort(arr, lo, i-1, compare)
	// 排序归位之后基准数字的右边
	quickSort(arr, i+1, hi, compare)
}

// 快速排序的第二个版本
func quickSortV2(arr []int, lo, hi int, compare func(i, j int) bool) {
	if lo > hi {
		return
	}
	// 划分基准数字的位置
	pos := partition(arr, lo, hi, compare)
	quickSortV2(arr, lo, pos-1, compare)
	quickSortV2(arr, pos+1, hi, compare)
}

// 划分基准数组的位置
func partition(arr []int, lo, hi int, compare func(i, j int) bool) (pos int) {
	var i, j = lo, hi
	var pov = arr[lo]
	for i < j {
		for i < j && compare(j, lo) {
			j--
		}
		for i < j && !compare(i, lo) {
			i++
		}
		if i != j {
			arr[i], arr[j] = arr[j], arr[i]
		}
	}
	// 把基准数字放到它应该在的位置,
	arr[i], arr[lo] = pov, arr[i]
	return i
}

// knuth 洗牌算法
func shuffle(data []int) {
	rand.Seed(time.Now().UnixNano())
	for i := len(data) - 1; i >= 0; i-- {
		var index = rand.Int() % (i + 1)
		data[index], data[i] = data[i], data[index]
	}
}

// 快速排序算法
func quickSortV3(data []int, left, right int) {
	var partition func(data []int, left, right int) int
	// 分区函数的实现
	partition = func(data []int, left, right int) int {
		piv := data[right]
		var i, j = left, left
		for j < right {
			if data[j] < piv {
				data[i], data[j] = data[j], data[i]
				i++
			}
			j++
		}
		// 交换基准数字的位置
		data[i], data[right] = data[right], data[i]
		return i
	}
	if left >= right {
		return
	}

	piv := partition(data, left, right)
	// 继续排序基准数字左边和右边的数组
	quickSortV3(data, left, piv-1)
	quickSortV3(data, piv+1, right)
}

func main() {
	var arr = make([]int, 0)
	rand.Seed(time.Now().UnixNano())
	for i := 0; i < rand.Intn(10)+10; i++ {
		arr = append(arr, rand.Intn(100))
	}
	// 打乱数据, 其实这里是多次一举, 其实也是可以看看 Donald E.Knuth 的 Shuffle 算法
	rand.Shuffle(len(arr), func(i, j int) {
		arr[i], arr[j] = arr[j], arr[i]
	})
	quickSort(arr, 0, len(arr)-1, func(i, j int) bool {
		return arr[i] > arr[j]
	})
	fmt.Println(arr)

	quickSortV2(arr, 0, len(arr)-1, func(i, j int) bool {
		return arr[i] < arr[j]
	})
	//  fmt.Println("quickSortV2: ",arr)
	shuffle(arr)
	fmt.Println(arr)
	quickSortV3(arr, 0, len(arr)-1)
	fmt.Println(arr)

}
