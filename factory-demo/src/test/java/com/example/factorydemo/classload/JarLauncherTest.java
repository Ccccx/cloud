package com.example.factorydemo.classload;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.JarFile;

import java.io.File;
import java.util.Iterator;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-09 14:02
 */
@Slf4j
public class JarLauncherTest {

    @Test
    @SneakyThrows
    void t1() {
        final File file = new File("E:\\IDEA\\cloud\\factory-demo\\target\\factory-demo-0.0.1-SNAPSHOT.jar");
        final TestLauncher jarLauncher = new TestLauncher(new JarFileArchive(file));
        jarLauncher.launch(null);
    }

    public static class TestLauncher extends JarLauncher {
        public TestLauncher() {
        }

        public TestLauncher(Archive archive) {
            super(archive);
        }

        @Override
        public void launch(String[] args) throws Exception {
            if (!isExploded()) {
                JarFile.registerUrlProtocolHandler();
            }
            final Iterator<Archive> classPathArchivesIterator = getClassPathArchivesIterator();
            ClassLoader classLoader = createClassLoader(classPathArchivesIterator);
            while (classPathArchivesIterator.hasNext()) {
                final Archive entries = classPathArchivesIterator.next();
            }
        }
    }


}
