# canvas fill rect 样例

``` vue3
<!-- vue3 vue 文件 -->
<template>
    <div>
        <!-- canvas 容器 -->
        <canvas id="canvas-fill-rect" width="200" height="150" style="border: 1px dashed orange" @click="fillRectDemo">
        </canvas>
    </div>
</template>
<script setup lang="ts">
// 封装公用函数, 返回 HTMLCanvasElement 对象
const $$ = (id: string): HTMLCanvasElement => {
    return document.getElementById(id) as HTMLCanvasElement;
};

const fillRectDemo = () => {
    const canvas: HTMLCanvasElement = $$("canvas-fill-rect");
    const ctx = canvas.getContext("2d");
    if (ctx == null) {
        return;
    }
    const range = 256;
    // 随机颜色值
    let r = Math.round(range * Math.random()).toString(16);
    let g = Math.round(range * Math.random()).toString(16);
    let b = Math.round(range * Math.random()).toString(16);
    let c = "#" + r + g + b;
    // 设置填充样式
    ctx.fillStyle = c;
    // 填充矩形
    ctx.fillRect(50, 50, 100, 80);
};
</script>
<style scoped>

</style>

```
