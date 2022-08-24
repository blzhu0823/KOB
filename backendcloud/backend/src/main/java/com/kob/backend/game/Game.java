package com.kob.backend.game;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {

    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final static int[] dx = {-1, 0, 1, 0};
    private final static int[] dy = {0, 1, 0, -1};

    private final Player playerA;
    private final Player playerB;


    private Integer nextStepA = null;
    private Integer nextStepB = null;

    private String loser = ""; // all: all players lose; a: player A lose; b: player B lose;

    private Integer eye_directionA = -1; // A死后眼睛朝向
    private Integer eye_directionB = -1; // B死后眼睛朝向

    private static final String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer userId1, Integer userId2, Integer botId1, Integer botId2) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        String botCode1 = null;
        String botCode2 = null;
        if (botId1 != -1) {
            botCode1 = WebSocketServer.botMapper.selectById(botId1).getContent();
        }
        if (botId2 != -1) {
            botCode2 = WebSocketServer.botMapper.selectById(botId2).getContent();
        }
        this.playerA = new Player(userId1, botCode1, rows - 2, 1, new ArrayList<>());
        this.playerB = new Player(userId2, botCode2, 1, cols - 2, new ArrayList<>());
    }


    private ReentrantLock lock = new ReentrantLock();


    public int[][] getG() {
        return g;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();  // 解锁
        }
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) {
            return true;
        }

        this.g[sx][sy] = 1;
        for (int i = 0; i < 4; i++) {
            int nx = sx + dx[i], ny = sy + dy[i];
            if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && g[nx][ny] == 0 && check_connectivity(nx, ny, tx, ty)) {
                this.g[sx][sy] = 0;
                return true;
            }
        }
        this.g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.g[i][j] = 0;
            }
        }

        for (int i = 0; i < this.rows; i++) {
            this.g[i][0] = 1;
            this.g[i][this.cols - 1] = 1;
        }

        for (int i = 0; i < this.cols; i++) {
            this.g[0][i] = 1;
            this.g[this.rows - 1][i] = 1;
        }

        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i++) {
            for (int j = 0; j < 1000; j++) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);
                if (this.g[r][c] == 1 || this.g[this.rows - r - 1][this.cols - c - 1] == 1) {
                    continue;
                }
                if ((r == this.rows - 2 && c == 1) || (r == 1 && c == this.cols - 2)) {
                    continue;
                }
                this.g[r][c] = this.g[this.rows - r - 1][this.cols - c - 1] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }


    public void createMap() {
        for (int i = 0; i < 1000; i++) {
            if (draw())
                break;
        }
    }

    private boolean nextStep() {
        sendBotCode(playerA);
        sendBotCode(playerB);
        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(100);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            lock.lock();
            try {
                if (this.nextStepA != null && this.nextStepB != null) {

                    this.playerA.getSteps().add(this.nextStepA);
                    this.playerB.getSteps().add(this.nextStepB);
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }

        return false;
    }

    private void sendMessage(String message) { // 向两名玩家广播相同的消息 move or result
        WebSocketServer.user2WebSocket.get(this.playerA.getId()).sendMessage(message);
        WebSocketServer.user2WebSocket.get(this.playerB.getId()).sendMessage(message);
    }

    private void sendResult() { // 向客户端发送两名玩家游戏结果
        saveGame();
        JSONObject resp = new JSONObject();
        if (!playerA.getSteps().isEmpty()) {
            eye_directionA = playerA.getSteps().get(playerA.getSteps().size() - 1);
        } else {
            eye_directionA = 0;
        }
        if (!playerB.getSteps().isEmpty()) {
            eye_directionB = playerB.getSteps().get(playerB.getSteps().size() - 1);
        } else {
            eye_directionB = 2;
        }
        resp.put("event", "game-result");
        resp.put("loser", this.loser);
        resp.put("eye_directionA", eye_directionA);
        resp.put("eye_directionB", eye_directionB);
        sendMessage(resp.toJSONString());
    }

    private void saveGame() {
        Record record = new Record(
                null,
                this.playerA.getId(),
                this.playerB.getId(),
                this.playerA.getSx(),
                this.playerA.getSy(),
                this.playerB.getSx(),
                this.playerB.getSy(),
                this.playerA.getStepString(),
                this.playerB.getStepString(),
                mapToString(),
                this.loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }


    private String mapToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                sb.append(this.g[i][j]);
            }
        }
        return sb.toString();
    }

    private void sendMove() { // 向客户端发送两名玩家move消息
        JSONObject resp = new JSONObject();
        resp.put("event", "game-move");
        resp.put("a_direction", this.playerA.getSteps().get(this.playerA.getSteps().size() - 1));
        resp.put("b_direction", this.playerB.getSteps().get(this.playerB.getSteps().size() - 1));
        sendMessage(resp.toJSONString());
        nextStepA = nextStepB = null;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        Cell cell = cellsA.get(cellsA.size() - 1);
        if (this.g[cell.getX()][cell.getY()] == 1) {
            return false;
        }

        for (int i = 0; i < cellsA.size() - 1; i++) {
            if (cellsA.get(i).getX() == cell.getX() && cellsA.get(i).getY() == cell.getY()) {
                return false;
            }
        }

        for (int i = 0; i < cellsB.size(); i++) {
            if (cellsB.get(i).getX() == cell.getX() && cellsB.get(i).getY() == cell.getY()) {
                return false;
            }
        }

        return true;
    }

    private boolean judge() { // 判断两名玩家下一步会不会导致游戏结束
        List<Cell> cellsA = this.playerA.getCells();
        List<Cell> cellsB = this.playerB.getCells();
        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA && !validB) {
            this.loser = "all";
            return true;
        } else if (!validA) {
            this.loser = "a";
            return true;
        } else if (!validB) {
            this.loser = "b";
            return true;
        }

        return false;
    }

    private void sendBotCode(Player player) {
        if (player.getBotCode() == null) {
            return; // 亲自出马
        }

        Player me, you;
        if (player.getId().equals(this.playerA.getId())) {
            me = this.playerA;
            you = this.playerB;
        } else {
            me = this.playerB;
            you = this.playerA;
        }

        MultiValueMap<String, String> data = new LinkedMultiValueMap();
        String game = mapToString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepString() + ")";
        data.put("userId", Collections.singletonList(player.getId().toString()));
        data.put("botCode", Collections.singletonList(player.getBotCode()));
        data.put("game", Collections.singletonList(game));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    @Override
    public void run() {
        while (nextStep()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (judge()) {
                sendResult();
                return;
            }

            sendMove();
        }

        lock.lock();
        try {
            if (nextStepA == null && nextStepB == null) {
                loser = "all";
            } else if (nextStepA == null) {
                loser = "a";
            } else {
                loser = "b";
            }
        } finally {
            lock.unlock();
        }


        sendResult();
    }

}
