<template>
  <div class="matchground">
    <div class="row">
      <div class="col-6">
        <div class="user-photo">
          <img :src="$store.state.user.photo" alt="" />
        </div>
        <div class="user-username">
          {{ $store.state.user.username }}
        </div>
      </div>
      <div class="col-6">
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
import { useStore } from "vuex";
import { ref } from "vue";

export default {
  setup() {
    const store = useStore();
    let button_info = ref("开始匹配");

    const click_button = () => {
      if (button_info.value === "开始匹配") {
        button_info.value = "取消";
        store.state.pk.socket.send(
          JSON.stringify({
            event: "start-matching",
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

    return {
      store,
      button_info,
      click_button,
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
  width: 30%;
}

div.user-username {
  text-align: center;
  font-size: larger;
  font-weight: bold;
  margin-top: 2vh;
  color: white;
}
</style>
