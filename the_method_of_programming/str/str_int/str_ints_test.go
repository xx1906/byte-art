package str_int

import "testing"

func TestStrToInt(t *testing.T) {
	toInt := StrToInt([]byte("  +23333"))
	t.Log(toInt)
	toInt = StrToInt([]byte("  -23333"))
	t.Log(toInt)
}
