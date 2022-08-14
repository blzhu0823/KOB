<template>
  <div class="container">
    <div class="row">
      <div class="col-3">
        <div class="card" style="margin-top: 20px">
          <div class="card-body">
            <img :src="store.state.user.photo" alt="" style="width: 100%" />
          </div>
        </div>
      </div>
      <div class="col-9">
        <div class="card" style="margin-top: 20px">
          <div class="card-header">
            <span style="font-size: 120%; font-weight: bold">我的Bots </span>
            <!-- Button trigger modal -->
            <button
              type="button"
              class="btn btn-primary float-end"
              data-bs-toggle="modal"
              data-bs-target="#add-bot-modal"
            >
              创建Bot
            </button>

            <!-- Modal -->
            <div class="modal fade" id="add-bot-modal" tabindex="-1">
              <div class="modal-dialog modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h5 class="modal-title">创建Bot</h5>
                    <button
                      type="button"
                      class="btn-close"
                      data-bs-dismiss="modal"
                      aria-label="Close"
                    ></button>
                  </div>
                  <div class="modal-body">
                    <div class="mb-3">
                      <label for="add-bot-title" class="form-label">名称</label>
                      <input
                        v-model="botadd.title"
                        type="text"
                        class="form-control"
                        id="add-bot-title"
                        placeholder="请输入Bot名称"
                      />
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-description" class="form-label">简介</label>
                      <textarea
                        v-model="botadd.description"
                        class="form-control"
                        id="add-bot-description"
                        rows="3"
                        placeholder="请输入Bot简介"
                      ></textarea>
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-code" class="form-label">代码</label>
                      <VAceEditor
                        v-model:value="botadd.content"
                        @init="editorInit"
                        lang="c_cpp"
                        theme="textmate"
                        style="height: 300px"
                        placeholder="请输入Bot代码"
                      />
                    </div>
                  </div>
                  <div class="modal-footer">
                    <div style="color: red">{{ botadd.error_message }}</div>
                    <button type="button" class="btn btn-primary" @click="add_bot">
                      创建
                    </button>
                    <button
                      type="button"
                      class="btn btn-secondary"
                      data-bs-dismiss="modal"
                    >
                      取消
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="card-body">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col">名称</th>
                  <th scope="col">创建时间</th>
                  <th scope="col">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="bot in bots" :key="bot.id">
                  <td>{{ bot.title }}</td>
                  <td>{{ bot.createTime }}</td>
                  <td>
                    <!-- Button trigger modal -->
                    <button
                      type="button"
                      class="btn btn-primary"
                      data-bs-toggle="modal"
                      :data-bs-target="'#update-bot-modal-' + bot.id"
                    >
                      编辑
                    </button>
                    <button
                      type="button"
                      class="btn btn-danger"
                      style="margin-left: 15px"
                      data-bs-toggle="modal"
                      :data-bs-target="'#delete-bot-modal-' + bot.id"
                    >
                      删除
                    </button>

                    <!-- Modal -->
                    <div
                      class="modal fade"
                      :id="'update-bot-modal-' + bot.id"
                      tabindex="-1"
                    >
                      <div class="modal-dialog modal-xl">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title">创建Bot</h5>
                            <button
                              type="button"
                              class="btn-close"
                              data-bs-dismiss="modal"
                              aria-label="Close"
                            ></button>
                          </div>
                          <div class="modal-body">
                            <div class="mb-3">
                              <label for="add-bot-title" class="form-label">名称</label>
                              <input
                                v-model="bot.title"
                                type="text"
                                class="form-control"
                                id="add-bot-title"
                                placeholder="请输入Bot名称"
                              />
                            </div>
                            <div class="mb-3">
                              <label for="add-bot-description" class="form-label"
                                >简介</label
                              >
                              <textarea
                                v-model="bot.description"
                                class="form-control"
                                id="add-bot-description"
                                rows="3"
                                placeholder="请输入Bot简介"
                              ></textarea>
                            </div>
                            <div class="mb-3">
                              <label for="add-bot-code" class="form-label">代码</label>
                              <VAceEditor
                                v-model:value="bot.content"
                                @init="editorInit"
                                lang="c_cpp"
                                theme="textmate"
                                style="height: 300px"
                                placeholder="请输入Bot代码"
                              />
                            </div>
                          </div>
                          <div class="modal-footer">
                            <div style="color: red">{{ bot.error_message }}</div>
                            <button
                              type="button"
                              class="btn btn-primary"
                              @click="update_bot(bot)"
                            >
                              保存修改
                            </button>
                            <button
                              type="button"
                              class="btn btn-secondary"
                              data-bs-dismiss="modal"
                            >
                              取消
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- Modal -->
                    <div
                      class="modal fade"
                      :id="'delete-bot-modal-' + bot.id"
                      tabindex="-1"
                      aria-labelledby="exampleModalLabel"
                      aria-hidden="true"
                    >
                      <div class="modal-dialog">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLabel">提示</h5>
                            <button
                              type="button"
                              class="btn-close"
                              data-bs-dismiss="modal"
                              aria-label="Close"
                            ></button>
                          </div>
                          <div class="modal-body">是否确定删除该Bot?</div>
                          <div class="modal-footer">
                            <button type="button" class="btn btn-danger" data-bs-dismiss="modal" @click="remove_bot(bot)">是</button>
                            <button
                              type="button"
                              class="btn btn-secondary"
                              data-bs-dismiss="modal"
                            >
                              否
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import $ from "jquery";
import { VAceEditor } from "vue3-ace-editor";
import { useStore } from "vuex";
import { ref, reactive } from "vue";
import ace from "ace-builds";
// import { Modal } from "bootstrap/dist/js/bootstrap";

