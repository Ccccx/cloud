package com.cx.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-01-04 16:30
 */
@Slf4j
public class HelloServlet extends HttpServlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
        final Enumeration<String> parameterNames = config.getInitParameterNames();
        log.info("{} 初始化开始 >>>", this.getClass().getSimpleName());
        StringBuilder sb = new StringBuilder("\n");
        while (parameterNames.hasMoreElements()) {
            final String element = parameterNames.nextElement();
            sb.append(String.format("%s \t %s %n", element, config.getInitParameter(element)));
        }
        log.info(sb.toString());
        log.info("{} 初始化结束 <<<", this.getClass().getSimpleName());
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		final ServletOutputStream out = resp.getOutputStream();
		resp.setContentType("text/html");
		String html = "<!DOCTYPE html>\n" +
				"<html>\n" +
				"    <head>\n" +
				"        <meta charset=\"UTF-8\">\n" +
				"        <title>HelloServlet</title>\n" +
				"    </head>\n" +
				"    <body>\n" +
				"        <p>\n" +
				"            <h1>Hello World!</h1> \nThis is HelloServlet[" + UUID.randomUUID() + "]. \n" +
				"        </p>\n" +
				"    </body>\n" +
				"</html>";
		out.write(html.getBytes());
	}
}
