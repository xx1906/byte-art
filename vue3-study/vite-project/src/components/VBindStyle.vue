<template>
    <!-- v-bind 绑定 style 属性 -->
    <div :style="style">v-bind 绑定 style 属性</div>

</template> 
<script  setup lang="ts">
import { ref } from 'vue';
console.log('new page');
type Style = {
    color: string,
    fontSize: string,
    // fontWeight: string,
}
let style: Style = { color: 'blue', fontSize: '30px' }
</script>
<style   scoped>

</style>