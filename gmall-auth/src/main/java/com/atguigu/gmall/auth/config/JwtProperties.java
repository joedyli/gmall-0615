package com.atguigu.gmall.auth.config;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
@Data
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    private String publicKeyPath;

    private String privateKeyPath;

    private Integer expire;

    private String cookieName;

    private String secret;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @PostConstruct
    public void init(){

        try {
            // 1. 初始化公钥私钥文件
            File publicFile = new File(publicKeyPath);
            File privateFile = new File(privateKeyPath);

            // 2. 检查文件对象是否为空
            if (!publicFile.exists() || !privateFile.exists()) {
                RsaUtils.generateKey(publicKeyPath, privateKeyPath, secret);
            }

            // 3. 读取密钥
            publicKey = RsaUtils.getPublicKey(publicKeyPath);
            privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥和私钥失败！！！");
            e.printStackTrace();
        }
    }
}
