package com.kob.backend.controller.pk;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/pk/")
public class BotInfoController {

    @RequestMapping("getbotinfo/")
    public List<Map<String, String>> getBotInfo() {
        List<Map<String, String>> botInfo = new LinkedList<>();
        botInfo.add(new HashMap<String, String>() {{
            put("name", "bot1");
            put("ip", "localhost");
            put("port", "8080");
        }});
        botInfo.add(new HashMap<String, String>() {{
            put("name", "bot2");
            put("ip", "localhost");
            put("port", "8081");
        }});

        return botInfo;
    }
}
