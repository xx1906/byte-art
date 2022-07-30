# nrm 管理

> 一个 npm 的源管理工具

``` shell
npm install -g nrm
```

## 查看当前 npm 支持的源地址

``` shell
 nrm ls

  npm ---------- https://registry.npmjs.org/
  yarn --------- https://registry.yarnpkg.com/
  tencent ------ https://mirrors.cloud.tencent.com/npm/
  cnpm --------- https://r.cnpmjs.org/
  taobao ------- https://registry.npmmirror.com/
  npmMirror ---- https://skimdb.npmjs.com/registry/
```

## 查看 npm 当前使用的源

``` shell
nrm current
```

## 切换 npm 的源地址

``` shell
nrm use taobao
```

## 测试延迟

``` shell
nrm test npm
```
