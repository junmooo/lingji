package com.junmooo.lingji;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class QwenChat {
    private static final String API_URL = "https://qwen.aliyun.com/api/prompt";
    private static final String API_KEY = "your_api_key_here";

    public static void main(String[] args) throws Exception {
        // 创建一个请求ID，用于保持会话的上下文
        String requestId = "1234567890abcdef";
        // 发起一次对话请求
        String response = chat(requestId, "你好，通义千问！");
        System.out.println(response);
        // 使用相同的request ID发起另一次对话请求，以保持上下文
        response = chat(requestId, "你能帮我解决一个问题吗？");
        System.out.println(response);
    }

    private static String chat(String requestId, String message) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\n");
        requestBody.append(" \"prompt\": \"" + message + "\",\n");
        requestBody.append(" \"history\": [\n");
        requestBody.append(" {\n");
        requestBody.append(" \"id\": \"" + requestId + "\",\n");
        requestBody.append(" \"content\": \"" + message + "\",\n");
        requestBody.append(" \"role\": \"user\"\n");
        requestBody.append(" }\n");
        requestBody.append(" ]\n");
        requestBody.append("}");
        connection.setDoOutput(true);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"))) {
            writer.write(requestBody.toString());
        }
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
        String output;
        StringBuilder responseText = new StringBuilder();
        while ((output = br.readLine()) != null) {
            responseText.append(output);
        }
        return responseText.toString();
    }
}