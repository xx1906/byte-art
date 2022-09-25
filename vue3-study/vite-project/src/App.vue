<script setup lang="ts">
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://vuejs.org/api/sfc-script-setup.html#script-setup
import HelloWorld from './components/HelloWorld.vue'
import HotDog from './components/HotDog.vue';
import TemplateAndCp from './components/TemplateAndCp.vue';
import VModel from './components/Vmodel.vue';
import VOnEvent from './components/VOnEvent.vue';
import VBindStyle from './components/VBindStyle.vue';
import RefReactive from './components/RefReactive.vue';
import RefDemo from './components/RefDemo.vue';
</script>

<template>
  <div>
  
  </div>
  <!-- <HelloWorld msg="vue3 typescript 学习" /> -->
  <!-- <HotDog/> -->
  <!-- <TemplateAndCp/> -->
  <!-- <VModel/> -->
  <!-- <VOnEvent/> -->
  <!-- <VBindStyle/> -->
  <RefReactive/>
  <RefDemo/>
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
