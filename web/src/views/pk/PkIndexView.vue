<template>
  <MatchGround v-if="$store.state.pk.status === 'matching'" />
  <PlayGround v-else-if="$store.state.pk.status === 'playing'" />
  <ResultBoard v-if="store.state.pk.loser !== null" />
</template>

<script>
import PlayGround from "../../components/PlayGround.vue";
import MatchGround from "../../components/MatchGround.vue";
import ResultBoard from "../../components/ResultBoard.vue";
import { onMounted, onUnmounted } from "vue";
import { useStore } from "vuex";

export default {
  components: {
    MatchGround,
    PlayGround,
    ResultBoard,
  },
  setup() {
    const store = useStore();

    const socketUrl = `ws://localhost:3000/websocket/${store.state.user.token}`;

    let socket = null;
    onMounted(() => {
      store.commit("updateOpponent", {
        username: "我的对手",
        photo:
          "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
      });
      socket = new WebSocket(socketUrl);

      socket.onopen = () => {
        console.log("connected");
      };
      socket.onmessage = (message) => {
        const data = JSON.parse(message.data);
        if (data.event === "match-success") {
          // 匹配成功
          store.commit("updateOpponent", {
            username: data.opponent_username,
            photo: data.opponent_photo,
          });
          store.commit("updateGame", data);
          console.log(data.gamemap);
          setTimeout(() => {
            store.commit("updateStatus", "playing");
          }, 200);
        } else if (data.event === "game-move") {
          const game = store.state.pk.gameobject;
          const [snake0, snake1] = game.snakes;
          snake0.set_direction(data.a_direction);
          snake1.set_direction(data.b_direction);
        } else if (data.event === "game-result") {
          const game = store.state.pk.gameobject;
          const [snake0, snake1] = game.snakes;
          if (data.loser === "all" || data.loser === "a") {
            snake0.status = "die";
            snake0.color = "white";
            snake0.eye_direction = data.eye_directionA;
          }
          if (data.loser === "all" || data.loser === "b") {
            snake1.status = "die";
            snake1.color = "white";
            snake1.eye_direction = data.eye_directionB;
          }
          store.commit("updateLoser", data.loser);
        }
      };
      socket.onclose = () => {
        console.log("disconnected");
      };
      store.commit("updateSocket", socket);
    });

    onUnmounted(() => {
      socket.close();
      store.commit("updateStatus", "matching");
    });

    return {
      store,
    };
  },
};
</script>

<style scoped></style>
