package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JwtTest {
	private static final String pubKeyPath = "D:\\project-0615\\tmp\\rsa.pub";

    private static final String priKeyPath = "D:\\project-0615\\tmp\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "ssd23243sFED%&^*&2132");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 1);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1NzM1Mjg1ODZ9.h71D729q1dMaXDIlqUah4cRt5vhpE6yitRkt2IBEfV1JxJ7wkEalrV9CgmoSHvB2urxBtNnjVXKYFQWfWgA_OQfEy-GLCvMBPpD0M0KRkj35L38H5dAicA1TFPXZa4Kk6hBXDA4EjEGdWdo2Hqa-908UCiITpRWv4tR9yfFghe5tXkM_2mE6eQxs7BMd9vq14AnYg49n1l1mDgBS9tYvzJM0X1EH5NWpzySjJNj4uSispk_VwfYuzlggSdC7nZz1K8TSEnphDdW3w1YrtP8YYgQZdNhHJX8QZhnE9Klj-joomg_nWXMH7LaZwNeSEt4AlYulwcw4uyfD-elrMSAqgA";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}