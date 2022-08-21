package com.kob.backend.service.impl.user.bot;


import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Integer id, String title, String description, String content) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
        User user = loginUser.getUser();
        Integer userId = user.getId();
        Bot bot = botMapper.selectById(id);
        Map<String, String> response = new HashMap<>();


        if (title == null || title.isEmpty()) {
            response.put("error_message", "标题不能为空");
            return response;
        }
        if (title.length() > 100) {
            response.put("error_message", "标题长度不能超过100");
            return response;
        }
        if (description == null || description.isEmpty()) {
            description = "这个用户很懒，什么也没留下~";
        }
        if (description.length() > 300) {
            response.put("error_message", "描述长度不能超过300");
            return response;
        }

        if (content == null || content.isEmpty()) {
            response.put("error_message", "代码不能为空");
            return response;
        }

        if (content.length() > 10000) {
            response.put("error_message", "代码长度不能超过10000");
            return response;
        }

        if (bot == null) {
            response.put("error_message", "Bot不存在或已被删除");
            return response;
        }

        if (!bot.getUserId().equals(userId)) {
            response.put("error_message", "您没有权限修改该Bot");
            return response;
        }

        Bot newBot = new Bot(
                bot.getId(),
                bot.getUserId(),
                title,
                description,
                content,
                bot.getCreateTime(),
                new Date()
        );

        botMapper.updateById(newBot);

        response.put("error_message", "success");
        return response;
    }

}
