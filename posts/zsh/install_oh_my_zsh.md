# 国内最可靠的 ohmyzsh 安装方式

> 以 unbuntu 为例子

[ohmyzsh仓库地址](https://github.com/ohmyzsh/ohmyzsh)

`oh-my-zsh` 是一个快速配置 `zsh` 的一个工具, 所以, 在安装 `oh-my-zsh` 之前, 需要先安装 `zsh` shell 工具(如果你没有安装 `zsh`, 就会收到这样的提示 `Zsh is not installed. Please install zsh first.`), 快速安装方式 `sudo apt-get install zsh -y` 类似的, (redhat 平台使用 `sudo yum install zsh -y` 即可), 这里还需要安装 `git` 工具

本来以为将官网上的安装方式直接粘贴到终端就能按爪给你好 `sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"`

```shell
curl: (35) gnutls_handshake() failed: The TLS connection was non-properly terminated.
```

在尝试多次, 上面👆的错误之后, 发现这种方式有问题

## 转折

其实, 其实看看上面的安装方式之后, 发现`ohmyzsh` 使用的是仓库中的 `tools/install.sh` 文件.

所以, 我们可以直接将仓库克隆到本地, 然后执行 `tools/install.sh` 这个文件就可以了

```shell
 git clone https://github.com/ohmyzsh/ohmyzsh.git
Cloning into 'ohmyzsh'...
remote: Enumerating objects: 29387, done.
remote: Counting objects: 100% (8/8), done.
remote: Compressing objects: 100% (8/8), done.
remote: Total 29387 (delta 1), reused 2 (delta 0), pack-reused 29379
Receiving objects: 100% (29387/29387), 9.59 MiB | 3.02 MiB/s, done.
Resolving deltas: 100% (14505/14505), done.
Checking connectivity... done.

```

进入 `tools` 执行 `install.sh`

```shell
ohmyzsh/tools$ ./install.sh
Cloning Oh My Zsh...
remote: Enumerating objects: 1295, done.
remote: Counting objects: 100% (1295/1295), done.
remote: Compressing objects: 100% (1249/1249), done.
remote: Total 1295 (delta 26), reused 1252 (delta 26), pack-reused 0
Receiving objects: 100% (1295/1295), 1.06 MiB | 0 bytes/s, done.
Resolving deltas: 100% (26/26), done.
From https://github.com/ohmyzsh/ohmyzsh
 * [new branch]      master     -> origin/master
Branch master set up to track remote branch master from origin.
Already on 'master'
```

如果不出什么意外, 这个脚本是可以成功执行的

```shell
Looking for an existing zsh config...
Using the Oh My Zsh template file and adding it to ~/.zshrc.

Time to change your default shell to zsh:
Do you want to change your default shell to zsh? [Y/n] y
Changing your shell to /usr/bin/zsh...
[sudo] password for yatt:
Shell successfully changed to '/usr/bin/zsh'.

         __                                     __
  ____  / /_     ____ ___  __  __   ____  _____/ /_
 / __ \/ __ \   / __ `__ \/ / / /  /_  / / ___/ __ \
/ /_/ / / / /  / / / / / / /_/ /    / /_(__  ) / / /
\____/_/ /_/  /_/ /_/ /_/\__, /    /___/____/_/ /_/
                        /____/                       ....is now installed!


Before you scream Oh My Zsh! look over the `.zshrc` file to select plugins, themes, and options.

• Follow us on Twitter: https://twitter.com/ohmyzsh
• Join our Discord community: https://discord.gg/ohmyzsh
• Get stickers, t-shirts, coffee mugs and more: https://shop.planetargon.com/collections/oh-my-zsh

```

## 安装插件

1.[来这里选择你想要的主题](https://github.com/ohmyzsh/ohmyzsh/wiki/Themes), 根据自己的喜好选择
然后编辑 `~/.zshrc` 文件, `ZSH_THEME="robbyrussell"` 其中 `robbyrussell` 是主题的名称, 编辑完之后, 保存退出, `source ~/.zshrc` 使修改生效

2.[选择插件](https://github.com/ohmyzsh/ohmyzsh/wiki/Plugins), 根据自己需要

....

## 总结

至此, `oh-my-zsh` 就安装并配置好了, 祝大家玩得开心
