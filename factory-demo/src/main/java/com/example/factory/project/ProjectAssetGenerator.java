package com.example.factory.project;

import java.io.IOException;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 9:46
 */
@FunctionalInterface
public interface ProjectAssetGenerator<T> {
    /**
     * @param context 子上下文
     * @return 返回结果
     * @throws IOException ig
     */
    T generate(ChildApplicationContext context) throws IOException;
}
