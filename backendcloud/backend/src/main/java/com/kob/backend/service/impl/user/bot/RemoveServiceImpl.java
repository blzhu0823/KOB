package com.kob.backend.service.impl.user.bot;


import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveServiceImpl implements RemoveService {

    @Autowired
    private BotMapper botMapper;


    @Override
    public Map<String, String> remove(Integer id) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
        User user = loginUser.getUser();
        Integer userId = user.getId();
        Bot bot = botMapper.selectById(id);
        Map<String, String> response = new HashMap<>();
        if (bot == null) {
            response.put("error_messsage", "Bot不存在或已被删除");
            return response;
        }

        if (!bot.getUserId().equals(userId)) {
            response.put("error_messsage", "没有权限删除该Bot");
            return response;
        }

        botMapper.deleteById(id);
        response.put("error_message", "success");
        return response;
    }
}
