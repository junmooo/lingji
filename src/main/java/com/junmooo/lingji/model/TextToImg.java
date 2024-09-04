package com.junmooo.lingji.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("text_to_img")
public class TextToImg {

    @TableId
    private String imgId;

    private String taskId;

    private String requestId;

    private String prompt;

    private String size;

    private Integer seed;

    private Integer steps;

    private String userId;

    private Long createTime;
}