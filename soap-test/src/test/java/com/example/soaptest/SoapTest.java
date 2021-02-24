package com.example.soaptest;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.message.Message;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-20 9:30
 */
public class SoapTest {
    @Test
    void t1() {
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient("http://10.2.137.32:39164/webService?wsdl");
        @SuppressWarnings("unchecked")
        Map<String, List<String>> headers = (Map<String, List<String>>) client.getRequestContext()
                .get(Message.PROTOCOL_HEADERS);

        if (headers == null) {
            headers = new TreeMap<String, List<String>>(
                    String.CASE_INSENSITIVE_ORDER);
            client.getRequestContext().put(Message.PROTOCOL_HEADERS, headers);
        }
        headers.put("UserID", Collections.singletonList("cdadmin"));
        headers.put("PassWord", Collections.singletonList("admin123"));
        Object[] objects;
        try {
            // invoke("方法名",参数1,参数2,参数3....);
            objects = client.invoke("SendSms", "15036113212", "测试短信", "1");
            System.out.println("返回数据:" + objects[0]);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }

    }
}
