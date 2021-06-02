package com.example.mybatis.rest;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.mybatis.rest.model.DyRest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-16 9:55
 */
@Slf4j
public class SqlParseTest {

    @Test
    @SneakyThrows
    void t1() {
        String sql1 = "(select * from user)";
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        final Statement parse = parserManager.parse(new StringReader(sql1));
        if (parse instanceof Select) {
            final Select select = (Select) parse;
            PlainSelect body = (PlainSelect) select.getSelectBody();
            log.info("{}", body);
            Expression where = body.getWhere();
            log.info("{}", where);
        }

        log.info("{}", parse.getClass());
    }

    @Test
    void t2() {
        final LambdaQueryWrapper<DyRest> queryWrapper = Wrappers.lambdaQuery(DyRest.class);
        queryWrapper.eq(DyRest::getId, "123");
    }
}
