package com.atguigu.gmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016101200666477";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCp6l+7ejOwopK/uSmA1Vv9HFK6T5FhLMpUXnF3t2XIf7LfpwWeB4ykD8qFd/v4HnOVV71wdiXoCaTs3Y3tdTVsWSCua7FB0Q1Qd/amEcjIa24fIABY2ZeZDsgn8hgX8GfBVdktFZWWuG6G4VsvlD2IYc3SsXSetOZDyXhzfgJ1P1S6vfvMsUQ3gXFkizx6PovF8ALDM/k0LKHA4igM/QpdQ+PmaK0QV0hpHaeiTQOjZaVtcHPkGhB4zr4z0z5W8deN71r2p5DomVLzAsT2ehCueqeAF77FYWlnZAFgVSj8gUFR9stewEWmhP3KBENG3lk7rVOasIcVT7TwRHFwGaf5AgMBAAECggEAcJN9W/xKFQSRj/9A9T/nU5qZYSFoo+aGI0Hzkarr/9xDihTiDuLXlrdzqdxmD+01DUR1mfZdhRAmulzMNQDMJX4jvgxSA9ZbKO5id4Me8VLkQjD0qel4nIsDqBupROseAyU637kGRDbr2+ehf7OK1TpfoPG7347hZRYt/O8G9+CqfU4A0GfImwgs1ndMsxGCTVCkniqe/GAf25eXkficCcJ02kcqBXy6J9yoezSmEb9M8c3P49LJiSu9A7jKOO798CsJWHryLZI0w4qUKuj0CiAiT12igaWbZoOuaUYEq+LfNhvgHDzJnfH8oc8SANkQA6G1xym6+cMC8pKr2+p2lQKBgQDUJjTGXq5LB6RtSttOi/q2+zawLO4SM8vmqsAQu7lHXVgE9bjzlsuh7aMvfwnhJth5F/fD8wX7AUf7moEwQVqAQneqO4XPrUtGqOIKXavOnIQYkz3zyC7X1QskpYn1wDhZHhQwtEkVPRuvo2FPjAhBFyi4QS2laQU0PJ364eTzEwKBgQDNCWMaiuo/GZuuD0ZFO6hN/f5qXB8pDsHJFy+UYwoT1xohnfBzzPpbcYcGdlm5xP8WSftl7zAUqlKca85usagdfeToB7vau9MK7dtrhR/6xbBHOU6u4FrTG7fL2C59tJixoC2V0CDaNhvWYTHs67M0OFdXttUS1buAr7/fDfEOQwKBgHhRxuKutiklh3LY3rKYnzQCHA0lHkzChaUoOFAFN9qqrQ37Es9MOed3sHJymXbRojpjhojinwSxwFKJWTW2Cw7tI5MvuP+E6EmF5NU+NP+0MxMBB0ToxvBwCqbZH/p57ztT3N2WklByXO5rOfh0kgXgjZ6akLncazAqJOsyDLhzAoGAFLoLM6axUVmkXC8dGmiSdffkHRpc6Aie3FYksCpm3WSUNqvBn+Mrt8y9KpESNu5MHxOxfzdzs9MX8MoRHy2EYh7U4gPOIKl2BxfOY4sclXwgesK3SoRRih+2L7wVzw1Plj0r/oYAnC52F/DRXDLjdbbwVWSdG39RqguaKNE++gMCgYAKpw968BBsxuInirGkwozv8vGkFh4xedVtySAXwyFFOnKHW8c85BUA7DMBVF8LPUFB1F+2gagHYH/jeLetj4j6IY9pcsb+QzMXBNTdPxPvQgnYB6ZUHuGoLb/sqiYyAvp55blMYvGPtFSyZqWp+1oF1UHW4jFaAb1Xp8UY8+c52g==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm0CbzDSMv9508/xEgXOciNrNyvR6pdJVDQ7iuXpJNXcniJB94G+wB3jn8EDygAwsdYHiQH8B6kp7clQ0tY3EEVy7w8/gleOT5zSG5eqLjtHy7VdWsWiQbDH6Tsl5ZMrr92Ki0QbWxepL642BG/8EeKsi3+/wdSWPV0Ujbw1+lpTID5wpiQbpCYOOs5bAuhJeqJNa4ovVPm238CEzmXCYmpH4fnsh6pjO/BiSLrdMx8Z2xy+cjQ3wcEF0R8vGgOz+q9yiuVkXaPU6emLJ/m8baRlkREszl80AghKNgBuV0771jsRykA81oR+9iJo/tdpvqisGW4TLirnrq1iDLDHZOQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://9glldacce2.52http.net/api/order/pay/success";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = null;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
