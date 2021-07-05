package com.example.factorydemo.conditional;

import com.example.factorydemo.bean.Bar;
import com.example.factorydemo.bean.Foo;
import com.example.factorydemo.conditional.ConditionalTest.ConditionalDemo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-08 14:44
 */
@Slf4j
@Configuration
class ConditionalTest {

    @Test
    void t1() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ConditionalTest.class);
        final ConfigurableEnvironment environment = context.getEnvironment();
        environment.setActiveProfiles("prd");
        context.refresh();
        final MutablePropertySources propertySources = environment.getPropertySources();
        log.info("{}", environment.getActiveProfiles());

    }



    @Bean
    @Profile("prd")
    @Conditional(value = ConditionalDemo.class)
    @ConditionalOnClass(value = Foo.class)
    @ConditionalOnBean(name = "foo")
    public Bar bar() {
        log.warn("Bar init ...");
        return new Bar();
    }

    @Bean
    @ConditionalOnExpression("${spring.profiles.active:default}.equals('prd')")
    public Foo foo() {
        log.warn("Foo init ...");
        return new Foo();
    }

    /**
     * 合并条件操作
     * 使用 OR 或 AND 逻辑运算符对条件注释进行分组。
     * OR 需要实现 AnyNestedCondition
     * AND 直接写就行：
     * <pre>
     *     {@code
     *@Service
     * @Conditional({IsWindowsCondition.class, Java8Condition.class})
     * @ConditionalOnJava(JavaVersion.EIGHT)
     * public class LoggingService {
     *     // ...
     * }
     *     }
     * </pre>
     */
    public static class Java8OrJava9 extends AnyNestedCondition {

        Java8OrJava9() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @Conditional(Java8Condition.class)
        static class Java8 { }

        @Conditional(Java9Condition.class)
        static class Java9 { }

    }

    public static class Java8Condition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return JavaVersion.getJavaVersion().equals(JavaVersion.EIGHT);
        }
    }

    public static class Java9Condition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return JavaVersion.getJavaVersion().equals(JavaVersion.NINE);
        }
    }



    public static class ConditionalDemo implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
            if (attrs != null) {
                for (Object value : attrs.get("value")) {
                    if (context.getEnvironment().acceptsProfiles(((String[]) value))) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
    }

}
