package com.junmooo.lingji.controller;

import com.alibaba.fastjson2.JSONObject;
import com.junmooo.lingji.constants.CommonResponse;
import com.junmooo.lingji.constants.ErrorCode;
import com.junmooo.lingji.model.User;
import com.junmooo.lingji.model.UserToken;
import com.junmooo.lingji.serivce.UserService;
import com.junmooo.lingji.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("login")
    public JSONObject login(@RequestBody User user) {
        try {
            User retUser = userService.getUserByName(user.getName());

            if (retUser == null) {
                return CommonResponse.error(ErrorCode.LOGIN_FAIL);
            }
            if (retUser.getDeleteFlag().equals("0")) {
                return CommonResponse.error(ErrorCode.LOGIN_FAIL);
            }
            if (!user.getPwd().equals(retUser.getPwd())) {
                return CommonResponse.error(ErrorCode.LOGIN_FAIL);
            }
            String token = TokenUtils.generateUserToken(UserToken.builder().id(retUser.getId()).name(retUser.getName()).email(retUser.getEmail()).avatar(retUser.getAvatar()).build(), 60);
            JSONObject res = new JSONObject();
            res.put("user", retUser);
            res.put("token", token);
            return CommonResponse.success(res);
        }catch (Exception e) {
            System.out.println(e);
            return CommonResponse.error(ErrorCode.SQL_ERROR);
        }
    }
    @GetMapping("getName")
    public JSONObject getName(@RequestParam String name) {
        try {
            return CommonResponse.success(userService.getUserByName(name));
        } catch (Exception e) {
            return CommonResponse.error(ErrorCode.SQL_ERROR);
        }
    }

    @PostMapping("register")
    public JSONObject register(@RequestBody User user) {
        try {
            User retUser = userService.add(user);
            JSONObject res = CommonResponse.success(retUser);
            return res;
        } catch (Exception e) {
            return CommonResponse.error(ErrorCode.SQL_ERROR);
        }
    }
}
