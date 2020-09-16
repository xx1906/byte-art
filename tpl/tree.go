/**
二叉树的遍历: 前中后序的遍历, 使用的是递归遍历, 这样基本上就是递归
二叉树的层序遍历: 思想就是使用队列来完成
*/
package tpl

type (
	binaryTree struct {
		left  *binaryTree // 左子树
		right *binaryTree // 右子树
		data  int         // data 域
	}
)

func ctrlCV(n *binaryTree) {
	if n == nil {
		return
	}
	println(n.data)
}

func init() {
	preOrder(nil, ctrlCV)
	inOrder(nil, ctrlCV)
	postOrder(nil, ctrlCV)
	levelVisit(nil, ctrlCV)
}

// 前序遍历二叉树
// 思想就是: 优先遍历根节点, 然后遍历左子树, 最后就是右子树
func preOrder(root *binaryTree, visit func(node *binaryTree)) {
	if root == nil {
		return
	}
	visit(root)
	preOrder(root.left, visit)  // 遍历左子树
	preOrder(root.right, visit) // 遍历右子树
}

// 中序遍历二叉树:
// 思想就是: 优先遍历左子树, 然后再访问根节点, 最后才是右子树 **递归遍历**
func inOrder(root *binaryTree, visit func(node *binaryTree)) {
	if root == nil {
		return
	}
	inOrder(root.left, visit) // 遍历左子树
	visit(root)
	inOrder(root.right, visit) // 遍历右子树
}

// 后序遍历二叉树
// 思想就是: 优先遍历右子树, 然后再到左子树, 最后才是根节点
func postOrder(root *binaryTree, visit func(node *binaryTree)) {
	if root == nil {
		return
	}

	postOrder(root.left, visit)  // 遍历左子树
	postOrder(root.right, visit) // 遍历右子树
	visit(root)                  // visit 节点
}

// 二叉树的层序遍历
// 思想: 使用队列的先进先出的思想, 把访问到的节点放入到队列, 每次都把当前的节点放入到队列
func levelVisit(root *binaryTree, visit func(node *binaryTree)) {
	if root == nil {
		return
	}
	queue := make([]*binaryTree, 0)
	queue = append(queue, root)
	for len(queue) > 0 {
		node := queue[0]

		// 把队列的第一个元素出队列
		queue = queue[1:]

		visit(node)

		// 把左子树放入到队列
		if node.left != nil {
			queue = append(queue, node.left)
		}

		// 把右子树放入到队列
		if node.right != nil {
			queue = append(queue, node.right)
		}
	}
}
