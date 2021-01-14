package com.example.factorydemo.classload;

import com.example.factorydemo.bean.Per;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-07 15:14
 */
@Slf4j
class ClassLoadTest {

    /**
     * 获取系统环境中的jar
     */
    @Test
    void t1() throws IOException {
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
    void t2() throws Exception {
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
        System.out.println("JarEntry ---------------");
        final JarFile jarFile = new JarFile("D:/software/apache-maven-3.6.0/repository/org/springframework/spring-beans/5.2.2.RELEASE/spring-beans-5.2.2.RELEASE.jar");
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            System.out.println(entry);
        }
        System.out.println("MANIFEST.MF ---------------");
        final JarEntry jarEntry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
        System.out.println(jarEntry);
        System.out.println("manifest ---------------");
//		URLConnection connection = url.openConnection();
//		connection.setUseCaches(false);
//		InputStream resourceAsStream = connection.getInputStream();

        Manifest manifest = jarFile.getManifest();
        Attributes mainAttributes = manifest.getMainAttributes();
        final Set<Object> objects = mainAttributes.keySet();
        objects.forEach(obj -> {
            System.out.println(obj + "\t" + mainAttributes.get(obj));
        });
        System.out.println("license ---------------");
        final JarEntry license = jarFile.getJarEntry("META-INF/license.txt");
        InputStream jarInputStream = jarFile.getInputStream(license);
        BufferedReader reader = new BufferedReader(new InputStreamReader(jarInputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        jarInputStream.close();
        System.out.println("ProtectionDomain ---------------");
        final ProtectionDomain protectionDomain = Tomcat.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = (codeSource != null) ? codeSource.getLocation().toURI() : null;
        String path = (location != null) ? location.getSchemeSpecificPart() : null;
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }
        File root = new File(path);
        if (!root.exists()) {
            throw new IllegalStateException("Unable to determine code source archive from " + root);
        }
        System.out.println(root.getPath());
    }

    @Test
    void t3() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        // 默认类加载器加载
        final Per bar = new Per();
        bar.logClassLoader();
        System.out.println("-----------");
        // 使用自定义类加载器加载,这里破坏了双亲委派机制,因此当前类加载的所有类都会被当前类加载器加载
        final MyClassLoaderParentFirst myClassLoaderParentFirst = new MyClassLoaderParentFirst(Thread.currentThread().getContextClassLoader().getParent());
        final Class<?> fooClz = myClassLoaderParentFirst.findClass("com.example.factorydemo.bean.Foo");
        final Class<?> perClz = myClassLoaderParentFirst.findClass("com.example.factorydemo.bean.Per");
        final Method newInstance = perClz.getDeclaredMethod("newInstance");
        newInstance.invoke(null);
    }


    protected void addRootPath(URL path, List<String> rootStrings) {
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
