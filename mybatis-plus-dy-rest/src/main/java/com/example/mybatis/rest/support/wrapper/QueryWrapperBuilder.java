package com.example.mybatis.rest.support.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.model.TableConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-24 9:26
 */
@Component
public class QueryWrapperBuilder {

    @Resource
    private List<IQueryWrapperProcesses> wrapperProcesses;

    public   QueryWrapper<BaseModel> buildQueryWrapper(TableConfig tableConfig, String requestKey, String requestVal) {
        final QueryWrapper<BaseModel> wrapper = new QueryWrapper<>();
        for (IQueryWrapperProcesses wrapperProcess : wrapperProcesses) {
            if (wrapperProcess.isSupport(requestKey)) {
                if (StringUtils.isNotEmpty(requestVal)) {
                    wrapperProcess.doProcess(tableConfig, wrapper, requestKey, requestVal);
                }
                break;
            }
        }
        return wrapper;
    }

    public<T extends BaseModel>  QueryWrapper<T> buildQueryWrapper(TableConfig tableConfig, Map<String, String[]> requestMap) {
        final QueryWrapper<T> wrapper = new QueryWrapper<>();
        requestMap.forEach((requestKey, requestVal) -> {
            for (IQueryWrapperProcesses wrapperProcess : wrapperProcesses) {
                if (wrapperProcess.isSupport(requestKey)) {
                    if (requestVal.length > 0) {
                        if (StringUtils.isNotEmpty(requestVal[0])) {
                            wrapperProcess.doProcess(tableConfig, wrapper, requestKey, requestVal[0]);
                        }
                    }
                    break;
                }
            }
        });
        return wrapper;
    }


}
