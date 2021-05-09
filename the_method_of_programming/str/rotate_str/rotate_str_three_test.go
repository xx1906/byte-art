package rotate_str

import (
	"reflect"
	"testing"
)

func TestLeftRotateString(t *testing.T) {
	var in []byte = []byte{'a', 'b', 'c', 'd', 'e', 'f'}
	var out []byte = []byte{'d', 'e', 'f', 'a', 'b', 'c'}
	LeftRotateString(in, 3)
	if reflect.DeepEqual(in, out) {
		t.Log("ok ")
	} else {
		t.Error("err", string(in), string(out))
	}
}

func BenchmarkLeftRotateString(b *testing.B) {
	for i := 0; i < b.N; i++ {
		var in []byte = []byte{'a', 'b', 'c', 'd', 'e', 'f'}
		var out []byte = []byte{'d', 'e', 'f', 'a', 'b', 'c'}
		LeftRotateString(in, 3)
		if reflect.DeepEqual(in, out) {
			b.Log("ok ")
		} else {
			b.Error("err", string(in), string(out))
		}
	}
}
