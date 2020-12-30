package com.example.factory.support;

import com.example.factory.support.model.InitializrMetadata;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 10:36
 */
public interface InitializrMetadataProvider {
    /**
     * 获取元数据
     *
     * @return ig
     */
    InitializrMetadata get();
}
