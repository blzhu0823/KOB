package com.kob.backend.service.impl.user.account;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> response = new HashMap<>();
        if (username == null || username.trim().length() == 0) {
            response.put("error_message", "用户名不能为空");
            return response;
        }

        if (password == null || confirmedPassword == null || password.length() == 0 || confirmedPassword.length() == 0) {
            response.put("error_message", "密码不能为空");
            return response;
        }

        if (username.trim().length() > 20) {
            response.put("error_message", "用户名长度不能大于20");
            return response;
        }

        if (password.length() > 30 || confirmedPassword.length() > 30) {
            response.put("error_message", "密码长度不能大于30");
            return response;
        }

        if (!password.equals(confirmedPassword)) {
            response.put("error_message", "两次输入的密码不一致");
            return response;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username.trim());
        List<User> users = userMapper.selectList(queryWrapper);

        if (!users.isEmpty()) {
            response.put("error_message", "用户名已存在");
            return response;
        }

        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/116076_lg_613cc9d012.jpg";
        User user = new User(null, username.trim(), encodedPassword, photo);
        userMapper.insert(user);


        response.put("error_message", "success");
        return response;
    }
}
