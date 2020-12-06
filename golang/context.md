# golang context 中文全注释



```golang
// A Context carries a deadline, a cancellation signal, and other values across
// 一个上下文中包含截止时间, 一个取消信号, 和其他的值
// API boundaries.
//
// Context's methods may be called by multiple goroutines simultaneously.
// 上下文的方法可以提供给多个 goroutine 调用
type Context interface {
	// Deadline returns the time when work done on behalf of this context
	// should be canceled. Deadline returns ok==false when no deadline is
	// set. Successive calls to Deadline return the same results.
    
    // Deadline 函数返回应取消该上下文完成工作的时间. 如果没有设置截止日期，返回的 ok == false, 连续调用 
    // Deadline 函数返回相同的结果
	Deadline() (deadline time.Time, ok bool)

	// Done returns a channel that's closed when work done on behalf of this
	// context should be canceled. Done may return nil if this context can
	// never be canceled. Successive calls to Done return the same value.
	// The close of the Done channel may happen asynchronously,
	// after the cancel function returns.
	//
    // Done 函数返回一个通道， 当取消代表该上下文的工作时， 该通道已经关闭。 如果无法取消上下次上下文， 则完成可能返回 nil. 
    // 连续调用 Done 将返回相同的值。 在取消函数完成返回之后， 完成通道的关闭可能异步发生。
    
	// WithCancel arranges for Done to be closed when cancel is called;
	// WithDeadline arranges for Done to be closed when the deadline
	// expires; WithTimeout arranges for Done to be closed when the timeout
	// elapses.
	//
    // WithCancel 安排在调用 cancel 是关闭 Done；
    // WithDeadline 安排在截止日期到期是关闭 Done;
    // WithTimeout 安排在超时后关闭 Done;
    
	// Done is provided for use in select statements:
    
    // Done 被提供在 select 语句块中
	//
	//  // Stream generates values with DoSomething and sends them to out
	//  // until DoSomething returns an error or ctx.Done is closed.
	//  func Stream(ctx context.Context, out chan<- Value) error {
	//  	for {
	//  		v, err := DoSomething(ctx)
	//  		if err != nil {
	//  			return err
	//  		}
	//  		select {
	//  		case <-ctx.Done():
	//  			return ctx.Err()
	//  		case out <- v:
	//  		}
	//  	}
	//  }
	//
	// See https://blog.golang.org/pipelines for more examples of how to use
	// a Done channel for cancellation.
    // Done 通道用来取消
	Done() <-chan struct{}

	// If Done is not yet closed, Err returns nil.
	// If Done is closed, Err returns a non-nil error explaining why:
	// Canceled if the context was canceled
	// or DeadlineExceeded if the context's deadline passed.
	// After Err returns a non-nil error, successive calls to Err return the same error.
    // 如果 Done 没有关闭， Err函数返回 nil
    // 如果 Done 已经关闭， Err 函数返回 非空错误，原因如下： 如果上下文已经取消， 则取消； 如果上下文的截止日期已经过， 则 DeadlineExceeded
    // Err 函数返回一个非空错误后， 对 Err 的函数连续调用将返回同样的错误。 
	Err() error

	// Value returns the value associated with this context for key, or nil
	// if no value is associated with key. Successive calls to Value with
	// the same key returns the same result.
    // Value 函数返回的值与此键的上下文相关联的值； 如果没有值与键相关联， 则返回 nil. 使用相同的键连续调用Value 函数
    // 会返回相同的结果。
    // 
	//
	// Use context values only for request-scoped data that transits
	// processes and API boundaries, not for passing optional parameters to
	// functions.
	//
    // 将上下文的值用于传递过程和 API 边界请求范围数据， 而不用于将可选参数传递个函数。 

	// A key identifies a specific value in a Context. Functions that wish
	// to store values in Context typically allocate a key in a global
	// variable then use that key as the argument to context.WithValue and
	// Context.Value. A key can be any type that supports equality;
	// packages should define keys as an unexported type to avoid
	// collisions.
	//
    // 一个 key 标识上下文中的特定的值。 函数希望在 Context中存储值得函数通常会在全局变量中分配一个键， 然后将改key用在 context.WithValue
    // 和 context.Value 的参数， key 可以使任意支持相等类型； (代码）包 应该将该键定义为未导出的类型， 以免发生冲突。 

	// Packages that define a Context key should provide type-safe accessors
	// for the values stored using that key:
	//
    // 定义上下文的包应该为改 key 存储的值提供类型安全的访问器
    
	// 	// Package user defines a User type that's stored in Contexts.
    // 举个🌰 ：
    
	// 	package user
	//
	// 	import "context"
	//
	// 	// User is the type of value stored in the Contexts.
    //  User 类型是存储在上下文中的值类型
	// 	type User struct {...}
	//
	// 	// key is an unexported type for keys defined in this package.
	// 	// This prevents collisions with keys defined in other packages.
	// 	type key int
	//
    // key 是软件包中定义的未导出的类型， 这样可以防止与其他程序包中定义的 key 类型冲突。

	// 	// userKey is the key for user.User values in Contexts. It is
	// 	// unexported; clients use user.NewContext and user.FromContext
	// 	// instead of using this key directly.
    // 
    // userKey 是用户的关键字， 上下文中保存的是用户信息的值。 userKey 是未导出的， 
    // 客户端使用 user.NewContext 和 user.FromContext 而不是
    // 直接使用这个 key
    // 
	// 	var userKey key
	//
	// 	// NewContext returns a new Context that carries value u.
    //   NewContext 返回一个带有用户信息的 Context 
	// 	func NewContext(ctx context.Context, u *User) context.Context {
	// 		return context.WithValue(ctx, userKey, u)
	// 	}
	//
    // 
    //  
	// 	// FromContext returns the User value stored in ctx, if any.
    // FromContext 返回一个存储在 context 中 User 的值， 如果有的话。
	// 	func FromContext(ctx context.Context) (*User, bool) {
	// 		u, ok := ctx.Value(userKey).(*User)
	// 		return u, ok
	// 	}
	Value(key interface{}) interface{}
}

```

