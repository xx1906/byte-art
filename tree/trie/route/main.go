// package router 包

// 在这里主要初始化了路由的
package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"regexp"
	"strings"
	"time"
)

func main() {
	err := http.ListenAndServe(":8080", &g)
	if err != nil {
		panic("err" + err.Error())
	}
}

var (
	m = regexp.MustCompile(`([a-z]+)`)
)

// 这里使用 trie 树来保存路由
type Node struct {
	path       string
	normal     map[string]*Node // 普通的路由
	isRegexp   bool
	reg        *regexp.Regexp
	regexpPath []*Node // 正则路由
	callback   map[string]CallBack
}

// 使用 context 来传递参数
type Context struct {
	http.ResponseWriter
	*http.Request
	Param map[string]interface{}
	Data  map[string]interface{}
}

// 注册的回调的方法
type CallBack struct {
	context  *Context             // 包装的请求函数
	method   string               // 注册的响应的方法 get, post, put, delete, head,
	callback []func(ctx *Context) // 回调方法
	index    int                  // 当前调用处于的层数
}

var (
	methods = map[string]struct {
	}{
		"GET":     struct{}{},
		"POST":    struct{}{},
		"HEAD":    struct{}{},
		"PUT":     struct{}{},
		"TRACE":   struct{}{},
		"DELETE":  struct{}{},
		"CONNECT": struct{}{},
		"OPTIONS": struct{}{},
		"PATCH":   struct{}{},
	}
	VERSION         = "1.0.1"
	defaultRecovery = func(url string) {
		if err := recover(); err != nil {
			fmt.Printf("[fatal recover] url %s  %s \n", url, time.Now().Format("2006-01-02 15:03:04"))
		}
	}
)

func (r *Node) GET(pattern string, handler ...func(ctx *Context)) {
	r.Insert(pattern, "get", handler...)
}
func (r *Node) POST(pattern string, handler ...func(ctx *Context)) {
	r.Insert(pattern, "post", handler...)
}

func (r *Node) PUT(pattern string, handler ...func(ctx *Context)) {
	r.Insert(pattern, "put", handler...)
}
func (r *Node) HEAD(pattern string, handler ...func(ctx *Context)) {
	r.Insert(pattern, "head", handler...)
}

func (r *Node) TRACE(pattern string, handler ...func(ctx *Context)) {
	r.Insert(pattern, "trace", handler...)
}
func (r *Node) OPTIONS(pattern string, handler ...func(ctx *Context)) {
	r.Insert(pattern, "options", handler...)
}
func (r *Node) PATCH(pattern string, handler ...func(ctx *Context)) {
	r.Insert(pattern, "patch", handler...)
}

func (r *Node) Insert(pattern, method string, handler ...func(ctx *Context)) {

	if !strings.HasPrefix(pattern, "/") {
		panic("PATTERN MUST START WITH / BUT GET " + fmt.Sprintf(strings.ToUpper(pattern)))
	}
	method = strings.ToUpper(method)
	if _, exist := methods[method]; !exist {
		panic("register fuzzy method see more [https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Methods]" + " found " + method)
	}

	if len(handler) == 0 {
		panic("must register once handler ")
	}

	split := strings.Split(pattern, "/")
	//fmt.Printf("splt %+v\n", split)
	ptr := r
	for _, val := range split {
		//fmt.Println(val)

		// 表示这是一个动态的路由
		if strings.HasPrefix(val, ":") {
			indexByte := strings.IndexByte(val, '(')
			isRegister := false
			regStr := val[indexByte:]
			param := val[1:indexByte]
			//	fmt.Println("regStr", regStr, "params : ", param)
			for _, re := range ptr.regexpPath {
				//re.reg.String() ==
				//fmt.Println(re.reg.String())
				if re.reg.String() == regStr {
					isRegister = true
					ptr = re
					break
				}
			}
			// 如果当前的路由没有注册
			if !isRegister {
				ptr.regexpPath = append(ptr.regexpPath, &Node{
					path:       param,
					normal:     nil,
					isRegexp:   true,
					reg:        regexp.MustCompile(regStr),
					regexpPath: nil,
					callback:   nil,
				})
				ptr = ptr.regexpPath[len(ptr.regexpPath)-1]
				//fmt.Println("append ", ptr )
			}
			continue
		}

		// 常规的路由实现
		node, exist := ptr.normal[val]
		if exist {
			ptr = node
			continue
		}

		i := &Node{
			path:       val,
			normal:     nil,
			isRegexp:   false,
			reg:        nil,
			regexpPath: nil,
			callback:   nil,
		}
		if ptr.normal == nil {
			ptr.normal = make(map[string]*Node)
		}
		ptr.normal[val] = i
		ptr = i

	}

	_, exist := ptr.callback[method]
	if exist {
		panic(fmt.Sprintf("[%s] - method [%+v]", pattern, method) + " is registered")
	}
	if ptr.callback == nil {
		ptr.callback = make(map[string]CallBack)
	}

	ptr.callback[method] = CallBack{
		context:  nil,
		method:   method,
		callback: handler,
		index:    0,
	}
	fmt.Printf("[info] [%s] register %s method %s\n", time.Now().Format("2006-01-02 15:03"), pattern, method)
	//fmt.Printf("ptr is %+v, ptr.regexpPath %+v, callback %+v\n", ptr, ptr.regexpPath, ptr.callback)
}

