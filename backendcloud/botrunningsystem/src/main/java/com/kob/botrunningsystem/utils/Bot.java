package com.kob.botrunningsystem.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Bot extends Thread {
    private Integer userId;
    private String botCode;
    private String game;

    private static RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Bot.restTemplate = restTemplate;
    }

    private static final String botMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";

    public void executeBot(Integer timeout) {
        this.start();
        try {
            this.join(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            this.interrupt();
        }
    }

    private String adduid(String code, String uid) {
        int k = code.indexOf(" implements BotInterface");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);
        GameStatus gameStatus = new GameStatus(game);
        BotInterface botInterface = Reflect.compile(
                "com.kob.botrunningsystem.utils.botimpl.Bot" + uid,
                "package com.kob.botrunningsystem.utils.botimpl;\n" +
                        "import com.kob.botrunningsystem.utils.BotInterface;\n" +
                        "import com.kob.botrunningsystem.utils.GameStatus;\n" +
                        "import com.kob.botrunningsystem.utils.Cell;\n" +
                        adduid(botCode, uid)).create().get();
        Integer direction = botInterface.nextMove(gameStatus);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.put("userId", Collections.singletonList(userId.toString()));
        data.put("direction", Collections.singletonList(direction.toString()));

        restTemplate.postForObject(botMoveUrl, data, String.class);
    }
}
