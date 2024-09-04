package com.junmooo.lingji.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dialogue")
public class Dialogue {

    @TableId
    private String requestId;
    private String question;
    private String response;
    private String userId;
    private Long createTime;
}