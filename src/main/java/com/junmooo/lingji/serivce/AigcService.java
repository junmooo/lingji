package com.junmooo.lingji.serivce;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.aigc.imagesynthesis.*;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junmooo.lingji.entities.Text2ImgRequest;
import com.junmooo.lingji.mapper.aigc.DialogueMapper;
import com.junmooo.lingji.mapper.aigc.TextToImgMapper;
import com.junmooo.lingji.model.Dialogue;
import com.junmooo.lingji.model.TextToImg;
import jakarta.websocket.Session;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AigcService {

    @Autowired
    private TextToImgMapper textToImgMapper;

    @Autowired
    private DialogueMapper dialogueMapper;

    @Value("${env.flux-path}")
    private String FLUXPATH;

    private static final OkHttpClient CLIENT = new OkHttpClient();

    ExecutorService executor = Executors.newFixedThreadPool(10); // Adjust the pool size as needed


    public static JSONObject convertToJSONObject(ImageSynthesisResult result) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requestId", result.getRequestId());

        ImageSynthesisOutput output = result.getOutput();
        if (output != null) {
            JSONObject outputJson = new JSONObject();
            outputJson.put("taskId", output.getTaskId());
            outputJson.put("taskStatus", output.getTaskStatus());
            outputJson.put("code", output.getCode());
            outputJson.put("message", output.getMessage());
            outputJson.put("results", output.getResults());

            ImageSynthesisTaskMetrics taskMetrics = output.getTaskMetrics();
            if (taskMetrics != null) {
                JSONObject taskMetricsJson = new JSONObject();
                taskMetricsJson.put("total", taskMetrics.getTotal());
                taskMetricsJson.put("succeeded", taskMetrics.getSucceeded());
                taskMetricsJson.put("failed", taskMetrics.getFailed());
                outputJson.put("taskMetrics", taskMetricsJson);
            }
            jsonObject.put("output", outputJson);
        }

        ImageSynthesisUsage usage = result.getUsage();
        if (usage != null) {
            JSONObject usageJson = new JSONObject();
            usageJson.put("imageCount", usage.getImageCount());
            jsonObject.put("usage", usageJson);
        }

        return jsonObject;
    }

    public JSONObject basicCall(Text2ImgRequest req, String userId) throws ApiException, NoApiKeyException, IOException {
        ImageSynthesis is = new ImageSynthesis();
        String MODEL = "flux-schnell";

        ImageSynthesisParam param = ImageSynthesisParam.builder().apiKey("sk-40f540e96272456288ff6890c06d9913").model(MODEL).n(1).size(req.getSize()).prompt(req.getPrompt()).steps(req.getSteps()).negativePrompt("garfield").build();

        ImageSynthesisResult result = is.call(param);
        System.out.println(result);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Map<String, String> item : result.getOutput().getResults()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    String paths = new URL(item.get("url")).getPath();
                    String[] parts = paths.split("/");
                    String fileName = parts[parts.length - 1];
                    Request request = new Request.Builder().url(item.get("url")).build();

                    try (Response response = CLIENT.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }
                        File dir = new File(FLUXPATH);
                        if (!dir.isDirectory()) {
                            FileUtils.forceMkdir(dir);
                        }
                        if (response.body() != null) {
                            FileUtils.copyInputStreamToFile(response.body().byteStream(), new File(dir.getAbsolutePath() + "/" + fileName));
                        }

                        TextToImg textToImg = new TextToImg();
                        textToImg.setSize(req.getSize());
                        textToImg.setPrompt(req.getPrompt());
                        textToImg.setSeed(req.getSeed());
                        textToImg.setSteps(req.getSteps());
                        textToImg.setImgId(extractUUID(result.getOutput().getResults().get(0).get("url")));
                        textToImg.setUserId(userId);
                        textToImg.setCreateTime(System.currentTimeMillis());
                        textToImgMapper.insert(textToImg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executor);

            futures.add(future);
        }
        //  CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        // 立即返回结果，而不等待所有异步任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            System.out.println("All tasks completed");
        });

        return convertToJSONObject(result);
    }

    public static String extractUUID(String url) {
        String regex = "/([a-f0-9\\-]{36}-\\d)\\.";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public Integer saveTextToImg(TextToImg textToImg) {
        return textToImgMapper.insert(textToImg);
    }

    public IPage<TextToImg> queryList(Integer pageIndex, Integer pageSize, String id) {
        IPage<TextToImg> page = new Page<>(pageIndex, pageSize);
        QueryWrapper<TextToImg> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time");
        qw.eq("user_id", id);
        return textToImgMapper.selectPage(page, qw);
    }

    public IPage<Dialogue> getDialogues(Integer pageIndex, Integer pageSize, String id) {
        IPage<Dialogue> page = new Page<>(pageIndex, pageSize);
        QueryWrapper<Dialogue> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time");
        qw.eq("user_id", id);
        return dialogueMapper.selectPage(page,qw);
    }

    public void saveDialogue(Dialogue dialogue) {
        dialogueMapper.insert(dialogue);
    }

    public void callWithStreamMsg(ArrayList<JSONObject> messages, Session session, String userId) throws NoApiKeyException, ApiException, InputRequiredException, InterruptedException {

        final Dialogue currentDialog = new Dialogue();
        String currentQuestion = "";

        Generation gen = new Generation();
        MessageManager msgManager = new MessageManager(10);
        Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content("You are a helpful assistant.").build();
        msgManager.add(systemMsg);
        messages.forEach(e -> {
            Object answer = e.get("answer");
            Object question = e.get("question");
            currentDialog.setQuestion(question.toString());

            msgManager.add(Message.builder().role(Role.USER.getValue()).content(question.toString()).build());
            if (answer != null) {
                msgManager.add(Message.builder().role(Role.ASSISTANT.getValue()).content(answer.toString()).build());
            }
        });
        QwenParam param = QwenParam.builder().model(Generation.Models.QWEN_PLUS).messages(msgManager.get()).resultFormat(QwenParam.ResultFormat.MESSAGE).topP(0.8).enableSearch(true).build();

        Semaphore semaphore = new Semaphore(0);
        gen.streamCall(param, new ResultCallback<GenerationResult>() {

            @Override
            public void onEvent(GenerationResult message) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("requestId", message.getRequestId());
                    jsonObject.put("content", message.getOutput().getChoices().get(0).getMessage().getContent());
                    currentDialog.setResponse(message.getOutput().getChoices().get(0).getMessage().getContent());
                    currentDialog.setCreateTime(System.currentTimeMillis());
                    currentDialog.setRequestId(message.getRequestId());
                    currentDialog.setUserId(userId);
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
                saveDialogue(currentDialog);
                session.getAsyncRemote().sendText("!$over$!");
                semaphore.release();
            }
        });
        semaphore.acquire();
    }

    public void callWithStreamMsgSSE(ArrayList<JSONObject> messages, String userId, SseEmitter emitter) throws NoApiKeyException, ApiException, InputRequiredException, InterruptedException {

        final Dialogue currentDialog = new Dialogue();
        String currentQuestion = "";

        Generation gen = new Generation();
        MessageManager msgManager = new MessageManager(10);
        Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content("You are a helpful assistant.").build();
        msgManager.add(systemMsg);
        messages.forEach(e -> {
            Object answer = e.get("answer");
            Object question = e.get("question");
            currentDialog.setQuestion(question.toString());

            msgManager.add(Message.builder().role(Role.USER.getValue()).content(question.toString()).build());
            if (answer != null) {
                msgManager.add(Message.builder().role(Role.ASSISTANT.getValue()).content(answer.toString()).build());
            }
        });
        QwenParam param = QwenParam.builder().model(Generation.Models.QWEN_PLUS).messages(msgManager.get()).resultFormat(QwenParam.ResultFormat.MESSAGE).topP(0.8).enableSearch(true).build();

        Semaphore semaphore = new Semaphore(0);
        gen.streamCall(param, new ResultCallback<GenerationResult>() {

            @Override
            public void onEvent(GenerationResult message) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("requestId", message.getRequestId());
                    jsonObject.put("content", message.getOutput().getChoices().get(0).getMessage().getContent());
                    currentDialog.setResponse(message.getOutput().getChoices().get(0).getMessage().getContent());
                    currentDialog.setCreateTime(System.currentTimeMillis());
                    currentDialog.setRequestId(message.getRequestId());
                    currentDialog.setUserId(userId);
                    emitter.send(jsonObject.toJSONString());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Exception err) {
                System.out.println(String.format("Exception: %s", err.getMessage()));
                semaphore.release();
            }

            @SneakyThrows
            @Override
            public void onComplete() {
                System.out.println("Completed");
                saveDialogue(currentDialog);
                emitter.send("!$over$!");
                emitter.complete();
                semaphore.release();
            }
        });
        semaphore.acquire();
    }
}
