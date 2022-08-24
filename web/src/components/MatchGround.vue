<template>
  <div class="matchground">
    <div class="row">
      <div class="col-5">
        <div class="user-photo">
          <img :src="$store.state.user.photo" alt="" />
        </div>
        <div class="user-username">
          {{ $store.state.user.username }}
        </div>
      </div>
      <div class="col-2">
        <div class="user-select-bot">
          <select
            v-model="selectBot"
            class="form-select"
            aria-label="Default select example"
          >
            <option value="-1" selected>亲自出马</option>
            <option v-for="bot in bots" :key="bot.id" :value="bot.id">
              {{ bot.title }}
            </option>
          </select>
        </div>
      </div>
      <div class="col-5">
        <div class="user-photo">
          <img :src="$store.state.pk.opponent_photo" alt="" />
        </div>

        <div class="user-username">
          {{ $store.state.pk.opponent_username }}
        </div>
      </div>

      <div class="col-12" style="text-align: center; margin-top: 10vh">
        <button type="button" class="btn btn-warning" @click="click_button">
          {{ button_info }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import $ from "jquery";
import { useStore } from "vuex";
import { ref } from "vue";

export default {
  setup() {
    const bots = ref([]);
    let selectBot = ref(-1);
    const store = useStore();
    let button_info = ref("开始匹配");

    const click_button = () => {
      if (button_info.value === "开始匹配") {
        button_info.value = "取消";
        console.log(selectBot.value);
        store.state.pk.socket.send(
          JSON.stringify({
            event: "start-matching",
            botId: selectBot.value,
          })
        );
      } else {
        button_info.value = "开始匹配";
        store.state.pk.socket.send(
          JSON.stringify({
            event: "stop-matching",
          })
        );
      }
    };

    const refresh_bots = () => {
      $.ajax({
        url: "http://localhost:3000/user/bot/getlist/",
        method: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success: (resp) => {
          bots.value = resp;
        },
      });
    };

    refresh_bots();

    return {
      store,
      button_info,
      click_button,
      bots,
      selectBot,
    };
  },
};
</script>

<style scoped>
div.matchground {
  width: 60vw;
  height: 70vh;
  background-color: rgba(50, 50, 50, 0.5);
  margin: 3vh auto;
}

div.user-photo {
  text-align: center;
  margin-top: 13vh;
}

div.user-photo > img {
  border-radius: 50%;
  width: 35%;
}

div.user-username {
  text-align: center;
  font-size: larger;
  font-weight: bold;
  margin-top: 2vh;
  color: white;
}

div.user-select-bot {
  padding-top: 23vh;
}

div.user-select-bot > select {
  width: 80%;
  margin: 0 auto;
}
</style>
