// 二叉搜索树

package main

import (
	"crypto/rand"
	"fmt"
	"math/big"
)

type (
	bstTree struct {
		left,
		right *bstTree
		val int
	}
)

func Init() *bstTree {
	return &bstTree{}
}

func newNode(val int) *bstTree {
	init := Init()
	init.val = val
	return init
}

// 插入节点, 比左子树小的插入左边
// 比右子树大的插入右边
func (c *bstTree) insert(val int) {
	if c == nil {
		panic(fmt.Sprintf("empty tree"))
	}
	node := newNode(val)

	tmp := c
	for tmp != nil {

		if tmp.val <= val && tmp.right == nil {
			tmp.right = node
			return
		}

		if tmp.val > val && tmp.left == nil {
			tmp.left = node
			return
		}
		if tmp.val < val {
			tmp = tmp.right
			continue
		}

		if tmp.val > val {
			tmp = tmp.left
			continue
		}
		break
	}
}

// 二叉查找树查找指定的关键值的父亲节点
func searchBstParent(root *bstTree, val int) *bstTree {
	var p *bstTree = nil
	for root != nil && root.val != val {
		p = root
		if val < root.val {
			root = root.left
		} else {
			root = root.right
		}
	}
	return p
}

// 二叉查找树查找指定的关键值的节点
func searchBst(root *bstTree, val int) *bstTree {
	for root != nil && root.val != val {
		if val < root.val {
			root = root.left
		} else {
			root = root.right
		}
	}
	return root
}

// p指向的节点的左子树为空
// p指向的节点的右子树为空
// p指向的节点的左右子树都不为空(这种情况较复杂点::思想就是找到需要删除节点的右子树的最小值填充到需要删除的节点, 然后把找到的最小值所在的节点
// 标记为需要删除的节点)
func RemoveVal(root **bstTree, val int) {
	fmt.Println("removeVal ", val)
	// 空树
	if *root == nil {
		return
	}
	var parent = searchBstParent(*root, val)
	var node = searchBst(*root, val)
	//  fmt.Println(parent, node)

	if node == nil {
		fmt.Println("没有找到需要删除的节点")
		return // 没有找到需要删除的节点
	}

	// 需要删除的节点左右子树都不为空
	if node.left != nil && node.right != nil {
		// 找到该节点右子树的最小节点
		tmp := node
		var pParent = node
		node = node.right
		for node.left != nil {
			pParent = node
			node = node.left
		}
		// 把当前最小值移动到 原来的 node 节点, 把当前找到的最小值标记为删除
		tmp.val = node.val
		// parent <= pParent
		parent = pParent
	}

	// 剩下的就有: parent 为空
	// node 只有左子树
	// node 只有右子树

	// 父亲节点为空
	if parent == nil {
		if node.left != nil {
			node = node.left
		} else if node.right != nil {
			node = node.right
		}
		*root = node
		return
	}

	// 删除的节点在父亲节点的左边
	if parent.left == node {
		if node.left != nil {
			node = node.left
		} else if node.right != nil {
			node = node.right
		}
		parent.left = node
		return
		// 删除的节点在父亲节点的右边
	} else if parent.right == node {
		if node.left != nil {
			node = node.left
		} else if node.right != nil {
			node = node.right
		}
		parent.right = node
		return
	}

}

// 中序遍历, 输出的顺序为升序输出二叉搜索树的
func InOrder(root *bstTree) {
	if root == nil {
		return
	}

	InOrder(root.left)
	fmt.Print(root.val, " ")
	InOrder(root.right)
}

func main() {
	const (
		sz = 10
	)

	root := Init()
	data := make([]int, 0, sz)
	for i := 0; i < sz; i++ {
		n, err := rand.Int(rand.Reader, big.NewInt(100))
		_ = err
		data = append(data, int(n.Int64()))
	}

	fmt.Println(data)
	for _, v := range data {
		root.insert(v)
	}

	InOrder(root)

	fmt.Println("\n", searchBst(root, data[sz/2]), data[sz/2])
	fmt.Println()

	RemoveVal(&root, data[sz/2])
	InOrder(root)
	fmt.Println()
	RemoveVal(&root, data[sz/2])
	InOrder(root)
	fmt.Println()
	RemoveVal(&root, data[sz/3])
	InOrder(root)
	fmt.Println()
	RemoveVal(&root, data[sz/4])
	InOrder(root)
	fmt.Println()
	RemoveVal(&root, data[sz/5])
	InOrder(root)
	fmt.Println()
	RemoveVal(&root, data[sz/6])
	InOrder(root)
	fmt.Println()
	RemoveVal(&root, data[sz/7])

	InOrder(root)
}
