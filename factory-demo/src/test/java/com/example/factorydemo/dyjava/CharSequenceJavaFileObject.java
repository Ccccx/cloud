package com.example.factorydemo.dyjava;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 内置的JavaFileObject标准实现SimpleJavaFileObject是面向类源码文件，由于动态编译时候输入的是类源码文件的内容字符串，需要自行实现JavaFileObject。
 * 如果编译成功之后，直接通过自行添加的CharSequenceJavaFileObject#getByteCode()
 * 方法即可获取目标类编译后的字节码对应的字节数组（二进制内容）。这里的CharSequenceJavaFileObject预留了多个构造函数用于兼容原有的编译方式。
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-10-27 15:50
 */
public class CharSequenceJavaFileObject extends SimpleJavaFileObject {
    public static final String CLASS_EXTENSION = ".class";

    public static final String JAVA_EXTENSION = ".java";
    private final CharSequence sourceCode;
    private ByteArrayOutputStream byteCode;

    public CharSequenceJavaFileObject(String className, CharSequence sourceCode) {
        super(fromClassName(className + JAVA_EXTENSION), Kind.SOURCE);
        this.sourceCode = sourceCode;
    }


    public CharSequenceJavaFileObject(String fullClassName, Kind kind) {
        super(fromClassName(fullClassName), kind);
        this.sourceCode = null;
    }

    public CharSequenceJavaFileObject(URI uri, Kind kind) {
        super(uri, kind);
        this.sourceCode = null;
    }

    private static URI fromClassName(String className) {
        try {
            return new URI(className);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(className, e);
        }
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return sourceCode;
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(getByteCode());
    }

    /**
     * 注意这个方法是编译结果回调的OutputStream，回调成功后就能通过下面的getByteCode()方法获取目标类编译后的字节码字节数组
     *
     * @return 结果流
     */
    @Override
    public OutputStream openOutputStream() {
        return byteCode = new ByteArrayOutputStream();
    }

    public byte[] getByteCode() {
        return byteCode.toByteArray();
    }
}
