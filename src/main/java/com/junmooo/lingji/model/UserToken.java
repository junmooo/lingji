package com.junmooo.lingji.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserToken {
    private String id;
    private String name;
    private String email;
    private String avatar;
}
