# vue3 setup 语法

``` vue
<template>
    <!-- 用户注册功能 -->
    <div class="container">
        <div class="subTitle">加入我们, 一起创建美好世界</div>
        <h1 class="title">创建你的账号</h1>
        <div v-for="(item, index) in fields" class="inputContainer">
            <div class="field">
                {{ item.title }}
                <span v-if="item.required" style="color: red">*</span>
                <input :type="item.type" class="input" v-model="item.model" />
                <div class="tip" v-if="index == 2">请确认密码程度需要大于 6 位</div>
            </div>
        </div>
        <div class="subContainer">
            <div class="setting">偏好设置</div>
            <input type="checkbox" class="checkbox" />
            <label for=""></label> 接收邮件更新
        </div>
        <button class="btn" @click="createAccount">创建账号</button>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, } from "vue";
type Field = {
    // 显示标题
    title: string;
    // 是否是必须的
    required: boolean;
    // 
    type: string;
    // 绑定值
    model: string;
};

let fields = reactive<Field[]>([]);
fields.push({ title: "用户名", required: true, type: "text", model: "" });
fields.push({ title: "邮箱地址", required: false, type: "text", model: "" });
fields.push({ title: "密码", required: true, type: "password", model: "" });

// 计算属性
const name = computed<string>({
    get(): string {
        return fields[0].model;
    },
    set(val: string) {
        fields[0].model = val;
    },

});

// 计算属性
const password = computed<string>({
    get(): string {
        return fields[2].model;
    },
    set(val: string) {
        fields[2].model = val;
    }
});

// 计算属性
const email = computed<string>({
    get(): string {
        return fields[1].model;
    },
    set(val: string) {
        fields[1].model = val;
    }

});


// 独立的方法
const createAccount = () => {
    console.log(name, password, email)
}

</script>
<style scoped>
.container {
    margin: 0 auto;
    margin-top: 70px;
    text-align: center;
    width: 300px;
}

.subTitle {
    color: grey;
    font-size: 14px;
}

.title {
    font-size: 45px;
}

.input {
    width: 90%;
}

.inputContainer {
    text-align: left;
    margin-bottom: 20px;
}

.subContainer {
    text-align: left;
}

.field {
    font-size: 14px;
}

input {
    border-radius: 6px;
    height: 25px;
    margin-top: 10px;
    border-color: silver;
    border-style: solid;
    background-color: cornsilk;
}

.tip {
    margin-top: 5px;
    font-size: 12px;
    color: grey;
}

.setting {
    font-size: 9px;
    color: black;
}

.label {
    font-size: 12px;
    margin-left: 5px;
    height: 20px;
    vertical-align: middle;
}

.checkbox {
    height: 20px;
    vertical-align: middle;
}

.btn {
    border-radius: 10px;
    height: 40px;
    width: 300px;
    margin-top: 30px;
    background-color: deepskyblue;
    border-color: blue;
    color: white;
}
</style>

```