#### 错误信息
```golang
// Canceled is the error returned by Context.Err when the context is canceled.
// Canceled 是 Context.Err 取消上下文是返回的错误。

var Canceled = errors.New("context canceled")
```

```golang
// DeadlineExceeded is the error returned by Context.Err when the context's
// deadline passes.
// DeadlineExceeded 是上下文的截止日期过去时, Context.Err 返回的错误信息。
var DeadlineExceeded error = deadlineExceededError{}
```


##### emptyCtx 
```golang
// An emptyCtx is never canceled, has no values, and has no deadline. It is not
// struct{}, since vars of this type must have distinct addresses.
// emptyCtx 永远不会取消， 没有值， 也没有截止日期。 它不是 struct{}, 因为这个类型的 var 必须具有不同的地址。
type emptyCtx int
```


#####  background 和 todo 是用来不同的 emptyCtx
```golang
var (
	background = new(emptyCtx)
	todo       = new(emptyCtx)
)
```


##### Background 函数

```golang
// Background returns a non-nil, empty Context. It is never canceled, has no
// values, and has no deadline. It is typically used by the main function,
// initialization, and tests, and as the top-level Context for incoming
// requests.
// Background 函数返回的是一个非空的 上下文， 它永远不会被取消， 没有值， 也没有过期时间。 它通常由主要功能， 初始化和测试使用， 
// 并用作传入请求的顶级上下文。 
func Background() Context {
	return background
}
```


##### TODO 
```go
// TODO returns a non-nil, empty Context. Code should use context.TODO when
// it's unclear which Context to use or it is not yet available (because the
// surrounding function has not yet been extended to accept a Context
// parameter).
// 
// TODO 返回的是一个非空的 Context, 如果不清楚使用哪个上下文或者还没有可用时(因为尚未扩展周围的功能用来接收 Context 参数), 代码应该使用 context.TODO 
func TODO() Context {
	return todo
}
```



##### CancelFunc

```golang
// A CancelFunc tells an operation to abandon its work.
// A CancelFunc does not wait for the work to stop.
// A CancelFunc may be called by multiple goroutines simultaneously.
// After the first call, subsequent calls to a CancelFunc do nothing.
// CancelFunc 函数告诉一个操作放弃它的工作
// CancelFunc 不登台工作停滞
// CancelFunc 可以被多个 goroutine 同时调用 
// 在第一个 CancelFunc 被调用之后， 随后的调用什么都不做
type CancelFunc func()
```

#### WithCancel

```golang
// WithCancel returns a copy of parent with a new Done channel. The returned
// context's Done channel is closed when the returned cancel function is called
// or when the parent context's Done channel is closed, whichever happens first.
//
// Canceling this context releases resources associated with it, so code should
// call cancel as soon as the operations running in this Context complete.

// WithCancel 返回新的具有 Done 通道的父级的拷贝。 这个返回context.Done 通道会被关闭当返回的 cancel 函数被调用
// 或者当 父亲的 context.Done 通道被关闭时(以先发生的为准)。
// 
// 取消上下文将释放与其关联的资源， 因此在此上下文中运行的操作完成后, 代码应该立即调用 cancel 函数。
func WithCancel(parent Context) (ctx Context, cancel CancelFunc) {
	if parent == nil {
		panic("cannot create context from nil parent")
	}
	c := newCancelCtx(parent)
    
	propagateCancel(parent, &c)
	return &c, func() { c.cancel(true, Canceled) }
}
```

