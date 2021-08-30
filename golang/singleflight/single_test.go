package singleflight

import (
	"context"
	"fmt"
	"sync"
	"testing"
	"time"
)

var g = NewGroup()

func todo(ctx context.Context) func() (interface{}, error) {
	return func() (interface{}, error) {
		time.Sleep(time.Second)
		return "todo:" + fmt.Sprintf("%v", ctx.Value("key")) + "," + time.Now().String(), nil
	}
}

func TestGroup_DoChan(t *testing.T) {
	wg := sync.WaitGroup{}
	for i := 0; i < 5; i++ {
		wg.Add(1)
		go func(i int) {
			defer wg.Done()
			t.Log(<-g.DoChan("todo",
				todo(context.WithValue(context.TODO(),
					"key", "job"+fmt.Sprintf("--%d", i)))))
		}(i)
	}
	wg.Wait()
}
