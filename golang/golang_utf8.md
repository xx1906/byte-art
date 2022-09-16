# golang 处理 utf8 字符串

golang 中的 string 类型, 底层是一个 byte 数组, 在我们想要通过下标的方式来获取 utf8 编码的内容时, 并不一定能够正确获取到我们想要获取的内容, 比如说

```golang
var s = "hello 字符串"
// 此时, 我们获取到的字符并不是一个有效的 utf8 编码的字符
var lastChar = []byte(s)[len([]byte(s)) -1]
```

```golang
package main

import "fmt"

func main() {
 var s = "hello utf8 字符串"
 fmt.Println([]rune(s))
 fmt.Println(string([]rune(s)))
 fmt.Println("输出字符串中包含utf8 编码字符的长度:", len([]rune(s)))
 fmt.Println("输出字符串中包含字节的长度:", len(s))
 fmt.Print("以 utf8 的方式切字符:")
 // 通过将字符串转为 []rune 类型再进行切分字符串
 for _, v := range []rune(s) {
  fmt.Print(string(v))
 }
 fmt.Println()
 var b = []rune(s)[len([]rune(s))-1]
 fmt.Println(string(b))
 for _, v := range s {
  fmt.Print(string(v))
 }

 fmt.Println()
 var b2 = []byte(s)[len([]byte(s))-1]
 fmt.Println(string(b2))
}
```
