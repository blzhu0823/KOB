package com.kob.botrunningsystem.utils;


import java.util.ArrayList;
import java.util.List;

public class GameStatus {
    public int[][] board = new int[15][20];
    public List<Cell> me;
    public List<Cell> you;
    public Integer step;

    private boolean check_tail_increasing(int step) {
        if (step <= 10) { // 前十回合蛇每回合增长，之后每两回合增长一次
            return true;
        }
        return step % 2 != 1;
    }

    private List<Cell> getCells(Integer sx, Integer sy, String steps) {
        ArrayList<Cell> cells = new ArrayList<>();
        int dx[] = {-1, 0, 1, 0};
        int dy[] = {0, 1, 0, -1};
        int step = 0;
        int x = sx, y = sy;
        cells.add(new Cell(x, y));
        for (int i = 0; i < steps.length(); i++) {
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            cells.add(new Cell(x, y));
            if (!check_tail_increasing(++step)) {
                cells.remove(0);
            }
        }

        return cells;
    }

    public GameStatus(String game) {
        String[] strs = game.split("#");
        for (int i = 0, k = 0; i < 15; i++) {
            for (int j = 0; j < 20; j++, k++) {
                if (strs[0].charAt(k) == '1') {
                    board[i][j] = 1;
                } else {
                    board[i][j] = 0;
                }
            }
        }

        int aSx = Integer.parseInt(strs[1]);
        int aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]);
        int bSy = Integer.parseInt(strs[5]);

        me = getCells(aSx, aSy, strs[3].substring(1, strs[3].length()-1));
        you = getCells(bSx, bSy, strs[6].substring(1, strs[6].length()-1));
        step = Integer.parseInt(String.valueOf(strs[3].length() + 1));
    }
}
