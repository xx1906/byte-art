<template>
    <!-- <div class="template">{{ msg }}</div> -->
    <div>
        <div>
            <section>通过节点来渲染</section>
            <div>我是一个div 节点</div>
            <div>
                <p> 我是一个 p 标签</p>
            </div>
        </div>
    </div>
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
