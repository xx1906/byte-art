package main

import (
	demo "byte-art/encryption/protobuffer/proto"
	"fmt"
	"github.com/golang/protobuf/proto"
	"reflect"
	"runtime"
)

func main() {

	var fns = make([]func(), 0)
	fns = append(fns, encTest1, encTest2, encTest3, encTest4, encTest5)
	for _, v := range fns {
		fmt.Print(runtime.FuncForPC(reflect.ValueOf(v).Pointer()).Name())
		v()
	}

}

func encTest1() {
	var (
		enc   []byte
		err   error
		test1 demo.Test1
	)
	test1.A = 255  // varint 编码 key: 1 << 3 | 0 = ; value: 11111111
	test1.B = 127  // zigzag32 编码 key: 2 << 3 | 0 = 10000; value: 11111110
	test1.C = 1023 // zigzag64 编码 key: 3 << 3 | 0 = 11000; value: 11111110
	test1.D = 256  //
	test1.E = 1023
	test1.F = 3.14
	test1.G = 3.14
	test1.H = []int32{1, 3, 7}
	test1.I = map[int32]int32{1: 233, 2: 255}
	enc, err = proto.Marshal(&test1)
	fmt.Printf("enc:%b\n", enc)
	_ = err

}

func encTest2() {
	var (
		enc   []byte
		err   error
		test2 demo.Test2
	)
	test2.A = 233
	test2.B = true
	enc, err = proto.Marshal(&test2)
	fmt.Printf("enc:%b\n", enc)
	_ = err
}

func encTest3() {
	var (
		enc   []byte
		err   error
		test3 demo.Test3
	)
	enc, err = proto.Marshal(&test3)
	fmt.Printf("enc:%b\n", enc)
	_ = err
}

func encTest4() {
	var (
		enc   []byte
		err   error
		test4 demo.Test4
	)
	enc, err = proto.Marshal(&test4)
	fmt.Printf("enc:%b\n", enc)
	_ = err
}

func encTest5() {
	var (
		enc   []byte
		err   error
		test5 demo.Test5
	)
	enc, err = proto.Marshal(&test5)
	fmt.Printf("enc:%b\n", enc)
	_ = err
}
