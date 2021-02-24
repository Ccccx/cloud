package com.example.mybatis.rest.support.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.model.RequestConstant;
import com.example.mybatis.rest.model.TableMetaConfig;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-24 10:03
 */
@Order(1)
@Service
public class SortQueryWrapperProcesses implements IQueryWrapperProcesses{
    @Override
    public boolean isSupport(String requestKey) {
        return RequestConstant.KEY_SORT.equals(requestKey);
    }

    /**
     * 处理排序, 格式 ?sort=a,asc,b,desc,c,asc
     * @param tableMetaConfig    表元信息
     * @param wrapper    查询wrapper
     * @param requestKey 请求key
     * @param requestVal 请求值
     */
    @Override
    public void doProcess(TableMetaConfig tableMetaConfig, QueryWrapper<BaseModel> wrapper, String requestKey, String requestVal) {
        final StringTokenizer tokenizer = new StringTokenizer(requestVal, RequestConstant.SPLIT);
        while (tokenizer.hasMoreTokens()) {
            final String sortKey = tokenizer.nextToken();
            final String sortMod = tokenizer.nextToken();
            switch (sortMod) {
                case RequestConstant.ORDER_ASC:
                    wrapper.orderByAsc(tableMetaConfig.getFieldMap().get(sortKey).getColumnName());
                    break;
                case RequestConstant.ORDER_DESC:
                    wrapper.orderByDesc(tableMetaConfig.getFieldMap().get(sortKey).getColumnName());
                    break;
                default:
                    throw new IllegalArgumentException( sortKey + "没有对应的排序方式");
            }
        }
    }
}
