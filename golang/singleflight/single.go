package singleflight

import (
	"fmt"
	"sync"
	"sync/atomic"
)

/***********************************************************************************************************************

func init() {
	var g = NewGroup()
	key := "hello world"
	fmt.Println(g.DoCall(key, func(ctx context.Context) func() (interface{}, error) {
		return func() (interface{}, error) {
			return key, nil
		}
	}(context.Background())))
	defer g.DeleteJob(key)
}

***********************************************************************************************************************/
type Result struct {
	Value interface{}
	Err   error
}

type Group struct {
	//noCopy
	mu     sync.Mutex       // mutex, use in modify the single field
	single map[string]*call // single job
}

func NewGroup() *Group {
	return &Group{}
}

type call struct {
	result interface{}   // execute job
	err    error         // error information
	done   chan struct{} // if job is done, close this chan
	refJob int32         // job ref
}

func (c *Group) DoChan(key string, execute func() (interface{}, error)) <-chan Result {
	c.mu.Lock()
	defer c.mu.Unlock()
	r := make(chan Result)

	var ca *call
	var ok bool
	if ca, ok = c.single[key]; !ok {
		ca = &call{done: make(chan struct{})}
		// if single is nil
		if c.single == nil {
			c.single = make(map[string]*call)
		}
		c.single[key] = ca
		// execute job
		go func() {
			defer func() {
				if err := recover(); err != nil {
					fmt.Errorf("execute panic:%s", err)
				}
			}()
			ca.result, ca.err = execute()
			ca.done <- struct{}{}
			close(ca.done)
		}()
	}
	// add job ref
	atomic.AddInt32(&ca.refJob, 1)

	go func() {
		// wait for job done
		<-ca.done
		r <- Result{Err: ca.err, Value: ca.result} // return job result
		close(r)
		c.releaseJob(key, ca)
	}()

	return r
}

func (c *Group) DoCall(key string, execute func() (interface{}, error)) Result {
	return <-c.DoChan(key, execute)
}

// release
func (c *Group) releaseJob(key string, ca *call) {
	c.mu.Lock()
	defer c.mu.Unlock()
	if atomic.AddInt32(&ca.refJob, -1) == 0 {
		delete(c.single, key)
	}
}
