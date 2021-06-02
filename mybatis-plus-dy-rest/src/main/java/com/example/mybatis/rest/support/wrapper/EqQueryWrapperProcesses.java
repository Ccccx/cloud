package com.example.mybatis.rest.support.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.model.TableConfig;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-24 10:03
 */
@Order
@Service
public class EqQueryWrapperProcesses implements IQueryWrapperProcesses{
    @Override
    public boolean isSupport(String requestKey) {
        return true;
    }

    /**
     * 通用参数 ?k1=v1&k2=v2
     * @param tableConfig    表元信息
     * @param wrapper    查询wrapper
     * @param requestKey 请求key
     * @param requestVal 请求值
     */
    @Override
    public   <T extends BaseModel> void doProcess(TableConfig tableConfig, QueryWrapper<T> wrapper, String requestKey, String requestVal) {
        wrapper.eq(tableConfig.getFieldMap().get(requestKey).getColumnName(), requestVal);
    }
}
