package com.junmooo.lingji.controller;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.junmooo.lingji.constants.CommonResponse;
import com.junmooo.lingji.constants.ErrorCode;
import com.junmooo.lingji.model.Dict;
import com.junmooo.lingji.serivce.DictService;
import com.junmooo.lingji.utils.CallWithMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("ask")
@CrossOrigin(origins = "*")
public class HelloController {


    @Autowired
    private DictService dictService;

    @PostMapping("me")
    public JSONObject world(@RequestBody ArrayList<JSONObject> messages) {
        String res;
        try {
            System.out.println("hello: i`m in");
            res = CallWithMsg.callWithMessage(messages);
        } catch (NoApiKeyException e) {
            return CommonResponse.error(ErrorCode.UNKNOWN_ERROR.getCode(), ErrorCode.UNKNOWN_ERROR.getMessage());
        } catch (InputRequiredException e) {
            return CommonResponse.error(ErrorCode.NO_API_KEY_ERROR.getCode(), ErrorCode.NO_API_KEY_ERROR.getMessage());
        }
        return CommonResponse.success(res);
    }

    @PostMapping("dict-save")
    public JSONObject saveDict(@RequestBody Dict dict) {
        try {
            System.out.println(dict.toString());
            if (dictService.save(dict) == 1) {
                return CommonResponse.success();
            }
            return CommonResponse.error(ErrorCode.DATABASE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.error(ErrorCode.NO_API_KEY_ERROR.getCode(), ErrorCode.NO_API_KEY_ERROR.getMessage());
        }
    }

    @GetMapping("sse")
    public SseEmitter sse() {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                emitter.send("Message 1: Hello, this is the first message.");
                Thread.sleep(1000); // 1 second delay

                emitter.send("Message 2: Here comes the second message.");
                Thread.sleep(1000); // 1 second delay

                emitter.send("Message 3: Finally, this is the third message.");
                emitter.send("$Es-end$");
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        return emitter;
    }

}
