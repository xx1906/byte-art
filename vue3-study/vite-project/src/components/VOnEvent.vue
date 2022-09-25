<template>
    <div @click="parent_click">
        <button @click="son_click">儿子节点的点击事件会传递给父节点</button>
    </div>

    <div @click="parent_click">
        <!-- 使用事件修饰符号, 禁止事件冒泡-->
        <button @click.stop="son_click_stop"><span style="color:red">儿子节点的点击事件不会传递给父节点</span></button>
    </div>

    <form>
        <div>使用事件修饰符号组织表单提交</div>
        <input type="text" v-model="email" />
        <button type="submit" @click.prevent="submit">提交</button>
    </form>
</template> 

<script setup  lang="ts">
import { ref } from 'vue';
let email = ref('')
console.log('new page');
const parent_click = () => {
    console.log('父节点的节点事件')
}
const son_click = () => {
    console.log('儿子点击事件')
}
const son_click_stop = () => {
    console.log('儿子点击事件, 不会传递给父组件')
}

const submit = () => {
    console.log('电子邮件是 ', email.value);
}
</script>
<style   scoped>

</style>