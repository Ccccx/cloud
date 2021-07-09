package com.example.mybatis.rest;

import org.springframework.boot.loader.jar.Handler;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-09 14:58
 */
public class SpringJavaFileObject  implements JavaFileObject {
    private final String binaryName;
    private final URL url;
    private final String name;
    public SpringJavaFileObject(String binaryName, URL url) {
        this.url = url;
        this.binaryName = binaryName;
        this.name = binaryName.substring(binaryName.lastIndexOf(".") + 1);
    }

    @Override
    public URI toUri() {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        try {
            return url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return null;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return null;
    }

    @Override
    public Writer openWriter() throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Kind getKind() {
        return Kind.CLASS;
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        String baseName = simpleName + kind.extension;
        return kind.equals(getKind())
                && (baseName.equals(getName())
                || getName().endsWith("/" + baseName));
    }

    @Override
    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Modifier getAccessLevel() {
        return null;
    }


    public String binaryName() {
        return binaryName;
    }


    @Override
    public String toString() {
        return "SpringJavaFileObject{url=" + url + '}';
    }
}

