package main

import (
	"errors"
	"fmt"
	"math"
)

// 拉个朗日查找,
func LagrangeSearch(nums []int, obj int) (idx int, err error) {
	var i int = 0
	var lo, hi = 0, len(nums) - 1
	for lo <= hi {
		i++
		fmt.Println(i)
		// 拉格朗日中值查找算法
		leftV := float64(obj - nums[lo])
		allV := float64(nums[hi] - nums[lo])
		diff := float64(hi - lo)
		pos := int(float64(lo) + diff*(leftV/allV))

		if pos < 0 || pos >= len(nums) {
			return -1, errors.New("not found")
		}

		if nums[pos] == obj {
			return pos, nil
		}

		if nums[pos] > obj {
			hi = pos - 1
		} else {
			lo = pos + 1
		}

	}
	return -1, errors.New("not found")
}

func main() {
	var nums = make([]int, 0)
	for i := 0; i < math.MaxInt16; i++ {
		nums = append(nums, i)
	}

	fmt.Println(LagrangeSearch(nums, math.MaxInt16-1))
	fmt.Println(LagrangeSearch(nums, math.MinInt64))
	fmt.Println(LagrangeSearch(nums, math.MaxInt16/3))
	fmt.Println(math.MaxInt16 / 3)
}
