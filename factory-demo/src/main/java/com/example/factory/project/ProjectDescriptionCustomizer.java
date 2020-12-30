package com.example.factory.project;

import com.example.factory.project.model.MutableProjectDescription;
import org.springframework.core.Ordered;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 13:52
 */
@FunctionalInterface
public interface ProjectDescriptionCustomizer extends Ordered {
    /**
     * 自定义方法
     *
     * @param description ig
     */
    void customize(MutableProjectDescription description);

    /**
     * 循序号
     *
     * @return 序号
     */
    @Override
    default int getOrder() {
        return 0;
    }
}
