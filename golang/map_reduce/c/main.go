package main

import (
	"byte-art/golang/map_reduce"
	"github.com/sirupsen/logrus"
	"strings"
)

func main() {
	var output = map_reduce.ReduceFunc(
		map_reduce.FilterFunc(
			map_reduce.MapFunc(
				[]string{"mvcc", "gcc", "hello", "world", "你好", "世", "界"},
				func(s string) string {
					return strings.ToUpper(s)
				},
			),
			func(s string) bool {
				return true
			},
		),
		func(s string) string {
			return s
		},
	)
	logrus.Info(output)
}
