<template>
    <div>
        <input type="text" v-model="message">
        <div>
            <span> 用户输入: {{message}}</span>
        </div>
    </div>
</template> 
<script setup  lang="ts">
import { ref } from 'vue';
// 这里使用托管状态, ref 的方式管理 message
let message = ref("xx");
</script>
<style   scoped>

</style>