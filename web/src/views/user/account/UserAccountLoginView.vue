<template>
  <ContentField v-if="!store.state.user.pulling_jwt">
    <div class="container overflow-hidden">
      <div class="row gx-5 justify-content-md-center">
        <div class="col-3">
          <form @submit.prevent="login">
            <div class="mb-2">
              <label for="username" class="form-label">用户名</label>
              <input
                v-model="username"
                type="text"
                class="form-control"
                id="username"
                aria-describedby="emailHelp"
              />
            </div>
            <div class="mb-3">
              <label for="password" class="form-label">密码</label>
              <input
                v-model="password"
                type="password"
                class="form-control"
                id="password"
              />
            </div>
            <div class="error-message">{{ error_message }}</div>
            <button type="submit" class="btn btn-primary">登陆</button>
          </form>
        </div>
      </div>
    </div>
  </ContentField>
</template>

<script>
import ContentField from "../../../components/ContentField.vue";
import { ref } from "vue";
import { useStore } from "vuex";
import router from "../../../router";

export default {
  components: {
    ContentField,
  },

  setup() {
    const store = useStore();
    let username = ref("");
    let password = ref("");
    let error_message = ref("");

    const jwt_token = localStorage.getItem("jwt_token");
    if (jwt_token) {
      store.commit("updateToken", jwt_token);
      store.dispatch("getInfo", {
        success: (resp) => {
          if (resp.error_message == "success") {
            router.push({ name: "home" });
            store.commit("updatePullingJwt");
          } else {
            store.commit("updatePullingJwt");
          }
        },
        error: () => {
            store.commit("updatePullingJwt");
        },
      });
    }

    const login = () => {
      error_message.value = "";
      store.dispatch("login", {
        username: username.value,
        password: password.value,
        success: (resp) => {
          if (resp.error_message == "success") {
            store.dispatch("getInfo", {
              success: (resp) => {
                if (resp.error_message == "success") {
                  router.push({ name: "home" });
                } else {
                  error_message.value = "用户名或密码错误";
                }
              },
              error: () => {
                error_message.value = "用户名或密码错误";
              },
            });
          } else {
            error_message.value = "用户名或密码错误";
          }
        },
        error: () => {
          error_message.value = "用户名或密码错误";
        },
      });
    };

    return {
      username,
      password,
      error_message,
      store,
      login,
    };
  },
};
</script>

<style scoped>
div.error-message {
  color: red;
}
button {
  width: 100%;
}
</style>
