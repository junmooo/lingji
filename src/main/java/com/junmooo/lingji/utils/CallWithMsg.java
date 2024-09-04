package com.junmooo.lingji.utils;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSONObject;
import com.junmooo.lingji.serivce.AigcService;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class CallWithMsg {

    public static String callWithMessage(ArrayList<JSONObject> messages) throws NoApiKeyException, ApiException, InputRequiredException {

        Generation gen = new Generation();
        MessageManager msgManager = new MessageManager(10);
        Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content("You are a helpful assistant.").build();
        msgManager.add(systemMsg);
        messages.forEach(e -> {
            Object answer = e.get("answer");
            Object question = e.get("question");
            if (question != null) {
                msgManager.add(Message.builder().role(Role.USER.getValue()).content(question.toString()).build());
            }
            if (answer != null) {
                msgManager.add(Message.builder().role(Role.ASSISTANT.getValue()).content(answer.toString()).build());
            }
        });
        QwenParam param = QwenParam.builder().model(Generation.Models.QWEN_TURBO).messages(msgManager.get()).resultFormat(QwenParam.ResultFormat.MESSAGE).topP(0.8).enableSearch(true).build();
        return gen.call(param).getOutput().getChoices().get(0).getMessage().getContent();
    }

    @Autowired
    private AigcService aigcService;
    public static void callWithStreamMsg(ArrayList<JSONObject> messages, Session session) throws NoApiKeyException, ApiException, InputRequiredException, InterruptedException {

        final JSONObject currentDialog = new JSONObject();
        String currentQuestion = "";

        Generation gen = new Generation();
        MessageManager msgManager = new MessageManager(10);
        Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content("You are a helpful assistant.").build();
        msgManager.add(systemMsg);
        messages.forEach(e -> {
            Object answer = e.get("answer");
            Object question = e.get("question");
            currentDialog.put("question", question);

            if (question != null) {
                msgManager.add(Message.builder().role(Role.USER.getValue()).content(question.toString()).build());
            }
            if (answer != null) {
                msgManager.add(Message.builder().role(Role.ASSISTANT.getValue()).content(answer.toString()).build());
            }
        });
        QwenParam param = QwenParam.builder().model(Generation.Models.QWEN_PLUS).messages(msgManager.get()).resultFormat(QwenParam.ResultFormat.MESSAGE).topP(0.8).enableSearch(true).build();


        Semaphore semaphore = new Semaphore(0);
        StringBuilder fullContent = new StringBuilder();
        gen.streamCall(param, new ResultCallback<GenerationResult>() {

            @Override
            public void onEvent(GenerationResult message) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("requestId", message.getRequestId());
                    jsonObject.put("content", message.getOutput().getChoices().get(0).getMessage().getContent());
                    currentDialog.put("response", message.getOutput().getChoices().get(0).getMessage().getContent());
                    session.getBasicRemote().sendText(jsonObject.toJSONString());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Exception err) {
                System.out.println(String.format("Exception: %s", err.getMessage()));
                semaphore.release();
            }

            @Override
            public void onComplete() {
                System.out.println("Completed");
                session.getAsyncRemote().sendText("!$over$!");
                semaphore.release();
            }
        });
        semaphore.acquire();
    }
}
