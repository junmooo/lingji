package com.junmooo.lingji.constants;

public enum ErrorCode {
    SUCCESS(0, "成功"),
    LOGIN_WRONG_USERNAME_OR_PASSWORD(9001, "用户名或密码错误！"),
    REGISTER_WRONG_USERNAME_ALREADY_USED(9002, "该用户名已经被使用！"),
    INVALID_INPUT(1001, "无效的输入"),
    LOGIN_FAIL(1002, "用户名或密码错误"),
    DATABASE_ERROR(2001, "数据库错误"),
    NETWORK_ERROR(3001, "网络错误"),
    NO_API_KEY_ERROR(4001, "没找到API KEY"),
    UNKNOWN_ERROR(9999, "未知错误"),
    SQL_ERROR(5001, "数据库异常");

    private int code;
    private String message;

    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}