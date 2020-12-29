#  MySQL PROFILING 不仅仅是性能检测
> 让 SQL 执行步骤更加透明！
> 深挖慢日志记录是哪一步执行的❓
>
> SQL 查询语句时间都消耗在哪一步了❓


[内容来自于MySQL手册26.24](https://dev.mysql.com/doc/refman/8.0/en/information-schema-profiling-table.html)

### 前置条件

需要开启 profiling, 慢查询日志;

```mysql
## 开启 profiling 
set profiling = 1;

## 查看 profiling 是否已经开启
show variables like 'profiling';

## innodb 存储引擎的版本 5.7.32 
show variables like 'innodb_version'; 

## 设置慢查询时间  -- 这里设置超过 1 秒算慢查询
set long_query_time = 1.0;

## 开启慢查询日志记录 --
set global slow_query_log = 1;

## 慢查询日志记录配置
show variables like 'slow%';
+---------------------+--------------------------------+
| Variable_name       | Value                          |
+---------------------+--------------------------------+
| slow_launch_time    | 1                              |
| slow_query_log      | ON                             |
| slow_query_log_file | /var/lib/mysql/ubuntu-slow.log |
+---------------------+--------------------------------+
-- slow_query_log_file:慢查询日志文件路径

## 查看 慢查询时间
show variables like 'long%';
+-----------------+----------+
| Variable_name   | Value    |
+-----------------+----------+
| long_query_time | 1.000000 |
+-----------------+----------+

```
建表语句：

```mysql
CREATE TABLE `accounts` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(12) NOT NULL DEFAULT '',
  `pwd` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_accounts_nick_name` (`nick_name`)
) ENGINE=InnoDB AUTO_INCREMENT=836754 DEFAULT CHARSET=latin1;
```



##  `profiling` 表的构成

```mysql
 show create table information_schema.profiling;
 CREATE TEMPORARY TABLE `PROFILING` (
  `QUERY_ID` int(20) NOT NULL DEFAULT '0',
  `SEQ` int(20) NOT NULL DEFAULT '0',
  `STATE` varchar(30) NOT NULL DEFAULT '',
  `DURATION` decimal(9,6) NOT NULL DEFAULT '0.000000',
  `CPU_USER` decimal(9,6) DEFAULT NULL,
  `CPU_SYSTEM` decimal(9,6) DEFAULT NULL,
  `CONTEXT_VOLUNTARY` int(20) DEFAULT NULL,
  `CONTEXT_INVOLUNTARY` int(20) DEFAULT NULL,
  `BLOCK_OPS_IN` int(20) DEFAULT NULL,
  `BLOCK_OPS_OUT` int(20) DEFAULT NULL,
  `MESSAGES_SENT` int(20) DEFAULT NULL,
  `MESSAGES_RECEIVED` int(20) DEFAULT NULL,
  `PAGE_FAULTS_MAJOR` int(20) DEFAULT NULL,
  `PAGE_FAULTS_MINOR` int(20) DEFAULT NULL,
  `SWAPS` int(20) DEFAULT NULL,
  `SOURCE_FUNCTION` varchar(30) DEFAULT NULL,
  `SOURCE_FILE` varchar(20) DEFAULT NULL,
  `SOURCE_LINE` int(20) DEFAULT NULL
) ENGINE=MEMORY DEFAULT CHARSET=utf8

```
`ENGINE=MEMORY`  使用的存储引擎是 MEMORY;

| 列名                | 说明                                   |
| ------------------- | -------------------------------------- |
| QUERY_ID            | 标识                                   |
| SEQ                 | 相同的 QUERY_ID 的执行序号             |
| STATE               |                                        |
| DURATION            | 执行时间， 以秒为单位                  |
| CPU_USER            | 用户态 CPU， 以秒为单位                |
| CPU_SYSTEM          | 内核态 CPU， 以秒为单位                |
| CONTEXT_VOLUNTARY   | 自愿上下文切换(非CPU 资源可能比较紧张) |
| CONTEXT_INVOLUNTARY | 非自愿上下文切换(CPU 资源可能比较紧张) |
| BLOCK_OPS_IN        | 块输入操作的数量                       |
| BLOCK_OPS_OUT       | 块输出操作的数量                       |
| MESSAGES_SENT       |                                        |
| MESSAGES_RECEIVED   |                                        |
| PAGE_FAULTS_MAJOR   | 主要页面错误                           |
| PAGE_FAULTS_MINOR   |                                        |
| SWAPS               | 交换数量                               |
| SOURCE_FUNCTION     | 对应操作的函数名                       |
| SOURCE_FILE         | 源代码所在的文件                       |
| SOURCE_LINE         |                                        |



## 名词解析

1. CPU 寄存器： 是 CPU 内置容量小， 但速度极快的内存。 
2. 程序计数器：用来保存 CPU 正在执行指令的位置， 或者即将执行的下一条指令的位置。
3. CPU 寄存器和程序计数器都是 CPU 在运行任何任务前， 必须要依赖的环境， 所以 被叫做 CPU 上下文
4. CPU上下文切换： 就是先把前一个任务的 CPU 上下文(也就是 CPU 寄存器和程序计数器) 保存起来, 然后加载新的任务的上下文到这些寄存器和程序计数器, 最后再跳转到程序计数器所指的新位置, 运行新的任务。
    * 自愿上下文切换: 是指进程无法阿获取所需的资源， 导致的上下文切换。 比如， I/O, 内核等系统资源不足时, 就会发生资源上下问切换
    * 非自愿上下文切换: 是指进程由于时间片已到等原因， 被系统强行调度， 进而发生上下问切换。 比如: 大量的进程都在争抢 CPU 时， 容易发生非资源的上下文切换。
> 以上解释来自 linux 性能优化实战



## 实际操作

生成数据： 就是向表中插入数据

```go
package main

