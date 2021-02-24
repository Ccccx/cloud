package com.example.soaptest;

import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 *  客户端调用配置
 * @author chengjz
 * @version 1.0
 * @since 2021-02-20 9:20
 */
public class WsClientConfig {
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        //会扫描此类下面的对应的 jaxb2实体类 因为是使用 Marshaller和 unmarshaller来进行xml和bean直接转换的
        //具体是判断此路径下是否包含 ObjectFactory.class 文件
        //设置 JAXBContext 对象
        marshaller.setContextPath("cn.lqdev.webservice");
        return marshaller;
    }


    @Bean
    public SoapClient wsClient(Jaxb2Marshaller marshaller) {
        SoapClient client = new SoapClient();
        //默认对应的ws服务地址 client请求中还能动态修改的
        client.setDefaultUri("http://10.2.137.32:29164/webService");
        //指定转换类
//        client.setMarshaller(marshaller);
//        client.setUnmarshaller(marshaller);
        return client;
    }
}
