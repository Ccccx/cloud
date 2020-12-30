package com.cjz.webmvc.liquibase.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义类加载器,在运行时扩展依赖来规避sl4j启动时检测日志工厂报错问题.
 * 注意:自定义的jar不要放在任何能被应用类加载器扫描到的地方,否则会因为类冲突无法启动.
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-09-03 18:08
 */
@Slf4j
public class CustomClassLoader extends URLClassLoader {
    private static final Map<String, String> JAR_PATHS;
    private static final Method ADD_URL;
    private static final CustomClassLoader CLASS_LOADER = new CustomClassLoader();

    static {
        final Map<String, String> paths = new HashMap<>();
        final URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        final URL generatorDir = classLoader.getResource("generator");
        try {
            final File fileDir = new File(generatorDir.toURI());
            if (fileDir.exists() && fileDir.isDirectory()) {
                final File[] jars = fileDir.listFiles(file -> file.getName().endsWith(".jar"));
                if (jars == null || jars.length == 0) {
                    throw new IllegalAccessException("缺失关键依赖,请检查generator目录!");
                }
                for (File file : jars) {
                    final String fileName = file.getName();
                    final String absolutePath = file.getAbsolutePath();
                    paths.put(fileName, absolutePath);
                    log.info("Add jar {} :  {}", fileName, absolutePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JAR_PATHS = Collections.unmodifiableMap(paths);
        ADD_URL = initAddMethod();
    }

    {
        JAR_PATHS.keySet().forEach(k -> {
            try {
                final URL url = new File(JAR_PATHS.get(k)).toURI().toURL();
                super.addURL(url);
                ADD_URL.invoke(super.getParent(), url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private CustomClassLoader() {
        super(new URL[]{}, Thread.currentThread().getContextClassLoader());
    }

    public static CustomClassLoader getInstance() {
        return CLASS_LOADER;
    }

    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