func (r *Node) PreTraverse(pattern, method string) (node *CallBack) {
	split := strings.Split(pattern, "/")
	//fmt.Println(split, len(split))
	ctx := &Context{Param: make(map[string]interface{})}
	ptr := r
	var (
		i   = 0
		val = ""
	)
	subFound := true
	for i, val = range split {
		subFound = false

		if _, exist := ptr.normal[val]; exist {
			ptr = ptr.normal[val]
			//	fmt.Println("normal", ptr .path)
			subFound = true
			continue
		}

		for _, r := range ptr.regexpPath {
			//	fmt.Println(" r ", r.reg.String(), val , r.reg.MatchString(val ))
			subFound = false
			if r.reg.MatchString(val) {
				ctx.Param[r.path] = val
				ptr = r
				subFound = true
				break
			}
		}
		if !subFound {
			break
		}
		//fmt.Println("not found", val )
		//break
	}

	if i != len(split)-1 || !subFound {
		node = nil
		return node
	}
	method = strings.ToUpper(method)
	back, exist := ptr.callback[method]
	if !exist {
		return node
	}
	back.context = ctx
	node = &back
	return node
}

var (
	g Node
)

func GetG() *Node {
	return &g
}
func Engine() *Node {
	return &g
}
func init() {
	g.Insert("/pattern/:name([a-z]+)", "get", func(ctx *Context) {
		ctx.WriteHeader(http.StatusOK)
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		fmt.Fprintf(ctx.ResponseWriter, `{"hello":"world"}`)
	})
	g.Insert("/pattern/:name([a-z]+)", "post", func(ctx *Context) {})
	g.Insert("/pattern/:name([a-z]+)/ni", "get", func(ctx *Context) {
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		ctx.ResponseWriter.WriteHeader(http.StatusMethodNotAllowed)
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":%+v}`, string(bytes))
	})
	g.Insert("/pattern/:name([a-z]+)/ni/:id([0-9]+)", "get", func(ctx *Context) {
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		ctx.ResponseWriter.WriteHeader(http.StatusOK)
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":%+v}`, string(bytes))
	})
	g.Insert("/pattern/:name([a-z]+)/ni/:id([0-9]+)", "post", func(ctx *Context) {
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		ctx.ResponseWriter.WriteHeader(http.StatusOK)
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":%+v}`, string(bytes))
	})
	g.Insert("/pattern/:name([a-z]+)/ni/:id([0-9]+)/hello/:uid([0-9]+)/more", "get", func(ctx *Context) {
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		ctx.ResponseWriter.WriteHeader(http.StatusOK)
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":%+v}`, string(bytes))
	})
	g.Insert("/pattern/:name([a-z]+)/ni/:id([0-9]+)/hello/:uid([0-9]+)/more", "post", func(ctx *Context) {
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		ctx.ResponseWriter.WriteHeader(http.StatusOK)
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":%+v}`, string(bytes))
	})
	g.GET("/pattern/:name([a-z]+)/ni/:id([0-9]+)/hello/:uid([0-9]+)/more/have", func(ctx *Context) {
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		ctx.ResponseWriter.WriteHeader(http.StatusOK)
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":%+v}`, string(bytes))
	})
	g.GET("/pattern/:name([a-z]+)/ni/:id([0-9]+)/hello/:uid([0-9]+)/more/have/func/bc", func(ctx *Context) {
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		ctx.ResponseWriter.WriteHeader(http.StatusOK)
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":%+v}`, string(bytes))
	})
	g.POST("/pattern/:name([a-z]+)/ni/:id([0-9]+)/hello/:uid([0-9]+)/more/have/func/bc", func(ctx *Context) {
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		ctx.ResponseWriter.WriteHeader(http.StatusOK)
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":%+v}`, string(bytes))
	})

	g.Insert("/pattern/:name([a-z]+)/ni/:id([0-9]+)/hello/:uid([0-9]+)/more", "head", func(ctx *Context) {
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		ctx.ResponseWriter.WriteHeader(http.StatusOK)
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":%+v}`, string(bytes))
	})
	g.Insert("/pattern/:name([a-z]+)", "put", func(ctx *Context) {})
	g.Insert("/pattern/:name([a-z]+)", "options", func(ctx *Context) {})
	g.Insert("/pattern/:name([a-z]+)/:id([0-9]+)/name", "get", func(ctx *Context) {

		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		ctx.ResponseWriter.WriteHeader(http.StatusOK)
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":%+v}`, string(bytes))
	})
	g.Insert("/pattern/:name([a-z]+)", "delete", func(ctx *Context) {})
	g.Insert("/", "delete", func(ctx *Context) {})
	g.Insert("/", "get", func(ctx *Context) {})
	g.Insert("/", "post", func(ctx *Context) {})
	g.Insert("/pattern/:name([0-9]+)", "get", func(ctx *Context) {})
	g.Insert("/more/pattern/:name([0-9]+)", "get", func(ctx *Context) {
		ctx.ResponseWriter.WriteHeader(http.StatusMethodNotAllowed)
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
		bytes, _ := json.Marshal(ctx.Param)
		fmt.Fprintf(ctx.ResponseWriter, `{"message":"not allowed","data":"%+v"}`, string(bytes))
	})
	g.Insert("/index/pattern/:name([0-9]+)", "get", func(ctx *Context) {})

	g.GET("/hello/world/c/cpp/java/php/go/ruby/masm/bitcode/hardcode/python/lisp/others", func(ctx *Context) {
		ctx.ResponseWriter.Write([]byte("ok"))
	})
	g.GET("/hello/world/c/cpp/java/php/go/ruby/masm/bitcode/hardcode/python/lisp/others/:name([a-z]+)", func(ctx *Context) {
		d, _ := json.Marshal(ctx.Param)
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json;utf8")
		ctx.ResponseWriter.Write(d)

	})
	g.GET("/hello/world/c/cpp/java/php/go/ruby/masm/bitcode/hardcode/python/lisp/others/:name([a-z]+)/u", func(ctx *Context) {
		d, _ := json.Marshal(ctx.Param)
		ctx.ResponseWriter.Header().Set("Content-Type", "application/json;utf8")
		ctx.ResponseWriter.Write(d)

	})
	//node := g.PreTraverse("/index/pattern/2333", "get")
	//node2:= g.PreTraverse("/pattern/hello", "get")
	//nodep := g.PreTraverse("/pattern/hello", "post")

	//fmt.Printf("node is %+v ctx %+v\n", node, node.context)
	//fmt.Printf("node is %+v\n", node2)
	//fmt.Printf("node is %+v\n", nodep)
	//fmt.Printf("find %+v\n", g.PreTraverse("/", "GET"))
	//fmt.Printf("find %+v\n", g.PreTraverse("/ni-hao", "GET"))
	//fmt.Printf("find %+v context\n", g.PreTraverse("/pattern/name/ni", "GET"))
	//fmt.Printf("find /pattern/xiaocheng/ni/2096 %+v context\n", g.PreTraverse("/pattern/xiaocheng/ni/2096", "GET"))
	//printpath(&g, 0)
	defaultGenerate()
}

