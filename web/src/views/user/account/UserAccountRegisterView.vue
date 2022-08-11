<template>
  <ContentField>
    <div class="container overflow-hidden">
      <div class="row gx-5 justify-content-md-center">
        <div class="col-3">
          <form @submit.prevent="register">
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
            <div class="mb-3">
              <label for="confirmed_password" class="form-label">确认密码</label>
              <input
                v-model="confirmed_password"
                type="password"
                class="form-control"
                id="confirmed_password"
              />
            </div>
            <div class="error-message">{{ error_message }}</div>
            <button type="submit" class="btn btn-primary">注册</button>
          </form>
        </div>
      </div>
    </div>
  </ContentField>
</template>

<script>
import $ from "jquery";
import ContentField from "../../../components/ContentField.vue";
import { ref } from "vue";
import router from "../../../router";

export default {
  components: {
    ContentField,
  },

  setup() {
    let username = ref("");
    let password = ref("");
    let confirmed_password = ref("");
    let error_message = ref("");

    const register = () => {
      error_message.value = "";
      $.ajax({
        url: "http://localhost:3000/user/account/register/",
        type: "POST",
        data: {
          username: username.value,
          password: password.value,
          confirmedPassword: confirmed_password.value,
        },
        success: (resp) => {
          if (resp.error_message == "success") {
            router.push({ name: "user_account_login" });
          } else {
            error_message.value = resp.error_message;
          }
        },
        error: (resp) => {
          error_message.value = resp.error_message;
        },
      });
    };

    return {
      username,
      password,
      confirmed_password,
      error_message,
      register,
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
