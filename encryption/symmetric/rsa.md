# golang [简单的]实现 RSA 的全过程

### RSA 介绍

RSA 由 Ron Rivest, Adi Shamir 和 Lennard Adleman 在 1977 年提出的算法。

RSA 加密算法是一种非对称机密算法, 对极大整数做因数分解的难度决定了RSA算法的可靠性。

### 公钥与私钥的产生

1. 选取两个大的质数： p 和 q
2. 计算 n = p * q
3. 计算小于 n 并且与 n 互质的整数的个数, 使用欧拉公式可以计算 $\omicron(n) =( p - 1)*( q - 1)$
4. 随机选择**加密的私钥** e, 使 1 < e < $\omicron(n)$, 并且 e 与 $\omicron(n)$ 互质
5. 使用欧几里得算法计算**解密私钥** d, 使 ed = 1 (mod($\omicron(n)$))
6. 公钥对 {e,n} PK(需要公开出去的);  私钥对 {e,n} 为 SK(**切不可泄露**)



### RSA 如何加密

密文 = $明文^{e}$*m**o**d* *n*
通过公式, 可以知道 RSA 的密文是通过明文的 e 次方再对 n 进行取模得到的。 这个加密过程只用到乘法和除法运算。
公钥对 = {e, n}

### RSA 如何解密
明文 = $密文^{d}$ mod  n
原理是: 密文的 d 次方， 然后对 n 取模得到明文。
私钥对 = {d,n}



### 素数

素数指在大于 1 自然数中， 除了 1 和该数本身外， 无法被其他自然数整除的数。(1 不是素数)

```golang
func isPrime(prime int) bool {
    flag := true
    for i := 2; i <= int(math.Sqrt(float64(prime))); i++ {
        if prime%i == 0 {
            flag = false
            break
        }
    }
    return flag
}
```

### 最大公因数

指的是能够整除多个整数的最大正整数

```golang
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
```



### 扩展欧几里得算法

裴蜀定理: 给定二个整数 a、b, 必存在整数 x、y 使得 ax + by = gcd(a,b), 其中 gcd(a,b) 结果为 a 和 b 的最大公约数。

```golang
func extGCD( a , b int , x , y * int ) int {
    if b == 0 {
        *x = 1
        *y = 0
        return a
    }
    d := extGCD(b, a%b, x, y)
    *x, *y = *y, *x-a/b*(*y)
    return d
}
```



### 同余

同余是数论上一种等价关系。 当两个整数除以一个正整数， 若得相同的余数， 则二整数同余。 

举个🌰 ： 

**3 % 4 ≡ 5 % 4**



快速幂同模

```golang
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
```



### 两个数互质

```golang
if gcd(a,b ) == 1 {
  fmt.Println("a b 互质")
}
```

### 例子
例子:  令 p = 47, q = 71, 求用到的 RSA 算法加密用到的公钥和私钥

> p 与 q 都是较大的质数

计算过程：

1. 计算 n = p * q = 47 * 71  = 3337;
2. 计算小于 n 并且与 n 互质的个数 $\omicron(n) = (p-1) * (q-1)$ ; $\omicron(n) = 46 * 70$ = 3220;
3. 随机选择 e = 79 (e需要满足 1 < e < n,并且 e 与 $\omicron(n)$ 互质);
4. 则私钥 d 满足: 79 * d mod 3220  = 1;

**由于 e 与 n 互质, 所以满足 79 * d - k * 3220 = 1**

使用辗转相除法计算 d;

a. 式子(4) 可以表示为 79 * d - 3220 * k  = 1 (其中 k 为正整数);

b. 将 3220 对 79 取模得到余数 60 代替 3220， 则变成 79 * d - 60 *k = 1;

c. 同理， 将 79 对 60 取模得到余数 19 代替 79， 则变成 19 * d - 60 *k = 1;

d. 同理， 将 60 对 19 取模得到余数 3 代替 60, 则变成 19 * d - 3 * k = 1;

e. 同理， 将 19 对 3 取模得到余数 1 代替 19, 则变成 1 * d - 3 * k =1;



当 d 的系数变成了 1 的时候, (d - 3 * k = 1)

令 k = 0, 代入式子 (e) 中, 得到 d =1;

将 d = 1 代入式子(d) 中， 得到 k = 6;

将 k = 6 代入式子(c) 中， 得到 d = 19;

将 d = 19 代入式子(b) 中， 得到 k =25;

将 k =25 代入式子(a) 中， 得到  d = 1019;

最后的 d 就是算出来的 d.

> 整个过程使用递归的思想可以很好地计算出最后的 d



### 实现

```golang
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

```



### 总结

1. RSA 算法的求公钥对和私钥对的整个过程
2. RSA 使用公钥加密
3. RSA 算法使用私钥解密
4. 可以同余定理来避免溢出问题
5. 使用快速幂思想来加速加密和解密的过程



### 参考

1. [RSA加密演算法](https://zh.wikipedia.org/wiki/RSA%E5%8A%A0%E5%AF%86%E6%BC%94%E7%AE%97%E6%B3%95#%E5%85%AC%E9%92%A5%E4%B8%8E%E7%A7%81%E9%92%A5%E7%9A%84%E4%BA%A7%E7%94%9F)
2. [扩展欧几里得算法](https://zh.wikipedia.org/wiki/%E6%89%A9%E5%B1%95%E6%AC%A7%E5%87%A0%E9%87%8C%E5%BE%97%E7%AE%97%E6%B3%95)
3. [最大公因数](https://zh.wikipedia.org/wiki/%E6%9C%80%E5%A4%A7%E5%85%AC%E5%9B%A0%E6%95%B8)
4. [同余](https://zh.wikipedia.org/wiki/%E5%90%8C%E9%A4%98)
5. [费马小定理](https://zh.wikipedia.org/wiki/%E8%B4%B9%E9%A9%AC%E5%B0%8F%E5%AE%9A%E7%90%86)
6. [素数](https://zh.wikipedia.org/wiki/%E8%B4%A8%E6%95%B0)

