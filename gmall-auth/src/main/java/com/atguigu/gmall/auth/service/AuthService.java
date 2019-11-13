package com.atguigu.gmall.auth.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.exception.GmallException;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@EnableConfigurationProperties({JwtProperties.class})
public class AuthService {

    @Autowired
    private GmallUmsClient gmallUmsClient;

    @Autowired
    private JwtProperties jwtProperties;

    public String accredit(String userName, String password) {

        try {
            // 1. 远程调用用户中心的数据接口，查询用户信息
            Resp<MemberEntity> memberEntityResp = this.gmallUmsClient.queryUser(userName, password);
            MemberEntity memberEntity = memberEntityResp.getData();

            // 2. 判断用户是否存在，不存在直接返回
            if (memberEntity == null) {
                return null;
            }

            // 3. 存在，生成jwt
            Map<String, Object> map = new HashMap<>();
            map.put("id", memberEntity.getId());
            map.put("username", memberEntity.getUsername());
            return JwtUtils.generateToken(map, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
            throw new GmallException("jwt认证失败！");
        }

    }
}
