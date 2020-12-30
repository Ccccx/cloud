package com.example.factorydemo.classload;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-30 9:20
 */
public class MyClassLoaderParentFirst extends ClassLoader {
    private final ClassLoader jdkClassLoader;

    private final Map<String, String> classPathMap = new HashMap<>();

    public MyClassLoaderParentFirst(ClassLoader jdkClassLoader) {
        this.jdkClassLoader = jdkClassLoader;
        classPathMap.put("com.example.factorydemo.bean.Per", "E:\\IDEA\\cloud\\factory-demo\\target\\test-classes\\com\\example\\factorydemo\\bean\\Per.class");
        classPathMap.put("com.example.factorydemo.bean.Bar", "E:\\IDEA\\cloud\\factory-demo\\target\\test-classes\\com\\example\\factorydemo\\bean\\Bar.class");
        classPathMap.put("com.example.factorydemo.bean.Foo", "E:\\IDEA\\cloud\\factory-demo\\target\\test-classes\\com\\example\\factorydemo\\bean\\Foo.class");
    }

    // 重写了 findClass 方法
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        Class result = null;
        try {
            //这里要使用 JDK 的类加载器加载 java.lang 包里面的类
            result = jdkClassLoader.loadClass(name);
        } catch (Exception e) {
            //忽略
        }
        if (result != null) {
            return result;
        }
        String classPath = classPathMap.get(name);
        File file = new File(classPath);
        if (!file.exists()) {
            throw new ClassNotFoundException();
        }

        byte[] classBytes = getClassData(file);
        if (classBytes == null || classBytes.length == 0) {
            throw new ClassNotFoundException();
        }
        return defineClass(classBytes, 0, classBytes.length);
    }

    private byte[] getClassData(File file) {
        try (InputStream ins = new FileInputStream(file); ByteArrayOutputStream baos = new
                ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesNumRead = 0;
            while ((bytesNumRead = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead);
            }
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }
}
