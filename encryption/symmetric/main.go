// @author 我的我的
// @date
// @desc rsa 算法的简单实现
package main

import (
	"fmt"
	"math"
)

type (
	// 64 位 平台 64 位
	// 整 64 位以上长度的需要自己实现了, 参考 IEEE[triple E]754
	PublicKey struct {
		E int
	}

	PrivateKey struct {
		D int
	}

	MiniRSA struct {
		PublicKey  PublicKey  `json:"public_key"`
		PrivateKey PrivateKey `json:"-"`
		N          int        `json:"n"`
	}
)

func main() {
	var p, q, e, x, y int = 47, 71, 79, 0, 0
	// 判断 p q 是否为素数
	if !(isPrime(p) && isPrime(q)) {
		panic(fmt.Sprintf("isPrime(%d) = %v and isPrime(%d) = %v need isPrime", p, isPrime(p), q, isPrime(q)))
	}
	N := p * q
	setaN := (p - 1) * (q - 1)
	if gcd(e, setaN) > 1 || N < e {
		panic("e 要 与 setaN 互质 并且小于 N")
	}
	extGCD(e, setaN, &x, &y)
	// 得出公钥和私钥
	miniRSA := MiniRSA{PrivateKey: PrivateKey{D: x}, PublicKey: PublicKey{E: e}, N: N}

	fmt.Printf("miniRSA %#v\n", miniRSA)
	plainText := 2020
	cipherText := enc(plainText, miniRSA.PublicKey.E, miniRSA.N)
	fmt.Println("明文:", plainText, "\t密文: ", cipherText, "\t解密:", dec(cipherText, miniRSA.PrivateKey.D, miniRSA.N))

	plainText = 233
	// 加密
	cipherText = enc(plainText, miniRSA.PublicKey.E, miniRSA.N)
	fmt.Println("明文:", plainText, "\t密文: ", cipherText, "\t解密:", dec(cipherText, miniRSA.PrivateKey.D, miniRSA.N))

}

// 求最大公约数
func gcd(a, b int) int {
	if a == 0 || b == 0 {
		return 0
	}
	// 直到 a == b 为止
	for a != b {
		if a > b {
			a -= b
		} else {
			b -= a
		}
	}
	return a
}

// 拓展的欧几里得算法
func extGCD(a, b int, x, y *int) int {
	if b == 0 {
		*x = 1
		*y = 0
		return a
	}
	d := extGCD(b, a%b, x, y)
	*x, *y = *y, *x-a/b*(*y)
	return d
}

// 判断素数
func isPrime(prime int) bool {
	// 1 不是素数
	flag := true
	for i := 2; i <= int(math.Sqrt(float64(prime))); i++ {
		if prime%i == 0 {
			fmt.Println(prime, i)
			flag = false
			break
		}
	}
	return flag
}

// 使用公钥加密
// @param plainText 明文
// @param e 和参数 n 够成公钥
// @param n 和参数 e 够成私钥
func enc(plainText int, e, n int) int {
	return powMod(plainText, e, n)
}

// 使用私钥来解密
func dec(cipherText int, d, n int) int {
	return powMod(cipherText, d, n)
}

// 快速幂问题
func quickPow(a, b int) int {
	// 不考虑小于零
	if b == 0 {
		return 1
	}
	ans := quickPow(a, b/2)
	ans *= ans
	if b%2 == 1 {
		ans *= a
	}
	return ans

}

// 同余
func powMod(a, n, m int) int {
	// 不考虑小于零
	if n == 0 {
		return 1
	}

	x := powMod(a, n/2, m)
	ans := x * x % m
	if n%2 == 1 {
		ans = ans * a % m
	}
	return ans
}

// end
