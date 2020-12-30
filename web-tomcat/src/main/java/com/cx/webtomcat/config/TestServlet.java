package com.cx.webtomcat.config;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-30 9:54
 */
@Slf4j
public class TestServlet extends HttpServlet {
    private String user;
    private String age;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("TestServlet init");
        final Enumeration<String> initParameterNames = config.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            final String key = initParameterNames.nextElement();
            log.info("{} : {}", key, config.getInitParameter(key));
        }
        final String user = config.getInitParameter("user");
        final String age = config.getInitParameter("age");
        this.user = user;
        this.age = age;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        final PrintWriter writer = resp.getWriter();
        writer.println("测试Servlet\tUser: " + user + "\tage: " + age);
        writer.flush();
    }
}
