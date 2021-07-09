package com.example.mybatis.rest.utils;

import lombok.SneakyThrows;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.jar.JarFile;
import org.springframework.boot.system.ApplicationHome;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-08 17:49
 */
@SuppressWarnings("all")
public class SpringClassFinder {
    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final Object lock = new Object();
    protected Predicate<JarEntry> NESTED_ARCHIVE_ENTRY_FILTER = (entry) -> {
        if (entry.isDirectory()) {
            return entry.getName().equals("BOOT-INF/classes/");
        }
        return entry.getName().startsWith("BOOT-INF/lib/");
    };
    private ClassLoader parentClassLoader;
    private volatile Map<String, List<JavaFileObject>> PACKAGE_MAP = new HashMap<>();

    public SpringClassFinder(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }

    public static String getPath() {
        ApplicationHome home = new ApplicationHome(SpringClassFinder.class);
        String path = home.getSource().getPath();
        return path;
    }

    @SneakyThrows
    public void init() {
        if (parentClassLoader instanceof LaunchedURLClassLoader) {
            JarFile jarFile = new JarFile(new File(getPath()));
            List<JarEntry> entries = jarFile.stream()
                    .filter(jarEntry -> NESTED_ARCHIVE_ENTRY_FILTER.test(jarEntry))
                    .collect(Collectors.toList());
            for (JarEntry entry : entries) {
                JarFile lib = jarFile.getNestedJarFile(jarFile.getEntry(entry.getName()));
                if (lib.getName().contains("tools.jar")) {
                    continue;
                }
                Enumeration<JarEntry> tempEntriesEnum = lib.entries();
                while (tempEntriesEnum.hasMoreElements()) {
                    JarEntry jarEntry = tempEntriesEnum.nextElement();
                    String classPath = jarEntry.getName().replace("/", ".");
                    if (!classPath.endsWith(CLASS_FILE_EXTENSION) || jarEntry.getName().lastIndexOf("/") == -1) {
                        continue;
                    }
                    String packgeName = classPath.substring(0, jarEntry.getName().lastIndexOf("/"));
                    List<JavaFileObject> javaFileObjects = PACKAGE_MAP.containsKey(packgeName) ? PACKAGE_MAP.get(packgeName) : new ArrayList<>();
                    javaFileObjects.add(
                            new SpringJavaFileObject(
                                    jarEntry.getName().replace("/", ".").replace(CLASS_FILE_EXTENSION, ""),
                                    new URL(lib.getUrl(), jarEntry.getName())
                            )
                    );
                    PACKAGE_MAP.put(packgeName, javaFileObjects);
                }
            }
        }
    }

    public List<JavaFileObject> find(String packageName) throws IOException {
        return PACKAGE_MAP.containsKey(packageName) ? PACKAGE_MAP.get(packageName) : Collections.emptyList();
    }

}
