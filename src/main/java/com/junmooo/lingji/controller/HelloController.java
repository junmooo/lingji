package com.junmooo.lingji.controller;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.junmooo.lingji.constants.CommonResponse;
import com.junmooo.lingji.constants.ErrorCode;
import com.junmooo.lingji.utils.CallWithMsg;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("ask")
public class HelloController {

    @PostMapping ("me")
    public JSONObject world(@RequestBody ArrayList<JSONObject> messages) {
        String res;
        try {
            System.out.println("hello: i`m in");
            res = CallWithMsg.callWithMessage(messages);
        } catch (NoApiKeyException e) {
            return CommonResponse.error(ErrorCode.UNKNOWN_ERROR.getCode(),ErrorCode.UNKNOWN_ERROR.getMessage());
        } catch (InputRequiredException e) {
            return CommonResponse.error(ErrorCode.NO_API_KEY_ERROR.getCode(),ErrorCode.NO_API_KEY_ERROR.getMessage());
        }
        return CommonResponse.success(res);
    }
}
