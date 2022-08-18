package com.kob.backend.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.game.Game;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    public static RecordMapper recordMapper;

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }
    public static final ConcurrentHashMap<Integer, WebSocketServer> user2WebSocket = new ConcurrentHashMap<>();
    private static final CopyOnWriteArraySet<User> matchPool = new CopyOnWriteArraySet<>();
    private Session session = null;
    private static UserMapper userMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    private Game game = null;

    User user = null;


    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        System.out.println("连接建立");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if (this.user != null) {
            user2WebSocket.put(userId, this);
        } else {
            this.session.close();
        }
        System.out.println(this.user);
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        if (this.user != null) {
            user2WebSocket.remove(this.user.getId());
            matchPool.remove(this.user);
        }
        System.out.println("连接关闭");
    }



    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        JSONObject data = JSON.parseObject(message);
        System.out.println(data);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getIntValue("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }


    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void move(int direction) {
        if (this.user.getId().equals(this.game.getPlayerA().getId())) {
            this.game.setNextStepA(direction);
        } else {
            this.game.setNextStepB(direction);
        }
    }

    private void startMatching() {
        System.out.println("开始匹配");
        matchPool.add(this.user);

        if (matchPool.size() >= 2) {
            Iterator<User> it = matchPool.iterator();
            User a = it.next();
            User b = it.next();
            matchPool.remove(a);
            matchPool.remove(b);

            Game game = new Game(15, 20, 30, a.getId(), b.getId());
            game.createMap();
            game.start();

            user2WebSocket.get(a.getId()).game = game;
            user2WebSocket.get(b.getId()).game = game;

            JSONObject respA = new JSONObject();
            respA.put("a_id", game.getPlayerA().getId());
            respA.put("a_sx", game.getPlayerA().getSx());
            respA.put("a_sy", game.getPlayerA().getSy());
            respA.put("event", "match-success");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("gamemap", game.getG());

            JSONObject respB = new JSONObject();
            respB.put("b_id", game.getPlayerB().getId());
            respB.put("b_sx", game.getPlayerB().getSx());
            respB.put("b_sy", game.getPlayerB().getSy());
            respB.put("event", "match-success");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("gamemap", game.getG());

            user2WebSocket.get(a.getId()).sendMessage(respA.toJSONString());
            user2WebSocket.get(b.getId()).sendMessage(respB.toJSONString());


        }
    }

    private void stopMatching() {
        System.out.println("停止匹配");
        matchPool.remove(this.user);
    }
}