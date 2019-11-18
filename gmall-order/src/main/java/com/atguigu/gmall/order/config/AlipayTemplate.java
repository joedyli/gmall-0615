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
    private  String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCpmuygB1vAThNWYAgdJcZob9pOJqoWuaXW3mhLzH4xB9kDmMWEbOWnqfn14EHDhsJKLXqdnriOpdS8SV1+4CMdBILc2NK0KRCqGrZbrMJNjbbTz2HPnN3vOL6kHPOvr3CF99G78ReMteEX0dDNC13dLdbOOiLPSc69neKmt5gTqfoG4xjgiwMi2yxLbkV2RlhR+tRXq8ou0waQ78LvRkMt4OYJCPoI91QrfSlqwEpSIktLoM9ENhPKDFF/U/QjUIZZwDZQQFah+KGlNpLgJa1/xRkhHGwU+UwiuXG/2aOu8ll4+615aGc1jS/8oKnzhI5Y1dfX57QUGoLuqigWiUTHAgMBAAECggEBAIM93+cBaq1HfU00aHLtnTeJfjtFTg7hv4OifMBS0D9unC5dVFJh1eBE9qidzSXZLJUr1hsMDDJN/m1otBGOrX0x1XXBzmIc+Mk4fG46I+zRQW1rp3t5Hn+TwGnMAYGsV31DRCeTqy52O2UsNc/FAPA7HWJduDwBGBrAhveNT/++IJp9bkQvFWeI7ya7kzaS51ZeeZrwb6G5AI8RTlRF5dHaCWuec36FQjA4xG+E4yIq1hJFYaKJzTY0eTdO53D1+V8eWt1CiwRIL9BN9H4Z/14R61z1cgthythBPt7Ws2nQ0uUpmerndnBbeHHpQl5G8+6vAQQFMOn4WKSmq+XgrlkCgYEA1sxWI5A7XfsKxRTktbp7oqbD1F+JXtNESS534+uWQ64nd94yQuaSBu5KcXjbFp+IeEAKycTpxvEf+Is/rpjc+bl4at9Q2mcb1ZNFh/W32vMxgYm+raRnCf9EoeA8y4kMd0lauGPTdr3CxdkQcwSObog82p8Ey3lz03r+adcJjs0CgYEAyiNiliFaSyGSsVmRIPL2BFsb3KQj67kLKrlrxiuSGm0iGsfp1jCctxqGBcn1dp5xvfBf+6JDcTK1dVG/4p7foCqfaHtXqjFoEkfLgvzPt1w0evWk1chaIiTZXiqg2zHGVwcI/x4BSHxs2w5ZGAlxEpsjszVFXEIkmEvuaiFUOeMCgYEAmvylytNvgsh5ZOtGe7orK5mZA8xLmphmVUeqSlLbAcrPv7YcHhmwlD+sh2Pk8dX66omLPztOU7X4k6YmNR0nlSk2siageHadpuW07f1mxB1mQ0nYxAI0NwpxwzvBojspO4k6ZIHjO3KA77FIJTXMcATto/HJy+e+o4HDwSrgQqECgYA8mRUSCtel1F/EuSQv64ZjUejtYWu/XA8D9OhhVWiMcZEa1Dm+0333yoTJMX1b3S4eEfTEXCW5xsjtbd+HkbdarcxyJx/lzr/zUwCouR+QSZ0WC5Qp3tFekQt1FnR4odhi+KHy0UzzLRH5Lbj54sK6IDY4oEy11sJfwcusl2dHnwKBgFAD7OZBbHCXW+LwFo4AHDGweZcolTTYX+uZy60SwSaPp80CAiVDj3zBEay0fhUdOVg+POUg7ZhH0uV2zSegGBCYLN2QfpTvkwW7uQP0f93S1ds5HblQDxhWoPjOCSmBJx1fmmBwMZ6VhwzL1dywsZz4xZH3JN0wjUjz4HTJXqvW";
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
