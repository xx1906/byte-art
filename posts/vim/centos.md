# centos 快速更新 vim

```shell
 rpm -Uvh http://mirror.ghettoforge.org/distributions/gf/gf-release-latest.gf.el7.noarch.rpm
 rpm --import http://mirror.ghettoforge.org/distributions/gf/RPM-GPG-KEY-gf.el7
 yum -y remove vim-minimal vim-common vim-enhanced
  yum update
 yum -y --enablerepo=gf-plus install vim-enhanced
```
