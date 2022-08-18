export default {
    state: {
        status: "matching",
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,
        a_id: 0,
        a_sx: 0,
        a_sy: 0,
        b_id: 0,
        b_sx: 0,
        b_sy: 0,
        gameobject: null,
        loser: null,
    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updateGame(state, data) {
            state.gamemap = data.gamemap;
            state.a_id = data.a_id;
            state.a_sx = data.a_sx;
            state.a_sy = data.a_sy;
            state.b_id = data.b_id;
            state.b_sx = data.b_sx;
            state.b_sy = data.b_sy;
        },
        updateGameObject(state, gameobject) {
            state.gameobject = gameobject;
        },
        updateLoser(state, loser) {
            state.loser = loser;
        }
    },
    actions: {
    },
    modules: {
    }
}