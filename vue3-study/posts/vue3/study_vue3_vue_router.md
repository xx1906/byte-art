# vue router 学习

vue 环境为 3
vue-router 版本为 4

## 安装 vue-router

```shell
npm init vite@latest
```

然后选择安装 vue-router

```shell
npm install vue-router@4
```

## 编写路由框架相关的代码

先让项目支持 `@` 的方式导入文件

在 vite.config.ts 文件中加入

```shell
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath } from "url";


// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias:{
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    }
  }
})
```

其中 `fileURLToPath` 这个函数是在 `@types/node` 这个库中做的声明， 所以， 需要安装 `@types/noode@14.20`

```shell
npm install @types/node@14.20
```

### 编写 router 声明文件

在 `src` 文件夹下创建一个 `router` 文件夹， 然后在 `router` 文件夹下创建 `index.ts` 文件

```ts
import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
  {
    // 路由的名称
    name: "index",
    // 路由的路径
    path: "/index",
    // 对应的组件
    // @/views/Index.vue 文件表示 src 目录下的 views 文件夹下的 Index.vue 文件
    component: () => import("@/views/Index.vue"),
  },
  {
    // 路由的名称， welcome
    name: "welcome",
    // 路由的路径， /welcome
    path: "/welcome",
    // 路由对应的视图声明文件
    component: () => import("@/views/Welcome.vue"),
  },
];

// 创建路由
const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 导出路由
export default router;
```

### 编写对应的视图文件

`views/index.vue` 文件

```vue
<template>
  <div>
    <h3>我是页面 index</h3>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
console.log("new page");
</script>
<style scoped></style>
```

`views/Welcome.vue` 文件

```vue3
<template>
    <div>
        <h3>我是页面 Welcome</h3>
    </div>
</template>
<script  setup lang="ts">
import { ref } from 'vue';
console.log('new page');
</script>
<style   scoped>

</style>
```

### 修改 `src/main.ts`

```ts
import { createApp } from "vue";
import "./style.css";
import App from "./App.vue";
// 从 router/index.ts 文件中引入 router 对象
import router from "./router/index";

let app = createApp(App);

// 将 router 对象挂载在 app 对象中
app.use(router);

app.mount("#app");
```

### 修改 `App.vue` 文件

```vue
<script setup lang="ts"></script>

<template>
  <header>
    <router-link class="cm" to="index">首页</router-link>
    <router-link class="cm" to="welcome">欢迎页</router-link>
  </header>
  <router-view></router-view>
</template>

<style scoped>
.cm {
  font-size: 20px;
  font-weight: bold;
  margin-left: 20px;
  color: orange;
  /** 加入下划线 */
  text-decoration: 5px blue underline;
}

.cm:nth-child(2) {
  color: plum;
  text-align: center;
}
</style>
```

然后在终端运行 `npm run dev` 就能看到效果了

## 路由懒加载

当打包构建引用的时候， js 包代码会变得非常大，影响页面加载， 如果我们能把不同的路由对应的组件分割成不同的代码块时， 然后当路由被访问的时候才加载对应的组件， 这样就会比较高效

```ts
const Home = () => import("@/components/Home.vue");
```

## 三种路由历史记录模式

hash 模式

`createWebHashHistory` 创建的历史纪录，它在内部传递的 URL 之前使用了一个哈希字符 `#`， 由于这部分 URL 从未被发送到服务器， 所以不需要在服务器层面做任何的特殊处理， 不过，在 SEO 中确实有不好的影响。 如果你担心这个问题， 可以使用 `HTML5` 模式。

改变 URL 中的 hash 部分不会引起页面的刷新

通过 `hashChange` 事件监听 URL 的变化，

改变 URL 的方式只有这几种

1. 通过浏览器前进后退改变 URL
2. 通过 `<a>` 标签改变 URL
3. 通过 windows.location 改变 URL

```ts
import { createRouter, createWebHashHistory } from "vue-router";

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    //...
  ],
});
```

HTML5 模式

使用 `createWebHistory` 函数创建的路由

```js
import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    //...
  ],
});
```

如果我们使用 hash 模式时， URL 看起来是长这样的， `https://examples.com/user/id`, 这样页面看起啦就比较正常， 不过问题来了， 由于我们的应用是一个单页面应用， 如果没有适当的服务器配置， 用户在浏览器中直接访问 `https://exmaples.com/user/id` 时就会得到一个 404 的错误，

