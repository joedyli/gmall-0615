package com.atguigu.gmall.order.interceptor;

import com.atguigu.core.bean.UserInfo;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.order.config.JwtProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties properties;

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // threadLocal中的载荷信息：userId userKey
        UserInfo userInfo = new UserInfo();

        // 获取cookie信息（GMALL_TOKEN,  UserKey）
        String token = CookieUtils.getCookieValue(request, this.properties.getCookieName());

        if (StringUtils.isEmpty(token)) {
            return false;
        }

        try {
            // 解析gmall_token
            Map<String, Object> userInfoMap = JwtUtils.getInfoFromToken(token, this.properties.getPublicKey());

            userInfo.setUserId(Long.valueOf(userInfoMap.get("id").toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        THREAD_LOCAL.set(userInfo);

        return true;
    }

    public static UserInfo get(){
        return THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 因为咱们使用的是tomcat线程池，请求结束不代表线程结束
        THREAD_LOCAL.remove();
    }
}
