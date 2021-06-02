package com.example.factorydemo.spel;

import com.example.factorydemo.bean.Foo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-12 13:54
 */
@Slf4j
class SpelTest {


    @Test
    void t1() {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext ctx = new StandardEvaluationContext();

        final Foo foo = new Foo();
        foo.name = "cxx";
        foo.setAge(10);
        foo.setFlag(true);
        ctx.setRootObject(foo);

        // 取值
        SpelExpression expr = parser.parseRaw("name");
        Object value = expr.getValue(ctx);
        assertThat(value).isEqualTo("cxx");

        // 赋值
        expr = parser.parseRaw("age=4");
        value = expr.getValue(ctx);
        expr = parser.parseRaw("age");
        value = expr.getValue(ctx);
        assertThat(value).isEqualTo(4);
    }


    public static String repeat(String s) {
        return s + s;
    }

    @Test
    void t2() {
        try {
            // Create a parser
            SpelExpressionParser parser = new SpelExpressionParser();
            // Use the standard evaluation context
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            ctx.registerFunction("repeat", SpelTest.class.getDeclaredMethod("repeat", String.class));

            Expression expr = parser.parseRaw("#repeat('hello')");
            Object value = expr.getValue(ctx);
            assertThat(value).isEqualTo("hellohello");
        } catch (EvaluationException | ParseException | NoSuchMethodException ex) {
            throw new AssertionError(ex.getMessage(), ex);
        }
    }

    @Test
    void t3() {
        try {
            // 配置编译模式, 默认是表达式解析模式
            SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
            // Create a parser
            SpelExpressionParser parser = new SpelExpressionParser(config);
            // Use the standard evaluation context
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            ctx.registerFunction("repeat", SpelTest.class.getDeclaredMethod("repeat", String.class));


            Expression expr = parser.parseRaw("#repeat('hello')");
            Object value = expr.getValue(ctx);
            assertThat(value).isEqualTo("hellohello");

            List<String> list = new ArrayList<>();
            list.add("str0");
            list.add("str1");
            list.add("str2");
            ctx.setVariable("arg", list);

            expr = parser.parseRaw("#arg[1]");
            value = expr.getValue(ctx);

            final SpelExpression spelExpression = parser.parseRaw("#{T(java.lang.Math).random()*100.0}");
            final Object random = spelExpression.getValue(ctx);
            log.info("random: {}", random);
            assertThat(value).isEqualTo("str1");
        } catch (EvaluationException | ParseException | NoSuchMethodException ex) {
            throw new AssertionError(ex.getMessage(), ex);
        }
    }

    @Test
    @SneakyThrows
    void t4() {
        final Foo root = new Foo();
        String str = "Spel Method Test";
        SpelExpressionParser parser = new SpelExpressionParser();
        ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(
                root, root.getClass().getMethod("say", String.class), new Object[]{str}, parameterNameDiscoverer);
        final Expression expression = parser.parseExpression("#str");
        final Object value = expression.getValue(evaluationContext);
        assertThat(value).isEqualTo(str);
    }

    @Test
    @SneakyThrows
    void t5() {
        GregorianCalendar c = new GregorianCalendar();
        c.set(1856, 7, 9);
    }

    @Test
    @SneakyThrows
    void t999() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Bar.class);
        final MutablePropertySources propertySources = context.getEnvironment().getPropertySources();
        final Properties properties = new Properties();
        properties.setProperty("dir", "default");
        final PropertiesPropertySource test = new PropertiesPropertySource("test", properties);
        propertySources.addFirst(test);
        context.refresh();
        final Bar bar = context.getBean(Bar.class);
        Assert.notNull(bar, "error");
        log.info("{}", FileCopyUtils.copyToString(new FileReader(bar.file)));
    }

    @Configuration
    public static class Bar {
        @Value("${dir}/resource.txt")
        private File file;
    }

}
