import { GameObject } from "./GameObject";
import { Wall } from "./Wall"

export class GameMap extends GameObject {
    constructor(ctx, parent) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;
        this.rows = 30;
        this.cols = 30;
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
        this.inner_walls_count = 300;
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
                if (this.g[r][c] || this.g[c][r])
                    continue;
                // left down and right up can not be walls
                if ((r == this.rows - 2 && c == 1) || (r == 1 && c == this.cols - 2))
                    continue;
                this.g[r][c] = this.g[c][r] = true;
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

    update() {
        this.update_size();
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