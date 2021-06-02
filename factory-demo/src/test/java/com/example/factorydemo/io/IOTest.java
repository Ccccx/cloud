package com.example.factorydemo.io;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-06 16:19
 */
@Slf4j
class IOTest {

    @Test
    @SneakyThrows
    void t1() {
        // 读取
        final FileInputStream fileInputStream = new FileInputStream("E:\\IDEA\\cloud\\factory-demo\\src\\test\\java\\com\\example\\factorydemo\\io\\1.txt");
        final DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        log.info("最大读取长度: {}", dataInputStream.available());
        final byte[] chars = new byte[dataInputStream.available()];
        dataInputStream.read(chars);
        log.info("txt: {}", new String(chars));

        // 写入
        final FileOutputStream fileOutputStream = new FileOutputStream("E:\\IDEA\\cloud\\factory-demo\\src\\test\\java\\com\\example\\factorydemo\\io\\2.txt");
        final DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
        dataOutputStream.write(chars);

        dataInputStream.close();
        fileOutputStream.close();
        dataInputStream.close();
        fileInputStream.close();
    }

    @Test
    @SneakyThrows
    void t2() {
        // 读取
        final FileReader fileReader = new FileReader("E:\\IDEA\\cloud\\factory-demo\\src\\test\\java\\com\\example\\factorydemo\\io\\1.txt");
        final char[] chars = new char[1024];
        final int readLength = fileReader.read(chars);
        log.info("读取到的字节数: {}", readLength);
        log.info("txt: {}", new String(chars));

        // 写入
        final FileWriter fileWriter = new FileWriter("E:\\IDEA\\cloud\\factory-demo\\src\\test\\java\\com\\example\\factorydemo\\io\\2.txt");
        fileWriter.write(chars, 0, readLength);

        fileWriter.close();
        fileReader.close();
    }

    @Test
    @SneakyThrows
    void t3() {
        // 读取
        final FileInputStream fileInputStream = new FileInputStream("E:\\IDEA\\cloud\\factory-demo\\src\\test\\java\\com\\example\\factorydemo\\io\\1.txt");
        final BufferedInputStream dataInputStream = new BufferedInputStream(fileInputStream);
        log.info("最大读取长度: {}", dataInputStream.available());
        final byte[] chars = new byte[dataInputStream.available()];
        dataInputStream.read(chars);
        log.info("txt: {}", new String(chars));

        // 写入
        final FileOutputStream fileOutputStream = new FileOutputStream("E:\\IDEA\\cloud\\factory-demo\\src\\test\\java\\com\\example\\factorydemo\\io\\2.txt");
        final BufferedOutputStream dataOutputStream = new BufferedOutputStream(fileOutputStream);
        dataOutputStream.write(chars);

        dataInputStream.close();
        fileOutputStream.close();
        dataInputStream.close();
        fileInputStream.close();
    }
}
