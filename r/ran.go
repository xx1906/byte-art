package r

// https://www.cnblogs.com/xkfz007/archive/2012/08/25/2656893.html
// 伪随机数的产生

//__int64 rand ()
//{
//static __int64 r = 0;
//const __int64 a = 25214903917;
//const __int64 c = 11;
//const __int64 m = 1 << 48;
//r = (r * a + c) % m;
//return r;
//}

func init() {
	//fmt.Println(math.MaxInt32  == 25214903917)
}
func RandBuild() func() int {
	var r = 0
	const (
		a = 25214903917
		c = 11
		m = 1 << 48
	)

	return func() int {
		r = (r*a + c) % m
		return r
	}
}

var Rand = RandBuild()
var RandWithSeed = RandBuildWithSeed(122)

func RandBuildWithSeed(seed int) func() int {
	var r = seed
	const (
		a = 25214903917
		c = 11
		m = 1 << 48
	)

	return func() int {
		r = (r*a + c) % m
		return r
	}
}
