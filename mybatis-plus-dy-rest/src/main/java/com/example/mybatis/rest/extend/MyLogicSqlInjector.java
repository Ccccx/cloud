package com.example.mybatis.rest.extend;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;

import java.util.List;

/**
 * 添加自定义方法
 *
 * @author dds
 * @since 2020/10/26
 */
public class MyLogicSqlInjector extends DefaultSqlInjector {

    /**
     * 如果只需增加方法，保留MP自带方法
     * 可以super.getMethodList() 再add
     *
     * @return
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new InsertAll());
        methodList.add(new MysqlInsertAllBatch());
        methodList.add(new OracleInsertAllBatch());
        return methodList;
    }
}
