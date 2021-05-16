package str_palindrome

import "testing"

func TestIsPalindrome(t *testing.T) {
	type args struct {
		data []byte
	}
	tests := []struct {
		name    string
		args    args
		wantAns bool
	}{
		{name: "success", args: args{data: []byte("this is error")}, wantAns: false},
		{name: "success", args: args{data: []byte("oheeho")}, wantAns: true},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if gotAns := IsPalindrome(tt.args.data); gotAns != tt.wantAns {
				t.Errorf("IsPalindrome() = %v, want %v", gotAns, tt.wantAns)
			}
		})
	}
}
