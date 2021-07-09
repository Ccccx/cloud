package com.example.mybatis.rest;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-09 14:56
 */
@Slf4j
public class SpringFile {


    public final static Map<String, List<JavaFileObject>> CLASS_OBJECT_PACKAGE_MAP = new HashMap<>();

    @Test
    @SneakyThrows
    void t9() {
        org.springframework.boot.loader.jar.JarFile jarFile = new org.springframework.boot.loader.jar.JarFile(new File("E:\\IDEA\\cloud\\mybatis-plus-dy-rest\\target\\mybatis-plus-dy-rest-0.0.1-SNAPSHOT.jar"));
        List<JarEntry> entries = jarFile.stream().filter(jarEntry -> jarEntry.getName().endsWith(".jar")).collect(Collectors.toList());
        org.springframework.boot.loader.jar.JarFile libTempJarFile = null;
        List<JavaFileObject> onePackgeJavaFiles =  null;
        String packgeName = null;
        for (JarEntry entry : entries) {
            libTempJarFile = jarFile.getNestedJarFile(jarFile.getEntry(entry.getName()));
            if(libTempJarFile.getName().contains("tools.jar")){
                continue;
            }
            Enumeration<JarEntry> tempEntriesEnum = libTempJarFile.entries();
            while (tempEntriesEnum.hasMoreElements()) {
                JarEntry jarEntry = tempEntriesEnum.nextElement();
                String classPath = jarEntry.getName().replace("/", ".");
                if (!classPath.endsWith(".class") || jarEntry.getName().lastIndexOf("/") == -1) {
                    continue;
                } else {
                    packgeName = classPath.substring(0, jarEntry.getName().lastIndexOf("/"));
                    onePackgeJavaFiles = CLASS_OBJECT_PACKAGE_MAP.containsKey(packgeName) ? CLASS_OBJECT_PACKAGE_MAP.get(packgeName) :  new ArrayList<>();
                    onePackgeJavaFiles.add(new SpringJavaFileObject(jarEntry.getName().replace("/", ".").replace(".class", ""),
                            new URL(libTempJarFile.getUrl(), jarEntry.getName())));
                    CLASS_OBJECT_PACKAGE_MAP.put(packgeName,onePackgeJavaFiles);
                }
            }
        }
        final List<JavaFileObject> javaFileObjects = CLASS_OBJECT_PACKAGE_MAP.get("com.fasterxml.jackson.core.json");
        for (JavaFileObject javaFileObject : javaFileObjects) {
            final SpringJavaFileObject springJavaFileObject = (SpringJavaFileObject) javaFileObject;
            IOUtils.copy(springJavaFileObject.openInputStream(), new FileOutputStream(new File("E:\\IDEA\\cloud\\mybatis-plus-dy-rest\\target\\tmp\\" + springJavaFileObject.getName() + ".class")));
        }
        log.info("end");
    }


}
