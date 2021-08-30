package singleflight

import (
	"context"
	"fmt"
	"math/rand"
	"sync"
	"testing"
	"time"
)

var g = NewGroup()

func todo(ctx context.Context) func() (interface{}, error) {
	return func() (interface{}, error) {
		time.Sleep(time.Millisecond * time.Duration(rand.Int63n(100)+100))
		return "todo:" + fmt.Sprintf("%v", ctx.Value("key")) + "," + time.Now().String(), nil
	}
}

func TestGroup_DoChan(t *testing.T) {
	wg := sync.WaitGroup{}
	for i := 0; i < 5999; i++ {
		wg.Add(1)
		go func(i int) {
			defer wg.Done()
			t.Log(<-g.DoChan("todo",
				todo(context.WithValue(context.TODO(),
					"key", "job"+fmt.Sprintf("--%d", i)))))
		}(i)
	}
	wg.Wait()

	//time.Sleep(time.Second*2)
	for i := 0; i < 908; i++ {
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
