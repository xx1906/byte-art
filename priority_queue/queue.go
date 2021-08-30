package priority_queue

// 优先级队列
type Comparator interface {
	Cmp(i, j int) bool
}
type PriorityQueue struct {
	data []interface{}
	cap  int
}

func NewPriorityQueue(cap int) *PriorityQueue {
	return &PriorityQueue{
		data: make([]interface{}, 0, cap),
		cap:  cap,
	}
}

func (c *PriorityQueue) Cmp(i, j int) bool {
	return c.data[i].(int) < c.data[j].(int)
}

// get the top data
// if data is empty, return nil
func (c *PriorityQueue) Top() interface{} {
	if len(c.data) == 0 {
		return nil
	}
	top := c.data[0]
	return top
}

// if cap is full, return false
// if push data, will heapify the data
func (c *PriorityQueue) Push(data interface{}) bool {
	if len(c.data) == cap(c.data) {
		return false
	}
	c.data = append(c.data, data)
	c.heapify()
	return true
}

// if len is empty, return the top data
// if pop the first data
// heapify the data
func (c *PriorityQueue) Pop() interface{} {
	if len(c.data) == 0 {
		return nil
	}
	top := c.data[0]
	c.data = c.data[1:]
	c.heapify()
	return top
}

// heapify the data
// resort the data
func (c *PriorityQueue) heapify() {
	var heap func(left, right int)
	heap = func(left, right int) {
		var dad, son = left, left*2 + 1
		for son <= right {
			if son+1 <= right && c.Cmp(son+1, son) {
				son += 1
			}
			if c.Cmp(dad, son) {
				return
			}
			c.data[dad], c.data[son] = c.data[son], c.data[dad]
			dad = son
			son = dad*2 + 1
		}
	}
	// if data is less 1
	if len(c.data) <= 1 {
		return
	}
	for i := len(c.data) / 2; i >= 0; i-- {
		heap(i, len(c.data)-1)
	}
}
