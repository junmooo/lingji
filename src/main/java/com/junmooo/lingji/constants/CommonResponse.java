package com.junmooo.lingji.constants;

import com.alibaba.fastjson2.JSONObject;

public class CommonResponse {

    public static JSONObject success(Object data) {
        JSONObject res = new JSONObject();
        res.put("code", ErrorCode.SUCCESS.getCode());
        res.put("msg", ErrorCode.SUCCESS.getMessage());
        res.put("data", data);
        return res;
    }

    public static JSONObject success() {
        JSONObject res = new JSONObject();
        res.put("code", ErrorCode.SUCCESS.getCode());
        res.put("msg", ErrorCode.SUCCESS.getMessage());
        res.put("data", null);
        return res;
    }

    public static JSONObject error(int code, String msg, Object data) {
        JSONObject res = new JSONObject();
        res.put("code", code);
        res.put("msg", msg);
        res.put("data", data);
        return res;
    }
    public static JSONObject error(int code, String msg) {
        JSONObject res = new JSONObject();
        res.put("code", code);
        res.put("msg", msg);
        res.put("data", null);
        return res;
    }

}
