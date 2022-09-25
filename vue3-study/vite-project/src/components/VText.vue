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
