<template>
    <!-- 在模板元素中使用 vue 的插值表达式, {{变量名}} -->
    <div>
        vue 中的插值语法: {{ message }} {{ message.length }}
        <!-- 使用 v-if 语法, 判断 message 的长度是否大于等于8  -->
        <!-- 插值语法也是支持条件判断的 -->
        <span v-if="message.length >= 8">v-if 指令使用</span>
        <span>在插值表达式中使用运算符: {{message.length +1}}</span>
    </div>
  
   
</template>
<script setup lang="ts">
import { ref } from "vue";
let message: string = "vue 插值语法";
</script>
<style scoped>

</style>
