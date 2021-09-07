package map_reduce

import (
	"bytes"
)

func MapFunc(input []string, mapFunc func(string) string) (output []string) {
	for _, v := range input {
		output = append(output, mapFunc(v))
	}
	return output
}

func FilterFunc(input []string, filter func(string) bool) (output []string) {
	for _, v := range input {
		if filter(v) {
			output = append(output, v)
		}
	}
	return
}

func ReduceFunc(input []string, reduce func(string) string) (output string) {
	var buff bytes.Buffer
	for _, v := range input {
		buff.WriteString(reduce(v))
	}
	return buff.String()
}
