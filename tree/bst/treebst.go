// [二叉搜索树台湾 wiki](https://baike.tw.lvfukeji.com/baike-%E4%BA%8C%E5%8F%89%E6%9F%A5%E6%89%BE%E6%A0%91)
//
// 二叉搜索树: Binary Search Tree, 也称二叉搜索树, 二叉有序树, 或者二叉排序树, 是指一颗空树或者具有下列性质的二叉树:
// 1. 若任意节点的左子树不空, 则左子树上所有节点的值均小于它的根节点的值
// 2. 若任意节点右子树不空, 则右子树上所有的值均大于或等于他的更节点的值
// 3. 任意节点的左, 右子树分别为二叉查找树
//
// 二叉查找树相比于其他数据结构的优势在于 查找, 插入的时间复杂度较低(Olog(N)), 最坏是 O(N), 二叉查找树是基础的数据机构, 用户构建更为
// 抽象的数据机构, 比如 集合, map(JAVA 里面有个 TreeMap)
//
// 二叉查找树通常采用二叉链表作为二叉树的存储结构. **中序遍历**二叉查找树可得到一个关键字的有序序列, 一个无序序列可以通过构建一个二叉查找树变成
// 一个有序的序列, 构建树的过程即为对无序序列进行查找的过程. 每次插入新的节点都是二叉书上的叶子节点
// 在进行插入操作时, 不必移动其他的结点, 只需要改动某个结点的指针, 由空变为非空即可.
//
// 搜索, 插入, 删除的复杂度等于树高, 期望值为 O(log(N)), 最坏时间复杂度是: O(N), 最坏的时间复杂度是由于树已经完全退化成了单边链表了
//
// 虽然二叉查找树的最坏效率是 O(n), 但它支持动态查询, 目前由很多的改进版本(比如 AVL, Red-Black Tree等)的二叉查找树可以使树高为 O(log(n))
//
//
// 二叉搜索树的查找算法的中搜索的过程为, 搜索值为 x, :
// 1. 如果树为空, 表示搜索失败
// 2. 如果 x 等于 b 的根节点的数据域的值, 表示搜索成功
// 3. 如果 x 小于 b 的根节点的数据域的值, 则搜索左子树
// 4. 如果 x 大于 b 的根节点的数据域的值, 则搜索右子树
//
//type bstTree struct {
//    left , right *bstTree
//    data int
//}
//
//
//// 二叉查找树查找指定的关键值的节点
//func searchBst(root *bstTree, val int) *bstTree {
//	for root != nil && root.val != val {
//		if val < root.val {
//			root = root.left
//		} else {
//			root = root.right
//		}
//	}
//	return root
//}

//
// 二叉查找树 b 插入结点 s 的算法过程:
// 1. 如果 b 为空树, 则将新的结点 s 直接作为根节点
// 2. 如果 s -> val 等于树 b 的根节点的数据域, 表示结点存在, 直接返回
// 3. 如果 s -> val 小于根节点的的数据域的值, 则把 s 所指向的结点插入到 左子树
// 4. 如果 s -> val 大于根节点的数据域的值, 则把 s 所执行的结点插入到右子树
// **新插入的结点总是叶子结点**
//
// // 插入节点, 比左子树小的插入左边
//// 比右子树大的插入右边
//func (c *bstTree) insert(val int) {
//	if c == nil {
//		panic(fmt.Sprintf("empty tree"))
//	}
//	node := newNode(val)
//
//	tmp := c
//	for tmp != nil {
//
//		if tmp.val <= val && tmp.right == nil {
//			tmp.right = node
//			return
//		}
//
//		if tmp.val > val && tmp.left == nil {
//			tmp.left = node
//			return
//		}
//		if tmp.val < val {
//			tmp = tmp.right
//			continue
//		}
//
//		if tmp.val > val {
//			tmp = tmp.left
//			continue
//		}
//		break
//	}
//}

// 二叉搜索树的删除算法的过程: 分三种情况讨论
// 1. 如果 *p 结点为叶子结点,
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

// 在二叉查找树删去一个结点，分三种情况讨论：
//
//1. 若*p结点为叶子结点，即PL（左子树）和PR（右子树）均为空树。由于删去叶子结点不破坏整棵树的结构，则只需修改其双亲结点的指针即可。
//2. 若*p结点只有左子树PL或右子树PR，此时只要令PL或PR直接成为其双亲结点*f的左子树（当*p是左子树）或右子树（当*p是右子树）即可，作此修改也不破坏二叉查找树的特性。
//3. 若*p结点的左子树和右子树均不空。在删去*p之后，为保持其它元素之间的相对位置不变，可按中序遍历保持有序进行调整，可以有两种做法：其一是令*p的左子树为*f的左/右（依*p是*f的左子树还是右子树而定）子树
//，*s为*p左子树的最右下的结点，而*p的右子树为*s的右子树；其二是令*p的直接前驱（in-order predecessor）或直接后继（in-order successor）替代*p，然后再从二叉查找树中删去它的直接前驱（或直接后继）
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
		} else if node.left == nil && node.right == nil {
			node = nil
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
		} else if node.left == nil && node.right == nil {
			node = nil
		}
		parent.left = node
		return
		// 删除的节点在父亲节点的右边
	} else if parent.right == node {
		if node.left != nil {
			node = node.left
		} else if node.right != nil {
			node = node.right
		} else if node.left == nil && node.right == nil {
			node = nil
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
	for _, v := range data {
		RemoveVal(&root, v)
		InOrder(root)
		fmt.Println()
	}
	InOrder(root)
}
