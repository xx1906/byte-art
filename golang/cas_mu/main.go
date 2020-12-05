package main

import "sync"

func main() {
	mu := sync.Mutex{}
	//atomic.CompareAndSwapInt32(&m.state, 0, mutexLocked)
	mu.Lock()
	// mu.Lock() //再次调用将会触发死锁
	defer mu.Unlock()
	///
}