import (
    _ "github.com/go-sql-driver/mysql"
    "github.com/jinzhu/gorm"
    "github.com/sirupsen/logrus"
    "math/rand"
    "time"
)

var db *gorm.DB

const dsn = "bytebyte:123456@tcp(127.0.0.1:3306)/bytebyte?charset=utf8mb4" // 数据源
const charSequence = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
const sz = 1024 * 64

type Account struct {
    Id       uint64 `json:"id"`
    NickName string `json:"nick_name" gorm:"index"`
    Pwd      string `json:"pwd"`
}

func init() {
    var err error
    db, err = gorm.Open("mysql", dsn)
    if err != nil {
        logrus.Fatalf("open db dsn%s %s", dsn, err)
    }
    db.LogMode(true)
    db.AutoMigrate(&Account{})
}

func RanStr(ln int) string {
    rand.Seed(time.Now().UnixNano())
    buff := make([]byte, 0, ln)
    _s := []byte(charSequence)
    for i := 0; i < ln; i++ {
        buff = append(buff, (_s)[rand.Int()%len(_s)])
    }
    return string(buff)
}

func BuildAccount() *Account {
    return &Account{NickName: RanStr(10), Pwd: RanStr(16)}
}
func main() {
    for i := 0; i < sz; i++ {
        err := db.Create(BuildAccount()).Error
        logrus.Infof("build %d error:%v\n", i, err)
    }
}

```

 

###### 实际操作

```mysql
##  构造一个慢查询语句 limit 这里的 offset 浪费了大量的资源
select * from accounts where id in (select id from accounts where pwd != "") limit 541024 , 45;
## 使用 show profiles 获取 query_id;
show profiles;

```



###### 获取查询语句的执行过程以及时间消耗

> 具体的语句执行过程与语句本身有关， 如果只是一个简单的查询语句， 就不会有两个 checking permissions 

```shell
## 查看指定 的id 的信息
mysql> select query_id, seq, state, duration, cpu_user, cpu_system, context_voluntary, context_involuntary from information_schema.profiling where query_id = 24;
+----------+-----+----------------------+----------+----------+------------+-------------------+---------------------+
| query_id | seq | state                | duration | cpu_user | cpu_system | context_voluntary | context_involuntary |
+----------+-----+----------------------+----------+----------+------------+-------------------+---------------------+
|       24 |   2 | starting             | 0.000085 | 0.000043 |   0.000041 |                 0 |                   0 |
|       24 |   3 | checking permissions | 0.000007 | 0.000003 |   0.000003 |                 0 |                   0 |
|       24 |   4 | checking permissions | 0.000006 | 0.000003 |   0.000002 |                 0 |                   0 |
|       24 |   5 | Opening tables       | 0.000021 | 0.000011 |   0.000010 |                 0 |                   0 |
|       24 |   6 | init                 | 0.000089 | 0.000046 |   0.000044 |                 0 |                   0 |
|       24 |   7 | System lock          | 0.000013 | 0.000006 |   0.000006 |                 0 |                   0 |
|       24 |   8 | optimizing           | 0.000027 | 0.000015 |   0.000014 |                 0 |                   0 |
|       24 |   9 | statistics           | 0.000032 | 0.000016 |   0.000015 |                 0 |                   0 |
|       24 |  10 | preparing            | 0.000015 | 0.000008 |   0.000007 |                 0 |                   0 |
|       24 |  11 | executing            | 0.000002 | 0.000001 |   0.000002 |                 0 |                   0 |
|       24 |  12 | Sending data         | 1.011868 | 0.996680 |   0.009376 |                26 |                 100 | 
|       24 |  13 | end                  | 0.000017 | 0.000007 |   0.000007 |                 0 |                   0 |
|       24 |  14 | query end            | 0.000011 | 0.000006 |   0.000005 |                 0 |                   0 |
|       24 |  15 | closing tables       | 0.000010 | 0.000005 |   0.000005 |                 0 |                   0 |
|       24 |  16 | freeing items        | 0.000153 | 0.000079 |   0.000075 |                 0 |                   0 |
|       24 |  17 | logging slow query   | 0.000063 | 0.000032 |   0.000030 |                 0 |                   0 |
|       24 |  18 | cleaning up          | 0.000069 | 0.000037 |   0.000034 |                 0 |                   0 |
+----------+-----+----------------------+----------+----------+------------+-------------------+---------------------+

```

`|       24 |  12 | Sending data         | 1.011868 | 0.996680 |   0.009376 |                26 |                 100 | ` 这里发生了上下文的切换， 且非自愿上下文切换的次数比较多

 ###### 获取慢查询日志信息

```shell
# Time: 2020-12-11T07:25:30.566826Z
# User@Host: root[root] @ localhost []  Id:    12
# Query_time: 1.012353  Lock_time: 0.000204 Rows_sent: 45  Rows_examined: 1082138
SET timestamp=1607671530;
select * from accounts where id in (select id from accounts where pwd != "") limit 541024 , 45;
```



## 总结

一个简单查询语句的在MySQL中的(Innodb引擎, 版本5.7.32)执行过程

starting -> checking permissions -> Opening tables -> init -> System lock -> optimizing -> statistics -> preparing -> executing -> Sending data -> end -> query end -> closing tables -> freeing items  -> logging slow query(如果触发了慢查询的话)  ->cleaning up

