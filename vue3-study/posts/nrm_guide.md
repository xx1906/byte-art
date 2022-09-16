# nrm 使用指南

nrm 是一个 npm 的源管理工具, 一般来说, 我们是在国内开发, 如果依赖包在国外的话, 那么使用 npm install 时, 可能安装过程会相当地漫, 或者下载不成功, 所以, 在实际开发过程中, 我们一边会切换 npm 的源, 比如

``` shell
npm set registry https://registry.npm.taobao.org/
```

安装 nrm

``` shell

npm install nrm -g
```

查看当前有哪些可选的源

``` shell
nrm ls
```

查看当前使用的源

``` shell

nrm current
```

切换源 (这里表示切换到腾讯的源)

``` shell
nrm use tencent
```

测试源

``` shell
nrm test npm 
```

.... 更多的用法直接在终端中输入 ``` nrm ```
