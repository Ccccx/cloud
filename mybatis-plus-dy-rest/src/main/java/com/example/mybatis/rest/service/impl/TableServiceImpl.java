package com.example.mybatis.rest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mybatis.rest.persistence.TableMapper;
import com.example.mybatis.rest.persistence.model.Table;
import com.example.mybatis.rest.service.ITableService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 表元信息 服务实现类
 * </p>
 *
 * @author chengjz
 * @since 2021-07-06
 */
@Service
public class TableServiceImpl extends ServiceImpl<TableMapper, Table> implements ITableService {

}
