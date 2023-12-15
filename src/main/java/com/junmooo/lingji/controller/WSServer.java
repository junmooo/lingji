package com.junmooo.lingji.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.junmooo.lingji.utils.CallWithMsg;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@ServerEndpoint("/ws/res/{user_id}")
@Component
@Log
public class WSServer {
    private String userId;

    @OnOpen
    public void onOpen(Session session, @PathParam("user_id") String userId) {
        this.userId = userId;
        System.out.println("建立连接"+ session.getUserProperties().toString());
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("关闭连接");
    }

    @OnMessage
    public void onMessage(String messages, Session session) {
        try {
            JSONArray jsonArray = JSONArray.parse(messages);
            ArrayList<JSONObject> list = new ArrayList<>();
            jsonArray.stream().map(e -> JSON.parseObject(e.toString(), JSONObject.class)).forEach(list::add);
//            session.getBasicRemote().sendText("alive");

            if (list.get(0).get("heartBath") != null) {
                session.getBasicRemote().sendText("alive");
            } else {
                log.info(36 + list.toString());
                CallWithMsg.callWithStreamMsg(list, session);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
