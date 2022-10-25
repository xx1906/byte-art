# vite vue3 typescript 集成 cesium

cesium 是 3d 地图渲染工具

本次采用 vite vue3 和 typescript 的方式构建

安装命令

```shell
 cesium cesium vite-plugin-cesium vite -D
```

在 `vite.config.js` 中引入构建插件

```js
import cesium from "vite-plugin-cesium"; // 引入插件
export default defineConfig({
  plugins: [vue(), cesium()],
});
```

然后在新的 `VCesium.vue` 文件中加入

```vue
<template>
  <div id="cesiumContainer"></div>
</template>

<script setup>
import { Viewer } from "cesium"; //引入cesium
import { onMounted } from "vue";

onMounted(() => {
  //初始化cesium实例
  const viewer = new Viewer("cesiumContainer");

  //Cesium处理iframe的allow-scripts权限问题
  const iframe = document.getElementsByClassName("cesium-infoBox-iframe")[0]; // 获取iframe dom元素
  iframe.setAttribute(
    "sandbox",
    "allow-same-origin allow-scripts allow-popups allow-forms"
  );
  iframe.setAttribute("src", "");
});
</script>

<style>
#cesiumContainer {
  width: 100%;
  height: 500px;
}
</style>
```

最后在 App.vue 中引入 `VCesium.vue` 配置文件

## 参考

1. [一分钟搭建 Vite + Vue3 + Cesium 开发环境](https://blog.csdn.net/u010602721/article/details/123187382)
