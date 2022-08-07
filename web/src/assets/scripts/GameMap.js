import { GameObject } from "./GameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall"

export class GameMap extends GameObject {
    constructor(ctx, parent) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;
        this.rows = 25;
        this.cols = 40;
        this.color_light = "#AAD751";
        this.color_dark = "#A2D149";

        this.g = [];
        for (let r = 0; r < this.rows; r++) {
            this.g.push([]);
            for (let c = 0; c < this.cols; c++) {
                this.g[r].push(false);
            }
        }
        this.walls = [];
        this.inner_walls_count = 80;

        this.snakes = [
            new Snake({ id: 0, color: "#4876EC", r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: "#F94848", r: 1, c: this.cols - 2 }, this)
        ];
    }

    add_listening_event() {
        this.ctx.canvas.focus();

        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {
            if (e.key === 'w') snake0.set_direction(0);
            else if (e.key === 'd') snake0.set_direction(1);
            else if (e.key === 's') snake0.set_direction(2);
            else if (e.key === 'a') snake0.set_direction(3);
            else if (e.key === 'ArrowUp') snake1.set_direction(0);
            else if (e.key === 'ArrowRight') snake1.set_direction(1);
            else if (e.key === 'ArrowDown') snake1.set_direction(2);
            else if (e.key === 'ArrowLeft') snake1.set_direction(3);
            console.log(e);
        });
    }

    start() {
        for (let i = 0; i < 100000; i++) {
            if (this.create_walls())
                break;
        }

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

    check_valid(next_cell) {
        for (const wall of this.walls) {
            if (wall.r === next_cell.r && wall.c === next_cell.c) {
                return false;
            }
        }

        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing()) k--;
            for (let i = 0; i < k; i++) {
                if (snake.cells[i].r === next_cell.r && snake.cells[i].c === next_cell.c) {
                    return false;
                }
            }
        }

        return true;
    }

    check_connectivity(sx, sy, tx, ty, g) {
        const dx = [0, 1, 0, -1];
        const dy = [-1, 0, 1, 0];
        if (sx == tx && sy == ty) {
            return true;
        }
        g[sx][sy] = true;
        for (let i = 0; i < 4; i++) {
            let nx = sx + dx[i];
            let ny = sy + dy[i];
            if (nx >= 0 && nx < this.rows && ny >= 0 && ny < this.cols && !g[nx][ny] && this.check_connectivity(nx, ny, tx, ty, g)) {
                return true;
            }
        }
        return false;
    }

    create_walls() {
        // 清空墙
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                this.g[r][c] = false;
            }
        }

        // 创建周围墙位置
        for (let r = 0; r < this.rows; r++) {
            this.g[r][0] = true;
            this.g[r][this.cols - 1] = true;
        }
        for (let c = 0; c < this.cols; c++) {
            this.g[0][c] = true;
            this.g[this.rows - 1][c] = true;
        }

        // 随机创建内部墙位置
        for (let i = 0; i < this.inner_walls_count / 2; i++) {
            for (let j = 0; j < 1000; j++) {
                let r = Math.floor(Math.random() * this.rows);
                let c = Math.floor(Math.random() * this.cols);
                // left down and right up can not be walls
                if ((r == this.rows - 2 && c == 1) || (r == 1 && c == this.cols - 2))
                    continue;
                if (this.g[r][c] || this.g[this.rows - r - 1][this.cols - c - 1])
                    continue;
                this.g[r][c] = this.g[this.rows - r - 1][this.cols - c - 1] = true;
                break;
            }
        }

        // 检测连通性
        if (this.check_connectivity(this.rows - 2, 1, 1, this.cols - 2, JSON.parse(JSON.stringify(this.g)))) {
            return true;
        }
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