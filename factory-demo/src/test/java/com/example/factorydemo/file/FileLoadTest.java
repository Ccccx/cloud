package com.example.factorydemo.file;

import javafx.application.Application;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.tomcat.Jar;
import org.junit.jupiter.api.Test;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.loader.jar.Handler;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-03-15 16:27
 */
@Slf4j
  class FileLoadTest {

    @Test
    @SneakyThrows
    void t1() {

        final Path savePath = Paths.get("E:\\tomcat\\tmp");
        String configHome = "META-INF";
        final URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        final Enumeration<URL> resources =  classLoader.getResources(configHome + "/spring.factories");
        while (resources.hasMoreElements()){
            final URL url = resources.nextElement();
            log.info("{}", url);
            JarFile jarFile = null;
            if (url.toExternalForm().startsWith("file")) {
//                final File file = new File(url.toURI());
//                jarFile = new JarFile(file);
                continue;
            } else if (url.toExternalForm().startsWith("jar")){
                URL file = new URL(url, "", new Handler());
                final JarURLConnection jarURLConnection = (JarURLConnection) file.openConnection();
                jarFile = jarURLConnection.getJarFile();
            }
            final Enumeration<JarEntry> entries = jarFile.entries();
            final String jarName = jarFile.getName().substring(jarFile.getName().lastIndexOf(File.separator) + 1, jarFile.getName().lastIndexOf("."));
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().startsWith(configHome)) {
                    System.out.println(jarName + ": " + jarEntry.getName());
                    final String newFilename = jarEntry.getName().replace(configHome, jarName);
                    final Path saveEntryPath = savePath.resolve(newFilename);
                    Files.createDirectories(saveEntryPath.getParent());
                    if (jarEntry.isDirectory()) {
                        continue;
                    } else {
                        Files.createFile(saveEntryPath);
                    }
                    try (final InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                        FileCopyUtils.copy(inputStream, new FileOutputStream(saveEntryPath.toFile()));
                    }
                }
            }
            System.out.println("----------------------------");
        }
    }

    @Test
    @SneakyThrows
    void t2() {
        String metaInf = "META-INF";
        String pathStr = "E:\\tm\\tmp";
        final Path path = Paths.get(pathStr);
        final URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        final Enumeration<URL> resources = new Vector<>(Arrays.asList(classLoader.getURLs())).elements();
        while (resources.hasMoreElements()){
            final URL url = resources.nextElement();
            JarFile jarFile = null;
            log.info("{}", url);
            final File file = new File(url.toURI());
            if (file.isDirectory()) {
                continue;
            }
            if (url.toExternalForm().startsWith("file:")) {
               jarFile = new JarFile(file);
            } else {
                URL jarUrl = new URL(url, "", new Handler());
                final JarURLConnection jarURLConnection = (JarURLConnection) jarUrl.openConnection();
                jarFile = jarURLConnection.getJarFile();
            }
            final String jarName = jarFile.getName().substring(jarFile.getName().lastIndexOf(File.separator) + 1, jarFile.getName().lastIndexOf("."));
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().startsWith(metaInf)) {
                    System.out.println(jarName + ": " + jarEntry.getName());
                    final String newFilename = jarEntry.getName().replace(metaInf, jarName);
                    final Path resolve = path.resolve(newFilename);
                    Files.createDirectories(resolve.getParent());
                    if (jarEntry.isDirectory()) {
                        continue;
                    } else {
                        Files.createFile(resolve);
                    }
                    try (final InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                        FileCopyUtils.copy(inputStream, new FileOutputStream(resolve.toFile()));
                    }
                }
            }
            System.out.println("----------------------------");
        }
    }

    @Test
     void t3() throws IOException {
        System.out.println(System.getenv("user.dir"));
        String path = "E:\\IDEA\\a-tm\\gitlab\\tiamaes-cloud-project-parent\\t4-right\\target\\classes\\t4";
        final File file = new File(path);
        final File[] files = file.listFiles();
        final Path tmp = Paths.get(path);
        System.out.println(tmp.toFile().getAbsolutePath());
        System.out.println("------------");

        final Path right = Files.createTempFile("right", ".tmp");
        System.out.println(right);
        System.out.println(right.toFile().getParentFile().getAbsolutePath());
        final FileEditor fileEditor = new FileEditor();
        // 规范路径
        System.out.println(right.toFile().getCanonicalPath());
        fileEditor.setAsText(right.toFile().getAbsolutePath());
        final Object value = fileEditor.getValue();
        System.out.println(value);

        final ConversionService sharedInstance = ApplicationConversionService.getSharedInstance();
        final File convert = sharedInstance.convert(tmp.toUri().toString() , File.class);
        System.out.println("使用Spring 提供的转换器转换" + convert);

        Files.delete(right);
        final ApplicationHome applicationHome = new ApplicationHome(getClass());
        final File dir = applicationHome.getDir();

        System.out.println(dir.getAbsolutePath());
    }

    @Test
    @SneakyThrows
    void t4() {

        String jarName = "/home/tmkj/zd/right/zd-right-2.2.3-SNAPSHOT-executable.jar!/BOOT-INF/lib/websocket.client-1.1.5-SNAPSHOT.jar";
        String jarInJar = jarName.startsWith("/") ? "jar:file:" + jarName : "jar:file:/" + jarName;
        URL url = new URL(new URL( jarInJar.replace("\\", "/") + "!/"), "", new Handler());
        JarURLConnection connection = (JarURLConnection) url.openConnection();
    }



    @Test
    @SneakyThrows
    void t5() {
        String path = "file:/E:\\IDEA\\cloud\\factory-demo\\src\\test\\resources\\logback-spring.xml";
        final ResourcePatternResolver patternResolver = ResourcePatternUtils.getResourcePatternResolver(null);
        final Resource[] resources = patternResolver.getResources(path);
        for (Resource resource : resources) {
            log.info("{} ", resource);
        }
    }


    @Test
    @SneakyThrows
    void t6() {
        FileSystemManager fsManager = VFS.getManager();
        final FileObject fileObject = fsManager.resolveFile("E:\\IDEA\\cloud\\upload-dir\\upload-dir-tmp");
        final FileObject[] fileObjects = fileObject.getChildren();
        for (FileObject object : fileObjects) {
            log.info("{} {}", object.getName(), object.getPath());
        }
    }


    @Test
    @SneakyThrows
    void  t7() {
        final File file = new File("E:\\taxi-command-1.1.0-SNAPSHOT-executable.jar");
        final JarFile jarFile = new JarFile(file);
        final JarEntry jarEntry = jarFile.getJarEntry("BOOT-INF/classes/application.yml");
        List<String> componentList = new ArrayList<>();
        componentList.add("${spring.application.name}");
        final InputStream inputStream = jarFile.getInputStream(jarEntry);
        final String yml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        log.info("\n是否存在${spring.application.name}： {} \n{}", yml.contains("${spring.application.name}"), yml);

//        final Enumeration<JarEntry> entries = jarFile.entries();
//        while (entries.hasMoreElements()) {
//            final JarEntry jarEntry = entries.nextElement();
//            System.out.println(jarEntry.getName());
//        }
    }
}
