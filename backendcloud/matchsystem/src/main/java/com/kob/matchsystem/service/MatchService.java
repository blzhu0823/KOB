package com.kob.matchsystem.service;

public interface MatchService {
    String addPlayer(Integer userId, Integer rating);
    String removePlayer(Integer userId);
}
