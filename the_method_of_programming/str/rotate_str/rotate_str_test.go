package rotate_str

import (
	"reflect"
	"testing"
)

func TestLeftRotateStringForce(t *testing.T) {

	var in []byte = []byte{'a', 'b', 'c', 'd', 'e', 'f'}
	var out []byte = []byte{'d', 'e', 'f', 'a', 'b', 'c'}
	LeftRotateStringForce(in, 3)
	if reflect.DeepEqual(in, out) {
		t.Log("ok ")
	} else {
		t.Error("err", string(in), string(out))
	}
}
