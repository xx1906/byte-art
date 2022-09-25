<template>
    <div>
        <button @click="change_msg">点击我修改值</button>
        <div>{{message}}</div>
    </div>
    <div>
        ref: {{ref_message}}
        <p></p>
        <button @click="ref_message_change">ref 响应式数据:点击我修改值</button>
    </div>
</template> 
<script  setup lang="ts">
import { ref, isRef, shallowRef, reactive } from 'vue';
let ref_message = ref<string>('响应式变量')
console.log('new page');
let message = '消息体'
const change_msg = () => {
    message = '改变消息体o'
    console.log(message, isRef(message))
}
const ref_message_change = () => {
    ref_message.value = '修改值 ' + new Date().toString();
}
console.log(ref_message);
// type UserInfo = {
//     name: string,
//     age: number,
//     address: string,
//     avatar: string
// }
// let user: UserInfo = {
//     name: '小明',
//     age: 18,
//     address: '未登记',
//     avatar: "https://xxx.yy.com/avatar/xiaoming.png"
// }
// console.log(user)
</script>
<style   scoped>

</style>