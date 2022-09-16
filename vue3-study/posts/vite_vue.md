# vite vue 项目, 以及项目目录说明

创建项目, 直接跟着[教程做](https://cn.vitejs.dev/guide/#trying-vite-online)

``` shell
npm create vite@latest 
```

然后接下来, 输入项目名, 选择 vue, 选择 typescript, 进入项目

``` shell
npm install
npm run dev
```

这样, 项目就启动了



## 项目目录说明

![image-20220917012117110](C:/Users/xkkhy/AppData/Roaming/Typora/typora-user-images/image-20220917012117110.png)



1. public 保存静态文件数据, 里面的内容不会被编译
2. assets 保存资源文件, 里面的内容可以被编译
3. components 保存的是 vue 的组件
4. App.vue 是 vue 的入口文件
5. main.ts 是全局的 ts 文件
6. index.html 是 html 的入口文件, 
7. vite.config.ts 是 vite 的配置文件
8. package.json 是项目的配置文件
9. tsconfig.json 是 typescript 的编译配置文件
10. tsconfig.node.json 是项目的编译文件



每一个 Vue 文件都是由三分部分组成,

``` vue
<template>
  <div class="template">{{ msg }}</div>
</template>
<script setup lang="ts">
import { ref } from "vue";
console.log("new page");
let msg = "this is page";
</script>
<style scoped>
    /* 保持不显示 */
    .template {
        display: none;
    }
</style>

```



1. template, 每个 vue 文件最多可以包含要给 <template> 块, 其中的内容会被提取出来并传递给 `@vue/compiler-dom` 来进行编译, 预编译为 javascript 的渲染函数, 并附属到导出的组件上作为其 `render` 选项.
2. script, 每个 vue 文件最多可以有多个 `<script>` 块, 但是  `<script setup >` 这个可是的块, 最多只能有一个, script 是默认导出的,内容应该是 Vue 组件的选项对象, 它要么是要给普通的对象, 要么是 `defineComponent` 的返回值
3. style `<style >` 每个 vue 文件可以有多个 style 标签

