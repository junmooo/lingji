package com.junmooo.lingji.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@TableName("dict")
@ToString
public class Dict {
    @TableId(value = "id")
    private Long id;
    private String dictKey;
    private String dictValue;
    private String description;
    private String extraField1;
    private String extraField2;
    private String deleteFlag;
    private Long timeCreated;
    private Long timeUpdated;
}
