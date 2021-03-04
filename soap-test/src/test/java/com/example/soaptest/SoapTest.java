package com.example.soaptest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.message.Message;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-20 9:30
 */
@Slf4j
public class SoapTest {
    SmsProperties smsProperties = new SmsProperties("http://10.2.137.32:8056/smsplatformwebservice.asmx", "cdadmin", "admin123");
    RestTemplate restTemplate = new RestTemplate();
    @Test
    void t1() {
        send("15036113212", "测试信息");
    }

    public boolean send(String telphone,String message){
        try{
            String url=smsProperties.getUrl() ;
            //构造http请求头
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/soap+xml;charset=UTF-8");
            headers.setContentType(type);
            headers.add("SOAPAction", "");

            String soapXml = soapXml(telphone,message);

            log.info("发送soap请求:{}",soapXml);

            HttpEntity<String> formEntity = new HttpEntity<>(soapXml, headers);

            //返回结果
            String result = restTemplate.postForObject(url, formEntity, String.class);

            log.debug("result:{}",result);

            assert result != null;
            return result.toLowerCase().contains("<sendsmsresult>success</sendsmsresult>");
        }catch(Exception e){
            log.error("短信发送错误:{}",e.toString());
        }
        return false;
    }

    /**
     * @param telphone	接收短信的手机号
     * @param message	短信内容
     * @return
     */
    protected  String soapXml(String telphone,String message){
        String soapXml="<?xml version=\"1.0\" encoding=\"utf-8\"?> <soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\"> <soap12:Header> <MySoapHeader xmlns=\"http://tempuri.org/\"> <UserID>"+smsProperties.getUserId()+"</UserID> <PassWord>"+smsProperties.getPassWord()+"</PassWord> </MySoapHeader> </soap12:Header> <soap12:Body> <SendSms xmlns=\"http://tempuri.org/\"> <nums>"+telphone+"</nums> <content>"+message+"</content> <sendLevel>0</sendLevel> </SendSms> </soap12:Body> </soap12:Envelope>";
        return soapXml;
    }


    @Setter
    @Getter
    @AllArgsConstructor
    public class SmsProperties {

        /**
         * 短信soap网关
         */
        private String url;

        /**
         * 短信soap账户
         */
        private String userId;

        /**
         * 短信soap密码
         */
        private String passWord;
    }
}