```golang
// newCancelCtx returns an initialized cancelCtx.
// newCancelCtx 函数返回一个初始化的 cancelCtx 
func newCancelCtx(parent Context) cancelCtx {
	return cancelCtx{Context: parent}
}
```

```golang
// goroutines counts the number of goroutines ever created; for testing.
// goroutines 变量用来计算曾创建的 goroutine 的数量， 测试用？
var goroutines int32
```


```golang
// propagateCancel arranges for child to be canceled when parent is.
// propagateCancel 函数安排在父级别取消时取消子级(就是父亲调用的时候, 也会调用 子)
func propagateCancel(parent Context, child canceler) {
	done := parent.Done()
	if done == nil {
		return // parent is never canceled
	}
    // 这一段代码是获取 parent 的 Done 的channel, 如果为空， 表示是 parent 上下文不具有可取消性

	select {
	case <-done:
		// parent is already canceled
		child.cancel(false, parent.Err())
		return
	default:
	}
    // 这一段代码， 表示执行权选判断一个 done 是否已经关闭， 如果已经关闭， 调用child 的 cancel 函数，
    // 如果没有关闭继续执行
    

	if p, ok := parentCancelCtx(parent); ok {
        // 如果 panrentCancelCtx 这一段代码返回 true
		p.mu.Lock()
		if p.err != nil {
			// parent has already been canceled
            // parent 已经遇到了错误， 调用 chidren 的cancel 函数
			child.cancel(false, p.err)
		} else {
			if p.children == nil {
                // 这里创建一个 map 等到 removechildren 的时候使用
				p.children = make(map[canceler]struct{})
			}
			p.children[child] = struct{}{}
		}
		p.mu.Unlock()
	} else {
        // 如果parentCancelCtx 返回 false
		atomic.AddInt32(&goroutines, +1)
        // goroutines 的数量加 1
    
		go func() {
            // 启动写成监听 Done 信号， 如果是 parent Done， 关闭 children 的
            // 如果是 child 的Done 无视
            // 所以每个 cancel 都会启动一个 goroutine 
			select {
            
			case <-parent.Done():
				child.cancel(false, parent.Err())
			case <-child.Done():
			}
		}()
	}
}

```

#### cancelCtxKey (最佳实践里面有提到的不导出的 key, 避免冲突)

```golang
// &cancelCtxKey is the key that a cancelCtx returns itself for.
// &cancelCtxKey 是 cancelCtx 返回自身的键。
var cancelCtxKey int
```

```golang
// parentCancelCtx returns the underlying *cancelCtx for parent.
// It does this by looking up parent.Value(&cancelCtxKey) to find
// the innermost enclosing *cancelCtx and then checking whether
// parent.Done() matches that *cancelCtx. (If not, the *cancelCtx
// has been wrapped in a custom implementation providing a
// different done channel, in which case we should not bypass it.)

// parentCancelCtx 函数返回的是父级的基础 *cancelCtx。 通过 调用 parent.Value(&cancelKey) 来查找最里面的 *cancelCtx， 
// 然后检查是否是 parent.Done() 匹配的 *cancelCtx。(如果没有， 则 *cancelCtx 已经包装在自定义实现中， 提供了不同的已完成渠道， 在这种情况下， 我们不应该绕过它)
func parentCancelCtx(parent Context) (*cancelCtx, bool) {
	done := parent.Done()
	if done == closedchan || done == nil {
		return nil, false
	}
    // 这一段代码表示如果: done 为可重用的已经关闭的 chanel 或者为空， 返回nil, false 


	p, ok := parent.Value(&cancelCtxKey).(*cancelCtx)
	if !ok {
		return nil, false
	}
    // 调用 Value 没有找到对应的 值， 也返回空

	p.mu.Lock()
	ok = p.done == done
	p.mu.Unlock()
    // 防止 done 被修改， 所以上锁
	if !ok {
		return nil, false
	}
	return p, true
}
```

##### closedchan 可重用的 closed channel
```golang
// closedchan is a reusable closed channel.
var closedchan = make(chan struct{})

func init() {
	close(closedchan)
}
```


