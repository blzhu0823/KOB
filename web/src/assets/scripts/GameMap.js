import { GameObject } from "./GameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall"


export class GameMap extends GameObject {
    constructor(ctx, parent, store) {
        super();

        store.commit("updateGameObject", this);
        this.store = store
        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;
        this.rows = store.state.pk.gamemap.length;
        this.cols = store.state.pk.gamemap[0].length;
        this.color_light = "#AAD751";
        this.color_dark = "#A2D149";

        this.g = store.state.pk.gamemap;
        this.walls = [];

        this.snakes = [
            new Snake({ id: 0, color: "#4876EC", r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: "#F94848", r: 1, c: this.cols - 2 }, this)
        ];
    }

    add_listening_event() {
        this.ctx.canvas.focus();

        this.ctx.canvas.addEventListener("keydown", e => {
            let d = -1;
            if (e.key === 'w') d = 0;
            else if (e.key === 'd') d = 1;
            else if (e.key === 's') d = 2;
            else if (e.key === 'a') d = 3
            else if (e.key === 'ArrowUp') d = 0;
            else if (e.key === 'ArrowRight') d = 1;
            else if (e.key === 'ArrowDown') d = 2;
            else if (e.key === 'ArrowLeft') d = 3;

            if (d !== -1) {
                this.store.state.pk.socket.send(JSON.stringify({
                    event: "move",
                    direction: d,
                }));
            }
        });
    }

    start() {
        // 创建墙
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (this.g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }

        this.add_listening_event();
    }


    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready() {
        for (const snake of this.snakes) {
            if (snake.status !== 'idle') return false;
            if (snake.direction == -1) return false;
        }

        return true;
    }

    next_step() {
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }

    update() {
        this.update_size();
        if (this.check_ready()) {
            this.next_step();
        }
        this.render();
    }

    render() {
        for (let r = 1; r < this.rows - 1; r++) {
            for (let c = 1; c < this.cols - 1; c++) {
                if (this.g[r][c])
                    continue;
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = this.color_light;
                } else {
                    this.ctx.fillStyle = this.color_dark;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }

}