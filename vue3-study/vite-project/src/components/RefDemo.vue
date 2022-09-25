<template>
    <!-- ref 对象 -->
    <button @click="add">ref 响应式对象 ++ {{count}}</button>
</template> 
<script  setup lang="ts">
import { ref } from 'vue';
let count = ref(0);
const add = () => {
    count.value++
};
console.log(count);
</script>
<style   scoped>

</style>