package com.example.factorydemo.classload;

import com.alibaba.fastjson.JSONObject;
import com.example.factorydemo.bean.Per;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.loader.jar.Handler;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

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

    @Test
    void t4() throws Exception{
        final URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        Enumeration<URL> baseUrls = new Vector<>(Arrays.asList(classLoader.getURLs())).elements();
        while (baseUrls.hasMoreElements()) {
            final URL url = baseUrls.nextElement();
           log.info("{}", url);
        }
        System.out.println("---------");
    }

    @Test
    @SneakyThrows
    void t5() {
        String jarFileStr = " jar:file:/E:/tm/t4-right-2.2.2-SNAPSHOT-executable.jar!/BOOT-INF/lib/tiamaes-cloud-core-2.2.2-SNAPSHOT.jar!/config/bootstrap.properties";
        final URL jarFile = new URL(jarFileStr);
        URL url = new URL(jarFile, "", new Handler());
        final URLConnection urlConnection = url.openConnection();
        final StringBuilderWriter sb = new StringBuilderWriter();
        final PrintWriter printWriter = new PrintWriter(sb);
        IOUtils.copy(urlConnection.getInputStream(), printWriter, StandardCharsets.UTF_8);
    }

    @Test
    @SneakyThrows
    void t6() {

        String rootPath = "file:/E:/IDEA/a-tm/gitlab/tiamaes-cloud-project-parent/t4-right/target/t4-right-2.2.2-SNAPSHOT-executable.jar";
        final File file = new File(URI.create(rootPath));
        System.out.println("----------");

        //jar:file:/
        String jarInJar = "E:\\IDEA\\a-tm\\gitlab\\tiamaes-cloud-project-parent\\t4-right\\target\\t4-right-2.2.2-SNAPSHOT-executable.jar!/BOOT-INF/lib/ria.bootstrap-1.1.5-SNAPSHOT.jar";
        final URL url = new URL(new URL("jar:file:/" +  jarInJar.replace("\\", "/") + "!/"), "", new Handler());
        final JarURLConnection connection =  (JarURLConnection)url.openConnection();
        final JarFile jarFile =   connection.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            System.out.println(entries.nextElement());
        }
    }

    @Test
    @SneakyThrows
    void t7() {
        String jarInJar = "jar:file:/E:\\tm\\m1\\m1-demo-2.2.3-SNAPSHOT-executable.jar!/BOOT-INF/lib/m1-online-form-2.2.3-SNAPSHOT.jar";
        final URL url = new URL(new URL("jar:file:/" +  jarInJar.replace("\\", "/") + "!/"), "", new Handler());
        final JarURLConnection connection =  (JarURLConnection)url.openConnection();
        final JarFile jarFile =   connection.getJarFile();
        List<URL> jars = new ArrayList<>();

    }

    @Test
    @SneakyThrows
    void t8() {
        // "ch.qos.logback.core.ContextBase";
//        String jarInJar = "jar:file:/E:\\IDEA\\a-tm\\gitlab\\tiamaes-cloud-project-parent\\m1-demo\\target\\m1-demo-2.2.3-SNAPSHOT-executable.jar!/BOOT-INF/lib/m1-online-form-2.2.3-SNAPSHOT.jar!/generator/logback-core-1.2.3.jar!/";
        String jarInJar = "jar:file:/E:\\IDEA\\a-tm\\gitlab\\tiamaes-cloud-project-parent\\m1-demo\\target\\m1-demo-2.2.3-SNAPSHOT-executable.jar!/BOOT-INF/lib/m1-online-form-2.2.3-SNAPSHOT.jar!/";
        final URL url = new URL(new URL( jarInJar.replace("\\", "/")), "", new Handler());
        final JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        final JarFile jarFile = jarURLConnection.getJarFile();

        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry jarEntry = entries.nextElement();
            if ( jarEntry.getName().startsWith("generator") && jarEntry.getName().endsWith(".jar") && !jarEntry.isDirectory()) {
                log.error(jarEntry.getName());
            }
            log.info(jarEntry.getName());
        }

        final ZipEntry core = jarFile.getEntry("generator/logback-core-1.2.3.jar");
        final ZipEntry classic = jarFile.getEntry("generator/logback-classic-1.2.3.jar");
        ClassPathClassLoader loader = new ClassPathClassLoader();
        final InputStream inputStream = jarFile.getInputStream(classic);

        ClassPathClassLoader.readJAR(readJar(jarFile.getInputStream(core), "logback-core-1.2.3.jar"));
        ClassPathClassLoader.readJAR(readJar(jarFile.getInputStream(core), "logback-classic-1.2.3.jar"));
      final Class<?> aClass = loader.loadClass("ch.qos.logback.core.ContextBase");
        log.info("-----------");
    }

    private  JarFile readJar(InputStream in, String  name) throws IOException {
        File targetFile = new File(name);
        FileUtils.copyInputStreamToFile(in, targetFile);
        return new JarFile(targetFile);
    }

    public static class ClassPathClassLoader extends ClassLoader{

        private static Map<String, byte[]> classMap = new ConcurrentHashMap<>();
        private String classPath;

        public ClassPathClassLoader() {

        }


        public static boolean addClass(String className, byte[] byteCode) {
            if (!classMap.containsKey(className)) {
                classMap.put(className, byteCode);
                return true;
            }
            return false;
        }

        /**
         * 遵守双亲委托规则
         */
        @Override
        protected Class<?> findClass(String name) {
            try {
                byte[] result = getClass(name);
                if (result == null) {
                    throw new ClassNotFoundException();
                } else {
                    return defineClass(name, result, 0, result.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private byte[] getClass(String className) {
            if (classMap.containsKey(className)) {
                return classMap.get(className);
            } else {
                return null;
            }
        }




        public static void readJAR(JarFile jar) throws IOException {
            Enumeration<JarEntry> en = jar.entries();
            while (en.hasMoreElements()) {
                JarEntry je = en.nextElement();
                je.getName();
                String name = je.getName();
                if (name.endsWith(".class")) {
                    //String className = name.replace(File.separator, ".").replace(".class", "");
                    String className = name.replace("\\", ".")
                            .replace("/", ".")
                            .replace(".class", "");
                    InputStream input = null;
                    ByteArrayOutputStream baos = null;
                    try {
                        input = jar.getInputStream(je);
                        baos = new ByteArrayOutputStream();
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        int bytesNumRead = 0;
                        while ((bytesNumRead = input.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesNumRead);
                        }
                        addClass(className, baos.toByteArray());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (baos != null) {
                            baos.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                    }
                }
            }
        }
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
