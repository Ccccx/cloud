package com.cjz.webmvc.liquibase.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjz.webmvc.liquibase.persistence.FormChangelogMapper;
import com.cjz.webmvc.liquibase.persistence.model.FormChangelog;
import com.cjz.webmvc.liquibase.service.IFormChangelogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chengjz
 * @since 2020-09-09
 */
@Service
public class FormChangelogServiceImpl extends ServiceImpl<FormChangelogMapper, FormChangelog> implements IFormChangelogService {

}
