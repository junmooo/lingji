package com.junmooo.lingji.controller;

import com.alibaba.dashscope.aigc.imagesynthesis.*;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.junmooo.lingji.constants.CommonResponse;
import com.junmooo.lingji.constants.ErrorCode;
import com.junmooo.lingji.entities.Text2ImgRequest;
import com.junmooo.lingji.model.Dialogue;
import com.junmooo.lingji.model.TextToImg;
import com.junmooo.lingji.serivce.AigcService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;

@RestController
@RequestMapping("aigc")
@CrossOrigin(origins = "*")
public class AigcController {


    @Autowired
    private AigcService aigcService;

    public void fetchTask() throws ApiException, NoApiKeyException {
        String taskId = "your task id";
        ImageSynthesis is = new ImageSynthesis();
        // If set DASHSCOPE_API_KEY environment variable, apiKey can null.
        ImageSynthesisResult result = is.fetch(taskId, null);
        System.out.println(result.getOutput());
        System.out.println(result.getUsage());
    }

    @GetMapping("get-img")
    public String getImg() throws NoApiKeyException, IOException {

        try {
            fetchTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "filed";
    }

    @PostMapping("text-2-img")
    public JSONObject text2Img(@RequestBody Text2ImgRequest request) {
        try {
            return aigcService.basicCall(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("save-text-2-img")
    public JSONObject saveText2Img(@RequestBody TextToImg textToImg) {
        try {
            if (aigcService.saveTextToImg(textToImg) == 1) {
                return CommonResponse.success();
            }
            return CommonResponse.error(ErrorCode.SQL_ERROR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("text-2-img-history")
    public JSONObject text2ImgHistory(@Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize) {
        try {
            return CommonResponse.success(aigcService.queryList(pageIndex, pageSize));
        } catch (Exception e) {
            return CommonResponse.error(ErrorCode.DATABASE_ERROR);
        }
    }

    @GetMapping("get-dialogues")
    public JSONObject getDialogue(@Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize) throws NoApiKeyException, IOException {
        try {
            IPage<Dialogue> dialogues = aigcService.getDialogues(pageIndex, pageSize);
            return CommonResponse.success(dialogues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
