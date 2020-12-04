package main

import (
	"context"
	"fmt"
	"github.com/bwmarrin/snowflake"
	"github.com/go-redis/redis/v8"
	"os"
	"sync"
	"time"
)

type ErrNo int64

const (
	Empty           ErrNo  = iota // 没有对应的值  0
	Success                       // 操作成功     1
	UnknownError    ErrNo  = 999  // 未知错误     4
	randomKeyPrefix string = "random:"
	listKey                = "list_key"
)

var (
	rdb     *redis.Client
	sf      *snowflake.Node
	wg      sync.WaitGroup
	maxSize = 1024 / 2

	pullScript   = "local list_key = KEYS[1]; local random_str_key = KEYS[2]; local res = {}; local opt_value = redis.call('lpop', list_key); if (not opt_value) then res[1] = 2; res[2] = random_str_key; res[3] = 'nil'; return res; end redis.call('set', random_str_key, opt_value); res[1] = 1; res[2] = random_str_key; res[3] = opt_value; return res;"
	ackScript    = "local random_str_key = KEYS[1]; return redis.call('del',random_str_key);"
	cancelScript = "local list_key = KEYS[1]; local random_str_key = KEYS[2]; local res = {}; local opt_value = redis.call('get', random_str_key); redis.call('del', random_str_key); redis.call('rpush', list_key, opt_value); res[1] = 1; res[2] = random_str_key; res[3] = opt_value; return res;"
	file         *os.File
)

func init() {
	rdb = redis.NewClient(&redis.Options{
		Addr:     "127.0.0.1:6379",
		Password: "", // no password set
		DB:       0,  // use default DB 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
		PoolSize: 2,
	})

	if err := rdb.Ping(context.Background()).Err(); err != nil {
		panic(err)
	}
	var err error
	sf, err = snowflake.NewNode(0)
	if err != nil {
		panic(err)
	}

	fmt.Println("sf start:", sf.Generate().String())
	file, err = os.OpenFile("redis_ack_lab.log", os.O_CREATE|os.O_RDWR|os.O_TRUNC, 0766)
	if err != nil {
		panic(err)
	}

}

func main() {
	// file = os.Stdout
	wg.Add(3)
	go consumerACK()
	go consumerCancel()
	go product()

	wg.Wait()

	fmt.Fprintln(file, "whole work done!")
	file.Close()
}

// 消息消息并且 ack
func consumerACK() {
	defer wg.Done()

	for i := 0; i < maxSize; {
		randomKey := randomKeyPrefix + sf.Generate().String()
		result, err := rdb.Eval(context.Background(), pullScript, []string{listKey, randomKey}, nil).Result()
		if err != nil {
			fmt.Fprintln(file, "consumerACK: error ", err)
			continue
		}
		fmt.Fprintln(file, "consumerACK data ", result)
		result, err = rdb.Eval(context.Background(), ackScript, []string{randomKey}).Result()
		fmt.Fprintln(file, "consumerACK data ", result, " err ", err)
		// 消息消费成功
		if err == nil {
			i++
		}
		time.Sleep(time.Millisecond * 100 * 2)
	}

}

// 消息消息但是 cancel
func consumerCancel() {
	defer wg.Done()

	for i := 0; i < maxSize; i++ {
		randomKey := randomKeyPrefix + sf.Generate().String()
		result, err := rdb.Eval(context.Background(), pullScript, []string{listKey, randomKey}, nil).Result()
		if err != nil {
			fmt.Fprintln(file, "consumerCancel: error ", err)
			continue
		}
		fmt.Fprintln(file, "consumerCancel data ", result)
		result, err = rdb.Eval(context.Background(), cancelScript, []string{listKey, randomKey}).Result()
		fmt.Fprintln(file, "consumerCancel data ", result, " err ", err)
		time.Sleep(time.Millisecond * 100)
	}
}

// 生产消息
func product() {
	defer wg.Done()
	for i := 0; i < maxSize; i++ {
		if err := rdb.RPush(context.Background(), listKey, i).Err(); err != nil {
			fmt.Fprintf(file, "product: error:%s\n", err)
		} else {
			fmt.Fprintf(file, "product: success:%v\n", i)

		}
		time.Sleep(time.Millisecond * 100)
	}
}