export default {
  components: {
    VAceEditor,
  },

  setup() {
    ace.config.set(
      "basePath",
      "https://cdn.jsdelivr.net/npm/ace-builds@" +
        require("ace-builds").version +
        "/src-noconflict/"
    );

    const store = useStore();
    const botadd = reactive({
      title: "",
      description: "",
      content: "",
      error_message: "",
    });
    const bots = ref([]);

    const refresh_bots = () => {
      $.ajax({
        url: "http://localhost:3000/user/bot/getlist/",
        method: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success: (resp) => {
          bots.value = resp;
          // add error_message for each bot in bots
          bots.value.forEach((bot) => {
            bot.error_message = "";
          });
        },
      });
    };

    const add_bot = () => {
      botadd.error_message = "";
      $.ajax({
        url: "http://localhost:3000/user/bot/add/",
        method: "POST",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        data: {
          title: botadd.title,
          description: botadd.description,
          content: botadd.content,
        },
        success: (resp) => {
          if (resp.error_message === "success") {
            botadd.title = "";
            botadd.description = "";
            botadd.content = "";
            // Modal.getInstance("#add-bot-modal").hide();
            refresh_bots();
          } else {
            botadd.error_message = resp.error_message;
          }
        },
        error: (resp) => {
          botadd.error_message = resp.error_message;
        },
      });
    };

    const update_bot = (bot) => {
      bot.error_message = "";
      $.ajax({
        url: "http://localhost:3000/user/bot/update/",
        method: "POST",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        data: {
          bot_id: bot.id,
          title: bot.title,
          description: bot.description,
          content: bot.content,
        },
        success: (resp) => {
          if (resp.error_message === "success") {
            refresh_bots();
          } else {
            bot.error_message = resp.error_message;
          }
        },
        error: (resp) => {
          bot.error_message = resp.error_message;
        },
      });
    };

    const remove_bot = (bot) => {
      $.ajax({
        url: "http://localhost:3000/user/bot/remove/",
        method: "POST",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        data: {
          bot_id: bot.id,
        },
        success: (resp) => {
          if (resp.error_message === "success") {
            refresh_bots();
          }
        },
      });
    };

    refresh_bots();

    return {
      store,
      bots,
      botadd,
      add_bot,
      update_bot,
      remove_bot,
    };
  },
};
</script>

<style scoped></style>