##### removeChild 从一个 parent 移除上下文人
```golang
// removeChild removes a context from its parent.
func removeChild(parent Context, child canceler) {
	p, ok := parentCancelCtx(parent)
	if !ok {
		return
	}
	p.mu.Lock()
	if p.children != nil {
		delete(p.children, child)
	}
	p.mu.Unlock()
}
```


````golang
// A canceler is a context type that can be canceled directly. The
// implementations are *cancelCtx and *timerCtx.
type canceler interface {
	cancel(removeFromParent bool, err error)
	Done() <-chan struct{}
}
````

##### cancelCtx
```golang
// A cancelCtx can be canceled. When canceled, it also cancels any children
// that implement canceler.
// cancelCtx 可以被取消, 取消后， 它也会把所有实现 canceler 接口的所有子级取消
type cancelCtx struct {
	Context

	mu       sync.Mutex            // protects following fields
    // mu 保护 done, children, err 字段
	done     chan struct{}         // created lazily, closed by first cancel call
    // 懒惰地创建, 通过第一个取消调用关闭
	children map[canceler]struct{} // set to nil by the first cancel call
    // 有第一个调用 cancal 设置为nil
	err      error                 // set to non-nil by the first cancel call
    // 由第一个 cancel 调用设置为 not nil
}
```

```golang
// 如果 key == &cancelCtxKey 返回 c 本身, 否则递归调用
func (c *cancelCtx) Value(key interface{}) interface{} {
	if key == &cancelCtxKey {
		return c
	}
	return c.Context.Value(key)
}
```

```golang
// 返回chanel， 如果 done == nil， (并设置值)返回一个新建的
func (c *cancelCtx) Done() <-chan struct{} {
	c.mu.Lock()
	if c.done == nil {
		c.done = make(chan struct{})
	}
	d := c.done
	c.mu.Unlock()
	return d
}
```

```golang
// 返回 c.err 的拷贝
func (c *cancelCtx) Err() error {
	c.mu.Lock()
	err := c.err
	c.mu.Unlock()
	return err
}
```
##### stringer 接口
```golang
type stringer interface {
	String() string
}

func contextName(c Context) string {
	if s, ok := c.(stringer); ok {
		return s.String()
	}
	return reflectlite.TypeOf(c).String()
}

func (c *cancelCtx) String() string {
	return contextName(c.Context) + ".WithCancel"
}
```



##### cancel 方法

```golang
// cancel closes c.done, cancels each of c's children, and, if
// removeFromParent is true, removes c from its parent's children.
// cancel 方法关闭 c.done, 取消每一个子级, 如果 removeFromParent 为 true, 则从其父级的子级中删除 c.
func (c *cancelCtx) cancel(removeFromParent bool, err error) {
	if err == nil {
		panic("context: internal error: missing cancel error")
	}
	c.mu.Lock()
	if c.err != nil {
		c.mu.Unlock()
		return // already canceled
	}
	c.err = err
	if c.done == nil {
		c.done = closedchan
	} else {
		close(c.done) // close channel, 然后另外一个端收到了通知
	}
	for child := range c.children {
		// NOTE: acquiring the child's lock while holding parent's lock.
		child.cancel(false, err)
	}
	c.children = nil
	c.mu.Unlock()

	if removeFromParent {
		removeChild(c.Context, c)
	}
}
```

#### Deadline

```golang
// WithDeadline returns a copy of the parent context with the deadline adjusted
// to be no later than d. If the parent's deadline is already earlier than d,
// WithDeadline(parent, d) is semantically equivalent to parent. The returned
// context's Done channel is closed when the deadline expires, when the returned
// cancel function is called, or when the parent context's Done channel is
// closed, whichever happens first.
//
// WithDeadline 函数返回一个父级别 context 的拷贝, 并将截止日期调整为不迟于 d 的时间。 如果父项的截止日期早于 d, 则 WithDeadline 函数上语意
// 上等同于父项。 当截止日期到期， 调用返回的 cancel 函数或父上下文的 Done 通道为 true 时， 关闭返回的 上下文通道。
// 关闭, 以先发生的为准。
// Canceling this context releases resources associated with it, so code should
// call cancel as soon as the operations running in this Context complete.
//  取消此上下文将释放与其相关联的资源， 因此在此上下文中运行操作完成后， 代码应该立即调用 cancel 函数

func WithDeadline(parent Context, d time.Time) (Context, CancelFunc) {
	if parent == nil {
		panic("cannot create context from nil parent")
	}
	if cur, ok := parent.Deadline(); ok && cur.Before(d) {
		// The current deadline is already sooner than the new one.
		return WithCancel(parent)
	}
	c := &timerCtx{
		cancelCtx: newCancelCtx(parent),
		deadline:  d,
	}
	propagateCancel(parent, c)
	dur := time.Until(d)
	if dur <= 0 {
		c.cancel(true, DeadlineExceeded) // deadline has already passed
		return c, func() { c.cancel(false, Canceled) }
	}
	c.mu.Lock()
	defer c.mu.Unlock()
	if c.err == nil {
        // 调用  time.AfterFunc 函数， 启动定时任务
		c.timer = time.AfterFunc(dur, func() {
			c.cancel(true, DeadlineExceeded)
		})
	}
	return c, func() { c.cancel(true, Canceled) }
}
```

