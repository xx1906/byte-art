# spleeter 安装使用

系统版本 `18.04.4 LTS`, 仓库地址 [spleeter](https://github.com/deezer/spleeter)

``` shell
cat /etc/issue              
>> Ubuntu 18.04.4 LTS
```

spleeter 是一款人声伴奏的分离工具

anaconda 环境配置

``` shell
wget https://mirrors.bfsu.edu.cn/anaconda/archive/Anaconda3-2022.10-Linux-x86_64.sh
# 非交互式方式安装 anaconda, 并且指定安装路径
bash ~/Anaconda3-2022.10-Linux-x86_64.sh -b -p /usr/local/share/miniconda
echo "export PATH=$PATH:/usr/local/share/miniconda/bin" >> ~/.bashrc
source ~/.bashrc
```


配置 pip 

``` shell
pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple
```

创建 python 环境

``` shell
conda create -n py3.8 python=3.8 -y
# 激活 环境
conda activate py3.8
echo "conda activate py3.8" >> ~/.bashrc
```

安装 spleeter

``` shell
conda install -c conda-forge ffmpeg libsndfile
# install spleeter with pip
pip install spleeter
```

使用 spleeter

``` shell
spleeter separate -p spleeter:2stems -o output audio_example.mp3
```

其中， `spleeter:2stems` 表示分离两个音轨

如果有报错信息， 例如: `save path xx` 则可能是表示， `pretrained_models` 路径下的对应的 model 文件不存在

``` text
(py3.8) nn in ~/pretrained_models λ tree
.
├── 2stems
│   ├── checkpoint
│   ├── model.data-00000-of-00001
│   ├── model.index
│   └── model.meta
└── 4stems
    ├── checkpoint
    ├── model.data-00000-of-00001
    ├── model.index
    └── model.meta

2 directories, 8 files
```

那么需要 从 [release](https://github.com/deezer/spleeter/releases) 中下载对应的模型文件, 然后解压放到放到 `pretrained_models` 文件夹下
