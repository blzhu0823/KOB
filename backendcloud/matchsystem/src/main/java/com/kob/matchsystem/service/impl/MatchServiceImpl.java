package com.kob.matchsystem.service.impl;


import com.kob.matchsystem.service.MatchService;
import com.kob.matchsystem.utils.MatchPool;
import org.springframework.stereotype.Service;

@Service
public class MatchServiceImpl implements MatchService {

    public static final MatchPool matchPool = new MatchPool();
    @Override
    public String addPlayer(Integer userId, Integer rating, Integer botId) {
        System.out.println("addPlayer: " + userId + " " + rating + " " + botId);
        matchPool.addPlayer(userId, rating, botId);
        return "add player success";
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("removePlayer: " + userId);
        matchPool.removePlayer(userId);
        return "remove player success";
    }

}
