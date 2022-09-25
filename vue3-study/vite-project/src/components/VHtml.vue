<template>
    <div>
        <!-- v-html 会以 h5 的方式来渲染内容 -->
        <span v-html="h5"></span>
    </div>
</template> 
<script setup  lang="ts">
import { ref } from 'vue';
console.log('new page');
let h5 = '<div>我是 html 中中间的内容, 我会以h5 的方式来展示 <img src="https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png"/></div>'

</script>
<style    scoped>

</style>