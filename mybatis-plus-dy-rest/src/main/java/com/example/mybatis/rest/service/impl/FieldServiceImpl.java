package com.example.mybatis.rest.service.impl;

import com.example.mybatis.rest.persistence.model.Field;
import com.example.mybatis.rest.persistence.FieldMapper;
import com.example.mybatis.rest.service.IFieldService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 字段信息 服务实现类
 * </p>
 *
 * @author chengjz
 * @since 2021-07-05
 */
@Service
public class FieldServiceImpl extends ServiceImpl<FieldMapper, Field> implements IFieldService {

}
