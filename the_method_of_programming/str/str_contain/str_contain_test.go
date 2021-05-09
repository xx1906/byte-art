package str_contain

import "testing"

func TestStrContainForce(t *testing.T) {
	var s1  = "abcef"
	var s2 = "ffex"
	var ans = StrContainForce(s1,s2)
	if ans {
		t.Log("s1 ", s1, ", s2",s2)
	}else {
		t.Error("pass", ans )
	}
}
