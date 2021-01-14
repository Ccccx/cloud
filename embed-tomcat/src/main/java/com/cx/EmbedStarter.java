package com.cx;

import com.cx.servlet.HelloServlet;
import com.cx.servlet.TestServlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletRegistration.Dynamic;
import java.io.File;
import java.util.Collections;

/**
 * 嵌入式tomcat 启动类, 启动后访问:
 * <a href="http://127.0.0.1:8080/"> 首页 </a>
 * <a href="http://127.0.0.1:8080/hello"> hello servlet </a>
 * <a href="http://127.0.0.1:8080/test"> test servlet </a>
 *
 * @author chengjz
 * @version 1.0
 * @since 2021-01-04 16:33
 */
@Slf4j
public class EmbedStarter {
	public static void main(String[] args) throws Exception {
		// 项目目录
		// 获取当前类启动路径
		String projectDir = System.getProperty("user.dir") + File.separator + "embed-tomcat";
		// Tomcat 应用存放的目录，JSP编译会放在这个目录。
		String tomcatBaseDir = projectDir + File.separatorChar + "tomcat";
		// 项目部署目录，我们这里需要设置为 $userDir$/target/classes 目录，因为项目编译的文件都会存到改目录下。
		String webappDir = projectDir + File.separatorChar + "target" + File.separatorChar + "classes";

		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir(tomcatBaseDir);

		Connector connector = new Connector();
		// 端口号
		connector.setPort(8080);
		connector.setURIEncoding("UTF-8");
		// 创建服务
		final Service service = tomcat.getService();
		service.addConnector(connector);

		/**
		 * addDefaultWebXmlToWebapp 默认情况下就是true，
		 * {@link Tomcat#addWebapp(Host, String, String, LifecycleListener)} 会根据这个参数添加默认web.xml配置。
		 * 默认会配置default servlet 和  jsp servlet以及其他参数 {@link Tomcat#initWebappDefaults(Context)}
		 */
		tomcat.setAddDefaultWebXmlToWebapp(true);

		// addWebapp(getHost(), contextPath, docBase);  重载方法getHost()也是一个实现了生命周期接口的监听器
		// 注意 Context 这里添加了默认的servlet, 这里是通过DefaultWebXmlListener添加的
		final Context context = tomcat.addWebapp("/", webappDir);
		context.addLifecycleListener(event -> log.info("自定义监听器: {}", event.getType()));

		// servlet 3.0方式添加TestServlet, spring boot 使用的就是这种方式
		context.addServletContainerInitializer((c, ctx) -> {
			log.warn("servlet 3.0方式添加TestServlet");
			final Dynamic dynamic = ctx.addServlet("test", new TestServlet());
			dynamic.addMapping("/test");
			dynamic.setInitParameter("aaa", "aaa");
		}, Collections.emptySet());

		// 监听器方式添加自定义的HelloServlet
		context.addLifecycleListener(event -> {
			if (Lifecycle.BEFORE_START_EVENT.equals(event.getType())) {
				log.warn(" 监听器方式添加自定义的HelloServlet");
				addServlet((Context) event.getLifecycle());
			}
		});
		tomcat.start();
		tomcat.getServer().await();
	}

	/**
	 * 等同的web.xml里的配置
	 * <p>
	 * <pre>
	 *         {@code
	 *         <!DOCTYPE web-app PUBLIC
	 *         "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
	 *         "http://java.sun.com/dtd/web-app_2_3.dtd" >
	 *
	 * <web-app>
	 *     <servlet>
	 *         <servlet-name>helloServlet</servlet-name>
	 *         <servlet-class>com.cx.servlet.HelloServlet</servlet-class>
	 *         <init-param>
	 *             <param-name>name</param-name>
	 *             <param-value>chengjz</param-value>
	 *         </init-param>
	 *         <init-param>
	 *             <param-name>sex</param-name>
	 *             <param-value>boy</param-value>
	 *         </init-param>
	 *         <init-param>
	 *             <param-name>address</param-name>
	 *             <param-value>shanghai</param-value>
	 *         </init-param>
	 *     </servlet>
	 *     <servlet-mapping>
	 *         <servlet-name>helloServlet</servlet-name>
	 *         <url-pattern>/hello</url-pattern>
	 *     </servlet-mapping>
	 * </web-app>
	 *         }
	 *     </pre>
	 * </p>
	 *
	 * @param ctx 上下文
	 * @return servlet
	 */
	public static Wrapper addServlet(Context ctx) {
		final Wrapper servlet = Tomcat.addServlet(ctx, "helloServlet", HelloServlet.class.getName());
		servlet.addInitParameter("name", "chengjz");
		servlet.addInitParameter("sex", "boy");
		servlet.addInitParameter("address", "shanghai");
		ctx.addServletMappingDecoded("/hello", "helloServlet");
		return servlet;
	}
}
