package com.kob.matchsystem.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


@Component
public class MatchPool extends Thread {

    private static List<Player> players = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private static final String startGameUrl = "http://127.0.0.1:3000/pk/startgame/";

    private static RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchPool.restTemplate = restTemplate;
    }
    public void addPlayer(Integer userId, Integer rating) {
        lock.lock();
        try {
            players.add(new Player(userId, rating, 0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            players.removeIf(player -> player.getUserId().equals(userId));
        } finally {
            lock.unlock();
        }

    }

    private void increasingWaitingTime() {
        for (Player player : players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    boolean checkMatched(Player a, Player b) {
        Integer ratingDiff = Math.abs(a.getRating() - b.getRating());
        Integer minWaitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDiff <= minWaitingTime * 10;
    }

    private void sendResult(Integer a, Integer b) {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.put("userId1", Collections.singletonList(a.toString()));
        data.put("userId2", Collections.singletonList(b.toString()));
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    private void matchPlayers() {
        boolean[] used = new boolean[players.size()];
        for (int i = 0; i < players.size(); i++) {
            if (used[i]) {
                continue;
            }
            for (int j = i + 1; j < players.size(); j++) {
                if (used[j]) {
                    continue;
                }
                Player a = players.get(i);
                Player b = players.get(j);
                if (checkMatched(a, b)) {
                    used[i] = true;
                    used[j] = true;
                    sendResult(a.getUserId(), b.getUserId());
                    break;
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if (!used[i]) {
                newPlayers.add(players.get(i));
            }
        }

        players = newPlayers;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();
            try {
                increasingWaitingTime();
                matchPlayers();
            } finally {
                lock.unlock();
            }
            System.out.println(players);
        }
    }

}
