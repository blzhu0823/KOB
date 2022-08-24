package com.kob.botrunningsystem.service;

import com.kob.botrunningsystem.service.impl.BotRunningServiceImpl;

public interface BotRunningService {

    String addBot(Integer userId, String botCode, String game);

}
