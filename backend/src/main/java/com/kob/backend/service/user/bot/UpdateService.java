package com.kob.backend.service.user.bot;


import java.util.Map;

public interface UpdateService {
    Map<String, String> update(Integer id, String title, String description, String content);
}
