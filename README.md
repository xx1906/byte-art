## byte-art 

社区著名的[err](http://stackoverflow.com/a/29138676/3158232)问题, 我之前也遇到了
```go
package main

type shower interface {
  getWater() []shower
}

type display struct {
  SubDisplay *display
}

func (d display) getWater() []shower {
  return []shower{display{}, d.SubDisplay}
}

func main() {
  // SubDisplay will be initialized with null
  s := display{}
  // water := []shower{nil}
  water := s.getWater()
  for _, x := range water {
    if x == nil {
      panic("everything ok, nil found")
    }

    //first iteration display{} is not nil and will
    //therefore work, on the second iteration
    //x is nil, and getWater panics.
    x.getWater()
  }
}
```

> 怎么判断解决这个问题
```go
// 答案也是来自社区
package main

import (
	"fmt"
	"io"
	"reflect"
)

type reader struct{}

func (*reader) Read(p []byte) (int, error) { return 0, nil }

func main() {
	var r io.Reader = (*reader)(nil)

	fmt.Println(r, r == nil)
	fmt.Println(reflect.ValueOf(r).IsNil())

```