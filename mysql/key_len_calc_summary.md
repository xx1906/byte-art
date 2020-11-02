
# MySQL key_len 计算方式

> 注意, 这里实验的索引不是部分索引

ken_len 就是占用的字节空间嘛? 这个不是很直观嘛? 真相真的是这样的嘛? 使用的数据库版本是 **8.x**

## 准备阶段

不管何种编程语言，　万事先写一个　`hello world`, 换到这里是建立一张表

```shell
mysql> show create table film\G
#      省略了不重要的输出信息
Create Table: CREATE TABLE `film` (
  `id` int NOT NULL,
  `film_name` varchar(300) CHARACTER SET ascii COLLATE ascii_general_ci DEFAULT NULL,
  `actor_name` char(255) DEFAULT NULL,
  `film_default` varchar(300) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL DEFAULT '',
  `film_utf8_mb4` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_film_name` (`film_name`),
  KEY `idx_actor_name` (`actor_name`),
  KEY `idx_film_default` (`film_default`),
  KEY `idx_film_mb4` (`film_utf8_mb4`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii
```

## 实验阶段

> key_len 是怎么计算的呢?

主要是验证 key_len 长度的不相同

```shell
mysql> explain select * from film where actor_name ='';
+----+-------------+-------+------------+------+----------------+----------------+---------+-------+------+----------+-------+
| id | select_type | table | partitions | type | possible_keys  | key            | key_len | ref   | rows | filtered | Extra |
+----+-------------+-------+------------+------+----------------+----------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | film  | NULL       | ref  | idx_actor_name | idx_actor_name | 256     | const |    1 |   100.00 | NULL  |
+----+-------------+-------+------------+------+----------------+----------------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)

mysql> explain select * from film where film_name ='';
+----+-------------+-------+------------+------+---------------+---------------+---------+-------+------+----------+-------+
| id | select_type | table | partitions | type | possible_keys | key           | key_len | ref   | rows | filtered | Extra |
+----+-------------+-------+------------+------+---------------+---------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | film  | NULL       | ref  | idx_film_name | idx_film_name | 303     | const |    1 |   100.00 | NULL  |
+----+-------------+-------+------------+------+---------------+---------------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)

mysql> explain select * from film where film_default= '';
+----+-------------+-------+------------+------+------------------+------------------+---------+-------+------+----------+-------+
| id | select_type | table | partitions | type | possible_keys    | key              | key_len | ref   | rows | filtered | Extra |
+----+-------------+-------+------------+------+------------------+------------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | film  | NULL       | ref  | idx_film_default | idx_film_default | 302     | const |    1 |   100.00 | NULL  |
+----+-------------+-------+------------+------+------------------+------------------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)

mysql> explain select * from film where film_utf8_mb4 ='';
+----+-------------+-------+------------+------+---------------+--------------+---------+-------+------+----------+-------+
| id | select_type | table | partitions | type | possible_keys | key          | key_len | ref   | rows | filtered | Extra |
+----+-------------+-------+------------+------+---------------+--------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | film  | NULL       | ref  | idx_film_mb4  | idx_film_mb4 | 1202    | const |    1 |   100.00 | NULL  |
+----+-------------+-------+------------+------+---------------+--------------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)
```





##　问题出在哪了

上面建表语句中, 使用字符类 `varchar` 最大长度都是 300, 但是 key_len 不一样了? 啥? 难道说这个不是正确的嘛?

嗯, 是的, 这个确实不正确! 但是这个是哪里引起的不正确呢? 其实对于 varchar 类型, 是需要额外的空间来保存真正存放的大小的

> In contrast to `CHAR`, `VARCHAR` values are stored as a 1-byte or 2-byte length prefix plus data. The length prefix indicates the number of bytes in the value. A column uses one length byte if values require no more than 255 bytes, two length bytes if values may require more than 255 bytes.  -- https://dev.mysql.com/doc/refman/8.0/en/char.html

varchar 类型是可变长的字符串, 万事也有例外, (MySQL 表使用 ROW_FORMAT=FIXED 时, 每一行都会使用定长的空间)

首先判断索引是否可以为空, 如果索引可以为空的话, 会使用一个字节的空间来保存 `NULL` 值, 

然后再次判断索引索引是不是 `varchar` 类型的字段, 如果是 `varchar` 类型的字段, 则需要额外的两个字节空间来保存真正的字节数

最后判断字符编码的类型乘以定义字段时的容量乘以对应的字符编码单字节容量

伪代码如下: 

```golang

# key_len 的初始长度 
var sum int

# 如果默认为空
if defaultNull { 
sum = 1
}

# 是不是 varchar 类型, 是, 加上两个字节的空间保存当前字段的真正长度
if ! fixed { 
sum += 2
}

# fieldWidth 定义的长度乘上 对应字符编码的单字节的长度
sum+= (fieldWidth) * typeSize  
sum 

```



中间的 `key_len` 是怎么计算的呢? 

结论: 

1. 如果索引的字段可以为空, 那么 MySQL 需要**一个字节**来标志 `NULL`

2. 如果索引的字段是可变类型, 比如 `varchar`, 那么 MySQL 需要**两个字节**标识是可变长字段, 那么这两个字节保存的是什么呢? 有什么用呢? 

   **这两个字节 里面保存的是, 当前字段保存的内容的多少, 可以把 varchar 看作是golang 里面的 切片 **,  定义字段时候varchar传入的 n 是容量, 需要两个字节来保存真正的长度

   

#### 编码方式以及对应字符占用的空间数

| 字符编码方式 | 占用空间(以字节为单位) |
| ------------ | ---------------------- |
| ascii        | 1                      |
| gbk          | 2                      |
| utf8         | 3                      |
| utf8mb4      | 4                      |



```latex
varchr(10)变长字段且允许NULL    =  10 * ( character set：utf8=3,gbk=2,latin1=1)+1(NULL)+2(变长字段)
varchr(10)变长字段且不允许NULL   =  10 *( character set：utf8=3,gbk=2,latin1=1)+2(变长字段)

char(10)固定字段且允许NULL          =  10 * ( character set：utf8=3,gbk=2,latin1=1)+1(NULL)
char(10)固定字段且不允许NULL         =  10 * ( character set：utf8=3,gbk=2,latin1=1)
```



## 参考内容

1. 高性能 MySQL  4.1.3 节字符串类型