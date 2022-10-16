# vite 构建 vue3 使用 @ 方式导包配置

一开始看到这种 `@/components/templates/my.vue`\_ 感觉非常良好, 为什么这么说呢?

因为在维护项目的时候终于不用记住当前引入的包与当前文件的路径关系了

`@` 到底是什么呢?

其实, `@` 就是一个别名引入而已

## vue 模板文件 `@` 别名导入

vue3 模板文件 `@` 别名的方式导入, [参考配置文件](https://vitejs.dev/config/shared-options.html#resolve-alias)\_

两种配置方式, 修改 `vite.config.ts` 文件

```ts
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import { fileURLToPath } from "url";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue({})],
  // https://vitejs.dev/config/shared-options.html#resolve-alias
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
});
```

第二种配置方式:

```ts
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import { fileURLToPath } from "url";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue({})],
  resolve: {
    // https://vitejs.dev/config/shared-options.html#resolve-alias
    alias: [
      {
        find: "@",
        replacement: fileURLToPath(new URL("./src", import.meta.url)),
      },
    ],
  },
});
```

配置完之后, 就能在其他文件中以, `import VRouterRegPage from '@/components/router/reg.vue';` 或者 `import VWeather from '@/components/VWeather.vue';`

## ts `@` 别名导入

想要这种以 `import { MyName } from '@/utils/other';` 方式导入 `*.ts` 文件

那么则需要在 `tsconfig.json` 加入如下这段

```json
"paths": {
    "@": [
    "./src"
    ],
    "@/*": [
    "./src/*"
    ]
}
```

```json
{
  "compilerOptions": {
    "target": "ESNext",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "moduleResolution": "Node",
    "strict": true,
    "jsx": "preserve",
    "sourceMap": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "esModuleInterop": true,
    "lib": ["ESNext", "DOM"],
    "skipLibCheck": true,
    "paths": {
      "@": ["./src"],
      "@/*": ["./src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [
    {
      "path": "./tsconfig.node.json"
    }
  ]
}
```

[vue3 如何用别名@引入 ts 文件`vite`](https://zhuanlan.zhihu.com/p/431723674)
