package com.kob.backend.service.user.bot;

import java.util.Map;

public interface AddService {
    Map<String, String> add(String title, String description, String content);
}
