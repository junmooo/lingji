package com.junmooo.lingji;

// Copyright (c) Alibaba, Inc. and its affiliates.

import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.tokenizers.Tokenization;
import com.alibaba.dashscope.tokenizers.TokenizationResult;
import com.alibaba.dashscope.utils.Constants;

public class TokenizationQuickStart {
    public static void tokenizer() throws ApiException, NoApiKeyException, InputRequiredException {
        Constants.apiKey="sk-40f540e96272456288ff6890c06d9913";
        Tokenization tokenizer = new Tokenization();
        MessageManager messageManager = new MessageManager(10);
        messageManager.add(Message.builder().role(Role.USER.getValue()).content("你好？").build());
        QwenParam param = QwenParam.builder()
                .model(Tokenization.Models.QWEN_PLUS)
                .messages(messageManager.get())
                .build();
        TokenizationResult result = tokenizer.call(param);
        System.out.println(result);
    }

    public static void main(String[] args) {
        try {
            tokenizer();
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            System.out.println(String.format("Exception %s", e.getMessage()));
        }
        System.exit(0);
    }
}