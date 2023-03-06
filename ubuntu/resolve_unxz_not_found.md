# 解决 `unxz not found` 问题

> 在导入 clickhouse 中的测试集数据时，解压数据的时候，遇到 `unxz not found`

```shell
apt-get update -y && apt-get install xz-utils -y
```

## 参考

1. [command-not-found](https://command-not-found.com/unxz)
2. [How To Install xz-utils on Ubuntu 20.04](https://installati.one/ubuntu/20.04/xz-utils/)
