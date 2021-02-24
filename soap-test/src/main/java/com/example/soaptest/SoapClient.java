package com.example.soaptest;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-20 9:19
 */
public class SoapClient extends WebServiceGatewaySupport {
    public String sendSms() {
        return (String) getWebServiceTemplate().marshalSendAndReceive(new Object());
    }
}