abstract 模式

abstract 模式适用于所有的 js 环境, `createMemoryHistory`\_

```js
const router = createRouter({
  history: createMemoryHistory(process.env.BASE_URL),
  routes,
});
```

## 带参数的动态路由

query 方式传参

```ts
<router-link class="cm" to="/welcome?name=233&query=9999">
  欢迎页
</router-link>
```

接收方式

```vue
<template>
  <div>
    <h3>我是页面 Welcome</h3>
    <span>name:{{ name }}</span>
    <hr />
    <span>query:{{ query }}</span>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
// 调用 useRoute 方法
import { useRoute } from "vue-router";
import { Param } from "./types";

// 获取路由对象
const route = useRoute();

// 获取 query 参数
let name = route.query?.name;
// 获取 query 参数
let query = route.query?.query;

console.log("new page");
</script>
<style scoped></style>
```

## 嵌套路由

嵌套路由一般用在 UI 由多个嵌套组件组成，每个组件都提供特定的组件结构

`src/router/index.ts` 中的路由定义如下:

```ts
import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
  // 嵌套路由
  {
    name: "nest",
    path: "/nest",
    component: () => import("@/views/nest/Nest.vue"),
    // children 用来定义子组件的路由的
    children: [
      {
        name: "userInfo",
        // 路由不需要以  / 开头了
        path: "userInfo",
        component: () => import("@/views/nest/UserInfo.vue"),
      },
      {
        name: "subLogin",
        // 同样这里也是， 路由不需要以 / 开头
        path: "subLogin",
        component: () => import("@/views/nest/SubLogin.vue"),
      },
    ],
  },
];

// 创建路由
const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 导出路由
export default router;
```

然后编写页面文件

`src/views/nest/Nest.vue` 页面的入口文件如下:

```vue3
<template>
    <router-link class="router-link" to="/nest/userInfo">用户信息</router-link>
    <router-link class="router-link" to="/nest/subLogin">登录</router-link>
    <!-- 记得使用 router-view 容器用于显示子路由 -->
    <router-view></router-view>
</template>
<script  setup lang="ts">

</script>
<style   scoped>
.router-link {
    border: 2px dashed orange;
    margin: 10px;
    border-radius: 5px;
    margin-top: 30px;
}
</style>
```

`src/views/nest/SubLogin.vue` 模板文件编写

```vue3
<template>
  <div>login</div>
</template>
<script setup lang="ts"></script>
<style scoped></style>

```

`src/views/nest/UserInfo.vue` 模板文件编写

```vue3
<template>
    <div>
        <!-- <h4>用户信息导航页面</h4> -->
        用户信息
    </div>
    <button @click="changePage('/nest/subLogin')">跳转到另一个页面</button>

</template>
<script  setup lang="ts">
import { useRouter } from 'vue-router'
const route = useRouter();
const changePage = (path: string) => {
    // console.log('path ', path)
    // console.log(route)
    route.push({
        path: path,
    })
}
</script>
<style   scoped>
button {
    border: 1px solid black;
}
</style>
```

`src/App.vue` 入口模板文件

```vue
<script setup lang="ts">
import { useRouter } from "vue-router";
const route = useRouter();

const routerPush = (path: string) => {
  // const route = useRouter();
  // route.push(name)
  route.push(path);
};

const routerObject = (path: string) => {
  route.push({
    path: path,
  });
};
const routerName = (name: string) => {
  route.push({
    name: name,
  });
};
</script>

<template>
  <header>
    <router-link class="cm" to="/nest">嵌套路由</router-link>
  </header>
  <router-view></router-view>
</template>

<style scoped>
nav {
  display: none;
}
.cm {
  font-size: 20px;
  font-weight: bold;
  margin-left: 20px;
  color: orange;
  /** 加入下划线 */
  text-decoration: 5px blue underline;
}

.cm:nth-child(2) {
  color: plum;
  text-align: center;
}

.nav-program {
  border: 3px dashed #ccc;
}

button {
  border: 2px solid orange;
  margin: 4px;
}
</style>
```

`src/main.ts` 文件编写

```ts
import { createApp } from "vue";
import "./style.css";
import App from "./App.vue";
// 从 router/index.ts 文件中引入 router 对象
import router from "./router/index";

let app = createApp(App);

// 将 router 对象挂载在 app 对象中
app.use(router);

app.mount("#app");
```
