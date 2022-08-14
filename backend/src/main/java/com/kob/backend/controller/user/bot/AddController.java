package com.kob.backend.controller.user.bot;


import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AddController {


    @Autowired
    private AddService addService;


    @PostMapping("/user/bot/add/")
    public Map<String, String> add(@RequestParam Map<String, String> data) {
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        return addService.add(title, description, content);
    }
}