var defaultCallBack = func(ctx *Context) {
	s := ctx.Request.RequestURI
	st := struct {
		URL    string `json:"url"`
		Method string `json:"method"`
	}{
		URL:    s,
		Method: ctx.Request.Method,
	}
	bytes, _ := json.Marshal(st)
	ctx.ResponseWriter.Header().Set("Content-Type", "application/json")
	ctx.ResponseWriter.WriteHeader(200)
	ctx.ResponseWriter.Write(bytes)
}

var defaultGenerate = func() {
	urls := [][]string{{"index", "hello", "nihao", ":name([1-5]+)", "jump"},
		{"index", "hello", "hao", ":name([1-5]+)", "jump"},
		{"index", "hello", "yop", ":name([1-5]+)", "jump"},
		{"index", "hi", "yop", ":id([1-9]+)", "jump"},

		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([9]+)", ":ni([z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([9]+)", ":ni([y-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([x-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([w-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([v-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([u-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([t-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([s-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([r-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([q-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([p-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([o-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([n-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([m-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([l-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([k-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([j-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([i-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([h-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([g-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([f-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([e-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([d-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([c-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([b-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([6-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([5-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([4-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([3-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([2-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([1-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([z]+)", ":id([9]+)", ":you([0-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([y-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([x-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([v-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([u-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([t-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([s-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([r-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([q-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([p-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([o-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([n-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([m-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([l-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([k-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([j-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([i-z]+)", ":id([8-9]+)", ":you([7-8]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([h-z]+)", ":id([7-9]+)", ":you([7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([f-z]+)", ":id([6-9]+)", ":you([6-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([e-z]+)", ":id([5-9]+)", ":you([5-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([d-z]+)", ":id([4-9]+)", ":you([4-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([c-z]+)", ":id([3-9]+)", ":you([3-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([b-z]+)", ":id([2-9]+)", ":you([2-7]+)", ":ni([a-z]+)"},

		{"usr", "you", ":name([a-z]+)", ":id([9]+)", ":you([9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([9]+)", ":you([8-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([9]+)", ":you([7-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([9]+)", ":you([6-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([9]+)", ":you([5-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([9]+)", ":you([4-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([9]+)", ":you([3-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([9]+)", ":you([2-9]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([9]+)", ":you([1-9]+)", ":ni([a-z]+)"},

		{"usr", "you", ":name([a-z]+)", ":id([8-9]+)", ":you([1-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([7-9]+)", ":you([1-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([6-9]+)", ":you([1-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([5-9]+)", ":you([1-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([4-9]+)", ":you([1-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([3-9]+)", ":you([1-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([2-9]+)", ":you([1-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([1-9]+)", ":you([1-7]+)", ":ni([a-z]+)"},
		{"usr", "you", ":name([a-z]+)", ":id([0-9]+)", ":you([1-7]+)", ":ni([a-z]+)"},
	}
	for i := 0; i < len(urls); i++ {
		s := strings.Join(urls[i], "/")
		//fmt.Println(s)
		g.GET("/"+s, defaultCallBack)
	}

} //

func (g *Node) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	defer func() {
		if err := recover(); err != nil {
			fmt.Printf("[err] call %+v", err)
		}
	}()
	start := time.Now()
	indexByte := strings.IndexByte(r.RequestURI, '?')
	var path string
	path = r.RequestURI
	if indexByte != -1 {
		path = r.RequestURI[0:indexByte]
	}

	//fmt.Printf("%+v\n", path)
	callBack := g.PreTraverse(path, r.Method)
	if callBack != nil {
		//fmt.Printf("found %+v\n", callBack)
		callBack.context.ResponseWriter = w
		callBack.context.Request = r
		// todo
		for i, c := range callBack.callback {
			c(callBack.context)
			if i == callBack.index {
				break
			}
		}
		fmt.Printf("[info] [%+v] [%s] [%+v]\n", "pass", path, time.Now().Sub(start))
		return
	}

	w.WriteHeader(http.StatusNotFound)
	w.Write([]byte("404 page not found"))
	fmt.Printf("[info] [%+v] [%+v] [%+v]\n", 404, path, time.Now().Sub(start))

}

//func init() {
//	r := regexp.MustCompile(`([a-z]+)`)
//	fmt.Println("r " + r.String())
//}

func printpath(root *Node, layer int) {
	//fmt.Println("printPath")
	if root == nil {
		fmt.Println()
		return
	}

	ptr := root
	fmt.Print(ptr.path + " - ")
	for _, val := range ptr.normal {
		ptr = val
		printpath(ptr, layer+1)
		fmt.Println()

	}

	for _, val := range ptr.regexpPath {
		ptr = val
		printpath(ptr, layer+1)
		fmt.Println()
	}
}
