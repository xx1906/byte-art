package priority_queue

import (
	"fmt"
	"testing"
)

var (
	p *PriorityQueue
)
type P struct {
	p PriorityQueue
}
func TestMain(m *testing.M) {
	p = NewPriorityQueue(16, )
	m.Run()
	fmt.Println(p)
}
func TestNewPriorityQueue(t *testing.T) {

}

func TestPriorityQueue_Pop(t *testing.T) {
}

func TestPriorityQueue_Push(t *testing.T) {
	p.Push(12)

	p.Push(132)
	p.Push(2)
	p.Push(89432)
	p.Push(1432)
	t.Log(p.Pop())
	t.Log(p.Pop())
	t.Log(p.Pop())
	t.Log(p.Pop())
	t.Log(p.Top())
	t.Log(p.Top())

}

func TestPriorityQueue_Top(t *testing.T) {

}

func TestPriorityQueue_heapify(t *testing.T) {

}
