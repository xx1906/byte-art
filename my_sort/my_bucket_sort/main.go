// 桶排序的基本思想
package main

import "fmt"

// 桶排序不是稳定的排序算法, 因为在把数据放入桶的时候就乱了
// 不过也是可以做到稳定的, 在计数的时候使用把数据放入到队列里面去可以做到有序了
// 缺点就是空间的消耗有点大了(可能不仅仅是空间消耗有点大吧)
//
func bucketSort(arr []int) {
	var (
		max int
	)
	/*****
	  这里没有考虑到出现负数的情况, 如果有负数可以做对应的函数映射就好了
	*/
	for _, v := range arr {
		if v > max {
			max = v
		}
	}
	// 申请的空间大一点
	var book = make([]int, max+1)

	// 遍历待排序的数组, 找到对应的下标, 不断地在对应的下标值加 1
	for _, v := range arr {
		book[v]++
	}

	var idx = 0
	// 把数组排序
	for i := 0; i < len(book); i++ {
		for book[i] > 0 {
			arr[idx] = i
			book[i]--
			idx++
		}
	}
}

func main() {
	arr := []int{23, 43, 3, 45, 54, 54, 90, 34, 65}
	bucketSort(arr)
	fmt.Println(arr)
}

// 上面的实现空间太大了, 怎么办?
//
// 如果把上面待排序的数据分组如何?
// 怎么分组?
//
// 比如上面的数字是十进制, 按照个位, 十位, 百位, 千位 ....上面个的数值划分数据
