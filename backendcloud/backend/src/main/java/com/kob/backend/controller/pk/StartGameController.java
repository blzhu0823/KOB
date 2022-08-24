package com.kob.backend.controller.pk;


import com.kob.backend.service.pk.StartGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartGameController {

    @Autowired
    private StartGameService startGameService;

    @PostMapping("/pk/startgame/")
    public String startGame(@RequestParam MultiValueMap<String, String> data) {
        Integer userId1 = Integer.parseInt(data.getFirst("userId1"));
        Integer userId2 = Integer.parseInt(data.getFirst("userId2"));
        Integer botId1 = Integer.parseInt(data.getFirst("botId1"));
        Integer botId2 = Integer.parseInt(data.getFirst("botId2"));

        return startGameService.startGame(userId1, userId2, botId1, botId2);
    }
}
