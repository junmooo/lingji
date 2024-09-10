package com.junmooo.lingji.interceptor;

import com.junmooo.lingji.constants.CommonResponse;
import com.junmooo.lingji.constants.ErrorCode;
import com.junmooo.lingji.model.UserToken;
import com.junmooo.lingji.utils.TokenUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class ClientInterceptor implements HandlerInterceptor {

    private boolean resp403(HttpServletResponse response) throws Exception{
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(403);
        PrintWriter out = response.getWriter();
        out.write(CommonResponse.error(ErrorCode.WRONG_TOKEN).toJSONString());
        out.close();
        response.flushBuffer();
        return false;
    }

    /**
     * handler 对应@RequestMapping对应的controller对象
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("token");
        String s = "";
        try{
            UserToken userToken = TokenUtils.getInfoFromUserToken(request.getHeader("token"));
            request.setAttribute("userToken",userToken);
        }catch (InvalidJwtException e){
            return resp403(response);
        }
        return true;//放行
    }
}
