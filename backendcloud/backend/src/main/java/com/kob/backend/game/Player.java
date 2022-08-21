package com.kob.backend.game;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class Player {
    private Integer id;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;

    private boolean check_tail_increasing(int step) {
        if (step <= 10) { // 前十回合蛇每回合增长，之后每两回合增长一次
            return true;
        }
        return step % 2 != 1;
    }

    public String getStepString() {
        StringBuilder sb = new StringBuilder();
        for (Integer step : steps) {
            sb.append(step);
        }
        return sb.toString();
    }

    public List<Cell> getCells() {
        ArrayList<Cell> cells = new ArrayList<>();
        int dx[] = {-1, 0, 1, 0};
        int dy[] = {0, 1, 0, -1};
        int step = 0;
        int x = sx, y = sy;
        cells.add(new Cell(x, y));
        for (int i = 0; i < steps.size(); i++) {
            int d = steps.get(i);
            x += dx[d];
            y += dy[d];
            cells.add(new Cell(x, y));
            if (!check_tail_increasing(++step)) {
                cells.remove(0);
            }
        }

        return cells;
    }
}
