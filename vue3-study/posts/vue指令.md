# vue 中模板语法与指令

vue 中的插值语法

```vue
<template>
  <!-- 在模板元素中使用 vue 的插值表达式, {{变量名}} -->
  <div>
    vue 中的插值语法: {{ message }} {{ message.length }}
    <!-- 使用 v-if 语法, 判断 message 的长度是否大于等于8  -->
    <!-- 插值语法也是支持条件判断的 -->
    <span v-if="message.length >= 8">v-if 指令使用</span>
    <span>在插值表达式中使用运算符: {{ message.length + 1 }}</span>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
let message: string = "vue 插值语法";
</script>
<style scoped></style>
```

## 在模板元素中使用 vue 的插值表达式, {{变量名}}

比如:

```vue
<template>
  <div>vue 中的插值语法: {{ message }}</div>
</template>
<script setup lang="ts">
import { ref } from "vue";
let message: string = "vue 插值语法";
</script>
<style scoped></style>
```

### 在插值表达式中使用条件判断

> v-if="message.length >= 8"

```vue
<template>
  <div>
    <span v-if="message.length >= 8">v-if 指令使用</span>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
let message: string = "vue 插值语法";
</script>
<style scoped></style>
```

## vue 中支持的指令

v- 开头都是 vue 支持的指令

1. v-text 用来显示文本
2. v-html 用来显示富文本
3. v-if 判断使用
4. v-else-if if 语句块
5. v-show 用来控制元素是否显示
6. v-bind 用来绑定元素属性的
7. v-model 双向绑定, 一般用在 input 标签元素中
8. v-for 用来遍历元素
9. v-on 修饰符号

### v-text 指令使用

```vue
<template>
  <div>
    <!-- v-text 只会以 文本的方式来展示内容 -->
    <span v-text="text"> </span>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
let text: string = "<div> 我是 text 文本, 我会显示所有的内容</div>";

console.log("new page");
</script>
<style scoped></style>
```

### v-html 指令使用

> 这种用法会触发安全问题

```vue
<template>
  <div>
    <!-- v-html 会以 h5 的方式来渲染内容 -->
    <span v-html="h5"></span>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
console.log("new page");
let h5 =
  '<div>我是 html 中中间的内容, 我会以h5 的方式来展示 <img src="https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png"/></div>';
</script>
<style scoped></style>
```

### v-mode 指令使用

```vue
<template>
  <div>
    <input type="text" v-model="message" />
    <div>
      <span> 用户输入: {{ message }}</span>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
// 这里使用托管状态, ref 的方式管理 message
let message = ref("xx");
</script>
<style scoped></style>
```

### 使用事件修饰符号, 方式事件冒泡等等

```vue
<template>
  <div @click="parent_click">
    <button @click="son_click">儿子节点的点击事件会传递给父节点</button>
  </div>

  <div @click="parent_click">
    <!-- 使用事件修饰符号, 禁止事件冒泡-->
    <button @click.stop="son_click_stop">
      <span style="color:red">儿子节点的点击事件不会传递给父节点</span>
    </button>
  </div>

  <form>
    <div>使用事件修饰符号组织表单提交</div>
    <input type="text" v-model="email" />
    <button type="submit" @click.prevent="submit">提交</button>
  </form>
</template>

<script setup lang="ts">
import { ref } from "vue";
let email = ref("");
console.log("new page");
const parent_click = () => {
  console.log("父节点的节点事件");
};
const son_click = () => {
  console.log("儿子点击事件");
};
const son_click_stop = () => {
  console.log("儿子点击事件, 不会传递给父组件");
};

const submit = () => {
  console.log("电子邮件是 ", email.value);
};
</script>
<style scoped></style>
```

### v-bind 绑定 style

```vue
<template>
  <!-- v-bind 绑定 style 属性 -->
  <div :style="style">v-bind 绑定 style 属性</div>
</template>
<script setup lang="ts">
import { ref } from "vue";
console.log("new page");
type Style = {
  color: string;
  fontSize: string;
  // fontWeight: string,
};
let style: Style = { color: "blue", fontSize: "30px" };
</script>
<style scoped></style>
```
