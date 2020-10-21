package main

// LRU 缓存淘汰算法的实现
// @AUTHOR: 我的我的
//
type Node struct {
	val  int
	key  int
	prev *Node
	next *Node
}

type LRUCache struct {
	head     *Node
	tail     *Node
	data     map[int]*Node
	capacity int
}

func Constructor(capacity int) LRUCache {

	tmp := LRUCache{
		head:     &Node{},
		tail:     &Node{},
		data:     make(map[int]*Node),
		capacity: capacity,
	}

	tmp.tail.prev = tmp.head
	tmp.head.next = tmp.tail
	return tmp
}

func (this *LRUCache) moveToHead(node *Node) {
	this.removeNode(node)
	this.addToHead(node)
}

func (this *LRUCache) addToHead(node *Node) {
	node.next = this.head.next
	this.head.next.prev = node
	this.head.next = node
	node.prev = this.head
}

func (this *LRUCache) removeNode(node *Node) {
	node.next.prev = node.prev
	node.prev.next = node.next
}

func (this *LRUCache) Get(key int) int {
	if data, ok := this.data[key]; !ok {
		return -1
	} else {
		//this.removeNode(data)
		//this.addToHead(data)
		this.moveToHead(data)
		return data.val
	}
}

// 设置缓存结点信息
func (this *LRUCache) Put(key int, value int) {
	if node, ok := this.data[key]; ok {
		node.val = value
		this.moveToHead(node)
		return
	}

	node := &Node{val: value, key: key}
	this.addToHead(node)
	this.data[key] = node
	if len(this.data) > this.capacity {
		tail := this.removeTail()
		delete(this.data, tail.key)

	}
}

// 移除最后一个结点
func (this *LRUCache) removeTail() *Node {
	node := this.tail.prev
	this.removeNode(node)
	return node
}

func main() {
	// TODO
}
