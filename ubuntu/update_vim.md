# ubuntu16.04 使用 apt 更新 vim

```shell
# 导入软件源地址
sudo add-apt-repository ppa:jonathonf/vim
# 更新源并安装
sudo apt install update -y && sudo apt install vim -y

```

如果遇到错误

> W: GPG 错误：<https://packages.microsoft.com/repos/edge> stable InRelease: 由于没有公钥，无法验证下列签名： NO_PUBKEY EB3E94ADBE1229CF

无法从 xx 下载 EB3E94ADBE1229CF

尝试解决办法:

```shell
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EB3E94ADBE1229CF

# gpg: 下载密钥‘BE1229CF’，从 hkp 服务器 keyserver.ubuntu.com
# gpg: 密钥 BE1229CF：公钥“Microsoft (Release #signing) <gpgsecurity@microsoft.com>”已导入
# gpg: 合计被处理的数量：1
# gpg:               已导入：1  (RSA: 1)

```

到这里 `vim` 就升级(安装)好了, 接下来做点别的事情, 比如, 安装个 [vim-plug](https://github.com/junegunn/vim-plug)插件
