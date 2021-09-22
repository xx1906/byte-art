package r

import (
	"testing"
	"time"
)

func TestRand(t *testing.T) {
	tests := []struct {
		name string
		want int
	}{
		// TODO: Add test cases.
		{name: "11", want: 11},
		{name: "277363943098", want: 277363943098},
		{name: "11718085204285", want: 11718085204285},
		{name: "49720483695876", want: 49720483695876},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := Rand(); got != tt.want {
				t.Errorf("Rand() = %v, want %v", got, tt.want)
			} else {
				t.Log(got)
			}
		})
	}
}


func TestRandBuildWithSeed(t *testing.T) {
	for i:=0;i<3;i++ {
		withSeed := RandBuildWithSeed(int(time.Now().UnixNano()))()
		t.Log(withSeed)
	}
}