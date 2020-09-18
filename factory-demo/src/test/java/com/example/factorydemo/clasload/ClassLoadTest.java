package com.example.factorydemo.clasload;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import sun.misc.IOUtils;
import sun.nio.ch.IOUtil;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.*;
import java.util.zip.ZipEntry;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-07 15:14
 */
@Slf4j
public class ClassLoadTest {

	/**
	 * 获取系统环境中的jar
	 */
	@Test
	public void t1() throws IOException {
		final String property = System.getProperty("java.class.path");
		final StringTokenizer tokenizer = new StringTokenizer(property, ";");
		while (tokenizer.hasMoreTokens()) {
			System.out.println("java.class.path : " + tokenizer.nextToken());
		}

		final PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		 Resource[] resources = resourcePatternResolver.getResources("META-INF/MANIFEST.MF");
		for (Resource resource : resources) {
			System.out.println(resource.getURI().toURL());
		}
	}

	@Test
	public void t2() throws Exception {
		final URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		Enumeration<URL> baseUrls = new Vector<>(Arrays.asList(classLoader.getURLs())).elements();
		List<String> rootStrings = new ArrayList<>();
		while (baseUrls.hasMoreElements()) {
			addRootPath(baseUrls.nextElement(), rootStrings);
		}
		baseUrls = classLoader.getResources("");

		while (baseUrls.hasMoreElements()) {
			addRootPath(baseUrls.nextElement(), rootStrings);
		}

		//rootStrings.forEach(System.out::println);
		PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resourcePatternResolver.getResources("");
		for (Resource resource : resources) {
			System.out.println(resource.getURI().toURL());
		}
		System.out.println("---------------");
		final JarFile jarFile = new JarFile("D:/software/apache-maven-3.6.0/repository/org/springframework/spring-beans/5.2.2.RELEASE/spring-beans-5.2.2.RELEASE.jar");
		final Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			System.out.println(entry);
		}
		System.out.println("---------------");
		final JarEntry jarEntry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
		System.out.println(jarEntry);
		System.out.println("---------------");
//		URLConnection connection = url.openConnection();
//		connection.setUseCaches(false);
//		InputStream resourceAsStream = connection.getInputStream();

		 Manifest manifest = jarFile.getManifest();
		 Attributes mainAttributes = manifest.getMainAttributes();
		final Set<Object> objects = mainAttributes.keySet();
		objects.forEach(obj -> {
			System.out.println(obj + "\t" + mainAttributes.get(obj));
		});
		System.out.println("---------------");
		final JarEntry license = jarFile.getJarEntry("META-INF/license.txt");
		InputStream  jarInputStream = jarFile.getInputStream(license);
		BufferedReader reader =new BufferedReader(new InputStreamReader(jarInputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		jarInputStream.close();
	}


	protected void addRootPath(URL path, List<String> rootStrings ) {
		if (path == null) {
			return;
		}
		String externalForm = path.toExternalForm();
		if (externalForm.startsWith("file:")) {
			try {
				externalForm = new File(path.toURI()).getCanonicalFile().toURI().toURL().toExternalForm();
			} catch (Exception e) {
				//keep original version
			}
		}
		if (!externalForm.endsWith("/")) {
			externalForm += "/";
		}
		if (!rootStrings.contains(externalForm)) {
			rootStrings.add(externalForm);
		}
	}
}
