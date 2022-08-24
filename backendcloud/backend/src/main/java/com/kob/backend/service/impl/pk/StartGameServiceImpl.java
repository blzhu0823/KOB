package com.kob.backend.service.impl.pk;


import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {

    @Override
    public String startGame(Integer userId1, Integer userId2, Integer botId1, Integer botId2) {
        System.out.println("startGame: " + userId1 + " " + userId2);
        WebSocketServer.startGame(userId1, userId2, botId1, botId2);
        return "start game success";
    }
}
