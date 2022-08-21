package com.kob.matchsystem.controller;


import com.kob.matchsystem.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping("/player/add")
    public String addPlayer(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(data.getFirst("userId"));
        Integer rating = Integer.parseInt(data.getFirst("rating"));
        return matchService.addPlayer(userId, rating);
    }

    @PostMapping("/player/remove")
    public String removePlayer(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(data.getFirst("userId"));
        return matchService.removePlayer(userId);
    }
}
