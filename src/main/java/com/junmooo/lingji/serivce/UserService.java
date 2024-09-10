package com.junmooo.lingji.serivce;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.junmooo.lingji.mapper.user.UserMapper;
import com.junmooo.lingji.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;


    public User getUserByName(String name) throws Exception {
        User user = User.builder().name(name).build();
        QueryWrapper<User> qw = new QueryWrapper<>(user);
        return userMapper.selectOne(qw);
    }

    public User add(User user) throws Exception {
        user.setId(UUID.randomUUID().toString());
        user.setTimeCreated(System.currentTimeMillis());
        if (userMapper.insert(user) == 1) {
            return user;
        }
        throw new Exception("user insert failed");
    }

}
