package com.example.mybatis.rest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mybatis.rest.persistence.TableRefMapper;
import com.example.mybatis.rest.persistence.model.TableRef;
import com.example.mybatis.rest.service.ITableRefService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 表字段关系 服务实现类
 * </p>
 *
 * @author chengjz
 * @since 2021-07-06
 */
@Service
public class TableRefServiceImpl extends ServiceImpl<TableRefMapper, TableRef> implements ITableRefService {

}
