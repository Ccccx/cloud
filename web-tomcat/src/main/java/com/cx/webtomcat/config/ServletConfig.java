package com.cx.webtomcat.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-30 9:56
 */
@Component
public class ServletConfig implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        final TestServlet testServlet = new TestServlet();
        final Dynamic servletRegistration = servletContext.addServlet("testServlet", testServlet);
        servletRegistration.addMapping("/testServlet");
        servletRegistration.setInitParameter("user", "cjz");
        servletRegistration.setInitParameter("age", "25");
        servletRegistration.setInitParameter("sex", "boy");
        servletRegistration.setInitParameter("addr", "ZhengZhou");


    }
}
