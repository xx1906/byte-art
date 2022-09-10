# 将 win11 的右键菜单恢复到经典菜单

> 将 win 11 的右键菜单恢复到经典菜单
>
``` shell
reg add "HKCU\Software\Classes\CLSID\{86ca1aa0-34aa-4e8b-a509-50c905bae2a2}\InprocServer32" /f /ve
```

经典菜单切换回到 win 11 的菜单

``` shell
reg delete "HKCU\Software\Classes\CLSID\{86ca1aa0-34aa-4e8b-a509-50c905bae2a2}" /f
```
