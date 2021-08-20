package main

import (
	"fmt"
	"math/rand"
	sort2 "sort"
	"time"
)

func mergeSort(data []int, left, right int, temp []int) {
	// 递归的出口
	if left >= right {
		return
	}
	var mid = (right + left) / 2
	// 分开合并两个部分
	mergeSort(data, left, mid, temp)
	mergeSort(data, mid+1, right, temp)
	// 合并有序数组
	mergeTwo(data, left, mid, right, temp)
}

// 合并两个有序的数组， 第一个数组的下标是 [left, mid], 第二个数组开头的下标是 [mid + 1, right]
func mergeTwo(data []int, left, mid, right int, temp []int) {
	var i, j int = left, mid + 1
	var tempIndex = 0
	// 合并两个数组的同时有序的部分
	for i <= mid && j <= right {
		if data[i] <= data[j] {
			temp[tempIndex] = data[i]
			i++
		} else {
			temp[tempIndex] = data[j]
			j++
		}

		tempIndex++
	}
	// 合并剩余的部分
	for i <= mid {
		temp[tempIndex] = data[i]
		i++
		tempIndex++
	}

	for j <= right {
		temp[tempIndex] = data[j]
		j++
		tempIndex++
	}

	tempIndex = 0
	// 将数据从 temp 中搬移到 data 中
	for left <= right {
		data[left] = temp[tempIndex]
		tempIndex++
		left++
	}
}

func sort(data []int) {
	var temp = make([]int, len(data))
	mergeSort(data, 0, len(data)-1, temp)
}

func main() {
	rand.Seed(time.Now().UnixNano())
	var cnt = rand.Intn(32)
	fmt.Println(cnt)
	var data = make([]int, 0, cnt)
	for i := 0; i < cnt; i++ {
		data = append(data, rand.Intn(cnt*8))
	}
	fmt.Println(data)
	sort(data)
	fmt.Println(sort2.IntsAreSorted(data))
	fmt.Println(data)
}
