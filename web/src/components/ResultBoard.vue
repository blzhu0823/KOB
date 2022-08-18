<template>
  <div class="result-board">
    <div class="result-board-text" v-if="draw">draw</div>
    <div class="result-board-text" v-if="win">win</div>
    <div class="result-board-text" v-if="lose">lose</div>
    <div class="result-board-btn">
      <button type="button" class="btn btn-warning" @click="restart">再来!</button>
    </div>
  </div>
</template>

<script>
import { useStore } from "vuex";

export default {
    setup() {
        const store = useStore();

        const restart = () => {
            store.commit("updateStatus", "matching");
            store.commit("updateGame", {});
            store.commit("updateOpponent", {
                username: "我的对手",
                photo:
                    "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            });
            store.commit("updateLoser", null);
        };


        return {
            store,
            restart,
            draw: store.state.pk.loser === "all",
            win: (store.state.pk.loser === "a" && parseInt(store.state.user.id) !== parseInt(store.state.pk.a_id)) || (store.state.pk.loser === "b" && parseInt(store.state.user.id) !== parseInt(store.state.pk.b_id)),
            lose: (store.state.pk.loser === "a" && parseInt(store.state.user.id) === parseInt(store.state.pk.a_id)) || (store.state.pk.loser === "b" && parseInt(store.state.user.id) === parseInt(store.state.pk.b_id)),
        }
    }
};
</script>

<style scoped>
div.result-board {
  width: 30vw;
  height: 40vh;
  background-color: rgba(50, 50, 50, 0.5);
  position: absolute;
  left: 35vw;
  top: 25vh;
}

div.result-board-text {
  text-align: center;
  color: white;
  font-size: 80px;
  font-weight: 600;
  font-style: italic;
  padding-top: 6vh;
}

div.result-board-btn {
  text-align: center;
  margin-top: 8vh;
}
</style>
