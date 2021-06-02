package com.example.mybatis.rest.utils;


import org.springframework.lang.Nullable;

import javax.tools.JavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 只要简单继承ClassLoader即可，关键是要覆盖原来的ClassLoader#findClass()方法，用于搜索自定义的JavaFileObject实例，从而提取对应的字节码字节数组进行装载，为了实现这一点可以添加一个哈希表作为缓存，键-值分别是全类名的别名（xx.yy.MyClass形式，而非URI模式）和目标类对应的JavaFileObject实例。
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-10-27 15:54
 */
public class JdkDynamicCompileClassLoader extends ClassLoader {

    public static final String CLASS_EXTENSION = ".class";

    private final Map<String, JavaFileObject> javaFileObjectMap = new ConcurrentHashMap<>();

    public JdkDynamicCompileClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        JavaFileObject javaFileObject = javaFileObjectMap.get(name);
        if (null != javaFileObject) {
            CharSequenceJavaFileObject charSequenceJavaFileObject = (CharSequenceJavaFileObject) javaFileObject;
            byte[] byteCode = charSequenceJavaFileObject.getByteCode();
            return defineClass(name, byteCode, 0, byteCode.length);
        }
        return super.findClass(name);
    }

    @Nullable
    @Override
    public InputStream getResourceAsStream(String name) {
        if (name.endsWith(CLASS_EXTENSION)) {
            String qualifiedClassName = name.substring(0, name.length() - CLASS_EXTENSION.length()).replace('/', '.');
            CharSequenceJavaFileObject javaFileObject = (CharSequenceJavaFileObject) javaFileObjectMap.get(qualifiedClassName);
            if (null != javaFileObject && null != javaFileObject.getByteCode()) {
                return new ByteArrayInputStream(javaFileObject.getByteCode());
            }
        }
        return super.getResourceAsStream(name);
    }

    /**
     * 暂时存放编译的源文件对象,key为全类名的别名（非URI模式）,如club.throwable.compile.HelloService
     */
    void addJavaFileObject(String qualifiedClassName, JavaFileObject javaFileObject) {
        javaFileObjectMap.put(qualifiedClassName, javaFileObject);
    }

    Collection<JavaFileObject> listJavaFileObject() {
        return Collections.unmodifiableCollection(javaFileObjectMap.values());
    }
}
