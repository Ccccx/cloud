package com.example.factorydemo.beaninit;

import com.example.factorydemo.bean.Bar;
import com.example.factorydemo.bean.Foo;
import com.example.factorydemo.bean.SpringContextUtils;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-08-14 13:41
 */
@Data
@Configuration
class BeanInitDemo implements BeanDefinitionRegistryPostProcessor {

    @Autowired
    private Bar bar;

    @Autowired
    private Foo foo;

    @Autowired
    private SpringContextUtils springContextUtils;

    @Test
    void t11() {
        Foo from = new Foo();
        from.setName("cjz");
        final PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Foo to = new Foo();
        mapper.from(from::getName).to(to::setName);
        mapper.from(from::isFlag).to(to::setFlag);
        mapper.from(from::getAge).to(to::setAge);
        System.out.println(to);
    }

    @Test
    void t1() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BeanInitDemo.class);
        // 测试通过代码方式手动注入
        final BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(AutoModeBean.class);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinition.setLazyInit(true);
        context.registerBeanDefinition("autoModeBean", beanDefinition.getBeanDefinition());
        context.refresh();

        System.out.println(context.getBean("autoModeBean"));
        // 测试引用问题  BeanInitDemo 加上@Configuration 为true ,  @Configuration(proxyBeanMethods = false) 或不加为false
        // @Configuration 为@Bean 做了方法拦截,保证是单例
        final Foo foo = context.getBean(Foo.class);
        final Bar bar = context.getBean(Bar.class);
        System.out.println("是否为相同引用 ?   " + (foo == bar.getFoo()));
    }

    @Test
    void t12() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BeanInitDemo.class);
        context.refresh();
        final BeanInitDemo beanInitDemo = context.getBean(BeanInitDemo.class);
        final SpringContextUtils contextBean = context.getBean(SpringContextUtils.class);
        final BeanFactory beanFactory = SpringContextUtils.beanFactory;
        System.out.println(beanFactory);
    }

    @Test
    void t13() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BeanInitDemo.class);
        context.refresh();
        final SpringContextUtils contextBean = context.getBean(SpringContextUtils.class);
        final BeanFactory beanFactory = SpringContextUtils.beanFactory;
        System.out.println(beanFactory);
    }



    @Test
    void t2() {
        BeanWrapper wrapper = new BeanWrapperImpl(new Foo());
        wrapper.setPropertyValue("name", "cx");
        wrapper.setPropertyValue(new PropertyValue("flag", true));
        final Foo foo = (Foo) wrapper.getWrappedInstance();
        Assertions.assertEquals("cx", foo.getName());
    }

    @PostConstruct
    public void init() {
        System.out.println("是否为相同引用 ?   " + (foo == bar.getFoo()));
    }

    @Bean
    public Bar bar() {
        final Bar bar = new Bar();
        bar.setFoo(foo());
        return bar;
    }


    @Bean
    public Foo foo() {
        System.out.println("foo init ...");
        return new Foo();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        final BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringContextUtils.class);
        beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        final AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        registry.registerBeanDefinition("springContextUtils", beanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        return;
    }


    @Data
    public static class AutoModeBean {
        @Autowired
        private Bar bar;
        @Autowired
        private Foo foo;
    }

}