```golang
// A timerCtx carries a timer and a deadline. It embeds a cancelCtx to
// implement Done and Err. It implements cancel by stopping its timer then
// delegating to cancelCtx.cancel.
// timerCtx 带有计时器和deadline. 它嵌入了 cancelCtx 用来实现 Done 和 Err. 它通过停止计时器实现委派到 cancelCtx.cancel 来实现取消

type timerCtx struct {
	cancelCtx
	timer *time.Timer // Under cancelCtx.mu. // 通过 time.AfterFunc() 返回

	deadline time.Time
}
```


```golang
// WithTimeout returns WithDeadline(parent, time.Now().Add(timeout)).
//
// Canceling this context releases resources associated with it, so code should
// call cancel as soon as the operations running in this Context complete:
//
// 	func slowOperationWithTimeout(ctx context.Context) (Result, error) {
// 		ctx, cancel := context.WithTimeout(ctx, 100*time.Millisecond)
// 		defer cancel()  // releases resources if slowOperation completes before timeout elapses
// 		return slowOperation(ctx)
// 	}
// WithTimeout 返回 WithDeadline(parent, time.Now().Add(timeout))
func WithTimeout(parent Context, timeout time.Duration) (Context, CancelFunc) {
	return WithDeadline(parent, time.Now().Add(timeout))
}

```

#### withValue

```golang
// WithValue returns a copy of parent in which the value associated with key is
// val.
//
// Use context Values only for request-scoped data that transits processes and
// APIs, not for passing optional parameters to functions.
//
// The provided key must be comparable and should not be of type
// string or any other built-in type to avoid collisions between
// packages using context. Users of WithValue should define their own
// types for keys. To avoid allocating when assigning to an
// interface{}, context keys often have concrete type
// struct{}. Alternatively, exported context key variables' static
// type should be a pointer or interface.
//
//  提供的键必须是可以比较的， 并且不能是字符串或者其他内置类型的值， 以免冲突
// 使用 WithValue 时， 用户应该定义自己 key. 为了避免在分配给接口时进行分配， 上下文的 key 通常具有具体的结构体类型或者 导出的上下文变量的静态类型
// 应该为指针或者接口 
func WithValue(parent Context, key, val interface{}) Context {
	if parent == nil {
		panic("cannot create context from nil parent")
	}
	if key == nil {
		panic("nil key")
	}
	if !reflectlite.TypeOf(key).Comparable() {
		panic("key is not comparable")
	}
	return &valueCtx{parent, key, val}
}
```


```golang
// A valueCtx carries a key-value pair. It implements Value for that key and
// delegates all other calls to the embedded Context.
type valueCtx struct {
	Context
	key, val interface{}
}

// stringify tries a bit to stringify v, without using fmt, since we don't
// want context depending on the unicode tables. This is only used by
// *valueCtx.String().
func stringify(v interface{}) string {
	switch s := v.(type) {
	case stringer:
		return s.String()
	case string:
		return s
	}
	return "<not Stringer>"
}

func (c *valueCtx) String() string {
	return contextName(c.Context) + ".WithValue(type " +
		reflectlite.TypeOf(c.key).String() +
		", val " + stringify(c.val) + ")"
}

func (c *valueCtx) Value(key interface{}) interface{} {
	if c.key == key {
		return c.val
	}
	return c.Context.Value(key)
}

```


### 总结
* WithValue 提供的 key 必须是可比较的， 并且不能是内置类型(避免冲突)， 比如 字符串等
* cancelCtx 的实现通过起一个 goroutine 来监听一个 channel， 然后 调用 cancel 方法时， 调用 clone(c.done) c.done 为一个 channel 
* WithTimeout 和 WithDeadline 通过开一个 timer = time.AfterFunc() 来定时 调用 cancel 函数 
* WithTimeout  WithDeadline 通过 内嵌 cancelCtx 来实现 cancel 