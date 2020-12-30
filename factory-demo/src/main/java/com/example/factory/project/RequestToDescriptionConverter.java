package com.example.factory.project;

import com.example.factory.vo.BaseRequest;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 10:25
 */
@FunctionalInterface
public interface RequestToDescriptionConverter<R extends BaseRequest> {
    /**
     * 转换
     *
     * @param request 原始请求
     * @return 转换结果
     */
    ProjectDescription convert(R request);
}
