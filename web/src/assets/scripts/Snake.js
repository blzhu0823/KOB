import { GameObject } from "./GameObject";
import { Cell } from "./Cell";


export class Snake extends GameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;
        this.cells = [new Cell(info.r, info.c)]; // 蛇头
        this.next_cell = null; // 蛇头下一个位置
        this.speed = 5; // 蛇每秒移动的格数
        this.eps = 1e-2 // 允许误差

        this.status = "idle"; // idle: 空闲，move: 移动，die: 死亡
        this.direction = -1 // -1: 待输入方向, 0: 向上, 1: 向右, 2: 向下, 3: 向左
        this.dx = [-1, 0, 1, 0];
        this.dy = [0, 1, 0, -1];

        this.step = 0; // 回合数

        if (this.id === 0)
            this.eye_direction = 0;
        else
            this.eye_direction = 2
        this.eye_dx = [[-1, 1], [1, 1], [-1, 1], [-1, -1]];
        this.eye_dy = [[-1, -1], [-1, 1], [1, 1], [-1, 1]];
        this.eye_color = "black";

    }

    start() {

    }

    check_tail_increasing() {
        if (this.step <= 10) {
            return true;
        }

        return this.step % 2 !== 1;
    }

    move() {
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < this.eps) {
            this.cells[0] = this.next_cell;
            this.next_cell = null;
            this.status = "idle";

            if (!this.check_tail_increasing()) {
                this.cells.pop();
            }
        } else {
            const move_dist = Math.min(this.speed * this.timedelta / 1000, dist); // 每两帧走的距离
            this.cells[0].x += move_dist * dx / dist;
            this.cells[0].y += move_dist * dy / dist;

            const k = this.cells.length;
            const tail = this.cells[k - 1];
            const tail_target = this.cells[k - 2];
            const tail_dx = tail_target.x - tail.x;
            const tail_dy = tail_target.y - tail.y;
            if (!this.check_tail_increasing()) {
                tail.x += move_dist * tail_dx / dist;
                tail.y += move_dist * tail_dy / dist;
            }
        }
    }

    set_direction(d) {
        this.direction = d;
    }


    update() {
        if (this.status === "move") {
            this.move();
        }
        this.render();
    }

    next_step() {
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dx[d], this.cells[0].c + this.dy[d]);
        this.status = "move";
        this.direction = -1;
        this.eye_direction = d;
        this.step++;

        const k = this.cells.length;
        for (let i = k; i > 0; i--) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
        }

    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        for (const cell of this.cells) {
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L * 0.4, 0, 2 * Math.PI);
            ctx.fill();
        }

        for (let i = 0; i < this.cells.length - 1; i++) {
            const a = this.cells[i];
            const b = this.cells[i + 1];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) {
                continue;
            } else if (Math.abs(a.x - b.x) < this.eps) {
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }
        
        // 画眼睛
        ctx.fillStyle = this.eye_color;
        for (let i = 0; i < 2; i++) {
            const dx = this.eye_dx[this.eye_direction][i];
            const dy = this.eye_dy[this.eye_direction][i];
            ctx.beginPath();
            ctx.arc((this.cells[0].x + dx * 0.1) * L, (this.cells[0].y + dy * 0.1) * L, L * 0.05, 0, 2 * Math.PI);
            ctx.fill();
        }
    }
}