package com.junmooo.lingji.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;


public class SteamOutput {
// Copyright (c) Alibaba, Inc. and its affiliates.

    public static void streamCallWithCallback()
            throws NoApiKeyException, ApiException, InputRequiredException, InterruptedException {
        Generation gen = new Generation();
        Message userMsg = Message
                .builder()
                .role(Role.USER.getValue())
                .content("如何做西红柿炖牛腩？")
                .build();
        QwenParam param = QwenParam
                .builder()
                .model(Generation.Models.QWEN_PLUS)
                .resultFormat(QwenParam.ResultFormat.MESSAGE)
                .messages(Arrays.asList(userMsg))
                .topP(0.8)
                .incrementalOutput(true) // get streaming output incrementally
                .build();
        ArrayList<Message> list = new ArrayList<>();
        Message systemMsg =
                Message.builder().role(Role.SYSTEM.getValue()).content("You are a helpful assistant.").build();
        Message useMsg = Message.builder().role(Role.USER.getValue()).content("杭州一日游攻略").build();
        Message assistantMsg = Message.builder().role(Role.ASSISTANT.getValue()).content("如果你想在杭州度过一天，我为你提供了四篇最新的杭州一日游攻略。这些建议可以帮助你在有限的时间里最大程度地体验杭州的美丽风光和文化气息。\n" +
                "以下是这四篇攻略的主要内容：\n" +
                "第一篇攻略介绍了春天来灵隐寺的最佳季节以及推荐的游玩地点。你可以在灵隐寺旁边品尝美味的素斋，并在下午4时观看僧众做晚课。\n" +
                "第二篇攻略提供了一条推荐的西湖线路，包括南山路附近的黄金湖岸线和主要景点，如莲花池、断桥和雷峰塔等。\n" +
                "第三篇攻略推荐了上午游览西湖的方法，可以租一辆自行车或乘坐游船欣赏美景，还可以找寻最美的莲花池、断桥和雷峰塔等西湖著名景点。\n" +
                "第四篇攻略是一篇更新的文章，发布于2023年5月23日，也是一份完整的杭州一日游攻略，其中提到了上午游览西湖的方法和其他一些景点。\n" +
                "希望这些建议对你有所帮助！如果你还有其他问题，请随时告诉我。").build();

        list.add(systemMsg);
        list.add(useMsg);
        list.add(assistantMsg);
        list.add(Message.builder().role(Role.USER.getValue()).content("如果是两天呢").build());

        param.setMessages(list);
        Semaphore semaphore = new Semaphore(0);
        StringBuilder fullContent = new StringBuilder();
        gen.streamCall(param, new ResultCallback<GenerationResult>() {

            @Override
            public void onEvent(GenerationResult message) {
                fullContent.append(message.getOutput().getChoices().get(0).getMessage().getContent());
                System.out.println(message);
            }

            @Override
            public void onError(Exception err) {
                System.out.println(String.format("Exception: %s", err.getMessage()));
                semaphore.release();
            }

            @Override
            public void onComplete() {
                System.out.println("Completed");
                semaphore.release();
            }

        });
        semaphore.acquire();
        System.out.println("Full content: \n" + fullContent.toString());
    }


    public static void main(String[] args) {
        Constants.apiKey = "sk-40f540e96272456288ff6890c06d9913";

        try {
            streamCallWithCallback();
        } catch (ApiException | NoApiKeyException | InputRequiredException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
}