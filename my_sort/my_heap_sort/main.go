// 给定一个非空的整数数组，返回其中出现频率前 k 高的元素。
// 老实说: 一开始的时候最直接的想法就是统计使用 map 统计元素出现的次数, 然后再把 map 转为数组,
// 再对数组进行降序排序, 然后返回前k个就是对应的结果了
// 上面的思路当然可以解决这个问题了, 但是如果使用普通的排序, 可能会把不必要的结果也给排序了, 但是有可能用
// 不到对应的结果
//
// 但是小顶堆这个东西有一个性质, 每次调整都可以需要调整数组的最小值放到堆顶
// 所以使用堆排序, 可以更加好地解决这个问题

// 下面的是使用大顶堆的方式来排序的
package main

import "fmt"

//
func main() {
	fmt.Println(topKFrequent([]int{1, 2, 3, 4, 5, 3, 4, 3, 23, 5}, 2))
}

func topKFrequent(nums []int, k int) []int {
	var m = make(map[int]int, len(nums))
	// 统计每个数字出现的次数
	for _, v := range nums {
		value := m[v]
		m[v] = value + 1
	}
	// 将结果转为数组
	var data = make([]Mo, 0, len(m))
	for k, v := range m {
		data = append(data, Mo{Val: k, Time: v})
	}
	// 排序结果
	heapSort(data)
	// 选择最后的 k 个结果
	data = data[len(data)-k:]
	// 提取对应的值
	var ans = make([]int, k, k)
	for i := 0; i < len(data); i++ {
		ans[i] = data[i].Val
	}
	// fmt.Println(ans)
	return ans
}

// 调整堆, 大顶堆的方式
func adjustHeap(arr []Mo, start, end int) {
	var dad = start
	var son = dad*2 + 1
	for son <= end {
		if son+1 <= end && arr[son].Time < arr[son+1].Time {
			son++
		}

		if arr[dad].Time > arr[son].Time {
			return
		}

		arr[dad], arr[son] = arr[son], arr[dad]
		dad = son
		son = dad*2 + 1
	}
}

func heapSort(arr []Mo) {
	var ln = len(arr)
	for i := ln / 2; i >= 0; i-- {
		adjustHeap(arr, i, ln-1)
	}

	for i := ln - 1; i > 0; i-- {
		arr[i], arr[0] = arr[0], arr[i]
		adjustHeap(arr, 0, i-1)
	}
}

type Mo struct {
	Val  int // 值
	Time int // 次数
}
