package main

import (
	"fmt"
	"math/rand"
)

// 冒泡排序算法
//
// 灵活点, 传入排序的函数
// @ Donald E.Knuth 说过, 冒泡算法除了它迷人的名字和导致了某些有趣的理论问题外, 似乎没有值得推荐的
//  难道还有更加有趣的算法么?
// 是的, 还有, 而且很棒
// 插入, 快排, 归并,堆排序 等等, 不等等, 插入排序? 我没听错吧? 你确定是插入排序嘛?
// 插入排序算法不是时间复杂度是O(n^2) 么? 那要它何用
// 虽然插入排序的时间复杂度特别高O(n^2)但是在排序小体量上特别快, 比如排序的长度是 100
// 看看 golang 排序的排序算法的实现ok了, 确实也是这样
func bubbleSort(arr []int, compare func(i, j int) bool) {
	for i := len(arr) - 1; i >= 0; i-- {

		for j := 1; j <= i; j++ {
			if /*arr[j] < arr[j-1]*/ compare(j, j-1) {
				arr[j], arr[j-1] = arr[j-1], arr[j]
			}
		}
	}
}

func main() {
	var arr = make([]int, 0)
	for i := 0; i < rand.Intn(10)+10; i++ {
		arr = append(arr, rand.Intn(100))
	}
	// 打乱数据, 其实这里是多次一举, 其实也是可以看看 Donald E.Knuth 的 Shuffle 算法
	rand.Shuffle(len(arr), func(i, j int) {
		arr[i], arr[j] = arr[j], arr[i]
	})

	bubbleSort(arr, func(i, j int) bool {
		return arr[i] > arr[j]
	})
	fmt.Println(arr)
}
