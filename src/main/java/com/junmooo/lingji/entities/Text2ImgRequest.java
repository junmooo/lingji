package com.junmooo.lingji.entities;

import lombok.Data;

@Data
public class Text2ImgRequest {
    private String prompt;
    private String size;
    private Integer seed;
    private Integer steps;
}
