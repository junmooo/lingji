package com.junmooo.lingji.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("user")
public class User {

    @TableId(value = "id")
    private String id;
    private String pwd;
    private String name;
    private String email;
    private String phoneNo;
    private String status;
    private String remark;
    private String deleteFlag;
    private Long timeCreated;
    private Long timeUpdated;
    private String avatar;
}
