# AVL (balance binary search tree)
> 自平衡二叉搜索树

## 二叉树的旋转操作

实现一个 AVL, 需要了解[树旋转](https://zh.wikipedia.org/wiki/%E6%A0%91%E6%97%8B%E8%BD%AC)和树平衡

> 树旋转 wikipedia 的定义是对二叉树的一种操作, 不影响元素的顺序(二叉树的中序遍历的结果), 但是会改变树的结构, **将一个结点上移, 一个结点下移**。
> 树的旋转会改变树的形状, 因此长被用来将**较小的子树下移, 较大的子树上移**, 从而降低树的高度、提升许多树的操作效率(主要是查找效率)。



![tree_rotation](./readme_image/tree_rotation.png)

对一棵树进行旋转时, 这棵树的根节点是被旋转的两棵子树的父节点, 称为旋转时的根(**Root**); 如果结点在旋转之后称为新的父结点, 则称该结点为旋转是的转轴(**Pivot**)。上图中, 树的右旋操作以 Q 为根, P 为转轴, 会将树顺时针旋转。 相应的逆操作为左旋, 会以 Q 为转轴, 将树根逆时针旋转。



理解树旋转过程的关键, 在于理解其中不变的约束。 旋转操作不会导致叶子结点的顺序改变(**可以理解为旋转操作前后, 树的中序遍历的结果是一致的**), 旋转过程中也是始终受到二叉搜索树的性质约束: **右子节点比父节点大, 左子节点比父节点小**。尤其需要注意的是, 进行右旋时, 旋转前根节点的左节点的右结点会变成根节点的左结点, 根本身则在旋转后变成新的根的右结点(看下面的动图理解(**β结点**)), 而在这一过程中, 整棵树一直遵守前面提到的约束。相反的左旋操作也是一样的逻辑。



![tree_rotation](./readme_image/tree_rotation_animation.gif)

> golang 是实现二叉树的右旋操作



```go

type (
    AVLTree struct {
        Root *AVLTreeNode `json:"root,omitempty"`
    }
    
    AVLTreeNode struct {
        Left   *AVLTreeNode `json:"left,omitempty"`
        Right  *AVLTreeNode `json:"right,omitempty"`
        Value  int64        `json:"value"`
        Height int64        `json:"height"`
        
        // other field
    }
)

/******************************************************************************
*           d               b
*          /               / \
*         b       == >    a   d
*        / \                 /
*       a   c               c
* 
* 二叉树的右旋转操作
*******************************************************************************/

// 二叉树的右旋操作
// AVL 树旋转操作之后, 需要调整树当前树为根节点的高度
//

func (root *AVLTreeNode) RightRotation() *AVLTreeNode {
    if root == nil {
        return root
    }
    
    pivot := root.Left
    beta := pivot.Right
    pivot.Right = root
    root.Left = beta
    
    pivot.UpdateHeight()
    root.UpdateHeight()
    
    return pivot
}

```



二叉树旋转操作不改变二叉树的**中序**遍历的顺序



树的左右旋转还可以进行组合执行, 称为双旋转(double rotation)。

* 将 X 的右子树右旋, 再将 X 本身左旋, 就是 X 的双左旋转。
* 将 X 的左子树左旋, 再将 X 本身右旋, 就是 X 的双右旋转。



> 二叉树的双左旋转操作

```go
type (
    AVLTree struct {
        Root *AVLTreeNode `json:"root,omitempty"`
    }
    
    AVLTreeNode struct {
        Left   *AVLTreeNode `json:"left,omitempty"`
        Right  *AVLTreeNode `json:"right,omitempty"`
        Value  int64        `json:"value"`
        Height int64        `json:"height"`
        
        // other field
    }
)

/******************************************************************************
*           d               b
*          /               / \
*         b       == >    a   d
*        / \                 /
*       a   c               c
*
* 二叉树的右旋转操作
*******************************************************************************/

// 二叉树的右旋操作
// AVL 树旋转操作之后, 需要调整树当前树为根节点的高度
//

func (root *AVLTreeNode) RightRotation() *AVLTreeNode {
    if root == nil {
        return root
    }
    
    pivot := root.Left
    beta := pivot.Right
    pivot.Right = root
    root.Left = beta
    
    pivot.UpdateHeight()
    root.UpdateHeight()
    
    return pivot
}

/******************************************************************************
*           a               c
*            \             / \
*             c    == >   a   d
*            / \           \
*           b   d           b
*
* 二叉树的左旋转操作
*******************************************************************************/

// 二叉树的左旋操作
// AVL 树旋转操作之后, 需要调整树当前树为根节点的高度
//
func (root *AVLTreeNode) LeftRotation() *AVLTreeNode {
    if root == nil {
        return root
    }
    
    pivot := root.Right
    beta := pivot.Left
    pivot.Left = root
    root.Right = beta
    
    pivot.UpdateHeight()
    root.UpdateHeight()
    return pivot
}

/******************************************************************************
*           d               d                 c
*          /               /                 / \
*         d       == >    c        =>       b   d
*          \             /
*           c           b
*
* 二叉树的双左旋转操作
*******************************************************************************/


func (root *AVLTreeNode) LeftRightRotation() *AVLTreeNode {
    if root == nil {
        return root
    }
    root.Left = root.Left.LeftRotation()
    return root.RightRotation()
}

```

> 还有一个双右旋操作的





## AVL 的定义

[avl维基百科](https://zh.wikipedia.org/wiki/AVL%E6%A0%91)



AVL 树时最早被发明的**自平衡二叉查找树**。 在 AVL 树中, 任一个结点对应的两棵子树的最大高度差为 1, 因此它被称为高度平衡二叉树。查找、插入和删除在平均和最坏的时间复杂度都是 O(log n) `1.44 * O(log n)`。增加和删除元素的操作可能借由一次或者多次的树旋转, 以实现树的重新平衡, 就是 👆 的代码。



### AVL 平衡因子, 节点删除, 增加需要用到的关键因子

节点的平衡因子(balance factor): 是它的左子树的高度减去右子树的高度(有时候是右子树的高度减去左子树的高度)。带有平衡因子值为(1、0 或者 -1) 的节点被认为是平衡的。带有平衡因子 -2 或者 2 的节点被认为是不平衡的, 并且需要重新平衡这课树。平衡因子可以直接保存在每个节点中, 或者可能存储在节点中的子树的高度计算出来(这里实现选择第二种)。



### 删除, 增加节点涉及到的 AVL 旋转

删除和增加节点之后, 二叉树节点的平衡因子可能由原来的 -1 (或者1) 变成 -2 (或者2), 此时可以认为这棵树是不平衡的, 因此一次或者多次旋转操作来使树重新平衡。

**以下图表以四列表示四种情况, 每行表示在改种情况下需要进行的操作。在 左左和右右情况下, 只需要进行一次旋转操作, 在左右和右左的情况下, 需要进行两次旋转操作。**

![tree_Rebalancing](./readme_image/tree_Rebalancing.png)

 插入的实现描述: 

假设平衡因子是左子树的高度减去右子树的高度所得的值, 又假设由于在二叉树上插入节点而失去平衡的最小子树根节点的指针为 a(即 a 是离插入节点最近, 且平衡因子绝对值超过 1 [绝对值是 2] 的祖先节点), 则失去平衡后进行的规律可以归纳为下列四种情况: 

* 左左情况下对应的是右旋, 旋转完之后, 还需要重新调整旋转节点的高度

  > 单向右旋处理平衡 LL: 由于在 *a 的右子树根节点的左子树上插入节点, *a 的平衡因子由 1 增至 2, 致使以 *a 为根节点的子树失去平衡, 则需要一次右旋操作重新平衡

  ```markdonw
  /***********************************************************************************************************************
  *  需要单向右旋的情况是: 原来左子树 a 的平衡因子为 1, 在左子树上继续插入一个节点后, 导致平衡因子变成了 2, 这个时候需要以 a 为根节点进行一次
  *  右旋。
  *
  *  // 举个栗子
  *  
  *            (a:9)                      (a:9)                                                          (b:6)
  *           /                          /                                                              /    \
  *         (b:6)  插入节点(c:4)=>      (b:6)          平衡因子由 1 变成了 2, 需要进行一次右旋操作  =>     (c:4)    (a:9)     
  *                                    /
  *                                (c:4)
  ************************************************************************************************************************/
  ```

  

  ```go
  func (root *AVLTreeNode) RightRotation() *AVLTreeNode {
      if root == nil {
          return root
      }
      
      pivot := root.Left
      beta := pivot.Right
      pivot.Right = root
      root.Left = beta
      
      pivot.UpdateHeight()
      root.UpdateHeight()
      
      return pivot
  }
  ```

* 右右的情况下对应的是左旋, 旋转完之后, 还需要重新调整旋转节点的高度

  > 单向左旋平衡处理 RR: 由于在 *a 的右子树根节点的右子树上插入节点, *a 的平衡因子由 -1 变成了 -2, 致使以 *a 为根节点的子树失去了平衡, 则需要一次左旋操。

  ```markdown
  
  /************************************************************************************************************************************
  *  需要单向左旋的情况是: 原来左子树 a 的平衡因子为 -1, 在右子树上继续插入一个节点后, 导致平衡因子变成了 -2, 这个时候需要以 a 为根节点进行一次
  *  左旋。
  *
  *  // 举个栗子
  *
  *            (a:9)                                               (a:9)                                                 (b:20)
  *                 \                                                   \                                                /   \
  *                 (b:20) 在 b 的节点的右子树上插入节点(c:37) 后 =>         (b:20)  平衡因子由原来的 -1 变成 -2, 需要左旋处理 (a:9)    (c:37)
  *                                                                       \
  *                                                                       (c:37)
  ************************************************************************************************************************************/
  ```

  

  ```go
  func (root *AVLTreeNode) LeftRotation() *AVLTreeNode {
      if root == nil {
          return root
      }
      
      pivot := root.Right
      beta := pivot.Left
      pivot.Left = root
      root.Right = beta
      
      pivot.UpdateHeight()
      root.UpdateHeight()
      return pivot
  }
  ```

* 右左的情况下, 对应的是先左旋(此时已经是左左)在右旋, 旋转完之后再作调节

  > 双向旋转(先右后左) 平衡处理LR: 由于在 *a 的左子树根节点的右子树上插入节点, *a 的平衡因子由原来的 1 增至 2, 致使以 *a 为根节点的子树失去平衡, 需要进行两次旋转(先左旋后右旋)操作

  ```markdonw
  /***********************************************************************************************************************
  *  需要单向先左旋后右旋的情况是: 原来左子树 a 的平衡因子为 1, 在右子树上继续插入一个节点后, 导致平衡因子变成了 2, 这个时候需要以 a 的左子树
  *  为根进行一次左旋, 然后以 a 的为根进行一次右旋
  *
  *
  *  // 举个栗子
  *
  *            (a:10)
  *           /
  *         (b:6)
  *
  *
  *  插入节点 (c:9) 后, 根据二叉查找树的规律, 此时节点 (c:9) 只能在 (b:6) 的右边插入
  *         👇
  *
  *           (a:10)
  *           /
  *          (b:6)
  *               \
  *               (c:9)
  *  此时以 a 为根节点的平衡因子由 1 变成了 2, 需要进行以 a 的左子树为根节点进行一次左旋操作, 调整为以 a 的根节点的, 平衡因子是 2 的类型(LL)
  *              👇
  *
  *           (a:10)
  *           /
  *         (c:9)
  *         /
  *       (b:6)
  *  此时以 a 为根节点的平衡因子依然是 2, 但是不平衡的类型由原来的 LR 变成了 LL, 此时只需要以 a 为根节点的进行一次右旋操作即可
  *            👇
  *
  *           (c:9)
  *           /   \
  *       (b:6)   (a:10)
  ************************************************************************************************************************/
  ```

  

  ```go
  
  func (root *AVLTreeNode) LeftRightRotation() *AVLTreeNode {
      if root == nil {
          return root
      }
      root.Left = root.Left.LeftRotation()
      return root.RightRotation()
  }
  
  ```

  

* 左右情况下, 对应的是先右旋(此时已经是右右)再左旋, 旋转完之后再作调节

  > 双向旋转(先右后左)平衡处理RL: 由于在 *a 的右子树根节点插入节点, *a 的平衡因子由原来的 -1 变成了 -2, 致使以 *a 为根的子树失去平衡, 则需要进行两次旋转(先右后左)操作。

  ```markdown
  
  /***********************************************************************************************************************
  *  需要单向先右旋后左旋的情况是: 原来右子树 a 的平衡因子为 -1, 在 a 的右子树的左子树上继续插入一个节点后, 导致平衡因子变成了 -2, 这个时候需
  *  要以 a 的右子树为根进行一次右边旋, 然后以 a 的为根进行一次左旋
  *
  *
  *  // 举个栗子
  *
  *            (a:10)
  *                 \
  *                 (b:20)
  *
  *
  *  插入节点 (c:13) 后, 根据二叉查找树的规律, 此时节点 (c:13) 只能在 (b:20) 的左边插入
  *         👇
  *
  *           (a:10)
  *               \
  *              (b:20)
  *               /
  *          (c:13)
  *  此时以 a 为根节点的平衡因子由 -1 变成了 -2, 需要进行以 a 的右子树为根节点进行一次右旋操作, 调整为以 a 的根节点的, 平衡因子是 -2 的类型(RR)
  *              👇
  *
  *           (a:10)
  *                \
  *               (c:13)
  *                   \
  *                  (b:20)
  *  此时以 a 为根节点的平衡因子依然是 -2, 但是不平衡的类型由原来的 RL 变成了 RR, 此时只需要以 a 为根节点的进行一次左边旋操作即可
  *            👇
  *
  *           (c:13)
  *           /   \
  *       (a:10)   (b:20)
  ************************************************************************************************************************/
  ```

  

  ```go
  func (root *AVLTreeNode) RightLeftRotation() *AVLTreeNode {
      if root == nil {
          return root
      }
      
      root.Right = root.Right.RightRotation()
      return root.LeftRotation()
  }
  ```

  

  ### 平衡二叉查找树插入一个新的数据元素 e 的递归描述:

  1. 若 BBST 为空树, 则插入一个新的数据元素 2 的新节点作为 BBST 的根节点, 树的深度增加 1;
  2. 若 e 的关键字和 BBST 的关键字相等, 则不进行;
  3. 若 e 的关键字小于 BBST 的根节点的关键字, 而且在 BBST 的左子树不存在和 e 有相同关键字的节点, 则将 e 插入在 BBST 的左子树上, 并且当插入之后, 左子树的深度增加 (+1) 时, 分别就下列不同的情况处理:
     1.  BBST 的根节点的平衡因子为 -1 (右子树的深度大于左子树的身的, 则将根节点的平衡因子改为 0, BBST 的深度不变);
     2. BBST 的根节点的平衡因子为 0(左右子树的深度相同): 则将 根节点的平衡椅子改为 1, BBST 的深度加 1;
     3. BBST 的根节点的平衡因子为 1(左子树的深度大于右子树的深度):则若 BBST 的左子树的平衡因子 为 1, 则需进行向右单旋平衡处理, 并且在右旋处理之后, 将根节点和其右子树根节点的平衡因子为更改为 0, 树的深度不变。
  4. 若 e 的关键字大于 BBST 的根节点的关键字, 而且在 BBST 的右子树中不存在和 e 有相同关键字的节点, 则将 e 插入到右子树上, 并且在插入之后更新右子树的深度, 然后情况和 3 相似处理。

  > 代码实现

  ```go
  func (root *AVLTree) Add(value int64) {
      if root == nil {
          return
      }
      root.Root = root.Root.Add(value)
  }
  
  // 1. 若 BBST 为空树, 则插入一个新的数据元素 2 的新节点作为 BBST 的根节点, 树的深度增加 1;
  //2. 若 e 的关键字和 BBST 的关键字相等, 则不进行;
  //3. 若 e 的关键字小于 BBST 的根节点的关键字, 而且在 BBST 的左子树不存在和 e 有相同关键字的节点, 则将 e 插入在 BBST 的左子树上, 并且当插
  //   入之后, 左子树的深度增加 (+1) 时, 分别就下列不同的情况处理:
  //   1. BBST 的根节点的平衡因子为 -1 (右子树的深度大于左子树的身的, 则将根节点的平衡因子改为 0, BBST 的深度不变);
  //   2. BBST 的根节点的平衡因子为 0(左右子树的深度相同): 则将 根节点的平衡椅子改为 1, BBST 的深度加 1;
  //   3. BBST 的根节点的平衡因子为 1(左子树的深度大于右子树的深度):则若 BBST 的左子树的平衡因子 为 1, 则需进行向右单旋平衡处理, 并且在右旋
  //      处理之后, 将根节点和其右子树根节点的平衡因子为更改为 0, 树的深度不变。
  
  //4. 若 e 的关键字大于 BBST 的根节点的关键字, 而且在 BBST 的右子树中不存在和 e 有相同关键字的节点, 则将 e 插入到右子树上, 并且在插入之后更
  //    新右子树的深度, 然后情况和 3 相似处理。
  func (root *AVLTreeNode) Add(value int64) *AVLTreeNode {
      // 如果 root 是空树
      if root == nil {
          return &AVLTreeNode{Value: value, Height: 1}
      }
      
      // 值重复, 不做处理, 返回当前的节点即可
      if value == root.Value {
          fmt.Printf("dumplicate value %v\n", value)
          return root
      }
      
      //
      var newNode *AVLTreeNode
      // 在左边插入节点
      if value < root.Value {
          root.Left = root.Left.Add(value)
          factor := root.BalanceFactor()
          if factor == 2 {
              if value < root.Left.Value {
                  // LL 类型的, 右旋
                  newNode = root.RightRotation()
              } else {
                  // LR 类型的, 需要左旋调整为 LL 类型, 然后右旋
                  newNode = root.LeftRightRotation()
              }
          }
      } else {
          // 需要插入的节点比当前的节点大, 需要在二叉树的右边插入节点
          root.Right = root.Right.Add(value)
          factor := root.BalanceFactor()
          // 如果原来的平衡因子是 -1, 现在变成了 -2
          if factor == -2 {
              // 没有插入新节点之前, 左子树比右子树矮
              // 如果插入节点后, 树的平衡因子是 -2, 表明根节点的左子树的高度比根节点的右子树的高度小 2
              // RR 类型的, 直接左旋
              if value > root.Right.Value {
                  newNode = root.LeftRotation()
              } else {
                  // RL 类型的, 需要先右旋调整为 RR 类型的, 然后左旋
                  newNode = root.RightLeftRotation()
              }
          }
      }
      
      if newNode != nil {
          // 发生旋转, 树的根节点发生了改变, newNode 为新的根节点, 需要重新调节点的高度
          newNode.UpdateHeight()
          return newNode
      } else {
          // 没有发生旋转, 调整当前节点的高度即可
          root.UpdateHeight()
          return root
      }
  }
  ```

## 删除节点

> 删除的元素: 思想类似于 bst 删除元素, 删除之后需要调整节点的高度

1. 删除的节点是叶子节点(没有儿子), 直接删除后, 看离他最近的父节点是否已经失衡, 如果失衡, 做旋转处理;

2. 删除的节点有两个子节点, 选择高度更高的节点删除(思想和删除 BST 左右子树不为空的情况简直不要太相同):

   ⅰ 如果左子树更高, 选择左子树中最大的节点替换要删除的节点, 然后转化为删除找到的最大的节点, 最后转化为和 1 相同;

   ⅱ 如果右子树更加高, 选择右子树中的最小值替换要删除的节点, 然后转化为删除找到的最小值的节点的情况, 最后转化为和 1 相同。

3. 要删除的节点只有左子树, 那么更具 AVL 树的定义, 只有左子树的情况, 高度只能为 1, 将左边的节点替换为当前要删除的节点, 最后转为删除左边的节点;

4. 要删除的节点只有右子树, 那么根据 AVL 树的定义, 只有右子树的情况下, 高度只能是 1, 将右边的节点的值替换为要删除的节点的值, 最后转为要删除右边节点。

   

   <!--我的-->

   ```go
   
   func (root *AVLTree) Delete(value int64) {
       if root == nil {
           return
       }
       root.Root = root.Root.Delete(value)
   }
   
   // 递归删除指定的节点
   func (root *AVLTreeNode) Delete(value int64) *AVLTreeNode {
       // 空树不用删除(递归的终止条件)
       if root == nil {
           return root
       }
       
       // 需要删除的元素可能在当前根节点的左边
       if value < root.Value {
          root.Left = root.Left.Delete(value)
          root.Left.UpdateHeight() // 递归更新当前根节点的左子树
       }
       
       // 需要删除的节点可能当前根节点的右边
       if value > root.Value {
          root.Right = root.Right.Delete(value)
          root.Right.UpdateHeight() // 递归更新当前根节点的右子树
       }
       
       if root.Left == nil && root.Right == nil && root.Value == value {
          // 找到的节点没有左右子树, 直接删除改节点
          return nil
       }
       
       if root.Right != nil && root.Left != nil && root.Value == value {
          // 待删除的节点有左右子树
          // 选最高或者更高的那一课树来的节点来替换要删除的节点, 然后变成第一种 1 中情况
          if root.Left.Height > root.Right.Height {
              // 左子树更加高, 找到左子树的最大值来替换要删除的节点, 然后转为删除左子树的最大值
              maxNode := root.Left
              for maxNode.Right != nil {
                  maxNode = maxNode.Right
              }
              root.Value = maxNode.Value
              root.Left = root.Left.Delete(maxNode.Value)
              root.Left.UpdateHeight()
       
          } else {
              // 右子树更加高, 找到右子树中最小值的节点替换要删除的节点, 然后转为删除右子树的最小节点
              minNode := root.Right
              for minNode.Left != nil {
                  minNode = minNode.Left
              }
              root.Value = minNode.Value
              root.Right = root.Right.Delete(minNode.Value)
              root.Right.UpdateHeight()
          }
       
          return root
       }
       
       // 需要删除的节点, 只有左子树, 没有右子树的情况
       if value == root.Value && root.Left != nil && root.Right == nil {
          //root.Value = root.Left.Value
          //root.Left = nil
          //root.Height = 1
          //return root
           
           root.Value = root.Left.Value
           root.Left = root.Left.Delete(root.Left.Value)
       }
       
       // 需要删除的节点, 只有右子树, 没有左子树的情况
       if value == root.Value && root.Left == nil && root.Right != nil {
          //root.Value = root.Right.Value
          //root.Right = nil
          //root.Height = 1
          //return root
       
           root.Value = root.Right.Value
          root.Right =  root.Right.Delete(root.Right.Value)
       }
       
       
       // 删除节点后, 是否需要调整树的高度
       var newNode *AVLTreeNode
       // 删除节点之后, 左子树比右子树高了
       if root.BalanceFactor() == 2 {
           if root.Left.BalanceFactor() >= 0 {
               newNode = root.RightRotation()
           } else {
               newNode = root.LeftRightRotation()
           }
       } else if root.BalanceFactor() == -2 {
           if root.Right.BalanceFactor() <= 0 {
               newNode = root.LeftRotation()
           } else {
               newNode = root.RightLeftRotation()
           }
       }
       
       if newNode != nil {
           newNode.UpdateHeight()
           return newNode
       } else {
           root.UpdateHeight()
           return root
       }
   }
   
   ```

   

##  查找操作

1. 最大值

   最大值一直在右子树的最右边

2. 最小值

   最小值只在左子树的最左边

3. 指定值

## 实现代码

```go
// AVL 实现代码
// @Author: 我的我的

package main

import (
    "fmt"
    "math/rand"
    "sort"
)

type (
    AVLTree struct {
        Root *AVLTreeNode `json:"root,omitempty"`
    }
    
    AVLTreeNode struct {
        Left   *AVLTreeNode `json:"left,omitempty"`
        Right  *AVLTreeNode `json:"right,omitempty"`
        Value  int64        `json:"value"`
        Height int64        `json:"height"`
        
        // other field
    }
)

func NewAVLTree() *AVLTree {
    return &AVLTree{Root: nil}
}

// 以 root 为根节点计算平衡因子
// 计算的方式是使用左节点的高度 - 右节点的高度 (也可以使用 右节点的高度 ➖ 左节点的高度)
func (root *AVLTreeNode) BalanceFactor() int64 {
    var leftHeight, rightHeight int64
    if root.Left != nil {
        leftHeight = root.Left.Height
    }
    
    if root.Right != nil {
        rightHeight = root.Right.Height
    }
    
    return leftHeight - rightHeight
}

// 更新根节点的高度
// 使用子树节点的高度 + 1, 作为节点的高度
func (root *AVLTreeNode) UpdateHeight() {
    if root == nil {
        return
    }
    
    var leftHeight, rightHeight int64
    if root.Left != nil {
        leftHeight = root.Left.Height
    }
    
    if root.Right != nil {
        rightHeight = root.Right.Height
    }
    
    maxHeight := leftHeight
    if maxHeight < rightHeight {
        maxHeight = rightHeight
    }
    root.Height = maxHeight + 1
}

func (root *AVLTreeNode) LeftRotation() *AVLTreeNode {
    if root == nil {
        return root
    }
    
    pivot := root.Right
    beta := pivot.Left
    pivot.Left = root
    root.Right = beta
    
    pivot.UpdateHeight()
    root.UpdateHeight()
    return pivot
}

func (root *AVLTreeNode) RightRotation() *AVLTreeNode {
    if root == nil {
        return root
    }
    
    pivot := root.Left
    beta := pivot.Right
    pivot.Right = root
    root.Left = beta
    
    pivot.UpdateHeight()
    root.UpdateHeight()
    
    return pivot
}

func (root *AVLTreeNode) LeftRightRotation() *AVLTreeNode {
    if root == nil {
        return root
    }
    root.Left = root.Left.LeftRotation()
    return root.RightRotation()
}

func (root *AVLTreeNode) RightLeftRotation() *AVLTreeNode {
    if root == nil {
        return root
    }
    
    root.Right = root.Right.RightRotation()
    return root.LeftRotation()
}

func (root *AVLTree) Add(value int64) {
    if root == nil {
        return
    }
    root.Root = root.Root.Add(value)
}

// 1. 若 BBST 为空树, 则插入一个新的数据元素 2 的新节点作为 BBST 的根节点, 树的深度增加 1;
//2. 若 e 的关键字和 BBST 的关键字相等, 则不进行;
//3. 若 e 的关键字小于 BBST 的根节点的关键字, 而且在 BBST 的左子树不存在和 e 有相同关键字的节点, 则将 e 插入在 BBST 的左子树上, 并且当插
//   入之后, 左子树的深度增加 (+1) 时, 分别就下列不同的情况处理:
//   1. BBST 的根节点的平衡因子为 -1 (右子树的深度大于左子树的身的, 则将根节点的平衡因子改为 0, BBST 的深度不变);
//   2. BBST 的根节点的平衡因子为 0(左右子树的深度相同): 则将 根节点的平衡椅子改为 1, BBST 的深度加 1;
//   3. BBST 的根节点的平衡因子为 1(左子树的深度大于右子树的深度):则若 BBST 的左子树的平衡因子 为 1, 则需进行向右单旋平衡处理, 并且在右旋
//      处理之后, 将根节点和其右子树根节点的平衡因子为更改为 0, 树的深度不变。

//4. 若 e 的关键字大于 BBST 的根节点的关键字, 而且在 BBST 的右子树中不存在和 e 有相同关键字的节点, 则将 e 插入到右子树上, 并且在插入之后更
//    新右子树的深度, 然后情况和 3 相似处理。
func (root *AVLTreeNode) Add(value int64) *AVLTreeNode {
    // 如果 root 是空树
    if root == nil {
        return &AVLTreeNode{Value: value, Height: 1}
    }
    
    // 值重复, 不做处理, 返回当前的节点即可
    if value == root.Value {
        fmt.Printf("dumplicate value %v\n", value)
        return root
    }
    
    //
    var newNode *AVLTreeNode
    // 在左边插入节点
    if value < root.Value {
        root.Left = root.Left.Add(value)
        factor := root.BalanceFactor()
        if factor == 2 {
            if value < root.Left.Value {
                // LL 类型的, 右旋
                newNode = root.RightRotation()
            } else {
                // LR 类型的, 需要左旋调整为 LL 类型, 然后右旋
                newNode = root.LeftRightRotation()
            }
        }
    } else {
        // 需要插入的节点比当前的节点大, 需要在二叉树的右边插入节点
        root.Right = root.Right.Add(value)
        factor := root.BalanceFactor()
        // 如果原来的平衡因子是 -1, 现在变成了 -2
        if factor == -2 {
            // 没有插入新节点之前, 左子树比右子树矮
            // 如果插入节点后, 树的平衡因子是 -2, 表明根节点的左子树的高度比根节点的右子树的高度小 2
            // RR 类型的, 直接左旋
            if value > root.Right.Value {
                newNode = root.LeftRotation()
            } else {
                // RL 类型的, 需要先右旋调整为 RR 类型的, 然后左旋
                newNode = root.RightLeftRotation()
            }
        }
    }
    
    if newNode != nil {
        // 发生旋转, 树的根节点发生了改变, newNode 为新的根节点, 需要重新调节点的高度
        newNode.UpdateHeight()
        return newNode
    } else {
        // 没有发生旋转, 调整当前节点的高度即可
        root.UpdateHeight()
        return root
    }
}

/***********************************************************************************************************************
*  需要单向右旋的情况是: 原来左子树 a 的平衡因子为 1, 在左子树上继续插入一个节点后, 导致平衡因子变成了 2, 这个时候需要以 a 为根节点进行一次
*  右旋。
*
*  // 举个栗子
*
*            (a:9)                      (a:9)                                                          (b:6)
*           /                          /                                                              /    \
*         (b:6)  插入节点(c:4)=>      (b:6)          平衡因子由 1 变成了 2, 需要进行一次右旋操作  =>     (c:4)    (a:9)
*                                    /
*                                (c:4)
************************************************************************************************************************/

/************************************************************************************************************************************
*  需要单向左旋的情况是: 原来左子树 a 的平衡因子为 -1, 在右子树上继续插入一个节点后, 导致平衡因子变成了 -2, 这个时候需要以 a 为根节点进行一次
*  左旋。
*
*  // 举个栗子
*
*            (a:9)                                               (a:9)                                                 (b:20)
*                 \                                                   \                                                /   \
*                 (b:20) 在 b 的节点的右子树上插入节点(c:37) 后 =>         (b:20)  平衡因子由原来的 -1 变成 -2, 需要左旋处理 (a:9)    (c:37)
*                                                                       \
*                                                                       (c:37)
************************************************************************************************************************************/

/***********************************************************************************************************************
*  需要单向先左旋后右旋的情况是: 原来左子树 a 的平衡因子为 1, 在右子树上继续插入一个节点后, 导致平衡因子变成了 2, 这个时候需要以 a 的左子树
*  为根进行一次左旋, 然后以 a 的为根进行一次右旋
*
*
*  // 举个栗子
*
*            (a:10)
*           /
*         (b:6)
*
*
*  插入节点 (c:9) 后, 根据二叉查找树的规律, 此时节点 (c:9) 只能在 (b:6) 的右边插入
*         👇
*
*           (a:10)
*           /
*          (b:6)
*               \
*               (c:9)
*  此时以 a 为根节点的平衡因子由 1 变成了 2, 需要进行以 a 的左子树为根节点进行一次左旋操作, 调整为以 a 的根节点的, 平衡因子是 2 的类型(LL)
*              👇
*
*           (a:10)
*           /
*         (c:9)
*         /
*       (b:6)
*  此时以 a 为根节点的平衡因子依然是 2, 但是不平衡的类型由原来的 LR 变成了 LL, 此时只需要以 a 为根节点的进行一次右旋操作即可
*            👇
*
*           (c:9)
*           /   \
*       (b:6)   (a:10)
************************************************************************************************************************/

/***********************************************************************************************************************
*  需要单向先右旋后左旋的情况是: 原来右子树 a 的平衡因子为 -1, 在 a 的右子树的左子树上继续插入一个节点后, 导致平衡因子变成了 -2, 这个时候需
*  要以 a 的右子树为根进行一次右边旋, 然后以 a 的为根进行一次左旋
*
*
*  // 举个栗子
*
*            (a:10)
*                 \
*                 (b:20)
*
*
*  插入节点 (c:13) 后, 根据二叉查找树的规律, 此时节点 (c:13) 只能在 (b:20) 的左边插入
*         👇
*
*           (a:10)
*               \
*              (b:20)
*               /
*          (c:13)
*  此时以 a 为根节点的平衡因子由 -1 变成了 -2, 需要进行以 a 的右子树为根节点进行一次右旋操作, 调整为以 a 的根节点的, 平衡因子是 -2 的类型(RR)
*              👇
*
*           (a:10)
*                \
*               (c:13)
*                   \
*                  (b:20)
*  此时以 a 为根节点的平衡因子依然是 -2, 但是不平衡的类型由原来的 RL 变成了 RR, 此时只需要以 a 为根节点的进行一次左边旋操作即可
*            👇
*
*           (c:13)
*           /   \
*       (a:10)   (b:20)
************************************************************************************************************************/

// 中序遍历
func (root *AVLTree) InOrder(fn func(value int64)) {
    if root == nil {
        return
    }
    root.Root.InOrder(fn)
}

func (root *AVLTreeNode) InOrder(fn func(value int64)) {
    if root == nil {
        return
    }
    
    root.Left.InOrder(fn)
    fn(root.Value)
    root.Right.InOrder(fn)
}

func (root *AVLTree) Delete(value int64) {
    if root == nil {
        return
    }
    root.Root = root.Root.Delete(value)
}

// 递归删除指定的节点
func (root *AVLTreeNode) Delete(value int64) *AVLTreeNode {
    // 空树不用删除(递归的终止条件)
    if root == nil {
        return root
    }
    
    // 需要删除的元素可能在当前根节点的左边
    if value < root.Value {
        root.Left = root.Left.Delete(value)
        root.Left.UpdateHeight() // 递归更新当前根节点的左子树
    }
    
    // 需要删除的节点可能当前根节点的右边
    if value > root.Value {
        root.Right = root.Right.Delete(value)
        root.Right.UpdateHeight() // 递归更新当前根节点的右子树
    }
    
    if root.Left == nil && root.Right == nil && root.Value == value {
        // 找到的节点没有左右子树, 直接删除改节点
        return nil
    }
    
    if root.Right != nil && root.Left != nil && root.Value == value {
        // 待删除的节点有左右子树
        // 选最高或者更高的那一课树来的节点来替换要删除的节点, 然后变成第一种 1 中情况
        if root.Left.Height > root.Right.Height {
            // 左子树更加高, 找到左子树的最大值来替换要删除的节点, 然后转为删除左子树的最大值
            maxNode := root.Left
            for maxNode.Right != nil {
                maxNode = maxNode.Right
            }
            root.Value = maxNode.Value
            root.Left = root.Left.Delete(maxNode.Value)
            root.Left.UpdateHeight()
            
        } else {
            // 右子树更加高, 找到右子树中最小值的节点替换要删除的节点, 然后转为删除右子树的最小节点
            minNode := root.Right
            for minNode.Left != nil {
                minNode = minNode.Left
            }
            root.Value = minNode.Value
            root.Right = root.Right.Delete(minNode.Value)
            root.Right.UpdateHeight()
        }
        
        return root
    }
    
    // 需要删除的节点, 只有左子树, 没有右子树的情况
    if value == root.Value && root.Left != nil && root.Right == nil {
        //root.Value = root.Left.Value
        //root.Left = nil
        //root.Height = 1
        //return root
        
        root.Value = root.Left.Value
        root.Left = root.Left.Delete(root.Left.Value)
    }
    
    // 需要删除的节点, 只有右子树, 没有左子树的情况
    if value == root.Value && root.Left == nil && root.Right != nil {
        //root.Value = root.Right.Value
        //root.Right = nil
        //root.Height = 1
        //return root
        
        root.Value = root.Right.Value
        root.Right = root.Right.Delete(root.Right.Value)
    }
    
    // 删除节点后, 是否需要调整树的高度
    var newNode *AVLTreeNode
    // 删除节点之后, 左子树比右子树高了
    if root.BalanceFactor() == 2 {
        if root.Left.BalanceFactor() >= 0 {
            newNode = root.RightRotation()
        } else {
            newNode = root.LeftRightRotation()
        }
    } else if root.BalanceFactor() == -2 {
        if root.Right.BalanceFactor() <= 0 {
            newNode = root.LeftRotation()
        } else {
            newNode = root.RightLeftRotation()
        }
    }
    
    if newNode != nil {
        newNode.UpdateHeight()
        return newNode
    } else {
        root.UpdateHeight()
        return root
    }
}

func (root *AVLTree) FindValue(value int64) *AVLTreeNode {
    if root == nil {
        return nil
    }
    return root.Root.FindValue(value)
}

func (root *AVLTreeNode) FindValue(value int64) *AVLTreeNode {
    if root == nil {
        return root
    }
    
    if value == root.Value {
        return root
    }
    
    if value < root.Value {
        return root.Left.FindValue(value)
    } else {
        return root.Right.FindValue(value)
    }
}

func (root *AVLTree) FindMin() *AVLTreeNode {
    if root == nil {
        return nil
    }
    
    return root.Root.FindMin()
}

// 找最小值
func (root *AVLTreeNode) FindMin() *AVLTreeNode {
    if root == nil {
        return nil
    }
    
    if root.Left == nil {
        return root
    }
    
    return root.Left.FindMin()
}

func (root *AVLTree) FindMax() *AVLTreeNode {
    if root == nil {
        return nil
    }
    
    return root.Root.FindMax()
}

// 找最大值
func (root *AVLTreeNode) FindMax() *AVLTreeNode {
    if root == nil {
        return nil
    }
    
    if root.Right == nil {
        return root
    }
    
    return root.Right.FindMax()
}

func main() {
    tree := NewAVLTree()
    const sz = 13
    values := make([]int64, 0)
    for i := 0; i < sz; i++ {
        values = append(values, rand.Int63n(sz))
    }
    sort.Slice(values, func(i, j int) bool {
        return values[i] < values[j]
    })
    for _, v := range values {
        tree.Add(v)
    }
    fmt.Println("max: ", tree.FindMax())
    fmt.Println("min: ", tree.FindMin())
    for _ ,v :=range values {
        fmt.Printf("find value %d, data:%#v\n", v,  tree.FindValue(v))
    }
    tree.InOrder(func(value int64) {
        fmt.Print(value, "  ")
    })
    fmt.Println("end")
    
    for _, v := range values {
        
        fmt.Println("delete ", v)
        tree.InOrder(func(value int64) {
            fmt.Print(value, "  ")
        })
        fmt.Println("end")
        tree.Delete(v)
    }
    tree.InOrder(func(value int64) {
        fmt.Println(value)
    })
}

```



## 外部链接



* [维基百科 avl](https://zh.wikipedia.org/wiki/AVL%E6%A0%91)