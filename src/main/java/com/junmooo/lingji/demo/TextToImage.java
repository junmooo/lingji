package com.junmooo.lingji.demo;// Copyright (c) Alibaba, Inc. and its affiliates.

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TextToImage {
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final String MODEL = "flux-schnell";
    private static final String PROMPT = "Eagle flying freely in the blue sky and white clouds";
    private static final String SIZE = "1024*1024";


    public static void main(String[] args){
        String url = "https://dashscope.aliyuncs.com/api/v1/tasks/d8b62fc1-f8a5-4be7-8cbc-6118fb7f376a";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Authorization", "Bearer sk-40f540e96272456288ff6890c06d9913")
                .addHeader("Origin", "http://localhost:1420")
                .addHeader("Referer", "http://localhost:1420/")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Site", "cross-site")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko)")
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Print the response body
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}