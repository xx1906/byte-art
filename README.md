## byte-art

[数据结构 演算法图谱](./builder.png)

### 数据机构与算法
* 线性表
    1. [数组](linear_list/array)
    2. [链表](linear_list/my_list)
    3. [队列](linear_list/my_queue)
    4. [栈](linear_list/my_stack)
* 树
    1. 二叉树
        * [BST](tree/bst) 
        * [堆-->完全二叉树](my_sort/my_heap_sort)
        * [AVL](tree/avl)
        
    2. 多叉树
        * BTree    
* 图

* 排序算法
    1. [快速排序](my_sort/my_quick_sort)
    2. [堆排序](my_sort/my_heap_sort)
* 查找算法
     1. 二分查找
        * 普通二分查找算法
        * [拉格朗日查找算法](search/lagrange_searh)
     2. map 
     3. 基于树
        * [bst](tree/bst)
        * [avl](tree/avl)
        * [trie](tree/trie/main.go)
        * red-black tree
        * BTree
     4. 基于图
        * 深度遍历
        * 广度遍历
     5. 其他
        * 启发式搜索
* HTTP 
    1. [基于Trie的路由实现](tree/trie/route/main.go)   
    
* 综合运用
    * [LRU缓存策略实现](linear_list/my_list/lru/main.go)         

### 数据库

1. MySQL
    * [MySQL 大纲图](asset/mysql/mysql.png) 
    * [ken_len](mysql/key_len_calc_summary.md)
    * [profiling-SQL执行(性能)流程分析](asset/mysql/profile/first/start/data.md)
2. redis
    * [redis 知识点总结](asset/redis/redis.xmind)
    * [基于redis list 实现的消息ack](asset/redis/redis_ack/redis消息队列ack实现.md)

### 错误集合

* stackOverflow
1. [如何正确验证 golang interface 的 nil](stack_overflow/question/hiding_nil_values.md)
* golang 
1. [切片高级操作](golang/slice_advance.md) 

2. [golang的context中文全文注释](golang/context.md)    

3. [cas_mutex_cmpxchgl](golang/挖掘mutex_cas_cmpxchgl.md) 
### 工程实践
* [blog](https://www.jianshu.com/p/07cf4093536a)
* [微软分布式链路追踪 -- Application insights](https://docs.microsoft.com/en-us/azure/azure-monitor/app/app-insights-overview)

### wireshark 数据包分析
* [tcp三次握手包](wireshark_analysis/TCP的三次握手.md)
* [tcp四次挥手包](wireshark_analysis/TCP的四次挥手数据包.md)
* [SYN-RST](wireshark_analysis/客户端连接存活的主机但是没有服务的tcp包.md)

### gists 集合
[判断一个数是否是2的整次幂](/go_gist/is_power_of_two.go)    


### go源代码
[golang 官方源代码](https://go.googlesource.com/go/)

[slice切片代码阅读](go_gist/sl/slice_src_int.md)

[unsafe 包](go_gist/unsafe_sty/unsafe.md)

### 加密解密算法

1. [rsa 算法加密解密的简单实现](encryption/symmetric/rsa.md)

### protocol buffer

1. [protocol buffer 编码原理实现](encryption/protobuffer/data.md)

