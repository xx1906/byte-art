# 弹条球游戏

``` vue3
<template>
    <!-- 游戏区域 -->
    <div class="container">
        <!-- 底部挡板 -->
        <div class="board" :style="{left:g.boaderX + 'px'}"></div>
        <!-- 弹球 -->
        <div class="ball" :style="{left:g.ballX + 'px', top:g.ballY + 'px'}"></div>
        <!-- 游戏结束提示 -->
        <div class="fail" v-if="g.fail">游戏结束</div>
    </div>
    <div>{{m?.altKey}} {{m?.changedTouches[0].clientX}}</div>

</template> 
<script  setup lang="ts">
import { ref, onMounted } from 'vue';
type Game = {
    // 底部挡板的位置
    boaderX: number,
    // 小球的 x 坐标 
    ballX: number,
    // 小球的 y 坐标
    ballY: number,
    // 小球的x 弹速
    rateX: number,
    // 小球的 y 弹速
    rateY: number,
    // 控制游戏失败的提示
    fail: boolean,
}

const g = ref<Game>({ boaderX: 0, ballX: 0, ballY: 0, rateX: .1, rateY: .1, fail: false });
const m = ref<TouchEvent>()
const clientX = ref<number>(0)
// 键盘事件回调函数
const keydown = (event: KeyboardEvent) => {
    if (event.key == 'ArrowLeft') {
        if (g.value.boaderX > 10) {
            g.value.boaderX -= 20;
        }
    } else if (event.key == 'ArrowRight') {
        if (g.value.boaderX < 440 - 80) {
            g.value.boaderX += 20;
        }
    }
    // console.log(event)
    if (event.code == "Space" && g.value.fail) {
        g.value.ballX = 0;
        g.value.ballY = 0;
        g.value.fail = false;
        // console.log("激活事件")
        startGame()
    }
}
const startGame = () => {
    if (g.value.fail) {
        return;
    }
    g.value.rateX = (Math.random() + .1);
    g.value.rateY = (Math.random() + .1);
    let timer = setInterval(() => {
        // 触碰到右边
        if (g.value.ballX + g.value.rateX >= 440 - 30) {
            g.value.rateX *= -1;
        }
        //  触碰到左边
        if (g.value.ballX + g.value.rateX <= 0) {
            g.value.rateX *= -1;
        }

        // 触碰到顶部
        if (g.value.ballY + g.value.rateY <= 0) {
            g.value.rateY *= -1;
        }

        g.value.ballX += g.value.rateX;
        g.value.ballY += g.value.rateY;

        // 判定 底部
        if (g.value.ballY >= 440 - 30 - 10) {
            // 底部挡板接住了小球
            if (g.value.ballX <= g.value.ballX + 30 &&
                g.value.boaderX + 80 > g.value.ballX) {
                g.value.rateY *= -1;
            } else {
                // 底部挡板没有接住小球
                clearInterval(timer);
                g.value.fail = true;
                console.error(g)
            }
        }
    },
        2);

}
onMounted(() => {
    enterKeyup();
    startGame();
});
const touchMove = (event: TouchEvent) => {
    console.log('')
    m.value = event;
    if (event.changedTouches[0].clientX > 0) {
        if (event.changedTouches[0].clientX > clientX.value && g.value.boaderX < 440 - 80) {
            {

                g.value.boaderX += 20
                clientX.value = event.changedTouches[0].clientX;
            }

        }
    }
}
const enterKeyup = () => {
    document.addEventListener('keydown', keydown);
    document.addEventListener('touchmove', touchMove);
}
</script>
<style   scoped>
.container {

    position: relative;
    margin: 0 auto;
    width: 440px;
    height: 440px;
    background-color: blanchedalmond;

}

.ball {
    position: absolute;
    width: 30px;
    height: 30px;
    left: 0;
    top: 0px;
    background-color: orange;
    border-radius: 30px;

}

.board {
    position: absolute;
    left: 0;
    bottom: 0;
    height: 10px;
    width: 80px;
    border-radius: 5px;
    background-color: red;
}
</style>
```
