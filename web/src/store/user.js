import $ from "jquery"


export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        pulling_jwt: true,
    },
    mutations: {
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = true;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingJwt(state) {
            state.pulling_jwt = false;
        }
    },
    actions: {
        login(context, data) {
            $.ajax({
                url: "http://localhost:3000/user/account/token/",
                type: "POST",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success: (resp) => {
                    if (resp.error_message == 'success') {
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error: (resp) => {
                    data.error(resp);
                },
            });
        },
        getInfo(context, data) {
            $.ajax({
                url: "http://localhost:3000/user/account/info/",
                type: "GET",
                headers: {
                    "Authorization": "Bearer " + context.state.token,
                },
                success: (resp) => {
                    if (resp.error_message == 'success') {
                        this.commit("updateUser", resp);
                        localStorage.setItem("jwt_token", context.state.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error: (resp) => {
                    data.error(resp);
                },
            });
        },
        logout(context) {
            localStorage.removeItem("jwt_token");
            context.commit("logout");
        }
    },
    modules: {
    }
}