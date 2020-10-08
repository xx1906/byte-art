## 索引组织表

innodb 存储引擎中, 表是根据主键的顺序组织存放的, 这种存储的方式叫做索引组织表. 在 Innodb
存储引擎表中, 每张表都有个主键(primary key), 如果创建表的时候没有显式定义主键, 则 innodb 
存储引擎会按照下列的方式选择或者创建主键;

* 收件判断表中是否有非空的唯一索引, 如果有, 则该列即为主键
* 如果不符合上述条件, innodb 存储引擎会自动创建一个 6 字节大小的指针.

> 非空的唯一索引是 int, 才会有 _rowid 列

> 如果表中有多个非空的唯一索引时, innodb 存储引擎将选择建表时的第一个定义非空索引为主键. (这里是定义索引的顺序不是列的顺序)


`_rowid` : 只能查看单列为主键的情况. 对于队列组成的主键就显得无能为力了

```mysql

-- 创建表 t6

CREATE TABLE `t6` (
  `a` int NOT NULL,
  UNIQUE KEY `a` (`a`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 插入数据
insert into t6 (a) values (2), (3);
-- Query OK, 2 rows affected (0.03 sec)
-- Records: 2  Duplicates: 0  Warnings: 0


-- 
select a , _rowid from t6;

-- +---+--------+
   | a | _rowid |
   +---+--------+
   | 2 |      2 |
   | 3 |      3 |
   +---+--------+
   2 rows in set (0.06 sec) --
```
